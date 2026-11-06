package app.jixxy.character

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.SystemClock
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import app.jixxy.engine.api.CharacterState
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

/**
 * Jixxy's floating sprite: the Owner's black fluffy cat, shown as expression images that crossfade into each
 * other, with cheap procedural motion on top (breathing, squash and stretch, bob, shake). The state machine
 * behind it is [PoseAnimator]; which picture a state shows is decided by [ExpressionMap] (see
 * `design/character/MAPPING.md`).
 *
 * Touch is handled here: taps (consecutive count), long-press and drags are reported through
 * [JixxyViewListener]; the host moves its window. The view already shows POKED on taps and DRAGGED
 * while held (then the landing), so the brain does not need to request those states.
 *
 * The view is cheap by construction: no allocation while drawing, at most 30 frames per second, and no
 * frame callbacks at all while the screen is off ([setScreenActive]), the view is detached or hidden.
 * Default size is 88 dp square; the host window decides the final size.
 */
class JixxyView(context: Context) : FrameLayout(context) {

    /** Receives taps, long-presses, drag events and emoji reactions. */
    var listener: JixxyViewListener? = null

    /** The state currently playing (may differ from the last requested one while a higher priority state runs). */
    internal val characterState: CharacterState get() = animator.state

    /** The expression being shown (or fading in). */
    internal val expression: Expression get() = fader.current

    private val palette = CharacterPalette.Default
    private val taps = TapCounter()
    private val animator = PoseAnimator()
    private val pose = animator.pose
    private val fader = ExpressionFader()
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val longPressMs = ViewConfiguration.getLongPressTimeout().toLong()

