package app.jixxy.engine.rules

import app.jixxy.engine.api.*
import app.jixxy.engine.memory.EventHistory
import org.junit.Assert.*
import org.junit.Test
import java.time.ZoneOffset

/** T-E3-3: facts and callbacks come only from recorded events; every number here is hand-counted. */
class FactsTest {
    private fun ev(ts: Long, t: UsageEventType, pkg: String? = null, v: Int? = null) = UsageEvent(ts, t, pkg, v)

    private val fixture = listOf(
        ev(at(0, 10), UsageEventType.PAUSE_STAY, "a"), ev(at(2, 11), UsageEventType.PAUSE_STAY, "a"),
        ev(at(3, 9), UsageEventType.PAUSE_STAY, "b"),
        ev(at(1, 10), UsageEventType.PAUSE_LEAVE, "a"), ev(at(1, 15), UsageEventType.PAUSE_LEAVE, "a"),
        ev(at(2, 10), UsageEventType.PAUSE_LEAVE, "b"),
        ev(at(1, 23, 30), UsageEventType.LATE_NIGHT_OPEN, "a"), ev(at(1, 23, 45), UsageEventType.LATE_NIGHT_OPEN, "b"),
        ev(at(3, 23, 10), UsageEventType.LATE_NIGHT_OPEN, "a"), ev(at(2, 0, 30), UsageEventType.LATE_NIGHT_OPEN, "a"),
        ev(at(3, 8), UsageEventType.SESSION_MINUTES, "a", 1), ev(at(4, 8), UsageEventType.SESSION_MINUTES, "a", 1),
        ev(at(4, 9), UsageEventType.SESSION_MINUTES, "a", 1), ev(at(4, 10), UsageEventType.SESSION_MINUTES, "a", 1),
        ev(at(4, 8), UsageEventType.SESSION_MINUTES, "b", 1), ev(at(4, 9), UsageEventType.SESSION_MINUTES, "b", 1),
        ev(at(4, 10), UsageEventType.SESSION_MINUTES, "b", 5)
    )

    private val h = EventHistory(ZoneOffset.UTC, fixture)

    @Test fun staysInTheWeek() {
        assertEquals(3, h.staysWeek(at(3, 12)))
        assertEquals(1, h.staysWeek(at(9, 12)))  // only day 3, 09:00 is within 7 days of day 9, 12:00
    }

    @Test fun lateOpensCountEveryOpen() {
        assertEquals(4, h.lateOpensWeek(at(4, 12)))
    }

    @Test fun topAppMinutesTodayAddsTicks() {
        assertEquals("b" to 7, h.topAppMinutesToday(at(4, 12)))   // b: 1+1+5, a: 3
        assertNull(h.topAppMinutesToday(at(6, 12)))
    }

    @Test fun bestLeaveDayIsTheBusiestDay() {
        assertEquals(TimeUtil.epochDay(at(1, 12), ZoneOffset.UTC) to 2, h.bestLeaveDayWeek(at(3, 12)))
        assertNull(EventHistory(ZoneOffset.UTC).bestLeaveDayWeek(at(3, 12)))
    }

    private fun brainWith(events: List<UsageEvent>, picker: FakePicker) =
        DefaultJixxyBrain(BrainState(firstSeenTs = 1L), { UserSettings() }, picker, { "App[$it]" }, ZoneOffset.UTC, events)

    private fun openQuery(events: List<UsageEvent>): LineQueryResult {
        val picker = FakePicker()
        val b = brainWith(events + ev(at(4, 8), UsageEventType.FOREGROUND, "early"), picker)
        b.onSignal(Signal.ForegroundApp("calm", AppCategory.SOCIAL, at(4, 12)))
        return LineQueryResult(picker.queries.lastOrNull { it.trigger == Trigger.CALLBACK })
    }

    private class LineQueryResult(val q: app.jixxy.engine.selector.LineQuery?)

    @Test fun staysCallbackQuotesTheCount() {
        val q = openQuery((0..2).map { ev(at(it, 10), UsageEventType.PAUSE_STAY, "x") }).q!!
        assertEquals("fact:stays_week", q.requiredTag)
        assertEquals("3", q.slots["count"])
    }

    @Test fun lateOpensCallbackQuotesTheCount() {
        val q = openQuery((0..2).map { ev(at(it, 23, 30), UsageEventType.LATE_NIGHT_OPEN, "x") }).q!!
        assertEquals("fact:late_opens_week", q.requiredTag)
        assertEquals("3", q.slots["count"])
    }

    @Test fun topAppMinutesCallbackNamesTheAppAndMinutes() {
        val q = openQuery(listOf(ev(at(4, 9), UsageEventType.SESSION_MINUTES, "big", 60), ev(at(4, 10), UsageEventType.SESSION_MINUTES, "big", 60))).q!!
        assertEquals("fact:top_app_minutes_day", q.requiredTag)
        assertEquals("120", q.slots["minutes"])
        assertEquals("App[big]", q.slots["app"])
    }

    @Test fun belowThresholdsNothingIsQuoted() {
        assertNull(openQuery(listOf(ev(at(0, 10), UsageEventType.PAUSE_STAY, "x"), ev(at(4, 9), UsageEventType.SESSION_MINUTES, "big", 119))).q)
    }
}
