package app.jixxy.character

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.SystemClock
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import app.jixxy.engine.api.CharacterState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Jixxy's floating sprite (concept A, "Blanket Bean"), drawn procedurally from layered shapes and
 * animated by a small state machine ([PoseAnimator]).
 *
 * Touch is handled here: taps (consecutive count), long-press and drags are reported through
 * [JixxyViewListener]; the host moves its window. The view already shows POKED on taps and DRAGGED
 * while held (then the landing), so the brain does not need to request those states.
 *
 * The view is cheap by construction: no allocation while drawing, at most 30 frames per second, and no
 * frame callbacks at all while the screen is off ([setScreenActive]), the view is detached or hidden.
 * Default size is 64 dp square; the host window decides the final size.
 */
class JixxyView(context: Context) : FrameLayout(context) {

    /** Receives taps, long-presses, drag events and emoji reactions. */
    var listener: JixxyViewListener? = null

    /** The state currently playing (may differ from the last requested one while a higher priority state runs). */
    internal val characterState: CharacterState get() = animator.state

    private val palette = CharacterPalette.Default
    private val taps = TapCounter()
    private val animator = PoseAnimator()
    private val pose = animator.pose
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val longPressMs = ViewConfiguration.getLongPressTimeout().toLong()

    private var screenActive = true
    private var downX = 0f
    private var downY = 0f
    private var dragging = false
    private var longPressFired = false
    private val longPressRunnable = Runnable {
        longPressFired = true
        listener?.onLongPress()
    }
    private val ticker = object : Runnable {
        override fun run() {
            if (!shouldAnimate()) return
            animator.step(SystemClock.uptimeMillis())
            invalidate()
            postOnAnimationDelayed(this, animator.frameIntervalMs)
        }
    }

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val rect = RectF()
    private val mouth = Path()
    private val lidEdge = Path()
    private val body = Path().apply {
        moveTo(30f, 102f); cubicTo(20f, 70f, 34f, 38f, 64f, 36f)
        cubicTo(94f, 38f, 108f, 70f, 98f, 102f); cubicTo(92f, 116f, 36f, 116f, 30f, 102f); close()
    }
    private val blanket = Path().apply {
        moveTo(22f, 72f); cubicTo(18f, 40f, 42f, 22f, 66f, 22f); cubicTo(92f, 22f, 110f, 42f, 106f, 72f)
        quadTo(98f, 62f, 90f, 70f); quadTo(82f, 60f, 74f, 70f); quadTo(66f, 60f, 58f, 70f)
        quadTo(50f, 60f, 42f, 70f); quadTo(32f, 62f, 22f, 72f); close()
    }
    private val blanketTail = Path().apply {
        moveTo(24f, 70f); quadTo(10f, 90f, 18f, 110f); quadTo(30f, 104f, 36f, 82f); close()
    }
    private val zShape = Path().apply { moveTo(-1f, -1f); lineTo(1f, -1f); lineTo(-1f, 1f); lineTo(1f, 1f) }
    private val noteTail = Path().apply { moveTo(2.3f, 0f); lineTo(2.3f, -9f); quadTo(5f, -8f, 6f, -4.5f) }
    private val star = Path().apply {
        moveTo(0f, -1f); lineTo(0.25f, -0.25f); lineTo(1f, 0f); lineTo(0.25f, 0.25f)
        lineTo(0f, 1f); lineTo(-0.25f, 0.25f); lineTo(-1f, 0f); lineTo(-0.25f, -0.25f); close()
    }
    private val drop = Path().apply {
        moveTo(0f, -4f); cubicTo(3f, 0f, 3f, 4f, 0f, 4f); cubicTo(-3f, 4f, -3f, 0f, 0f, -4f); close()
    }

    init {
        setWillNotDraw(false)
        contentDescription = context.getString(R.string.jixxy_content_description)
    }

    /**
     * Requests a pose. Higher priority states win: DRAGGED, then POKED, then everything else. A request
     * that loses is played afterwards if it is still fresh (2 s). DRAGGED and FALLING come from touch.
     * One-shot states return to IDLE on their own; SLEEP, SNORE, MUSIC and SOFT hold until you send another
     * state (send IDLE to end them).
     */
    fun setCharacterState(state: CharacterState) {
        animator.request(state, SystemClock.uptimeMillis())
    }

