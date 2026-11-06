package app.jixxy.engine.memory

import app.jixxy.engine.api.BrainConfig
import app.jixxy.engine.api.UsageEvent
import app.jixxy.engine.api.UsageEventType
import app.jixxy.engine.rules.TimeUtil
import java.time.ZoneId

/**
 * In-memory view of recent [UsageEvent]s (last [BrainConfig.retentionDays] days) used for callbacks and
 * for restoring runtime state after the process restarts. Every count comes from a real recorded event.
 */
class EventHistory(private val zone: ZoneId, initial: List<UsageEvent> = emptyList()) {
    private val events = ArrayDeque<UsageEvent>()

    init {
        initial.sortedBy { it.ts }.forEach { events.addLast(it) }
    }

    /** All events, oldest first. */
    fun all(): List<UsageEvent> = events.toList()

    /** Timestamp of the newest event, or null if none. */
    fun lastTs(): Long? = events.lastOrNull()?.ts

    /** Records [e] and drops events older than the retention window. */
    fun add(e: UsageEvent) {
        events.addLast(e)
        val cutoff = e.ts - BrainConfig.retentionDays * DAY_MS
        while (events.isNotEmpty() && events.first().ts < cutoff) events.removeFirst()
    }

    /** Foreground opens of [pkg] on the local day of [now]. */
    fun appVisitsToday(pkg: String, now: Long): Int = countToday(UsageEventType.FOREGROUND, pkg, now)

    /** Foreground opens of [pkg] in the 7 days up to [now]. */
    fun appVisitsWeek(pkg: String, now: Long): Int = countSince(UsageEventType.FOREGROUND, pkg, now - 7 * DAY_MS)

    /** Pause prompts shown on the local day of [now]. */
    fun pausesToday(now: Long): Int = countToday(UsageEventType.PAUSE_SHOWN, null, now)

    /** Pause prompts shown in the 7 days up to [now]. */
    fun pausesWeek(now: Long): Int = countSince(UsageEventType.PAUSE_SHOWN, null, now - 7 * DAY_MS)

    /** Times the user chose Leave in the 7 days up to [now]. */
    fun leavesWeek(now: Long): Int = countSince(UsageEventType.PAUSE_LEAVE, null, now - 7 * DAY_MS)

    /** Distinct late nights (see [TimeUtil.nightKey]) with a late-night app open in the 7 days up to [now]. */
    fun lateNightsWeek(now: Long): Int =
        events.filter { it.type == UsageEventType.LATE_NIGHT_OPEN && it.ts >= now - 7 * DAY_MS }
            .map { TimeUtil.nightKey(it.ts, zone) }.toSet().size

    /** Timestamp of the last shown line whose id starts with [prefix], or null. */
    fun lastLineShownTs(prefix: String): Long? =
        events.lastOrNull { it.type == UsageEventType.LINE_SHOWN && it.lineId?.startsWith(prefix) == true }?.ts

    private fun countToday(type: UsageEventType, pkg: String?, now: Long): Int {
        val day = TimeUtil.epochDay(now, zone)
        return events.count { it.type == type && (pkg == null || it.pkg == pkg) && TimeUtil.epochDay(it.ts, zone) == day }
    }

    private fun countSince(type: UsageEventType, pkg: String?, from: Long): Int =
        events.count { it.type == type && (pkg == null || it.pkg == pkg) && it.ts >= from }

    private companion object {
        const val DAY_MS = 86_400_000L
    }
}
