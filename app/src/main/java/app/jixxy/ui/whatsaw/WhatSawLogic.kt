package app.jixxy.ui.whatsaw

import app.jixxy.engine.api.UsageEvent
import app.jixxy.engine.api.UsageEventType
import java.time.Instant
import java.time.ZoneId

/** One line in the "What Jixxy saw" list. Contains only what the engine logged: type, optional app, optional minutes, time. */
data class SawRow(val ts: Long, val type: UsageEventType, val appLabel: String?, val minutes: Int?)

/** All rows of one local calendar day, newest first. [dayStartTs] is local midnight in epoch millis. */
data class SawDay(val dayStartTs: Long, val rows: List<SawRow>)

/** Pure grouping rules for the transparency log. No Android imports; unit-tested. */
object WhatSawLogic {
    const val WINDOW_DAYS = 7

    /** Local midnight of the oldest day shown (today plus the previous 6 days). */
    fun windowStart(now: Long, zone: ZoneId = ZoneId.systemDefault()): Long =
        Instant.ofEpochMilli(now).atZone(zone).toLocalDate().minusDays((WINDOW_DAYS - 1).toLong())
            .atStartOfDay(zone).toInstant().toEpochMilli()

    /**
     * Groups [events] into days, newest day first and newest row first within a day.
     * Events older than the window are dropped. [labelOf] turns a package name into an app label.
     * Line ids are deliberately not shown: they mean nothing to the user.
     */
    fun group(
        events: List<UsageEvent>,
        now: Long,
        labelOf: (String) -> String,
        zone: ZoneId = ZoneId.systemDefault(),
    ): List<SawDay> {
        val from = windowStart(now, zone)
        return events
            .filter { it.ts in from..now }
            .sortedByDescending { it.ts }
            .groupBy { dayStart(it.ts, zone) }
            .map { (day, list) ->
                SawDay(day, list.map { e ->
                    SawRow(
                        ts = e.ts,
                        type = e.type,
                        appLabel = e.pkg?.let(labelOf),
                        minutes = if (e.type == UsageEventType.SESSION_MINUTES) e.value else null,
                    )
                })
            }
            .sortedByDescending { it.dayStartTs }
    }

    private fun dayStart(ts: Long, zone: ZoneId): Long =
        Instant.ofEpochMilli(ts).atZone(zone).toLocalDate().atStartOfDay(zone).toInstant().toEpochMilli()
}