    private val bitmaps = arrayOfNulls<Bitmap>(Expression.values().size)
    private var blinkFrame: Bitmap? = null
    private var shownState: CharacterState? = null
    private var shownPoke = 0

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
            val now = SystemClock.uptimeMillis()
            animator.step(now)
            syncExpression(now)
            fader.step(now)
            invalidate()
            postOnAnimationDelayed(this, if (fader.isFading) PoseAnimator.ACTIVE_FRAME_MS else animator.frameIntervalMs)
        }
    }

    private val spritePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val spriteBounds = RectF(0f, 0f, VIEWBOX, VIEWBOX)
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
     * Requests a state. Higher priority states win: DRAGGED, then POKED, then everything else. A request
     * that loses is played afterwards if it is still fresh (2 s). DRAGGED and FALLING come from touch.
     * One-shot states return to IDLE on their own; SLEEP, SNORE, MUSIC and SOFT hold until you send another
     * state (send IDLE to end them). Each state shows the expression listed in `design/character/MAPPING.md`.
     */
    fun setCharacterState(state: CharacterState) {
        animator.request(state, SystemClock.uptimeMillis())
    }

    /**
     * Shows a speech bubble with [text] for [ttlMs]; tapping it will reveal the emoji bar that ends in
     * [JixxyViewListener.onReaction] with [lineId]. Not rendered until T-C2-3.
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        releaseBitmaps()
        loadCurrent()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimating()
    }

    override fun onDetachedFromWindow() {
        stopAnimating()
        cancelGesture()
        releaseBitmaps()
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
        val now = SystemClock.uptimeMillis()
        animator.resume(now)
        loadCurrent()
        syncExpression(now)
        postOnAnimation(ticker)
    }

    private fun stopAnimating() = removeCallbacks(ticker)

    // ---- expressions -------------------------------------------------------------------------------------

    /** Switches the shown expression when the playing state (or poke level) changed since the last frame. */
    private fun syncExpression(nowMs: Long) {
        val state = animator.state
        val poke = animator.pokeLevel
        if (state == shownState && poke == shownPoke) return
        shownState = state
        shownPoke = poke
        val hour = if (state.name == "SOFT") Calendar.getInstance().get(Calendar.HOUR_OF_DAY) else NOON
        val wanted = ExpressionMap.forState(state.name, poke, hour)
        fader.show(if (bitmapFor(wanted) != null) wanted else Expression.REALLY, nowMs)
    }

    private fun loadCurrent() {
        bitmapFor(fader.current)
        if (blinkFrame == null) blinkFrame = decode(optionalRes(BLINK_FRAME))
    }

    private fun bitmapFor(e: Expression): Bitmap? {
        bitmaps[e.ordinal]?.let { return it }
        val loaded = decode(drawableRes(e)) ?: return null
        bitmaps[e.ordinal] = loaded
        return loaded
    }

    private fun releaseBitmaps() {
        bitmaps.fill(null)
        blinkFrame = null
    }

    private fun decode(resId: Int): Bitmap? {
        if (resId == 0) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(resources, resId, bounds)
        val target = if (width > 0) min(width, height) else (DEFAULT_SIZE_DP * resources.displayMetrics.density).toInt()
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSizeFor(bounds.outWidth, target)
            inScaled = false
        }
        return BitmapFactory.decodeResource(resources, resId, options)
    }

    /** Looks up an optional frame the Owner may add later; 0 when it does not exist. */
    private fun optionalRes(name: String): Int = resources.getIdentifier(name, "drawable", context.packageName)

    private fun drawableRes(e: Expression): Int = when (e) {
        Expression.REALLY -> R.drawable.jixxy_expr_really
        Expression.NAP -> R.drawable.jixxy_expr_nap
        Expression.PEEK -> R.drawable.jixxy_expr_peek
        Expression.BRO_WHAT -> R.drawable.jixxy_expr_bro_what
        Expression.OOH_NICE -> R.drawable.jixxy_expr_ooh_nice
        Expression.SOFT -> R.drawable.jixxy_expr_soft
        Expression.SMUG -> R.drawable.jixxy_expr_smug
        Expression.SHOCKED -> R.drawable.jixxy_expr_shocked
        Expression.DROPPED -> R.drawable.jixxy_expr_dropped
        Expression.LOW_BATTERY -> R.drawable.jixxy_expr_low_battery
        Expression.CHARGING -> R.drawable.jixxy_expr_charging
        Expression.SNACKS -> R.drawable.jixxy_expr_snacks
        Expression.HEADPHONES -> R.drawable.jixxy_expr_headphones
        Expression.RESPECT -> R.drawable.jixxy_expr_respect
        Expression.BLANKET -> R.drawable.jixxy_expr_blanket
    }

    // ---- touch -------------------------------------------------------------------------------------------

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

    // ---- drawing -----------------------------------------------------------------------------------------

    override fun onDraw(canvas: Canvas) {
        val current = bitmaps[fader.current.ordinal] ?: return
        val unit = min(width, height) / VIEWBOX
        canvas.save()
        canvas.translate((width - VIEWBOX * unit) / 2f, (height - VIEWBOX * unit) / 2f)
        canvas.scale(unit, unit)

        val blink = animator.blinkEnvelope
        val swapBlinkFrame = blinkFrame != null && blink > BLINK_SWAP_AT && fader.current == Expression.REALLY
        val squash = if (blinkFrame == null) BLINK_SQUASH * blink else 0f
        canvas.save()
        canvas.translate(pose[P.DX], pose[P.DY])
        canvas.rotate(pose[P.ROT], FEET_X, FEET_Y)
        canvas.scale(pose[P.SX], pose[P.SY] * (1f - squash), FEET_X, FEET_Y)
        val under = fader.previous?.let { bitmaps[it.ordinal] }
        if (under != null) {
            spritePaint.alpha = 255
            canvas.drawBitmap(under, null, spriteBounds, spritePaint)
        }
        spritePaint.alpha = (255f * fader.progress).toInt()
        canvas.drawBitmap(if (swapBlinkFrame) blinkFrame ?: current else current, null, spriteBounds, spritePaint)
        drawSweat(canvas)
        canvas.restore()

        drawFloatingZs(canvas)
        drawNotes(canvas)
        drawSparkles(canvas)
        canvas.restore()
    }

    /** Draws [path] in cream with a thin ink outline, so it reads on any app underneath. */
    private fun strokeOutlined(canvas: Canvas, path: Path, width: Float, alpha: Int) {
        line.color = palette.ink
        line.strokeWidth = width + OUTLINE
        line.alpha = alpha
        canvas.drawPath(path, line)
        line.color = palette.cream
        line.strokeWidth = width
        line.alpha = alpha
        canvas.drawPath(path, line)
        line.alpha = 255
    }

    private fun drawSweat(canvas: Canvas) {
        val s = pose[P.SWEAT]
        if (s < 0.03f) return
        val alpha = (s * 255f).toInt().coerceIn(0, 255)
        canvas.save()
        canvas.translate(98f, 40f + 2f * sin(animator.clockSec * 4f))
        fill.color = palette.cream
        fill.alpha = alpha
        canvas.drawPath(drop, fill)
        fill.alpha = 255
        line.color = palette.ink
        line.strokeWidth = 1.5f
        line.alpha = alpha
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
            canvas.translate(92f + 18f * phase + 2f * sin(phase * TAU), 34f - 30f * phase)
            canvas.scale(size, size)
            strokeOutlined(canvas, zShape, 1.8f / size, (amount * sin(PI.toFloat() * phase) * 255f).toInt().coerceIn(0, 255))
            canvas.restore()
        }
    }

    private fun drawNotes(canvas: Canvas) {
        val amount = pose[P.NOTES]
        if (amount < 0.03f) return
        for (i in 0 until 2) {
            val phase = (animator.clockSec / 2.2f + i * 0.5f) % 1f
            val alpha = (amount * sin(PI.toFloat() * phase) * 255f).toInt().coerceIn(0, 255)
            canvas.save()
            canvas.translate(104f + 8f * phase + 3f * sin(phase * TAU * 1.5f), 40f - 32f * phase)
            fill.color = palette.ink
            fill.alpha = alpha
            canvas.drawCircle(0f, 0f, 3f, fill)
            fill.color = palette.cream
            fill.alpha = alpha
            canvas.drawCircle(0f, 0f, 2.1f, fill)
            fill.alpha = 255
            strokeOutlined(canvas, noteTail, 1.5f, alpha)
            canvas.restore()
        }
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
            fill.color = palette.cream
            canvas.drawPath(star, fill)
            line.color = palette.ink
            line.strokeWidth = 1.5f / r
            canvas.drawPath(star, line)
            canvas.restore()
        }
    }

    private companion object {
        const val DEFAULT_SIZE_DP = 88f
        const val VIEWBOX = 128f
        const val FEET_X = 64f
        const val FEET_Y = 126f
        const val NOON = 12
        const val TAU = (2.0 * PI).toFloat()
        const val OUTLINE = 1.4f
        const val BLINK_SQUASH = 0.04f
        const val BLINK_SWAP_AT = 0.5f
        const val BLINK_FRAME = "jixxy_expr_really_blink"
        val SPARKLE_X = floatArrayOf(14f, 116f, 110f)
        val SPARKLE_Y = floatArrayOf(34f, 26f, 70f)
    }
}
