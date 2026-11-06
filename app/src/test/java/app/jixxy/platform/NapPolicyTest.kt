package app.jixxy.platform

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NapPolicyTest {
    private val now = 1_000_000L

    @Test fun napActiveHidesSprite() {
        val until = NapPolicy.napUntil(now)
        assertTrue(NapPolicy.isNapping(until, now))
        assertFalse(NapPolicy.shouldShowSprite(true, until, now, false))
    }

    @Test fun napExpiredShowsSprite() {
        val until = now - 1
        assertFalse(NapPolicy.isNapping(until, now))
        assertTrue(NapPolicy.shouldShowSprite(true, until, now, false))
        assertEquals(0L, NapPolicy.remainingMs(until, now))
    }

    @Test fun napEndingExactlyNowIsOver() {
        assertFalse(NapPolicy.isNapping(now, now))
    }

    @Test fun napThenStopThenWakeShowsSprite() {
        var nap: Long? = NapPolicy.napUntil(now)
        assertFalse(NapPolicy.shouldShowSprite(true, nap, now, false))
        nap = NapPolicy.afterStop()
        assertNull(nap)
        nap = NapPolicy.afterWake()
        assertTrue(NapPolicy.shouldShowSprite(true, nap, now + 1, false))
    }

    @Test fun wakeClearsActiveNapWithoutStop() {
        val nap = NapPolicy.afterWake()
        assertNull(nap)
        assertTrue(NapPolicy.shouldShowSprite(true, nap, now, false))
    }

    @Test fun napExpiredWhileStoppedDoesNotHideOnNextStart() {
        val persisted = now
        val later = now + 3 * NapPolicy.NAP_MS
        assertTrue(NapPolicy.shouldShowSprite(true, persisted, later, false))
    }

    @Test fun screenOffHidesEvenWithoutNap() {
        assertFalse(NapPolicy.shouldShowSprite(false, null, now, false))
        assertTrue(NapPolicy.shouldShowSprite(true, null, now, false))
    }

    @Test fun screenOnDuringNapStaysHidden() {
        val until = NapPolicy.napUntil(now)
        assertFalse(NapPolicy.shouldShowSprite(true, until, now + 10, false))
    }

    @Test fun blindModeDoesNotChangeVisibility() {
        assertTrue(NapPolicy.shouldShowSprite(true, null, now, true))
        assertFalse(NapPolicy.shouldShowSprite(true, NapPolicy.napUntil(now), now, true))
    }

    @Test fun remainingMsCountsDown() {
        val until = NapPolicy.napUntil(now)
        assertEquals(NapPolicy.NAP_MS, NapPolicy.remainingMs(until, now))
        assertEquals(NapPolicy.NAP_MS - 500, NapPolicy.remainingMs(until, now + 500))
    }
}
