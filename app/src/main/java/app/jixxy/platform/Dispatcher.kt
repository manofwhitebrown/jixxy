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
import kotlinx.coroutines.launch

/** What the dispatcher needs from the UI side of the platform layer (implemented by [OverlayService]). */
interface ReactionRenderer {
    fun say(text: String, bubbleMs: Long, lineId: String?)
    fun setCharacterState(state: CharacterState)
    fun showPause(spec: PauseSpec, text: String, onChoice: (PauseChoiceKind) -> Unit)
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
) {
    private var inCall = false
    private var currentCategory = AppCategory.OTHER
    private var pauseShowing = false

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
            is Signal.CallState -> inCall = signal.inCall
            is Signal.ForegroundApp -> currentCategory = signal.category
            else -> Unit
        }
    }

    private fun canRender(): Boolean {
        val nap = settings().napUntilTs
        val napping = nap != null && nap > now()
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
        scope.launch { store.logEvent(UsageEvent(now(), UsageEventType.PAUSE_SHOWN, spec.pkg)) }
        renderer.showPause(spec, text) { choice ->
            pauseShowing = false
            if (choice == PauseChoiceKind.LEAVE) renderer.goHome()
            bus.emit(Signal.PauseChoice(spec.pkg, choice, now()))
        }
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
}
