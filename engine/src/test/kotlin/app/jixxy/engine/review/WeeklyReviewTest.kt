package app.jixxy.engine.review

import app.jixxy.engine.api.RoastLevel
import app.jixxy.engine.api.UsageEvent
import app.jixxy.engine.api.UsageEventType
import app.jixxy.engine.rules.at
import app.jixxy.engine.selector.LinePicker
import app.jixxy.engine.selector.LineQuery
import app.jixxy.engine.selector.PickedLine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneOffset

class WeeklyReviewTest {
    private val tags = mutableListOf<String?>()
    private val picker = object : LinePicker {
        override fun pick(query: LineQuery): PickedLine? { tags += query.requiredTag; return PickedLine("w:${query.requiredTag}", "n=${query.slots["count"]}") }
    }
    private val b = WeeklyReviewBuilder(picker, ZoneOffset.UTC) { "L($it)" }
    private fun mins(pkg: String, ts: Long, n: Int) = (1..n).map { UsageEvent(ts + it * 1000L, UsageEventType.SESSION_MINUTES, pkg, 1) }
    private fun ev(t: UsageEventType, ts: Long, pkg: String? = null, v: Int? = null) = UsageEvent(ts, t, pkg, v)

    // Hand-computed fixture: day0 insta 30 + yt 10 (23:30-23:59 = late night), day1 insta 5, day2 yt 20.
    private val events =
        mins("insta", at(0, 23, 10), 30) + mins("yt", at(0, 12), 10) + mins("insta", at(1, 12), 5) + mins("yt", at(2, 12), 20) +
            listOf(
                ev(UsageEventType.PAUSE_SHOWN, at(0, 12)), ev(UsageEventType.PAUSE_SHOWN, at(1, 12)), ev(UsageEventType.PAUSE_SHOWN, at(2, 12)),
                ev(UsageEventType.PAUSE_LEAVE, at(0, 12)), ev(UsageEventType.PAUSE_LEAVE, at(1, 12)), ev(UsageEventType.PAUSE_STAY, at(2, 12)),
                ev(UsageEventType.FOREGROUND, at(0, 12), "insta"), ev(UsageEventType.FOREGROUND, at(1, 12), "insta"), ev(UsageEventType.FOREGROUND, at(2, 12), "yt"),
                ev(UsageEventType.LINE_REACTION, at(0, 13), null, 1)
            )

    @Test fun numbersMatchHandComputedFixture() {
        val r = b.build(events, at(0, 0), at(7, 0), bondXpNow = 100, roast = RoastLevel.NORMAL)
        assertEquals(65, r.totalMinutes)
        assertEquals(listOf("insta" to 35, "yt" to 30), r.topApps.map { it.pkg to it.minutes })
        assertEquals("L(insta)", r.topApps[0].label)
        assertEquals(30, r.lateNightMinutes)
        assertEquals(3, r.pausesFaced); assertEquals(2, r.pausesLeft); assertEquals(1, r.pausesStayed)
        assertEquals(5, r.bestDay!!.minutes)
        assertEquals(3 * 3 + 2 * 5 + 1 * 2, r.bondXpGained)
        assertTrue(r.bondLevelUp) // 100 now, 100-21=79 before: crossed ROOMMATE
    }

    @Test fun roastCarriesPauseCountAndComplimentIsEarned() {
        val r = b.build(events, at(0, 0), at(7, 0), 0, RoastLevel.NORMAL)
        assertEquals("n=3", r.roast!!.text); assertEquals("w:weekly_roast", r.roast!!.id)
        assertEquals("w:weekly_praise", r.compliment!!.id)
        val stayHeavy = events.filter { it.type != UsageEventType.PAUSE_LEAVE }
        assertNull(b.build(stayHeavy, at(0, 0), at(7, 0), 0, RoastLevel.NORMAL).compliment)
    }

    @Test fun quietWeekUsesQuietLineAndNoCompliment() {
        val r = b.build(emptyList(), at(0, 0), at(7, 0), 0, RoastLevel.NORMAL)
        assertTrue(r.isQuiet); assertEquals("w:weekly_quiet", r.roast!!.id); assertNull(r.compliment); assertNull(r.bestDay)
    }

    @Test fun eventsOutsideTheWeekAreIgnored() {
        val r = b.build(events, at(1, 0), at(2, 0), 0, RoastLevel.NORMAL)
        assertEquals(5, r.totalMinutes); assertNotNull(r.roast)
    }

    @Test fun oneBusyDayIsThinGetsQuietLineAndNoCompliment() {
        tags.clear()
        val one = mins("insta", at(0, 12), 10) + listOf(ev(UsageEventType.FOREGROUND, at(0, 12), "insta"), ev(UsageEventType.PAUSE_LEAVE, at(0, 12)))
        val r = b.build(one, at(0, 0), at(7, 0), bondXpNow = 0, roast = RoastLevel.NORMAL)
        assertTrue(r.isThin)
        assertEquals(1, r.activeDays)
        assertNull(r.compliment)
        assertEquals(listOf<String?>("weekly_quiet"), tags)
        assertNull(r.bestDay)
    }

    @Test fun aFullFixtureWeekIsNotThin() {
        val r = b.build(events, at(0, 0), at(7, 0), bondXpNow = 100, roast = RoastLevel.NORMAL)
        assertEquals(3, r.activeDays)
        assertTrue(!r.isThin)
        assertNotNull(r.compliment)
    }

    @Test fun lowMinutesWithoutAnyPauseIsThinEvenOverSeveralDays() {
        val few = (0..3).flatMap { listOf(ev(UsageEventType.FOREGROUND, at(it, 12), "a")) + mins("a", at(it, 12), 5) }
        assertTrue(b.build(few, at(0, 0), at(7, 0), 0, RoastLevel.NORMAL).isThin)
    }

    @Test fun anEmptyWeekIsQuietAndThinAndInventsNothing() {
        val r = b.build(emptyList(), at(0, 0), at(7, 0), 0, RoastLevel.NORMAL)
        assertTrue(r.isQuiet && r.isThin)
        assertEquals(0, r.totalMinutes + r.pausesFaced + r.activeDays)
        assertTrue(r.topApps.isEmpty() && r.bestDay == null && r.compliment == null)
    }
}
