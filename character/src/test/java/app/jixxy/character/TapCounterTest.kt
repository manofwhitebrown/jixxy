package app.jixxy.character

import org.junit.Assert.assertEquals
import org.junit.Test

class TapCounterTest {
    @Test fun firstTapIsOne() {
        assertEquals(1, TapCounter().register(10_000L))
    }

    @Test fun tapsWithinWindowAccumulate() {
        val c = TapCounter()
        assertEquals(1, c.register(0L))
        assertEquals(2, c.register(1_000L))
        assertEquals(3, c.register(2_400L))
    }

    @Test fun tapExactlyOnWindowEdgeCounts() {
        val c = TapCounter()
        c.register(0L)
        assertEquals(2, c.register(1_500L))
    }

    @Test fun gapLongerThanWindowStartsNewRun() {
        val c = TapCounter()
        c.register(0L)
        c.register(500L)
        assertEquals(1, c.register(2_001L))
    }

    @Test fun resetForgetsRun() {
        val c = TapCounter()
        c.register(0L)
        c.reset()
        assertEquals(1, c.register(100L))
    }
}
