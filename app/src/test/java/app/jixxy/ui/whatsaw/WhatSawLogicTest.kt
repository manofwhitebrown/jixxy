package app.jixxy.ui.whatsaw

import app.jixxy.engine.api.UsageEvent
import app.jixxy.engine.api.UsageEventType
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WhatSawLogicTest {
    private val utc = ZoneId.of("UTC")
    private val day = 86_400_000L
    private val now = 10 * day + 12 * 3_600_000L   // day 10, 12:00

    @Test fun windowStartIsLocalMidnightSixDaysBack() = assertEquals(4 * day, WhatSawLogic.windowStart(now, utc))

    @Test fun dropsOlderThanWindowAndFuture() {
        val events = listOf(
            UsageEvent(4 * day - 1, UsageEventType.FOREGROUND, "old"),
            UsageEvent(4 * day, UsageEventType.FOREGROUND, "edge"),
            UsageEvent(now + 1, UsageEventType.FOREGROUND, "future"),
        )
        assertEquals(listOf("edge"), WhatSawLogic.group(events, now, { it }, utc).flatMap { d -> d.rows.map { it.appLabel } })
    }

    @Test fun groupsByDayNewestFirst() {
        val events = listOf(
            UsageEvent(9 * day + 100, UsageEventType.FOREGROUND, "a"),
            UsageEvent(10 * day + 50, UsageEventType.FOREGROUND, "b"),
            UsageEvent(10 * day + 90, UsageEventType.PAUSE_SHOWN, "c"),
        )
        val days = WhatSawLogic.group(events, now, { it }, utc)
        assertEquals(listOf(10 * day, 9 * day), days.map { it.dayStartTs })
        assertEquals(listOf("c", "b"), days[0].rows.map { it.appLabel })
    }

    @Test fun usesLabelAndOnlyShowsMinutesForSessions() {
        val events = listOf(
            UsageEvent(10 * day + 1, UsageEventType.SESSION_MINUTES, "p.x", value = 25),
            UsageEvent(10 * day + 2, UsageEventType.NAP, value = 60),
        )
        val rows = WhatSawLogic.group(events, now, { "Label($it)" }, utc).single().rows
        assertNull(rows[0].appLabel)          // NAP has no app
        assertNull(rows[0].minutes)           // minutes only for SESSION_MINUTES
        assertEquals("Label(p.x)", rows[1].appLabel)
        assertEquals(25, rows[1].minutes)
    }

    @Test fun emptyInputGivesNoDays() = assertEquals(emptyList<SawDay>(), WhatSawLogic.group(emptyList(), now, { it }, utc))
}
