package app.jixxy.platform

import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Pure weekly-review timing (T-P3-8). The review is "due" once per week, from a configurable weekday + hour.
 * Works from timestamps only, so a phone that was off or a service that was stopped at the exact moment still gets it
 * the next time anything checks (heartbeat or app open).
 */
object WeeklySchedule {
    /** Latest scheduled moment (weekday at hour:00, local time) that is not after [now]. */
    fun latestSlot(now: Long, zone: ZoneId, day: DayOfWeek = DayOfWeek.SUNDAY, hour: Int = 19): Long {
        val local = Instant.ofEpochMilli(now).atZone(zone)
        var slot: ZonedDateTime = local.toLocalDate().atTime(hour, 0).atZone(zone)
        while (slot.dayOfWeek != day || slot.toInstant().toEpochMilli() > now) slot = slot.minusDays(1)
        return slot.toInstant().toEpochMilli()
    }

    /** True when a slot has passed since [lastShownTs]. A [lastShownTs] of 0 means "never" and is NOT due (caller seeds it). */
    fun isDue(lastShownTs: Long, now: Long, zone: ZoneId, day: DayOfWeek = DayOfWeek.SUNDAY, hour: Int = 19): Boolean =
        lastShownTs > 0 && lastShownTs < latestSlot(now, zone, day, hour)
}
