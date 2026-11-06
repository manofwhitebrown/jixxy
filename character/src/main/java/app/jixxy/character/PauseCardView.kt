package app.jixxy.character

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.SystemClock
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import app.jixxy.engine.api.PauseChoiceKind
import app.jixxy.engine.api.PauseSpec
import kotlin.math.ceil

/**
 * The Pause card: Jixxy peeks over the top edge, says one line, and offers **Leave** and **Stay N more minutes**.
 *
 * - Leave is active from the first frame and is the primary button. It is always available.
 * - Stay unlocks after [PauseSpec.stayEnabledAfterSec]; until then it shows a gentle countdown ring and "Stay (n)".
 *   The wait uses the real clock, so turning system animations off does not skip it.
 * - Calm colours only (charcoal, purple, cream): never alarming red, never full screen. Real [Button]s, 48 dp tall
 *   or more, with a pane title for TalkBack.
 *
 * The host (PLATFORM's own overlay window) sizes the view, usually about 300 dp wide with wrap-content height, and
 * removes it after [bind]'s `onChoice` fires. Nothing runs while the view is detached.
 */
class PauseCardView(context: Context) : FrameLayout(context) {
    private val palette = CharacterPalette.Default
    private val density = resources.displayMetrics.density

    private val peek = PeekFaceView(context)
    private val message = TextView(context)
    private val note = TextView(context)
    private val leave = Button(context)
    private val stay = Button(context)
    private val ring = RingDrawable(palette.cream)

    private var countdown: StayCountdown? = null
    private var grantMinutes = DEFAULT_GRANT_MINUTES
    private var onChoice: ((PauseChoiceKind) -> Unit)? = null
    private var answered = false
    private var lastShownSec = -1

    private val tick = object : Runnable {
        override fun run() {
            val c = countdown ?: return
            val now = SystemClock.uptimeMillis()
            if (c.isDone(now)) {
                unlockStay()
                return
            }
            ring.progress = c.progress(now)
            val sec = c.remainingSec(now)
            if (sec != lastShownSec) {
                lastShownSec = sec
                stay.text = resources.getString(R.string.pause_stay_waiting, sec)
            }
            postDelayed(this, TICK_MS)
        }
    }

    init {
        val root = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
        root.addView(peek, LinearLayout.LayoutParams(dp(PEEK_W_DP), dp(PEEK_H_DP)).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            bottomMargin = -dp(4)
        })
        root.addView(buildCard(), LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        addView(root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        ViewCompat.setAccessibilityPaneTitle(this, context.getString(R.string.a11y_pause_card))
        leave.setOnClickListener { choose(PauseChoiceKind.LEAVE) }
        stay.setOnClickListener { choose(PauseChoiceKind.STAY) }
    }

    /**
     * Fills the card. [text] is the line from the line bank, [spec] says how long Stay stays locked and how many
     * minutes it grants. [onChoice] is called once, with LEAVE or STAY; the host then removes the card.
     */
    fun bind(spec: PauseSpec, text: String, onChoice: (PauseChoiceKind) -> Unit) {
        this.onChoice = onChoice
        answered = false
        grantMinutes = spec.grantMinutes
        message.text = text
        leave.text = resources.getString(R.string.pause_leave)
        removeCallbacks(tick)
        val wait = spec.stayEnabledAfterSec
        if (wait <= 0) {
            countdown = null
            unlockStay()
        } else {
            countdown = StayCountdown(SystemClock.uptimeMillis(), wait)
            lastShownSec = -1
            stay.isEnabled = false
            stay.setCompoundDrawablesRelativeWithIntrinsicBounds(ring, null, null, null)
            stay.contentDescription = resources.getQuantityString(R.plurals.pause_stay_available_in, wait, wait)
            ring.progress = 0f
            if (isAttachedToWindow) post(tick)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (countdown != null && !answered) post(tick)
        peek.translationY = dp(SLIDE_DP).toFloat()
        peek.animate().translationY(0f).setDuration(SLIDE_MS).start()
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(tick)
        peek.animate().cancel()
        super.onDetachedFromWindow()
    }

    private fun unlockStay() {
        countdown = null
        stay.isEnabled = true
        stay.setCompoundDrawablesRelative(null, null, null, null)
        stay.text = resources.getQuantityString(R.plurals.pause_stay, grantMinutes, grantMinutes)
        stay.contentDescription = null
    }

    private fun choose(kind: PauseChoiceKind) {
        if (answered) return
        answered = true
        removeCallbacks(tick)
        onChoice?.invoke(kind)
    }

    private fun buildCard(): View {
        val card = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(12))
            background = GradientDrawable().apply {
                setColor(palette.charcoal)
                cornerRadius = dp(CARD_RADIUS_DP).toFloat()
                setStroke(dp(STROKE_DP), palette.purple)
            }
        }
        message.apply {
            setTextColor(palette.cream)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
            gravity = Gravity.CENTER_HORIZONTAL
            setLineSpacing(0f, 1.1f)
        }
        card.addView(message, LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        styleButton(leave, primary = true)
        card.addView(leave, buttonParams(topMargin = dp(14), height = dp(LEAVE_H_DP)))
        styleButton(stay, primary = false)
        card.addView(stay, buttonParams(topMargin = dp(8), height = dp(STAY_H_DP)))
        note.apply {
            text = resources.getString(R.string.pause_dismiss_note)
            setTextColor(palette.cream)
            alpha = 0.6f
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            gravity = Gravity.CENTER_HORIZONTAL
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
        }
        card.addView(note, LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(8)
        })
        return card
    }

