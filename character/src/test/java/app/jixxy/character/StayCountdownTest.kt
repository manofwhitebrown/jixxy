package app.jixxy.character

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StayCountdownTest {
    private val start = 5_000L

    @Test fun staysLockedUntilTheWaitHasPassed() {
        val c = StayCountdown(start, waitSec = 3)
        assertFalse(c.isDone(start))
        assertFalse(c.isDone(start + 2_999L))
        assertTrue(c.isDone(start + 3_000L))
    }

    @Test fun showsWholeSecondsRoundedUp() {
        val c = StayCountdown(start, waitSec = 3)
        assertEquals(3, c.remainingSec(start))
        assertEquals(3, c.remainingSec(start + 1L))
        assertEquals(2, c.remainingSec(start + 1_000L))
        assertEquals(1, c.remainingSec(start + 2_999L))
        assertEquals(0, c.remainingSec(start + 3_000L))
        assertEquals(0, c.remainingSec(start + 9_000L))
    }

    @Test fun ringFillsLinearlyAndStopsAtOne() {
        val c = StayCountdown(start, waitSec = 4)
        assertEquals(0f, c.progress(start), 0.001f)
        assertEquals(0.5f, c.progress(start + 2_000L), 0.001f)
        assertEquals(1f, c.progress(start + 40_000L), 0.001f)
    }
}
