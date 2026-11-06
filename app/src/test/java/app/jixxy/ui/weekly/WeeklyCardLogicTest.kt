package app.jixxy.ui.weekly

import app.jixxy.engine.review.WeeklyReview
import app.jixxy.engine.selector.PickedLine
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WeeklyCardLogicTest {
    private fun review(faced: Int = 4, left: Int = 3, stayed: Int = 1, minutes: Int = 120, compliment: PickedLine? = null) = WeeklyReview(
        fromTs = 0, toTs = 1, totalMinutes = minutes, topApps = emptyList(), lateNightMinutes = 0,
        pausesFaced = faced, pausesLeft = left, pausesStayed = stayed, bestDay = null,
        bondXpGained = 0, bondLevelUp = false, roast = PickedLine("r1", "Four pauses."), compliment = compliment,
    )

    @Test fun modelCopiesCountsAndLines() {
        val m = WeeklyCardLogic.model(review(compliment = PickedLine("c1", "Respect.")))
        assertEquals(4, m.pauses); assertEquals(3, m.left); assertEquals(1, m.stayed)
        assertEquals("Four pauses.", m.roast); assertEquals("Respect.", m.compliment)
        assertFalse(m.quiet)
    }

    @Test fun complimentIsNullWhenNotEarned() = assertNull(WeeklyCardLogic.model(review()).compliment)

    @Test fun emptyWeekIsQuiet() = assertTrue(WeeklyCardLogic.model(review(faced = 0, left = 0, stayed = 0, minutes = 0)).quiet)

    @Test fun rangeCoversSevenLocalDaysIncludingToday() {
        val utc = ZoneId.of("UTC")
        val now = 10 * 86_400_000L + 5_000L // day 10, 00:00:05
        val (from, to) = WeeklyCardLogic.range(now, utc)
        assertEquals(4 * 86_400_000L, from)
        assertEquals(now + 1, to)
    }
}
