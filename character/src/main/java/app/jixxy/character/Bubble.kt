package app.jixxy.character

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Timing of one speech bubble: typewriter reveal, fade in/out, auto-dismiss and the emoji bar window.
 * Pure maths on a monotonic millisecond clock, so it is unit-tested on the JVM.
 */
internal class BubbleTimeline(private val charCount: Int, ttlMs: Long, private val startMs: Long) {
    private var endMs = startMs + max(ttlMs, charCount * CHAR_MS + MIN_READ_MS)
    private var barUntilMs = 0L

    /** How many characters the typewriter has revealed at [nowMs]. */
    fun revealed(nowMs: Long): Int = ((nowMs - startMs) / CHAR_MS).toInt().coerceIn(0, charCount)

    /** True once the bubble is gone and should be removed. */
    fun isExpired(nowMs: Long): Boolean = nowMs >= endMs

    /** Overall opacity 0..1: a short fade in, a longer fade out before the end. */
    fun alpha(nowMs: Long): Float {
        val fadeIn = ((nowMs - startMs) / FADE_IN_MS.toFloat()).coerceIn(0f, 1f)
        val fadeOut = ((endMs - nowMs) / FADE_OUT_MS.toFloat()).coerceIn(0f, 1f)
        return min(fadeIn, fadeOut)
    }

    fun barVisible(nowMs: Long): Boolean = nowMs < barUntilMs

    /** The user tapped the bubble: show the emoji bar for [BAR_MS], and keep the bubble alive that long. */
    fun openBar(nowMs: Long) {
        barUntilMs = nowMs + BAR_MS
        endMs = max(endMs, barUntilMs + FADE_OUT_MS)
    }

    /** The user picked a reaction: hide the bar and let the bubble fade away shortly after. */
    fun closeSoon(nowMs: Long) {
        barUntilMs = 0L
        endMs = min(endMs, nowMs + CLOSE_MS)
    }

    companion object {
        const val CHAR_MS = 28L
        const val MIN_READ_MS = 1_400L
        const val FADE_IN_MS = 120L
        const val FADE_OUT_MS = 250L
        const val BAR_MS = 2_000L
        const val CLOSE_MS = 450L
    }
}

/**
 * Where everything sits inside [JixxyView] while a bubble is shown: the sprite keeps its on-screen position, the
 * bubble goes above it (or below when there is no room) and leans towards the screen side with more room.
 * All values are pixels. Pure, so the flipping rules are unit-tested.
 *
 * @param band height of the tail and of the emoji bar's overhang
 * @param overlap how far the bubble's tail reaches into the sprite's empty top area (above placement only)
 * @param alignRight true when the sprite sits on the right half of the screen: the bubble extends to the left
 */
internal class BubbleGeometry(
    val spritePx: Float,
    val bodyW: Float,
    val bodyH: Float,
    val band: Float,
    val overlap: Float,
    val alignRight: Boolean,
    val above: Boolean,
) {
    private val blockH = if (above) bodyH + band else band + bodyH + band

    val viewW: Float = max(spritePx, bodyW)
    val spriteX: Float = if (alignRight) viewW - spritePx else 0f
    val spriteY: Float = if (above) blockH - overlap else 0f
    val viewH: Float = if (above) spriteY + spritePx else spritePx + blockH

    val bodyLeft: Float = if (alignRight) viewW - bodyW else 0f
    val bodyTop: Float = if (above) 0f else spritePx + band
    val bodyRight: Float get() = bodyLeft + bodyW
    val bodyBottom: Float get() = bodyTop + bodyH

    /** How far the host window must move left when the view grows, so the sprite stays where it was. */
    val shiftX: Int get() = spriteX.roundToInt()

    /** How far the host window must move up when the view grows, so the sprite stays where it was. */
    val shiftY: Int get() = spriteY.roundToInt()

    /** X of the tail tip: over the middle of the sprite, kept clear of the bubble's rounded corners. */
    fun tailX(corner: Float): Float {
        val low = bodyLeft + corner + band
        val high = bodyRight - corner - band
        return (spriteX + spritePx / 2f).coerceIn(low, max(low, high))
    }
}
