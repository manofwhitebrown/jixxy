package app.jixxy.engine.rules

import app.jixxy.engine.api.*
import app.jixxy.engine.lines.Line
import app.jixxy.engine.lines.LineBank
import app.jixxy.engine.memory.EventHistory
import app.jixxy.engine.selector.BankLinePicker
import app.jixxy.engine.selector.LinePicker
import app.jixxy.engine.selector.LineQuery
import app.jixxy.engine.selector.PickedLine
import org.junit.Assert.*
import org.junit.Test
import java.time.ZoneOffset
import kotlin.random.Random

class MemoryTest {
    private val a = "a.app"
    private fun open(r: Rig, ts: Long, pkg: String) = r.send(Signal.ForegroundApp(pkg, AppCategory.SOCIAL, ts))
    private fun callbackQueries(r: Rig) = r.picker.queries.filter { it.trigger == Trigger.CALLBACK }

    /** Opens [a] [times] times, 30 minutes apart, with another app in between. Returns the last reaction list. */
    private fun visits(r: Rig, times: Int): List<Reaction> {
        var out = emptyList<Reaction>()
        for (i in 0 until times) {
            open(r, at(0, 10 + i), "other$i")
            out = open(r, at(0, 10, 30) + i * 3_600_000L, a)
        }
        return out
    }

    @Test fun callbackNeedsEnoughVisits() {
        val r = Rig()
        visits(r, BrainConfig.callbackVisitsDay - 1)
        assertTrue(callbackQueries(r).isEmpty())
    }

    @Test fun callbackQuotesRealVisitCount() {
        val r = Rig()
        val out = visits(r, BrainConfig.callbackVisitsDay)
        val q = callbackQueries(r).single()
        assertEquals("fact:app_visits_day", q.requiredTag)
        assertEquals("4", q.slots["count"])
        assertEquals("App[$a]", q.slots["app"])
        assertEquals(Trigger.CALLBACK, out.single().trigger())
    }

    @Test fun callbackRespectsMutedApp() {
        val r = Rig(UserSettings(mutedApps = setOf(a)))
        visits(r, 6)
        assertTrue(callbackQueries(r).isEmpty())
    }

    @Test fun callbackCountsTowardTheDailyCap() {
        val r = Rig(state = BrainState(firstSeenTs = 1L, unpromptedDayEpoch = TimeUtil.epochDay(at(0, 10), ZoneOffset.UTC),
            unpromptedToday = BrainConfig.maxUnpromptedPerDay))
        visits(r, 6)
        assertTrue(callbackQueries(r).isEmpty())
    }

    @Test fun pausesTodayCallbackUsesRealPauseCount() {
        val r = Rig(UserSettings(targetApps = setOf("t.app")))
        repeat(3) { i ->
            r.send(Signal.ForegroundApp("t.app", AppCategory.SOCIAL, at(0, 9 + i)))
            r.send(Signal.PauseChoice("t.app", PauseChoiceKind.LEAVE, at(0, 9 + i, 1)))
            open(r, at(0, 9 + i, 5), "other")
        }
        open(r, at(0, 13), "calm.app")
        val q = callbackQueries(r).single()
        assertEquals("fact:pauses_day", q.requiredTag)
        assertEquals("3", q.slots["count"])
    }

    /** Fake that gives almost-caring lines a real-looking id so the gap rule can find them. */
    private class CaringPicker : LinePicker {
        val triggers = mutableListOf<Trigger>()
        private var n = 0
        override fun pick(query: LineQuery): PickedLine? {
            triggers += query.trigger
            n++
            val id = if (query.trigger == Trigger.ALMOST_CARING) "almost_caring_any_$n" else "x_$n"
            return PickedLine(id, "t")
        }
    }

    private fun nightBrain(picker: CaringPicker) =
        DefaultJixxyBrain(BrainState(firstSeenTs = 1L), { UserSettings() }, picker, { it }, ZoneOffset.UTC)

    private fun night(b: DefaultJixxyBrain, day: Int) {
        b.onSignal(Signal.ForegroundApp("n$day", AppCategory.SOCIAL, at(day, 23, 30)))
    }

    @Test fun almostCaringAfterThreeLateNightsThenWaitsThreeDays() {
        val p = CaringPicker()
        val b = nightBrain(p)
        night(b, 0); night(b, 1)
        assertFalse(Trigger.ALMOST_CARING in p.triggers)
        night(b, 2)
        assertEquals(1, p.triggers.count { it == Trigger.ALMOST_CARING })
        night(b, 3); night(b, 4)
        assertEquals(1, p.triggers.count { it == Trigger.ALMOST_CARING })
        night(b, 5)
        assertEquals(2, p.triggers.count { it == Trigger.ALMOST_CARING })
    }