    /**
     * Shows a speech bubble with [text] for [ttlMs]; tapping it will reveal the emoji bar that ends in
     * [JixxyViewListener.onReaction] with [lineId]. Not rendered until T-CHAR-3.
     */
    @Suppress("UNUSED_PARAMETER")
    fun showBubble(text: String, ttlMs: Long, lineId: String?) = Unit

    /** `false` means the screen is off: every animation frame and timer stops. `true` resumes. */
    fun setScreenActive(active: Boolean) {
        if (screenActive == active) return
        screenActive = active
        if (active) startAnimating() else {
            stopAnimating()
            cancelGesture()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desired = (DEFAULT_SIZE_DP * resources.displayMetrics.density).toInt()
        setMeasuredDimension(resolveSize(desired, widthMeasureSpec), resolveSize(desired, heightMeasureSpec))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimating()
    }

    override fun onDetachedFromWindow() {
        stopAnimating()
        cancelGesture()
        super.onDetachedFromWindow()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (isVisible) startAnimating() else stopAnimating()
    }

    private fun shouldAnimate(): Boolean = screenActive && isAttachedToWindow && isShown

    private fun startAnimating() {
        removeCallbacks(ticker)
        if (!shouldAnimate()) return
        animator.resume(SystemClock.uptimeMillis())
        postOnAnimation(ticker)
    }

    private fun stopAnimating() = removeCallbacks(ticker)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX
                downY = event.rawY
                dragging = false
                longPressFired = false
                postDelayed(longPressRunnable, longPressMs)
            }
            MotionEvent.ACTION_MOVE -> onMove(event)
            MotionEvent.ACTION_UP -> onRelease(event)
            MotionEvent.ACTION_CANCEL -> cancelGesture()
        }
        return true
    }

    private fun onMove(event: MotionEvent) {
        if (!dragging && hypot(event.rawX - downX, event.rawY - downY) > touchSlop) {
            removeCallbacks(longPressRunnable)
            dragging = true
            taps.reset()
            animator.setDragging(true, SystemClock.uptimeMillis())
            listener?.onDragStart()
        }
        if (dragging) listener?.onDragMove(event.rawX, event.rawY)
    }

    private fun onRelease(event: MotionEvent) {
        removeCallbacks(longPressRunnable)
        val now = SystemClock.uptimeMillis()
        when {
            dragging -> {
                animator.setDragging(false, now)
                listener?.onDragEnd(event.rawX, event.rawY)
            }
            longPressFired -> taps.reset()
            else -> {
                performClick()
                val count = taps.register(now)
                animator.poke(count, now)
                listener?.onTap(count)
            }
        }
        dragging = false
    }

    private fun cancelGesture() {
        removeCallbacks(longPressRunnable)
        if (dragging) animator.setDragging(false, SystemClock.uptimeMillis())
        dragging = false
        longPressFired = false
    }

    override fun performClick(): Boolean = super.performClick()

    override fun onDraw(canvas: Canvas) {
        val unit = min(width, height) / VIEWBOX
        canvas.save()
        canvas.translate((width - VIEWBOX * unit) / 2f, (height - VIEWBOX * unit) / 2f)
        canvas.scale(unit, unit)
        drawShadow(canvas)
        canvas.save()
        canvas.translate(pose[P.DX], pose[P.DY])
        canvas.rotate(pose[P.ROT], FEET_X, FEET_Y)
        canvas.scale(pose[P.SX], pose[P.SY], FEET_X, FEET_Y)
        drawArm(canvas, 29f, 96f, 30f, 88f, 12f, pose[P.ARM_L])
        drawArm(canvas, 99f, 96f, 98f, 88f, -12f, -pose[P.ARM_R])
        drawShape(canvas, body, palette.body)
        drawCheek(canvas, 40f, 88f)
        drawCheek(canvas, 88f, 88f)
        drawEye(canvas, 50f, 78f)
        drawEye(canvas, 78f, 78f)
        drawBrow(canvas, 49.5f, pose[P.BROW_L], 1f)
        drawBrow(canvas, 78.5f, pose[P.BROW_R], -1f)
        drawMouth(canvas)
        drawSnack(canvas)
        drawShape(canvas, blanket, palette.blanket)
        canvas.save()
        canvas.rotate(pose[P.TAIL], 28f, 72f)
        drawShape(canvas, blanketTail, palette.blanket)
        canvas.restore()
        drawSweat(canvas)
        canvas.restore()
        drawFloatingZs(canvas)
        drawNotes(canvas)
        drawSparkles(canvas)
        canvas.restore()
    }

    private fun drawShadow(canvas: Canvas) {
        fill.color = palette.outline
        fill.alpha = (56f * (1f + pose[P.DY] * 0.05f)).toInt().coerceIn(20, 56)
        canvas.save()
        canvas.scale(pose[P.SX], 1f, FEET_X, 118f)
        rect.set(30f, 113f, 98f, 123f)
        canvas.drawOval(rect, fill)
        canvas.restore()
        fill.alpha = 255
    }

    private fun drawArm(canvas: Canvas, cx: Float, cy: Float, pivotX: Float, pivotY: Float, baseDeg: Float, raiseDeg: Float) {
        canvas.save()
        canvas.rotate(raiseDeg, pivotX, pivotY)
        canvas.rotate(baseDeg, cx, cy)
        rect.set(cx - 7f, cy - 10f, cx + 7f, cy + 10f)
        fill.color = palette.body
        canvas.drawOval(rect, fill)
        line.color = palette.outline
        line.strokeWidth = 3f
        canvas.drawOval(rect, line)
        canvas.restore()
    }

    private fun drawShape(canvas: Canvas, path: Path, color: Int) {
        fill.color = color
        canvas.drawPath(path, fill)
        line.color = palette.outline
        line.strokeWidth = 3f
        canvas.drawPath(path, line)
    }

    private fun drawCheek(canvas: Canvas, cx: Float, cy: Float) {
        fill.color = palette.cheek
        fill.alpha = (pose[P.CHEEK] * 255f).toInt().coerceIn(0, 255)
        rect.set(cx - 6f, cy - 4f, cx + 6f, cy + 4f)
        canvas.drawOval(rect, fill)
        fill.alpha = 255
    }

    private fun drawEye(canvas: Canvas, cx: Float, cy: Float) {
        rect.set(cx - 9f, cy - 8f, cx + 9f, cy + 8f)
        fill.color = palette.eye
        canvas.drawOval(rect, fill)
        fill.color = palette.outline
        canvas.drawCircle(cx + 1f + pose[P.PUPIL_DX], cy + 3f + pose[P.PUPIL_DY], pose[P.PUPIL_R], fill)

        val cover = animator.lid()
        val lidY = cy - 8f + cover * 15f
        canvas.save()
        canvas.clipRect(cx - 11f, cy - 11f, cx + 11f, lidY)
        rect.set(cx - 9.5f, cy - 9f, cx + 9.5f, cy + 9f)
        fill.color = palette.body
        canvas.drawOval(rect, fill)
        canvas.restore()

        line.color = palette.outline
        line.strokeWidth = 2.5f
        rect.set(cx - 9f, cy - 8f, cx + 9f, cy + 8f)
        canvas.drawOval(rect, line)
        val dy = (lidY - cy).coerceIn(-8f, 8f)
        val halfWidth = (9f * sqrt(1f - (dy / 8f) * (dy / 8f))).coerceAtLeast(6.5f)
        lidEdge.rewind()
        lidEdge.moveTo(cx - halfWidth, lidY)
        lidEdge.quadTo(cx, lidY + 6f * cover * cover, cx + halfWidth, lidY)
        canvas.drawPath(lidEdge, line)
    }

    /** [inner] is +1 when the inner end of the brow is on the right (left eye), -1 for the right eye. */
    private fun drawBrow(canvas: Canvas, cx: Float, lift: Float, inner: Float) {
        val rad = pose[P.BROW_TILT] * DEG_TO_RAD
        val dx = BROW_HALF * cos(rad)
        val dy = BROW_HALF * sin(rad)
        val cy = 67.5f - lift
        line.color = palette.outline
        line.strokeWidth = 3f
        canvas.drawLine(cx - inner * dx, cy + dy, cx + inner * dx, cy - dy, line)
    }

    private fun drawMouth(canvas: Canvas) {
        mouth.rewind()
        mouth.moveTo(57f, 96f)
        mouth.quadTo(64.5f, 96f + 3f * pose[P.SMILE], 72f, 95f)
        line.color = palette.outline
        line.strokeWidth = 3f
        canvas.drawPath(mouth, line)
        val open = pose[P.OPEN]
        if (open * 7f > 0.6f) {
            rect.set(64.5f - (4f + 2f * open), 97f, 64.5f + (4f + 2f * open), 98f + 7f * open)
            fill.color = palette.outline
            canvas.drawOval(rect, fill)
        }
    }

    private fun drawSnack(canvas: Canvas) {
        val s = pose[P.SNACK]
        if (s < 0.03f) return
        line.color = palette.outline
        line.strokeWidth = 6.4f * s
        canvas.drawCircle(94f, 96f, 5.5f * s, line)
        line.color = palette.cheek
        line.strokeWidth = 3.2f * s
        canvas.drawCircle(94f, 96f, 5.5f * s, line)
    }

    private fun drawSweat(canvas: Canvas) {
        val s = pose[P.SWEAT]
        if (s < 0.03f) return
        canvas.save()
        canvas.translate(92f, 52f + 2f * sin(animator.clockSec * 4f))
        fill.color = palette.eye
        fill.alpha = (s * 255f).toInt().coerceIn(0, 255)
        canvas.drawPath(drop, fill)
        fill.alpha = 255
        line.color = palette.outline
        line.strokeWidth = 1.5f
        line.alpha = (s * 255f).toInt().coerceIn(0, 255)
        canvas.drawPath(drop, line)
        line.alpha = 255
        canvas.restore()
    }

    private fun drawFloatingZs(canvas: Canvas) {
        val amount = pose[P.ZS]
        if (amount < 0.03f) return
        for (i in 0 until 3) {
            val phase = (animator.clockSec / 2.6f + i / 3f) % 1f
            val size = 2.4f + 3.6f * phase
            canvas.save()
            canvas.translate(84f + 20f * phase + 2f * sin(phase * TAU), 40f - 28f * phase)
            canvas.scale(size, size)
            line.color = palette.outline
            line.strokeWidth = 1.8f / size
            line.alpha = (amount * sin(PI.toFloat() * phase) * 255f).toInt().coerceIn(0, 255)
            canvas.drawPath(zShape, line)
            canvas.restore()
        }
        line.alpha = 255
    }

    private fun drawNotes(canvas: Canvas) {
        val amount = pose[P.NOTES]
        if (amount < 0.03f) return
        for (i in 0 until 2) {
            val phase = (animator.clockSec / 2.2f + i * 0.5f) % 1f
            val alpha = (amount * sin(PI.toFloat() * phase) * 255f).toInt().coerceIn(0, 255)
            canvas.save()
            canvas.translate(100f + 8f * phase + 3f * sin(phase * TAU * 1.5f), 46f - 34f * phase)
            fill.color = palette.outline
            fill.alpha = alpha
            canvas.drawCircle(0f, 0f, 2.4f, fill)
            line.color = palette.outline
            line.strokeWidth = 1.8f
            line.alpha = alpha
            canvas.drawPath(noteTail, line)
            canvas.restore()
        }
        fill.alpha = 255
        line.alpha = 255
    }

    private fun drawSparkles(canvas: Canvas) {
        val amount = pose[P.SPARKLE]
        if (amount < 0.03f) return
        for (i in 0 until 3) {
            val r = 6f * amount * (0.75f + 0.25f * sin(TAU * (animator.clockSec * 1.1f + i / 3f)))
            if (r < 0.5f) continue
            canvas.save()
            canvas.translate(SPARKLE_X[i], SPARKLE_Y[i])
            canvas.scale(r, r)
            fill.color = palette.eye
            canvas.drawPath(star, fill)
            line.color = palette.outline
            line.strokeWidth = 1.5f / r
            canvas.drawPath(star, line)
            canvas.restore()
        }
    }

    private companion object {
        const val DEFAULT_SIZE_DP = 64f
        const val VIEWBOX = 128f
        const val FEET_X = 64f
        const val FEET_Y = 114f
        const val BROW_HALF = 7.5f
        const val TAU = (2.0 * PI).toFloat()
        const val DEG_TO_RAD = (PI / 180.0).toFloat()
        val SPARKLE_X = floatArrayOf(18f, 112f, 106f)
        val SPARKLE_Y = floatArrayOf(34f, 26f, 70f)
    }
}
