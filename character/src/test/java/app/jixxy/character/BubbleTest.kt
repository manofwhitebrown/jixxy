package app.jixxy.character

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BubbleTest {
    private val start = 10_000L

    @Test fun typewriterRevealsOneCharacterPerStepAndStopsAtTheEnd() {
        val t = BubbleTimeline(charCount = 10, ttlMs = 1_000L, startMs = start)
        assertEquals(0, t.revealed(start))
        assertEquals(2, t.revealed(start + 2 * BubbleTimeline.CHAR_MS))
        assertEquals(10, t.revealed(start + 60_000L))
    }

    @Test fun shortTtlIsStretchedSoTheTextCanBeRead() {
        val t = BubbleTimeline(charCount = 10, ttlMs = 500L, startMs = start)
        val readable = start + 10 * BubbleTimeline.CHAR_MS + BubbleTimeline.MIN_READ_MS
        assertFalse(t.isExpired(readable - 1))
        assertTrue(t.isExpired(readable))
    }

    @Test fun longTtlIsHonoured() {
        val t = BubbleTimeline(charCount = 10, ttlMs = 9_000L, startMs = start)
        assertFalse(t.isExpired(start + 8_999L))
        assertTrue(t.isExpired(start + 9_000L))
    }

    @Test fun fadesInAtTheStartAndOutAtTheEnd() {
        val t = BubbleTimeline(charCount = 10, ttlMs = 9_000L, startMs = start)
        assertEquals(0f, t.alpha(start), 0.001f)
        assertEquals(1f, t.alpha(start + 4_000L), 0.001f)
        assertEquals(0.5f, t.alpha(start + 9_000L - BubbleTimeline.FADE_OUT_MS / 2), 0.01f)
        assertEquals(0f, t.alpha(start + 9_000L), 0.001f)
    }

    @Test fun tappingOpensTheEmojiBarAndKeepsTheBubbleAlive() {
        val t = BubbleTimeline(charCount = 10, ttlMs = 1_000L, startMs = start)
        val tap = start + 1_500L
        assertFalse(t.barVisible(tap))
        t.openBar(tap)
        assertTrue(t.barVisible(tap + BubbleTimeline.BAR_MS - 1))
        assertFalse(t.barVisible(tap + BubbleTimeline.BAR_MS))
        assertFalse(t.isExpired(tap + BubbleTimeline.BAR_MS))
    }

    @Test fun pickingAReactionClosesTheBarAndEndsTheBubbleSoon() {
        val t = BubbleTimeline(charCount = 10, ttlMs = 9_000L, startMs = start)
        val pick = start + 2_000L
        t.openBar(pick - 500L)
        t.closeSoon(pick)
        assertFalse(t.barVisible(pick))
        assertFalse(t.isExpired(pick + BubbleTimeline.CLOSE_MS - 1))
        assertTrue(t.isExpired(pick + BubbleTimeline.CLOSE_MS))
    }

    private fun geometry(alignRight: Boolean, above: Boolean) = BubbleGeometry(
        spritePx = 100f, bodyW = 150f, bodyH = 60f, band = 14f, overlap = 25f, alignRight = alignRight, above = above,
    )

    @Test fun aboveLeftKeepsTheSpriteInTheBottomLeftCorner() {
        val g = geometry(alignRight = false, above = true)
        assertEquals(150f, g.viewW, 0.001f)
        assertEquals(49f, g.spriteY, 0.001f)
        assertEquals(149f, g.viewH, 0.001f)
        assertEquals(0, g.shiftX)
        assertEquals(49, g.shiftY)
        assertEquals(0f, g.bodyTop, 0.001f)
    }

    @Test fun aboveRightFlipsTheBubbleToTheLeftOfTheSprite() {
        val g = geometry(alignRight = true, above = true)
        assertEquals(50f, g.spriteX, 0.001f)
        assertEquals(50, g.shiftX)
        assertEquals(0f, g.bodyLeft, 0.001f)
        assertEquals(150f, g.bodyRight, 0.001f)
    }

    @Test fun belowPutsTheBubbleUnderTheSpriteWithoutMovingTheWindowUp() {
        val g = geometry(alignRight = false, above = false)
        assertEquals(0f, g.spriteY, 0.001f)
        assertEquals(0, g.shiftY)
        assertEquals(114f, g.bodyTop, 0.001f)
        assertEquals(188f, g.viewH, 0.001f)
    }

    @Test fun tailStaysAwayFromTheRoundedCorners() {
        val g = geometry(alignRight = false, above = true)
        assertEquals(50f, g.tailX(corner = 16f), 0.001f)
        assertEquals(54f, g.tailX(corner = 40f), 0.001f)
    }

    @Test fun wideSpriteViewIsNeverNarrowerThanTheSprite() {
        val g = BubbleGeometry(100f, 60f, 40f, 14f, 25f, alignRight = false, above = true)
        assertEquals(100f, g.viewW, 0.001f)
    }

    @Test fun bubbleFontScaleIsClamped() {
        assertEquals(1f, bubbleFontScale(0.85f), 0f)
        assertEquals(1.2f, bubbleFontScale(1.2f), 0f)
        assertEquals(BUBBLE_MAX_FONT_SCALE, bubbleFontScale(2f), 0f)
    }

    @Test fun textWidthGrowsWithFontScaleButNeverPastTheScreen() {
        assertEquals(600, bubbleTextWidthPx(200f, 3f, 1f, 1080, 36f, 24f))
        assertEquals(840, bubbleTextWidthPx(200f, 3f, 1.4f, 1080, 36f, 24f))
        // a 320 dp wide phone at density 2 (640 px), scale 1.4: wanted 560 px, room 640 - 2*24 - 2*16 = 560
        assertEquals(560, bubbleTextWidthPx(200f, 2f, 1.4f, 640, 24f, 16f))
        // very narrow screen: capped to what is left, never below 1
        assertEquals(380, bubbleTextWidthPx(200f, 3f, 1.4f, 500, 36f, 24f))
        assertTrue(bubbleTextWidthPx(200f, 3f, 1.4f, 40, 36f, 24f) >= 1)
    }

    @Test fun tallerSpriteAreaPushesTheViewAndBelowBubbleDown() {
        val plain = BubbleGeometry(100f, 60f, 40f, 14f, 25f, alignRight = false, above = false)
        val tall = BubbleGeometry(100f, 60f, 40f, 14f, 25f, alignRight = false, above = false, spriteH = 116f)
        assertEquals(plain.viewH + 16f, tall.viewH, 0.001f)
        assertEquals(plain.bodyTop + 16f, tall.bodyTop, 0.001f)
        val aboveTall = BubbleGeometry(100f, 60f, 40f, 14f, 25f, alignRight = false, above = true, spriteH = 116f)
        assertEquals(aboveTall.spriteY + 116f, aboveTall.viewH, 0.001f)
        assertEquals(0f, aboveTall.bodyTop, 0f)
    }

    @Test fun theViewAlwaysContainsTheSpriteAndTheBubble() {
        for (alignRight in listOf(false, true)) for (above in listOf(false, true)) for (bodyW in listOf(40f, 150f, 330f, 700f)) {
            val g = BubbleGeometry(264f, bodyW, 120f, 28f, 66f, alignRight, above, spriteH = 307f)
            val tag = "right=$alignRight above=$above bodyW=$bodyW"
            assertTrue(tag, g.spriteX >= 0f && g.spriteX + g.spritePx <= g.viewW + 0.001f)
            assertTrue(tag, g.spriteY >= 0f && g.spriteY + g.spriteH <= g.viewH + 0.001f)
            assertTrue(tag, g.bodyLeft >= 0f && g.bodyRight <= g.viewW + 0.001f)
            assertTrue(tag, g.bodyTop >= 0f && g.bodyBottom <= g.viewH + 0.001f)
        }
    }
}
