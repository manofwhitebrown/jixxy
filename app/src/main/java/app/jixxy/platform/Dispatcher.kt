package app.jixxy.platform

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.CharacterState
import app.jixxy.engine.api.JixxyBrain
import app.jixxy.engine.api.PauseChoiceKind
import app.jixxy.engine.api.PauseSpec
import app.jixxy.engine.api.Reaction
import app.jixxy.engine.api.ReactionKindOut
import app.jixxy.engine.api.Signal
import app.jixxy.engine.api.StateStore
import app.jixxy.engine.api.UsageEvent
import app.jixxy.engine.api.UsageEventType
import app.jixxy.engine.api.UserSettings
import app.jixxy.platform.signals.SignalBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** What the dispatcher needs from the UI side of the platform layer (implemented by [OverlayService]). */
interface ReactionRenderer {
    fun say(text: String, bubbleMs: Long, lineId: String?)
    fun setCharacterState(state: CharacterState)
    /** [onCancelled] fires when the card is removed WITHOUT a choice (screen off, nap, service stop). */
    fun showPause(spec: PauseSpec, text: String, onChoice: (PauseChoiceKind) -> Unit, onCancelled: () -> Unit)

    /** Removes the card if one is up. Must not report a choice. */
    fun dismissPause()
    fun goHome()
}

/**
 * Glue (T-PLATFORM-5): SignalBus -> JixxyBrain.onSignal -> render each Reaction.
 * The brain decides what to say; this class only decides whether it is allowed to be *shown*:
 * never during a call, in a PROTECTED app, while napping or in blind mode.
 * All methods run on the main thread (collected from [scope]).
 */
class Dispatcher(
    private val scope: CoroutineScope,
    private val bus: SignalBus,
    private val brain: JixxyBrain,
    private val store: StateStore,
    private val renderer: ReactionRenderer,
    private val settings: () -> UserSettings,
    private val now: () -> Long = System::currentTimeMillis,
    private val diag: (String, String) -> Unit = { _, _ -> },
) {
    private var inCall = false
    private var currentCategory = AppCategory.OTHER
    private var pauseShowing = false
    private var pausePkg: String? = null
    private var pauseSeq = 0  // identifies which card a timeout belongs to

    fun start() {
        scope.launch { bus.signals.collect { handle(it) } }
    }

    private suspend fun handle(signal: Signal) {
        if (settings().blindMode) return
        track(signal)
        log(signal)
        brain.onSignal(signal).forEach { render(it) }
    }

    private fun track(signal: Signal) {
        when (signal) {
            is Signal.CallState -> { inCall = signal.inCall; if (inCall) cancelPause() }
            is Signal.ForegroundApp -> {
                // Facts only (no package names): lets the debug report show what the detector saw while a card was up.
                diag("FOREGROUND", "category=${signal.category} cardShowing=$pauseShowing sameAppAsCard=${signal.pkg == pausePkg}")
                currentCategory = signal.category
                // The user moved on: a card left over the wrong app would be confusing and blocks nothing useful.
                if (signal.pkg != pausePkg) cancelPause()
            }
            is Signal.ScreenState -> if (!signal.on) cancelPause()
            else -> Unit
        }
    }

    /** Drops a showing card without recording a choice, and frees the dispatcher for the next Pause. */
    private fun cancelPause(reason: String = "left-app-or-call-or-screen") {
        if (!pauseShowing) return
        pauseShowing = false
        pausePkg = null
        diag("PAUSE_CANCELLED", reason)
        renderer.dismissPause()
    }

    private fun canRender(): Boolean {
        val napping = NapPolicy.isNapping(settings().napUntilTs, now())
        return !inCall && currentCategory != AppCategory.PROTECTED && !napping
    }

    private fun render(r: Reaction) {
        if (!canRender()) return
        renderer.setCharacterState(r.state)
        when (r.kind) {
            ReactionKindOut.SAY -> r.text?.let {
                renderer.say(it, r.bubbleMs, r.lineId)
                scope.launch { store.logEvent(UsageEvent(now(), UsageEventType.LINE_SHOWN, lineId = r.lineId)) }
            }
            ReactionKindOut.PAUSE_PROMPT -> r.pause?.let { showPause(it, r.text.orEmpty()) }
            ReactionKindOut.ANIMATE_ONLY, ReactionKindOut.SILENT_JUDGE -> Unit  // state only
        }
    }

    private fun showPause(spec: PauseSpec, text: String) {
        if (pauseShowing) return
        pauseShowing = true
        pausePkg = spec.pkg
        val seq = ++pauseSeq
        diag("PAUSE_SHOWN", spec.pkg)
        // Safety net: a card nobody answered (missed switch signal, odd phone) must not stay on screen for ever.
        scope.launch { delay(CARD_TIMEOUT_MS); if (pauseShowing && pauseSeq == seq) cancelPause("timeout") }
        scope.launch { store.logEvent(UsageEvent(now(), UsageEventType.PAUSE_SHOWN, spec.pkg)) }
        renderer.showPause(
            spec, text,
            onChoice = { choice ->
                pauseShowing = false
                pausePkg = null
                // A refused/failed navigation must never swallow the choice: the card is already gone and the engine must hear it.
                diag("PAUSE_CHOICE", choice.name)
                if (choice == PauseChoiceKind.LEAVE) runCatching { renderer.goHome() }.onFailure { diag("GO_HOME_FAILED", it.javaClass.simpleName) }
                bus.emit(Signal.PauseChoice(spec.pkg, choice, now()))
            },
            onCancelled = { pauseShowing = false; pausePkg = null; diag("PAUSE_CANCELLED", "external") },
        )
    }

    /** Logs only facts the engine asked for; never anything from PROTECTED apps. Respects blind mode via the store. */
    private suspend fun log(signal: Signal) {
        val ts = signal.ts
        val protectedApp = currentCategory == AppCategory.PROTECTED
        when (signal) {
            is Signal.ForegroundApp -> if (signal.category != AppCategory.PROTECTED)
                store.logEvent(UsageEvent(ts, UsageEventType.FOREGROUND, signal.pkg))
            is Signal.SessionTick -> if (!protectedApp)
                store.logEvent(UsageEvent(ts, UsageEventType.SESSION_MINUTES, signal.pkg, signal.minutes))
            is Signal.Charger -> if (signal.connected) store.logEvent(UsageEvent(ts, UsageEventType.CHARGER_ON))
            is Signal.NapRequested -> store.logEvent(UsageEvent(ts, UsageEventType.NAP, value = signal.minutes))
            is Signal.UserReaction -> store.logEvent(UsageEvent(ts, UsageEventType.LINE_REACTION, lineId = signal.lineId))
            is Signal.PauseChoice -> store.logEvent(
                UsageEvent(
                    ts,
                    if (signal.choice == PauseChoiceKind.STAY) UsageEventType.PAUSE_STAY else UsageEventType.PAUSE_LEAVE,
                    signal.pkg,
                )
            )
            else -> Unit
        }
    }

    private companion object {
        const val CARD_TIMEOUT_MS = 120_000L
    }
}
