package app.jixxy.platform

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime

class WeeklyScheduleTest {
    private val zone = ZoneId.of("Asia/Kolkata")
    private fun ms(y: Int, m: Int, d: Int, h: Int, min: Int = 0) =
        ZonedDateTime.of(y, m, d, h, min, 0, 0, zone).toInstant().toEpochMilli()

    // 2026-11-08 is a Sunday.
    @Test fun slotIsLatestSundayAtSevenPm() {
        assertEquals(ms(2026, 11, 8, 19), WeeklySchedule.latestSlot(ms(2026, 11, 8, 20), zone))
        assertEquals(ms(2026, 11, 1, 19), WeeklySchedule.latestSlot(ms(2026, 11, 8, 18, 59), zone))
        assertEquals(ms(2026, 11, 8, 19), WeeklySchedule.latestSlot(ms(2026, 11, 11, 9), zone))
    }

    @Test fun dueOncePerWeek() {
        val shownLastWeek = ms(2026, 11, 1, 19, 5)
        assertFalse(WeeklySchedule.isDue(shownLastWeek, ms(2026, 11, 8, 18), zone))
        assertTrue(WeeklySchedule.isDue(shownLastWeek, ms(2026, 11, 8, 19, 1), zone))
        val shownNow = ms(2026, 11, 8, 19, 2)
        assertFalse(WeeklySchedule.isDue(shownNow, ms(2026, 11, 10, 12), zone))
        assertTrue(WeeklySchedule.isDue(shownNow, ms(2026, 11, 15, 19, 30), zone))
    }

    @Test fun missedWeekStillOnlyDueOnce() {
        val old = ms(2026, 10, 18, 12)
        assertTrue(WeeklySchedule.isDue(old, ms(2026, 11, 12, 10), zone))
    }

    @Test fun neverShownIsNotDue() = assertFalse(WeeklySchedule.isDue(0L, ms(2026, 11, 8, 20), zone))

    @Test fun otherDayAndHour() {
        assertEquals(ms(2026, 11, 7, 9), WeeklySchedule.latestSlot(ms(2026, 11, 8, 12), zone, DayOfWeek.SATURDAY, 9))
    }
}
