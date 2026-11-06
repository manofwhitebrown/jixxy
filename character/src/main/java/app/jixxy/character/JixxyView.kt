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
import kotlin.math.hypot
import kotlin.math.min

/**
 * Jixxy's floating sprite (concept A, "Blanket Bean"), drawn procedurally from layered shapes.
 *
 * Skeleton status (T-CHAR-1): static art, touch gestures and the public API are in place.
 * Animation arrives in T-CHAR-2, the speech bubble in T-CHAR-3.
 *
 * Default size is 64 dp square; the host window decides the final size. The view never moves itself:
 * dragging is reported through [JixxyViewListener] and the host moves its window.
 */
class JixxyView(context: Context) : FrameLayout(context) {

    /** Receives taps, long-presses, drag events and emoji reactions. */
    var listener: JixxyViewListener? = null

    private val palette = CharacterPalette.Default
    private val taps = TapCounter()
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val longPressMs = ViewConfiguration.getLongPressTimeout().toLong()

    private var state = CharacterState.IDLE
    private var screenActive = true

    private var downX = 0f
    private var downY = 0f
    private var dragging = false
    private var longPressFired = false
    private val longPressRunnable = Runnable {
        longPressFired = true
        listener?.onLongPress()
    }

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val rect = RectF()
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
    private val mouth = Path().apply { moveTo(57f, 96f); quadTo(64f, 99f, 72f, 95f) }

    init {
        setWillNotDraw(false)
        contentDescription = context.getString(R.string.jixxy_content_description)
    }

    /** Switches the pose. In this skeleton only SLEEP and SNORE look different (eyes closed). */
    fun setCharacterState(state: CharacterState) {
        if (this.state == state) return
        this.state = state
        invalidate()
    }

    /**
     * Shows a speech bubble with [text] for [ttlMs]; tapping it will reveal the emoji bar that ends in
     * [JixxyViewListener.onReaction] with [lineId]. Not rendered until T-CHAR-3.
     */
    @Suppress("UNUSED_PARAMETER")
    fun showBubble(text: String, ttlMs: Long, lineId: String?) = Unit

    /** `false` means the screen is off: all animation and timers must stop. Nothing animates yet. */
    fun setScreenActive(active: Boolean) {
        if (screenActive == active) return
        screenActive = active
        if (!active) cancelGesture()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desired = (DEFAULT_SIZE_DP * resources.displayMetrics.density).toInt()
        setMeasuredDimension(resolveSize(desired, widthMeasureSpec), resolveSize(desired, heightMeasureSpec))
    }

    override fun onDetachedFromWindow() {
        cancelGesture()
        super.onDetachedFromWindow()
    }

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
            listener?.onDragStart()
        }
        if (dragging) listener?.onDragMove(event.rawX, event.rawY)
    }

    private fun onRelease(event: MotionEvent) {
        removeCallbacks(longPressRunnable)
        when {
            dragging -> listener?.onDragEnd(event.rawX, event.rawY)
            longPressFired -> taps.reset()
            else -> {
                performClick()
                listener?.onTap(taps.register(SystemClock.uptimeMillis()))
            }
        }
        dragging = false
    }

    private fun cancelGesture() {
        removeCallbacks(longPressRunnable)
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
        drawArm(canvas, 29f, 96f, 12f)
        drawArm(canvas, 99f, 96f, -12f)
        drawShape(canvas, body, palette.body, 3f)
        drawCheek(canvas, 40f, 88f)
        drawCheek(canvas, 88f, 88f)
        drawEye(canvas, 50f, 78f)
        drawEye(canvas, 78f, 78f)
        drawBrow(canvas, 42f, 68f, 57f, 67f)
        drawBrow(canvas, 71f, 67f, 86f, 68f)
        line.color = palette.outline
        line.strokeWidth = 3f
        canvas.drawPath(mouth, line)
        drawShape(canvas, blanket, palette.blanket, 3f)
        drawShape(canvas, blanketTail, palette.blanket, 3f)
        canvas.restore()
    }

    private fun drawShadow(canvas: Canvas) {
        fill.color = palette.outline
        fill.alpha = 56
        rect.set(30f, 113f, 98f, 123f)
        canvas.drawOval(rect, fill)
        fill.alpha = 255
    }

    private fun drawArm(canvas: Canvas, cx: Float, cy: Float, degrees: Float) {
        canvas.save()
        canvas.rotate(degrees, cx, cy)
        rect.set(cx - 7f, cy - 10f, cx + 7f, cy + 10f)
        fill.color = palette.body
        canvas.drawOval(rect, fill)
        line.color = palette.outline
        line.strokeWidth = 3f
        canvas.drawOval(rect, line)
        canvas.restore()
    }

    private fun drawShape(canvas: Canvas, path: Path, color: Int, strokeWidth: Float) {
        fill.color = color
        canvas.drawPath(path, fill)
        line.color = palette.outline
        line.strokeWidth = strokeWidth
        canvas.drawPath(path, line)
    }

    private fun drawCheek(canvas: Canvas, cx: Float, cy: Float) {
        fill.color = palette.cheek
        fill.alpha = 178
        rect.set(cx - 6f, cy - 4f, cx + 6f, cy + 4f)
        canvas.drawOval(rect, fill)
        fill.alpha = 255
    }

    private fun drawBrow(canvas: Canvas, x1: Float, y1: Float, x2: Float, y2: Float) {
        line.color = palette.outline
        line.strokeWidth = 3f
        canvas.drawLine(x1, y1, x2, y2, line)
    }

    private fun drawEye(canvas: Canvas, cx: Float, cy: Float) {
        line.color = palette.outline
        if (state == CharacterState.SLEEP || state == CharacterState.SNORE) {
            line.strokeWidth = 3f
            rect.set(cx - 8f, cy - 5f, cx + 8f, cy + 5f)
            canvas.drawArc(rect, 20f, 140f, false, line)
            return
        }
        rect.set(cx - 9f, cy - 8f, cx + 9f, cy + 8f)
        fill.color = palette.eye
        canvas.drawOval(rect, fill)
        line.strokeWidth = 2.5f
        canvas.drawOval(rect, line)
        fill.color = palette.outline
        canvas.drawCircle(cx + 1f, cy + 3f, 3.6f, fill)
        rect.set(cx - 9.5f, cy - 9f, cx + 9.5f, cy + 9f)
        fill.color = palette.body
        canvas.drawArc(rect, 180f, 180f, true, fill)
        canvas.drawArc(rect, 180f, 180f, true, line)
    }

    private companion object {
        const val DEFAULT_SIZE_DP = 64f
        const val VIEWBOX = 128f
    }
}
