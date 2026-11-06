package app.jixxy.platform

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.Bond
import app.jixxy.engine.api.BrainSnapshot
import app.jixxy.engine.api.CharacterState
import app.jixxy.engine.api.JixxyBrain
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.PauseChoiceKind
import app.jixxy.engine.api.PauseSpec
import app.jixxy.engine.api.Reaction
import app.jixxy.engine.api.ReactionKindOut
import app.jixxy.engine.api.Signal
import app.jixxy.engine.api.UsageEventType
import app.jixxy.engine.api.UserSettings
import app.jixxy.engine.fakes.InMemoryStateStore
import app.jixxy.platform.signals.SignalBus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DispatcherTest {
    private val say = Reaction(ReactionKindOut.SAY, "l1", "hi", Mood.CHILL, CharacterState.IDLE, 3000, null)
    private val pause = Reaction(
        ReactionKindOut.PAUSE_PROMPT, "p1", "wait", Mood.GRUMPY, CharacterState.JUDGING, 0,
        PauseSpec("com.x", "X", 42),
    )

    private class FakeBrain(var out: List<Reaction>) : JixxyBrain {
        val seen = mutableListOf<Signal>()
        override fun onSignal(signal: Signal): List<Reaction> { seen += signal; return out }
        override val snapshot = BrainSnapshot(Mood.CHILL, 100, Bond.STRANGER, 0, false)
    }

    private class FakeRenderer : ReactionRenderer {
        val said = mutableListOf<String>()
        var pauses = 0
        var homes = 0
        var onChoice: ((PauseChoiceKind) -> Unit)? = null
        override fun say(text: String, bubbleMs: Long, lineId: String?) { said += text }
        override fun setCharacterState(state: CharacterState) = Unit
        override fun showPause(spec: PauseSpec, text: String, onChoice: (PauseChoiceKind) -> Unit) {
            pauses++; this.onChoice = onChoice
        }
        override fun goHome() { homes++ }
    }

    private class Rig(scope: kotlinx.coroutines.CoroutineScope, reactions: List<Reaction>, var settings: UserSettings = UserSettings()) {
        val bus = SignalBus()
        val brain = FakeBrain(reactions)
        val store = InMemoryStateStore()
        val renderer = FakeRenderer()
        val dispatcher = Dispatcher(scope, bus, brain, store, renderer, { settings }, now = { 1_000L })
            .also { it.start() }
    }

    @Test fun rendersSayWhenAllowed() = runTest(UnconfinedTestDispatcher()) {
        val r = Rig(backgroundScope, listOf(say))
        r.bus.emit(Signal.Heartbeat(1))
        assertEquals(listOf("hi"), r.renderer.said)
    }

    @Test fun silentDuringCall() = runTest(UnconfinedTestDispatcher()) {
        val r = Rig(backgroundScope, listOf(say))
        r.bus.emit(Signal.CallState(true, 1))
        r.bus.emit(Signal.Heartbeat(2))
        assertTrue(r.renderer.said.isEmpty())
        assertEquals(2, r.brain.seen.size)  // the brain still hears about the call
    }

    @Test fun silentInProtectedApp() = runTest(UnconfinedTestDispatcher()) {
        val r = Rig(backgroundScope, listOf(say))
        r.bus.emit(Signal.ForegroundApp("com.bank", AppCategory.PROTECTED, 1))
        assertTrue(r.renderer.said.isEmpty())
        assertTrue(r.store.eventsBetween(0, 10).none { it.type == UsageEventType.FOREGROUND })  // not logged either
    }

    @Test fun silentWhileNapping() = runTest(UnconfinedTestDispatcher()) {
        val r = Rig(backgroundScope, listOf(say), UserSettings(napUntilTs = 5_000L))
        r.bus.emit(Signal.Heartbeat(1))
        assertTrue(r.renderer.said.isEmpty())
    }

    @Test fun blindModeDropsEverything() = runTest(UnconfinedTestDispatcher()) {
        val r = Rig(backgroundScope, listOf(say), UserSettings(blindMode = true))
        r.bus.emit(Signal.Heartbeat(1))
        assertTrue(r.brain.seen.isEmpty())
        assertTrue(r.renderer.said.isEmpty())
    }

    @Test fun leaveGoesHomeAndEmitsChoice() = runTest(UnconfinedTestDispatcher()) {
        val r = Rig(backgroundScope, listOf(pause))
        r.bus.emit(Signal.Heartbeat(1))
        assertEquals(1, r.renderer.pauses)
        r.brain.out = emptyList()
        r.renderer.onChoice!!.invoke(PauseChoiceKind.LEAVE)
        assertEquals(1, r.renderer.homes)
        assertTrue(r.brain.seen.any { it is Signal.PauseChoice && it.choice == PauseChoiceKind.LEAVE })
        assertTrue(r.store.eventsBetween(0, 10_000).any { it.type == UsageEventType.PAUSE_LEAVE })
    }

    @Test fun stayDoesNotGoHome() = runTest(UnconfinedTestDispatcher()) {
        val r = Rig(backgroundScope, listOf(pause))
        r.bus.emit(Signal.Heartbeat(1))
        r.brain.out = emptyList()
        r.renderer.onChoice!!.invoke(PauseChoiceKind.STAY)
        assertEquals(0, r.renderer.homes)
    }

    @Test fun secondPauseIgnoredWhileOneShowing() = runTest(UnconfinedTestDispatcher()) {
        val r = Rig(backgroundScope, listOf(pause))
        r.bus.emit(Signal.Heartbeat(1))
        r.bus.emit(Signal.Heartbeat(2))
        assertEquals(1, r.renderer.pauses)
    }
}