    private fun buttonParams(topMargin: Int, height: Int) =
        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height).apply { this.topMargin = topMargin }

    private fun styleButton(button: Button, primary: Boolean) {
        button.apply {
            isAllCaps = false
            stateListAnimator = null
            minHeight = 0
            minimumHeight = 0
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            typeface = if (primary) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            setTextColor(palette.cream)
            compoundDrawablePadding = dp(8)
            background = GradientDrawable().apply {
                cornerRadius = dp(BUTTON_RADIUS_DP).toFloat()
                if (primary) setColor(palette.purple) else {
                    setColor(Color.TRANSPARENT)
                    setStroke(dp(STROKE_DP), palette.purple)
                }
            }
        }
    }

    private fun dp(v: Float): Int = (v * density).toInt()
    private fun dp(v: Int): Int = (v * density).toInt()

    private companion object {
        const val DEFAULT_GRANT_MINUTES = 5
        const val TICK_MS = 100L
        const val SLIDE_MS = 220L
        const val SLIDE_DP = 18
        const val PEEK_W_DP = 120f
        const val PEEK_H_DP = 60f
        const val CARD_RADIUS_DP = 20f
        const val BUTTON_RADIUS_DP = 14f
        const val STROKE_DP = 2
        const val LEAVE_H_DP = 52
        const val STAY_H_DP = 48
    }
}

/** Timing of the Stay lock. Pure maths on a monotonic millisecond clock, unit-tested on the JVM. */
internal class StayCountdown(private val startMs: Long, waitSec: Int) {
    private val totalMs = waitSec * 1000L

    /** 0..1 of the wait that has passed. */
    fun progress(nowMs: Long): Float = ((nowMs - startMs) / totalMs.toFloat()).coerceIn(0f, 1f)

    /** Whole seconds left, rounded up, never negative. */
    fun remainingSec(nowMs: Long): Int = ceil((totalMs - (nowMs - startMs)).coerceAtLeast(0L) / 1000.0).toInt()

    fun isDone(nowMs: Long): Boolean = nowMs - startMs >= totalMs
}

/** The peeking face, bottom-aligned so only the part above the card's edge shows. */
internal class PeekFaceView(context: Context) : View(context) {
    private var face: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val dst = RectF()

    init {
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        face = decode(w)
    }

    override fun onDetachedFromWindow() {
        face = null
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (face == null && width > 0) face = decode(width)
    }

    private fun decode(targetPx: Int): Bitmap? {
        val id = R.drawable.jixxy_expr_peek
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(resources, id, bounds)
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSizeFor(bounds.outWidth, targetPx)
            inScaled = false
        }
        return BitmapFactory.decodeResource(resources, id, options)
    }

    override fun onDraw(canvas: Canvas) {
        val bitmap = face ?: return
        val side = width.toFloat()
        val top = height - side * FEET_FRACTION
        dst.set(0f, top, side, top + side)
        canvas.drawBitmap(bitmap, null, dst, paint)
    }

    private companion object {
        /** The cat's bottom edge sits at y = 502 on the 512 px canvas (see design/character/reference/CROPS.md). */
        const val FEET_FRACTION = 502f / 512f
    }
}

/** A thin ring that fills clockwise as [progress] goes from 0 to 1. */
internal class RingDrawable(tint: Int) : Drawable() {
    var progress = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            invalidateSelf()
        }

    private val track = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = tint
        alpha = TRACK_ALPHA
    }
    private val arc = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        color = tint
    }
    private val oval = RectF()

    override fun getIntrinsicWidth(): Int = SIZE_PX
    override fun getIntrinsicHeight(): Int = SIZE_PX

    override fun draw(canvas: Canvas) {
        val b = bounds
        val stroke = b.width() * STROKE_FRACTION
        track.strokeWidth = stroke
        arc.strokeWidth = stroke
        val inset = stroke / 2f
        oval.set(b.left + inset, b.top + inset, b.right - inset, b.bottom - inset)
        canvas.drawOval(oval, track)
        canvas.drawArc(oval, START_ANGLE, 360f * progress, false, arc)
    }

    override fun setAlpha(alpha: Int) {
        arc.alpha = alpha
    }

    override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {
        arc.colorFilter = colorFilter
        track.colorFilter = colorFilter
    }

    @Deprecated("Required by Drawable", ReplaceWith("PixelFormat.TRANSLUCENT"))
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    private companion object {
        const val SIZE_PX = 54
        const val STROKE_FRACTION = 0.14f
        const val START_ANGLE = -90f
        const val TRACK_ALPHA = 70
    }
}
