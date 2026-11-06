package app.jixxy.engine.rules

import java.time.Instant
import java.time.ZoneId

/** Pure time helpers. All take an explicit zone; nothing reads the system clock. */
object TimeUtil {
    private const val HALF_DAY_MS = 12L * 60 * 60 * 1000

    /** Minutes after local midnight for [ts]. */
    fun minuteOfDay(ts: Long, zone: ZoneId): Int {
        val t = Instant.ofEpochMilli(ts).atZone(zone)
        return t.hour * 60 + t.minute
    }

    /** Local calendar day number for [ts]. */
    fun epochDay(ts: Long, zone: ZoneId): Long = Instant.ofEpochMilli(ts).atZone(zone).toLocalDate().toEpochDay()

    /** Identifies "the night" a timestamp belongs to, so 23:30 and 00:30 across midnight share a key. */
    fun nightKey(ts: Long, zone: ZoneId): Long = epochDay(ts - HALF_DAY_MS, zone)

    /** True if [minute] lies in [start, end), where the window may wrap past midnight. start == end is empty. */
    fun inWindow(minute: Int, start: Int, end: Int): Boolean = when {
        start == end -> false
        start < end -> minute in start until end
        else -> minute >= start || minute < end
    }

    /** "HH:mm" local time for [ts], used for the `{time}` slot. */
    fun clock(ts: Long, zone: ZoneId): String {
        val m = minuteOfDay(ts, zone)
        return "%02d:%02d".format(m / 60, m % 60)
    }
}
