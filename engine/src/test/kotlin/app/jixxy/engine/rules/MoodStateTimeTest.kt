package app.jixxy.engine.rules

import app.jixxy.engine.api.*
import org.junit.Assert.*
import org.junit.Test
import java.time.ZoneOffset

class MoodStateTimeTest {
    @Test fun lateNightIsSoftAndQuietHoursAreSleepy() {
        val r = Rig()
        r.send(Signal.Heartbeat(at(0, 23, 30)))
        assertEquals(Mood.SOFT, r.brain.snapshot.mood)
        r.send(Signal.Heartbeat(at(1, 3)))
        assertEquals(Mood.SLEEPY, r.brain.snapshot.mood)
        r.send(Signal.Heartbeat(at(1, 12)))
        assertEquals(Mood.CHILL, r.brain.snapshot.mood)
    }

    @Test fun lowBatteryPanicsUntilCharging() {
        val r = Rig()
        val out = r.send(Signal.BatteryLow(9, at(0, 12))).single()
        assertEquals(Mood.PANICKING, out.mood)
        assertEquals(CharacterState.PANIC, out.state)
        assertEquals("9", r.picker.queries.last().slots["battery"])
        r.send(Signal.Charger(true, 9, at(0, 12, 30)))
        assertEquals(Mood.CHILL, r.brain.snapshot.mood)
    }

    @Test fun threeTapsMakeHimGrumpyAndItFades() {
        val r = Rig()
        val out = r.send(Signal.SpriteTapped(3, at(0, 12))).single()
        assertEquals(Mood.GRUMPY, out.mood)
        assertEquals(CharacterState.POKED, out.state)
        r.send(Signal.Heartbeat(at(0, 12, 11)))
        assertEquals(Mood.CHILL, r.brain.snapshot.mood)
    }

    @Test fun wakingHimIsGrumpy() {
        val r = Rig()
        r.send(Signal.NapRequested(30, at(0, 12)))
        val out = r.send(Signal.WakeRequested(at(0, 12, 5))).single()
        assertEquals(Mood.GRUMPY, out.mood)
        assertEquals(CharacterState.WAKE_GRUMPY, out.state)
    }

    @Test fun napRepliesSleepyAndRestoresEnergyWhenItEnds() {
        val r = Rig(state = BrainState(firstSeenTs = 1L, energy = 10))
        val out = r.send(Signal.NapRequested(60, at(0, 12))).single()
        assertEquals(Mood.SLEEPY, out.mood)
        assertEquals(CharacterState.SLEEP, out.state)
        r.send(Signal.Heartbeat(at(0, 13, 1)))
        assertTrue(r.brain.snapshot.energy >= 40)
        assertTrue(r.brain.snapshot.napping.not())
    }

    @Test fun smugAfterAStay() {
        val r = Rig()
        val out = r.send(Signal.PauseChoice("t", PauseChoiceKind.STAY, at(0, 12))).single()
        assertEquals(Mood.SMUG, out.mood)
    }

    @Test fun energyLimitsChatterThenHeSaysHesOnBreak() {
        val r = Rig()
        val outs = (0 until 14).map { r.send(Signal.SpriteTapped(1, at(0, 12) + it * 1000L)).single() }
        assertEquals(14, outs.count { it.kind == ReactionKindOut.SAY })
        assertEquals(13, r.picker.queries.count { !it.onBreak })
        assertTrue(r.picker.queries.last().onBreak)
        assertEquals(0, r.brain.snapshot.energy)
    }

    @Test fun onBreakWithNoLineFallsBackToSilentJudging() {
        val r = Rig(state = BrainState(firstSeenTs = 1L, energy = 0))
        r.picker.returnNull = true
        assertEquals(ReactionKindOut.SILENT_JUDGE, r.send(Signal.SpriteTapped(1, at(0, 12))).single().kind)
    }

    @Test fun energyRegeneratesWithTime() {
        val r = Rig(state = BrainState(firstSeenTs = 1L, energy = 0))
        r.send(Signal.Heartbeat(at(0, 12)))
        r.send(Signal.Heartbeat(at(0, 13)))
        assertEquals(10, r.brain.snapshot.energy)
    }

    @Test fun chargerRestoresEnergy() {
        val r = Rig(state = BrainState(firstSeenTs = 1L, energy = 50))
        r.send(Signal.Charger(true, 40, at(0, 12)))
        assertEquals(52, r.brain.snapshot.energy)
    }

    @Test fun bondGrowsWithLaughsAndLeavesAndNeverShrinksFromAbsence() {
        val r = Rig()
        for (i in 0 until 50) r.send(Signal.UserReaction("fx:POKE:$i", ReactionKind.LAUGH, at(0, 12) + i))
        assertEquals(100, r.brain.snapshot.bondXp)
        assertEquals(Bond.ROOMMATE, r.brain.snapshot.bond)
        r.send(Signal.Heartbeat(at(40, 12)))
        assertEquals(100, r.brain.snapshot.bondXp)
        assertEquals(Bond.ROOMMATE, r.brain.snapshot.bond)
        assertEquals(50, r.picker.reactions.size)
    }

    @Test fun sleepyReactionDoesNotChangeBond() {
        val r = Rig()
        r.send(Signal.UserReaction("fx", ReactionKind.SLEEPY, at(0, 12)))
        assertEquals(0, r.brain.snapshot.bondXp)
    }

    @Test fun slotsOnlyCarryKnownFacts() {
        val r = Rig()
        r.send(Signal.ForegroundApp("a", AppCategory.OTHER, at(0, 12)))
        val q = r.picker.queries.last()
        assertEquals("App[a]", q.slots["app"])
        assertEquals("12:00", q.slots["time"])
        assertNull(q.slots["battery"])
        assertNull(q.slots["minutes"])
    }

    @Test fun eventsAreRecordedWithoutContentAndDrained() {
        val r = Rig()
        r.send(Signal.ForegroundApp("a", AppCategory.OTHER, at(0, 23, 30)))
        r.send(Signal.SessionTick("a", 1, at(0, 23, 31)))
        val types = r.brain.drainEvents().map { it.type }
        assertTrue(UsageEventType.FOREGROUND in types)
        assertTrue(UsageEventType.LATE_NIGHT_OPEN in types)
        assertTrue(UsageEventType.SESSION_MINUTES in types)
        assertTrue(r.brain.drainEvents().isEmpty())
    }

    @Test fun windowWrapsPastMidnight() {
        assertTrue(TimeUtil.inWindow(23 * 60 + 30, 23 * 60, 60))
        assertTrue(TimeUtil.inWindow(30, 23 * 60, 60))
        assertFalse(TimeUtil.inWindow(60, 23 * 60, 60))
        assertFalse(TimeUtil.inWindow(12 * 60, 23 * 60, 60))
        assertTrue(TimeUtil.inWindow(2 * 60, 60, 420))
        assertFalse(TimeUtil.inWindow(5, 5, 5))
    }

    @Test fun nightKeySpansMidnightAndClockFormats() {
        val z = ZoneOffset.UTC
        assertEquals(TimeUtil.nightKey(at(0, 23, 30), z), TimeUtil.nightKey(at(1, 0, 30), z))
        assertTrue(TimeUtil.nightKey(at(0, 23, 30), z) != TimeUtil.nightKey(at(1, 23, 30), z))
        assertEquals("07:05", TimeUtil.clock(at(0, 7, 5), z))
    }
}
