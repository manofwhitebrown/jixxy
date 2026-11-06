package app.jixxy.data

import org.junit.Assert.assertEquals
import org.junit.Test

class RetentionTest {
    @Test fun cutoffIsThirtyDaysBack() =
        assertEquals(1_000_000_000_000L - 30L * 86_400_000L, retentionCutoff(1_000_000_000_000L))

    @Test fun customWindow() = assertEquals(0L, retentionCutoff(86_400_000L, days = 1))
}
