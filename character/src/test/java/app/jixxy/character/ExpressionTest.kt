package app.jixxy.character

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpressionTest {
    private fun map(name: String, poke: Int = 1, hour: Int = 14) = ExpressionMap.forState(name, poke, hour)

    @Test fun calmStatesShowTheDefaultFace() {
        for (n in listOf("IDLE", "BLINK", "JUDGING", "YAWN", "STRETCH")) assertEquals(n, Expression.REALLY, map(n))
    }

    @Test fun eachStateShowsItsCaptionFace() {
        assertEquals(Expression.OOH_NICE, map("HAPPY"))
        assertEquals(Expression.NAP, map("SLEEP"))
        assertEquals(Expression.NAP, map("SNORE"))
        assertEquals(Expression.BRO_WHAT, map("WAKE_GRUMPY"))
        assertEquals(Expression.SNACKS, map("SNACK"))
        assertEquals(Expression.SHOCKED, map("PANIC"))
        assertEquals(Expression.RESPECT, map("IMPRESSED"))
        assertEquals(Expression.SMUG, map("SMUG"))
        assertEquals(Expression.HEADPHONES, map("MUSIC"))
        assertEquals(Expression.DROPPED, map("DRAGGED"))
        assertEquals(Expression.DROPPED, map("FALLING"))
    }

    @Test fun d010StatesWorkByName() {
        assertEquals(Expression.PEEK, map("PEEK"))
        assertEquals(Expression.CHARGING, map("CHARGING"))
        assertEquals(Expression.LOW_BATTERY, map("LOW_BATTERY"))
    }

    @Test fun unknownStateFallsBackToDefaultFace() {
        assertEquals(Expression.REALLY, map("SOMETHING_NEW"))
    }

    @Test fun pokesEscalate() {
        assertEquals(Expression.REALLY, map("POKED", poke = 1))
        assertEquals(Expression.BRO_WHAT, map("POKED", poke = 2))
        assertEquals(Expression.BRO_WHAT, map("POKED", poke = 3))
    }

    @Test fun softWrapsInBlanketOnlyLateAtNight() {
        assertEquals(Expression.BLANKET, map("SOFT", hour = 0))
        assertEquals(Expression.BLANKET, map("SOFT", hour = 3))
        assertEquals(Expression.SOFT, map("SOFT", hour = 12))
        assertEquals(Expression.SOFT, map("SOFT", hour = 22))
    }

    @Test fun faderCrossfadesAndSettles() {
        val f = ExpressionFader()
        assertFalse(f.isFading)
        f.show(Expression.OOH_NICE, 1_000L)
        assertEquals(Expression.REALLY, f.previous)
        assertEquals(Expression.OOH_NICE, f.current)
        f.step(1_070L)
        assertTrue(f.isFading)
        assertEquals(0.5f, f.progress, 0.01f)
        f.step(1_000L + ExpressionFader.FADE_MS)
        assertFalse(f.isFading)
        assertNull(f.previous)
    }

    @Test fun faderIgnoresTheExpressionAlreadyShown() {
        val f = ExpressionFader(Expression.NAP)
        f.show(Expression.NAP, 5L)
        assertFalse(f.isFading)
        assertNull(f.previous)
    }

    @Test fun sampleSizeKeepsAtLeastTheTargetWidth() {
        assertEquals(1, sampleSizeFor(512, 400))
        assertEquals(1, sampleSizeFor(512, 300))
        assertEquals(2, sampleSizeFor(512, 256))
        assertEquals(4, sampleSizeFor(512, 128))
        assertEquals(1, sampleSizeFor(256, 300))
    }
}
