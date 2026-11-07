package app.jixxy.engine.rules

import app.jixxy.engine.api.*
import org.junit.Assert.*
import org.junit.Test
import java.time.ZoneOffset

/** D-017: Pause runtime state survives process death through [BrainState.pause], with old saves still loading. */
class PauseRuntimeTest {
    private val app = "a.app"
    private val settings = UserSettings(targetApps = setOf(app))

    private fun brain(state: BrainState, picker: FakePicker, events: List<UsageEvent> = emptyList()) =
        DefaultJixxyBrain(state, { settings }, picker, { "App[$it]" }, ZoneOffset.UTC, events)

    private fun open(b: DefaultJixxyBrain, ts: Long) = b.onSignal(Signal.ForegroundApp(app, AppCategory.SOCIAL, ts))
    private fun choose(b: DefaultJixxyBrain, ts: Long, c: PauseChoiceKind) = b.onSignal(Signal.PauseChoice(app, c, ts))
    private fun tick(b: DefaultJixxyBrain, ts: Long, min: Int) = b.onSignal(Signal.SessionTick(app, min, ts))
    private fun isPause(r: List<Reaction>) = r.any { it.kind == ReactionKindOut.PAUSE_PROMPT }
    private val fresh get() = BrainState(firstSeenTs = 1L)

    @Test fun encodeDecodeRoundTrip() {
        val p = PauseRuntime(
            savedTs = 123L, graceUntil = mapOf("a.b" to 5L), stayExpiry = mapOf("a.b" to 9L),
            secondPromptDone = setOf("a.b"), promptsToday = mapOf("a.b" to 2), promptsDayEpoch = 20_000L, leaveStreak = 2
        )
        assertEquals(p, PauseRuntime.decode(p.encode()))
        assertEquals(PauseRuntime(), PauseRuntime.decode(PauseRuntime().encode()))
    }

    @Test fun missingOrDamagedTextLoadsAsDefault() {
        assertEquals(PauseRuntime(), PauseRuntime.decode(null))
        assertEquals(PauseRuntime(), PauseRuntime.decode(""))
        assertEquals(PauseRuntime(), PauseRuntime.decode("{not json"))
        assertEquals(PauseRuntime(), PauseRuntime.decode("[1,2]"))
        assertEquals(PauseRuntime(savedTs = 7L), PauseRuntime.decode("""{"v":9,"savedTs":7,"future":{"x":1}}"""))
    }

    @Test fun oldSaveWithoutPauseFieldsLoadsAndWorks() {
        val old = BrainState(energy = 80, bondXp = 12, firstSeenTs = 1L, unpromptedToday = 2)
        assertEquals(PauseRuntime(), old.pause)
        val b = brain(old, FakePicker())
        assertTrue(isPause(open(b, at(0, 10))))
        assertEquals(80, b.snapshot.energy)
    }

    @Test fun stayGraceSurvivesRestartWithoutAnyEvents() {
        val b1 = brain(fresh, FakePicker())
        open(b1, at(0, 10))
        choose(b1, at(0, 10, 0, 5), PauseChoiceKind.STAY)
        val saved = b1.currentState()
        val b2 = brain(saved, FakePicker(), events = emptyList())
        assertFalse("inside the grant: no new card", isPause(open(b2, at(0, 10, 5))))
    }

    @Test fun leaveGraceSurvivesRestartAndEndsOnTime() {
        val b1 = brain(fresh, FakePicker())
        open(b1, at(0, 10))
        choose(b1, at(0, 10, 0, 5), PauseChoiceKind.LEAVE)
        val saved = b1.currentState()
        val b2 = brain(saved, FakePicker())
        assertFalse(isPause(open(b2, at(0, 10, 1))))
        val b3 = brain(saved, FakePicker())
        assertTrue(isPause(open(b3, at(0, 10, 0, 5) + (BrainConfig.leaveGraceMinutes + 1) * 60_000L)))
    }

    @Test fun secondPromptComesOnceAcrossRestarts() {
        val b1 = brain(fresh, FakePicker())
        open(b1, at(0, 10))
        choose(b1, at(0, 10, 0, 5), PauseChoiceKind.STAY)
        val dueTs = at(0, 10, 0, 5) + BrainConfig.stayGraceMinutes * 60_000L
        val b2 = brain(b1.currentState(), FakePicker())
        b2.onSignal(Signal.ForegroundApp(app, AppCategory.SOCIAL, dueTs - 1_000L))
        assertTrue("second prompt after restart", isPause(tick(b2, dueTs, 8)))
        val b3 = brain(b2.currentState(), FakePicker())
        b3.onSignal(Signal.ForegroundApp(app, AppCategory.SOCIAL, dueTs + 1_000L))
        assertFalse("never a third", isPause(tick(b3, dueTs + 60_000L, 9)))
    }

    @Test fun promptCountContinuesAfterRestartToday() {
        val b1 = brain(fresh, FakePicker())
        open(b1, at(0, 10))
        choose(b1, at(0, 10, 0, 5), PauseChoiceKind.LEAVE)
        val afterGrace = at(0, 11)
        val b2 = brain(b1.currentState(), FakePicker())
        open(b2, afterGrace)
        assertEquals(2, b2.currentState().pause.promptsToday[app])
    }

    @Test fun promptCountResetsOnANewDay() {
        val b1 = brain(fresh, FakePicker())
        open(b1, at(0, 10))
        val b2 = brain(b1.currentState(), FakePicker())
        open(b2, at(1, 10))
        assertEquals(1, b2.currentState().pause.promptsToday[app])
    }

    @Test fun leaveStreakSurvivesRestart() {
        val b1 = brain(fresh, FakePicker())
        for (i in 0 until BrainConfig.impressedLeaveStreak - 1) {
            open(b1, at(0, 10 + i))
            choose(b1, at(0, 10 + i, 0, 5), PauseChoiceKind.LEAVE)
        }
        val b2 = brain(b1.currentState(), FakePicker())
        val t = at(0, 10 + BrainConfig.impressedLeaveStreak)
        open(b2, t)
        val out = choose(b2, t + 5_000L, PauseChoiceKind.LEAVE)
        assertEquals(Trigger.IMPRESSED, out.single().trigger())
    }

    @Test fun eventsNewerThanTheSaveStillApplyOnTopOfIt() {
        val b1 = brain(fresh, FakePicker())
        open(b1, at(0, 10))
        val saved = b1.currentState()
        val stayTs = saved.pause.savedTs + 60_000L
        val newer = listOf(UsageEvent(stayTs, UsageEventType.PAUSE_STAY, app))
        val b2 = brain(saved, FakePicker(), newer)
        assertFalse(isPause(open(b2, stayTs + 60_000L)))
    }

    @Test fun savedStateIsSmall() {
        val b = brain(fresh, FakePicker())
        open(b, at(0, 10))
        choose(b, at(0, 10, 0, 5), PauseChoiceKind.STAY)
        assertTrue(b.currentState().pause.encode().length < 400)
    }

    @Test fun expiredGrantsAreNotKept() {
        val b = brain(fresh, FakePicker())
        open(b, at(0, 10))
        choose(b, at(0, 10, 0, 5), PauseChoiceKind.LEAVE)
        b.onSignal(Signal.Heartbeat(at(0, 12)))
        assertTrue(b.currentState().pause.graceUntil.isEmpty())
    }
}
