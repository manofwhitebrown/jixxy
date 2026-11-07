package app.jixxy.character

import kotlin.math.PI
import kotlin.math.sin

/**
 * The soft cloud Jixxy floats on (D-023): ten circles plus a rounded base, all in viewbox units (the 128-unit square
 * the sprite is drawn in; one unit = 4 px of the 512 px art). The Manager's prototype numbers are kept in art px and
 * converted with [PX]. Pure maths with no Android types, so the shape, the float and the "stays inside the view"
 * rule are unit-tested; [JixxyView] only loops over the numbers and draws them (no allocation per frame).
 */
internal object CloudShape {
    /** Viewbox units per art pixel. */
    const val PX = 0.25f

    /** Where the sprites' flat bottom edge sits (y = 500 px). */
    const val CUT_Y = 125f

    /** The view grows by this much below the 128-unit sprite square so the cloud's bottom bumps fit (84 px). */
    const val EXTRA_UNITS = 21f

    const val PUFFS = 10
    const val OUTLINE = 5f * PX
    const val SHADE_DROP = 8f * PX
    const val BASE_RADIUS = 14f * PX

    /** Top row sits this much lower than the prototype so the face stays clear (really's lower eyelid). */
    private const val TOP_LOWER = 10f * PX

    private const val SPAN_PAD = 2f
    private const val BOB_UNITS = 1.5f
    private const val BOB_PERIOD_SEC = 3.5f
    private const val DRAG_BOB_UNITS = 0.5f
    private const val DRAG_BOB_PERIOD_SEC = 1.1f
    private const val BREATH = 0.03f
    private const val BREATH_PERIOD_SEC = 4.2f

    private val BOTTOM_X = floatArrayOf(0.03f, 0.20f, 0.40f, 0.60f, 0.80f, 0.97f)
    private val BOTTOM_DY = floatArrayOf(6f, 20f, 26f, 26f, 20f, 6f)
    private val BOTTOM_R = floatArrayOf(34f, 40f, 44f, 44f, 40f, 34f)
    private val TOP_X = floatArrayOf(0.14f, 0.86f, 0.32f, 0.68f)
    private val TOP_DY = floatArrayOf(-6f, -6f, -2f, -2f)
    private val TOP_R = floatArrayOf(22f, 22f, 16f, 16f)

    /** Widest the outer bottom puffs reach sideways (radius + outline), used to keep them inside the view. */
    private val SIDE_MARGIN = BOTTOM_R[0] * PX + OUTLINE

    /**
     * Fills [out] (size [PUFFS] * 3) with cx, cy, radius of each circle for a cloud under a sprite whose flat edge spans
     * [left]..[right]. [breathing] makes the puffs swell and shrink 3 % out of phase; false (reduce motion) freezes them.
     */
    fun layout(left: Float, right: Float, timeSec: Float, breathing: Boolean, out: FloatArray) {
        val spanL = left - SPAN_PAD
        val span = (right + SPAN_PAD) - spanL
        var n = 0
        for (i in BOTTOM_X.indices) {
            n = put(out, n, i, spanL + BOTTOM_X[i] * span, CUT_Y + BOTTOM_DY[i] * PX, BOTTOM_R[i] * PX, timeSec, breathing)
        }
        for (i in TOP_X.indices) {
            n = put(out, n, BOTTOM_X.size + i, spanL + TOP_X[i] * span, CUT_Y + TOP_DY[i] * PX + TOP_LOWER, TOP_R[i] * PX, timeSec, breathing)
        }
    }

    private fun put(out: FloatArray, at: Int, index: Int, x: Float, y: Float, r: Float, t: Float, breathing: Boolean): Int {
        val k = if (breathing) 1f + BREATH * sin(2f * PI.toFloat() * t / BREATH_PERIOD_SEC + index * 1.3f) else 1f
        val radius = r * k
        val edge = radius + OUTLINE
        out[at] = x.coerceIn(edge, 128f - edge)
        out[at + 1] = y
        out[at + 2] = radius
        return at + 3
    }

    /** Fills [out] (size 4) with the rounded base's left, top, right, bottom (no outline). */
    fun baseRect(left: Float, right: Float, out: FloatArray) {
        val spanL = left - SPAN_PAD
        val span = (right + SPAN_PAD) - spanL
        out[0] = (spanL + 0.08f * span).coerceAtLeast(OUTLINE)
        out[1] = CUT_Y - 4f * PX
        out[2] = (spanL + 0.92f * span).coerceAtMost(128f - OUTLINE)
        out[3] = CUT_Y + 40f * PX
    }

    /** Vertical float offset in viewbox units for cat and cloud together; 0 when motion is reduced. */
    fun bob(timeSec: Float, dragging: Boolean, reduceMotion: Boolean): Float {
        if (reduceMotion) return 0f
        val twoPi = 2f * PI.toFloat()
        var y = BOB_UNITS * sin(twoPi * timeSec / BOB_PERIOD_SEC)
        if (dragging) y += DRAG_BOB_UNITS * sin(twoPi * timeSec / DRAG_BOB_PERIOD_SEC)
        return y
    }

    /** Largest |[bob]|, so tests and the view height can rely on it. */
    const val MAX_BOB = BOB_UNITS + DRAG_BOB_UNITS

    /** Margin the outer puffs keep from the view's side edges. */
    val sideMargin: Float get() = SIDE_MARGIN
}