    @Test fun pauseStateSurvivesRestart() {
        val t = "t.app"
        val s = UserSettings(targetApps = setOf(t))
        val first = Rig(s)
        first.send(Signal.ForegroundApp(t, AppCategory.SOCIAL, at(0, 12)))
        first.send(Signal.PauseChoice(t, PauseChoiceKind.STAY, at(0, 12, 0, 5)))
        val saved = first.brain.drainEvents()

        val picker = FakePicker()
        val second = DefaultJixxyBrain(BrainState(firstSeenTs = 1L), { s }, picker, { "App[$it]" }, ZoneOffset.UTC, saved)
        // Restarted inside the 5 minute grace: no new card.
        assertTrue(second.onSignal(Signal.ForegroundApp(t, AppCategory.SOCIAL, at(0, 12, 3))).isEmpty())
        // After grace the card returns and the count continues (2nd prompt of the day), not restarts at 1.
        second.onSignal(Signal.ForegroundApp("other", AppCategory.SOCIAL, at(0, 12, 4)))
        val out = second.onSignal(Signal.ForegroundApp(t, AppCategory.SOCIAL, at(0, 12, 20))).single()
        assertEquals(ReactionKindOut.PAUSE_PROMPT, out.kind)
        assertEquals("2", picker.queries.last().slots["count"])
        assertEquals(1, picker.queries.last().tier)
    }

    @Test fun withoutRestoredEventsGraceIsLost() {
        val t = "t.app"
        val s = UserSettings(targetApps = setOf(t))
        val fresh = DefaultJixxyBrain(BrainState(firstSeenTs = 1L), { s }, FakePicker(), { it }, ZoneOffset.UTC)
        assertEquals(ReactionKindOut.PAUSE_PROMPT,
            fresh.onSignal(Signal.ForegroundApp(t, AppCategory.SOCIAL, at(0, 12, 3))).single().kind)
    }

    @Test fun restoredLeaveStreakStillEarnsImpressed() {
        val t = "t.app"
        val s = UserSettings(targetApps = setOf(t))
        val first = Rig(s)
        repeat(2) { i ->
            first.send(Signal.ForegroundApp(t, AppCategory.SOCIAL, at(0, 9 + i)))
            first.send(Signal.PauseChoice(t, PauseChoiceKind.LEAVE, at(0, 9 + i, 1)))
            first.send(Signal.ForegroundApp("o", AppCategory.SOCIAL, at(0, 9 + i, 20)))
        }
        val second = DefaultJixxyBrain(BrainState(firstSeenTs = 1L), { s }, FakePicker(), { it }, ZoneOffset.UTC,
            first.brain.drainEvents())
        second.onSignal(Signal.ForegroundApp(t, AppCategory.SOCIAL, at(0, 12)))
        val out = second.onSignal(Signal.PauseChoice(t, PauseChoiceKind.LEAVE, at(0, 12, 1))).single()
        assertEquals(Trigger.IMPRESSED, out.trigger())
    }

    @Test fun historyCountsAndRetention() {
        val h = EventHistory(ZoneOffset.UTC)
        h.add(UsageEvent(at(0, 10), UsageEventType.FOREGROUND, "x"))
        h.add(UsageEvent(at(0, 11), UsageEventType.FOREGROUND, "x"))
        h.add(UsageEvent(at(1, 9), UsageEventType.FOREGROUND, "x"))
        h.add(UsageEvent(at(1, 9), UsageEventType.PAUSE_LEAVE, "x"))
        assertEquals(1, h.appVisitsToday("x", at(1, 12)))
        assertEquals(3, h.appVisitsWeek("x", at(1, 12)))
        assertEquals(1, h.leavesWeek(at(1, 12)))
        h.add(UsageEvent(at(40, 9), UsageEventType.FOREGROUND, "x"))
        assertEquals(1, h.all().size)
        assertEquals(at(40, 9), h.lastTs())
    }

    @Test fun lateNightsWeekCountsDistinctNights() {
        val h = EventHistory(ZoneOffset.UTC)
        h.add(UsageEvent(at(0, 23, 30), UsageEventType.LATE_NIGHT_OPEN, "x"))
        h.add(UsageEvent(at(1, 0, 20), UsageEventType.LATE_NIGHT_OPEN, "x"))  // same night as above
        h.add(UsageEvent(at(2, 23, 40), UsageEventType.LATE_NIGHT_OPEN, "x"))
        assertEquals(2, h.lateNightsWeek(at(3, 8)))
    }

    @Test fun restoredRingStopsRepeatsAfterRestart() {
        fun l(id: String) = Line(id, Trigger.APP_OPEN, emptySet(), setOf(Mood.CHILL), 1, 0, "en", "hi", emptySet(), emptySet(), false)
        val p = BankLinePicker(LineBank(listOf(l("one"), l("two"))), Random(3))
        p.restoreRecent(listOf("one"))
        val q = LineQuery(Trigger.APP_OPEN, null, Mood.CHILL, RoastLevel.NORMAL, Bond.STRANGER, "en", emptyMap())
        assertEquals("two", p.pick(q)!!.id)
    }
}
