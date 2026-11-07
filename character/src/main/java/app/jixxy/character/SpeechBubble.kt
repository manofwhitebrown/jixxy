package app.jixxy.character

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.TypedValue
import app.jixxy.engine.api.ReactionKind
import kotlin.math.ceil
import kotlin.math.max

/**
 * Jixxy's speech bubble in his style: dark rounded bubble, cream text, small tail to the cat (like the phone mock-up
 * on the character sheet). Owns the text layout, the typewriter reveal, the auto-dismiss and the 😂/😴 bar that a tap
 * on the bubble reveals. [JixxyView] hosts it and forwards touches. Nothing is allocated while drawing.
 */
internal class SpeechBubble(context: Context, private val palette: CharacterPalette) {
    private val resources = context.resources
    private val metrics = resources.displayMetrics
    private val density = metrics.density

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = palette.cream
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SP, metrics)
    }
    private val emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, EMOJI_SP, metrics)
    }
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = STROKE_DP * density
        strokeJoin = Paint.Join.ROUND
    }
    private val body = RectF()
    private val bar = RectF()
    private val tailFill = Path()
    private val tailLine = Path()

    private var layout: StaticLayout? = null
    private var timeline: BubbleTimeline? = null

    /** Where things sit while a bubble is showing; null when there is none. */
    var geometry: BubbleGeometry? = null
        private set

    /** The line the bubble shows, reported back with the user's reaction. */
    var lineId: String? = null
        private set

    val isShowing: Boolean get() = geometry != null

    private val corner = CORNER_DP * density

    /**
     * Prepares a bubble for [text] (capped at [MAX_CHARS]). [roomAbovePx] is the free screen space above the sprite;
     * the bubble goes below the sprite when that is too small. [alignRight] puts the bubble to the left of a sprite
     * that sits on the right half of the screen.
     */
    fun show(text: String, ttlMs: Long, lineId: String?, nowMs: Long, spritePx: Float, alignRight: Boolean, roomAbovePx: Float) {
        val clean = if (text.length > MAX_CHARS) text.take(MAX_CHARS - 1) + "…" else text
        val padX = PAD_X_DP * density
        val padY = PAD_Y_DP * density
        val scale = bubbleFontScale(resources.configuration.fontScale)
        textPaint.textSize = TEXT_SP * density * scale
        emojiPaint.textSize = EMOJI_SP * density * scale
        val textWidth = bubbleTextWidthPx(MAX_TEXT_DP, density, scale, metrics.widthPixels, padX, SCREEN_SIDE_DP * density)
        val built = StaticLayout.Builder.obtain(clean, 0, clean.length, textPaint, textWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setIncludePad(false)
            .build()
        var widest = 0f
        for (i in 0 until built.lineCount) widest = max(widest, built.getLineWidth(i))
        val bodyW = max(ceil(widest) + 2f * padX, MIN_BODY_DP * density)
        val bodyH = built.height + 2f * padY
        val band = BAND_DP * density
        val overlap = spritePx * OVERLAP_FRACTION
        val needed = bodyH + band - overlap + TOP_INSET_DP * density
        val g = BubbleGeometry(spritePx, bodyW, bodyH, band, overlap, alignRight, above = roomAbovePx >= needed)
        layout = built
        timeline = BubbleTimeline(clean.length, ttlMs, nowMs)
        geometry = g
        this.lineId = lineId
        body.set(g.bodyLeft, g.bodyTop, g.bodyRight, g.bodyBottom)
        placeBar(g)
    }

    fun dismiss() {
        layout = null
        timeline = null
        geometry = null
        lineId = null
    }

    /** Advances the bubble; returns false when it has run its course and should be removed. */
    /** Characters the typewriter has revealed so far (0 when no bubble); the view uses it to time text blips. */
    fun revealedChars(nowMs: Long): Int = timeline?.revealed(nowMs) ?: 0

    fun step(nowMs: Long): Boolean = timeline?.isExpired(nowMs) == false

    /** True when the point (view coordinates) is on the bubble or on its open emoji bar. */
    fun contains(x: Float, y: Float, nowMs: Long): Boolean {
        val open = timeline?.barVisible(nowMs) == true
        return body.contains(x, y) || (open && bar.contains(x, y))
    }

    /**
     * A tap at (x, y). Tapping the bubble opens the emoji bar; tapping an emoji returns the reaction and starts
     * dismissing the bubble. Returns null otherwise.
     */
    fun onTap(x: Float, y: Float, nowMs: Long): ReactionKind? {
        val t = timeline ?: return null
        if (t.barVisible(nowMs)) {
            if (!bar.contains(x, y)) return null
            t.closeSoon(nowMs)
            return if (x < bar.centerX()) ReactionKind.LAUGH else ReactionKind.SLEEPY
        }
        if (body.contains(x, y)) t.openBar(nowMs)
        return null
    }

    fun draw(canvas: Canvas, nowMs: Long) {
        val g = geometry ?: return
        val t = timeline ?: return
        val text = layout ?: return
        val alpha = (t.alpha(nowMs) * 255f).toInt().coerceIn(0, 255)
        if (alpha == 0) return
        drawBody(canvas, g, alpha)
        drawText(canvas, g, t, text, alpha, nowMs)
        if (t.barVisible(nowMs)) drawBar(canvas, alpha)
    }

    private fun drawBody(canvas: Canvas, g: BubbleGeometry, alpha: Int) {
        fill.color = palette.charcoal
        fill.alpha = alpha
        stroke.color = palette.purple
        stroke.alpha = alpha
        canvas.drawRoundRect(body, corner, corner, fill)
        canvas.drawRoundRect(body, corner, corner, stroke)

        // The tail points at the sprite's head. Its fill is drawn over the body outline so the join is seamless.
        val half = g.band * TAIL_HALF_FRACTION
        val tx = g.tailX(corner)
        val inset = stroke.strokeWidth
        val baseY = if (g.above) g.bodyBottom else g.bodyTop
        val tipY = if (g.above) g.bodyBottom + g.band else g.bodyTop - g.band
        val innerY = if (g.above) baseY - inset else baseY + inset
        tailFill.rewind()
        tailFill.moveTo(tx - half, innerY)
        tailFill.lineTo(tx - half, baseY)
        tailFill.lineTo(tx, tipY)
        tailFill.lineTo(tx + half, baseY)
        tailFill.lineTo(tx + half, innerY)
        tailFill.close()
        canvas.drawPath(tailFill, fill)
        tailLine.rewind()
        tailLine.moveTo(tx - half, baseY)
        tailLine.lineTo(tx, tipY)
        tailLine.lineTo(tx + half, baseY)
        canvas.drawPath(tailLine, stroke)
    }

    /** Draws the text with the typewriter reveal: finished lines in full, the current line up to the revealed character. */
    private fun drawText(canvas: Canvas, g: BubbleGeometry, t: BubbleTimeline, text: StaticLayout, alpha: Int, nowMs: Long) {
        textPaint.alpha = alpha
        canvas.save()
        canvas.translate(g.bodyLeft + PAD_X_DP * density, g.bodyTop + PAD_Y_DP * density)
        val shown = t.revealed(nowMs)
        if (shown >= text.text.length) {
            text.draw(canvas)
        } else {
            val line = text.getLineForOffset(shown)
            val top = text.getLineTop(line).toFloat()
            val bottom = text.getLineBottom(line).toFloat()
            canvas.save()
            canvas.clipRect(0f, 0f, text.width.toFloat(), top)
            text.draw(canvas)
            canvas.restore()
            canvas.save()
            canvas.clipRect(0f, top, text.getPrimaryHorizontal(shown), bottom)
            text.draw(canvas)
            canvas.restore()
        }
        canvas.restore()
    }

    private fun drawBar(canvas: Canvas, alpha: Int) {
        fill.color = palette.ink
        fill.alpha = alpha
        stroke.color = palette.purple
        stroke.alpha = alpha
        val r = bar.height() / 2f
        canvas.drawRoundRect(bar, r, r, fill)
        canvas.drawRoundRect(bar, r, r, stroke)
        emojiPaint.alpha = alpha
        val baseline = bar.centerY() - (emojiPaint.ascent() + emojiPaint.descent()) / 2f
        canvas.drawText(LAUGH_EMOJI, bar.left + bar.height() / 2f, baseline, emojiPaint)
        canvas.drawText(SLEEPY_EMOJI, bar.right - bar.height() / 2f, baseline, emojiPaint)
    }

    /** Puts the emoji bar on the bubble's bottom edge, at the end away from the tail. */
    private fun placeBar(g: BubbleGeometry) {
        val h = 2f * g.band
        val w = 2f * h + BAR_GAP_DP * density
        val margin = BAR_MARGIN_DP * density
        val tailOnLeft = g.tailX(corner) < g.bodyLeft + g.bodyW / 2f
        val left = if (tailOnLeft) g.bodyRight - margin - w else g.bodyLeft + margin
        bar.set(left, g.bodyBottom - g.band, left + w, g.bodyBottom + g.band)
    }

    companion object {
        /** Longest text shown; the line bank keeps lines at 90 characters, this is only a safety net. */
        const val MAX_CHARS = 140
        const val LAUGH_EMOJI = "\uD83D\uDE02"
        const val SLEEPY_EMOJI = "\uD83D\uDE34"
        private const val TEXT_SP = 14f
        private const val EMOJI_SP = 16f
        private const val MAX_TEXT_DP = 200f
        private const val SCREEN_SIDE_DP = 8f
        private const val MIN_BODY_DP = 150f
        private const val PAD_X_DP = 12f
        private const val PAD_Y_DP = 9f
        private const val CORNER_DP = 16f
        private const val STROKE_DP = 1.5f
        private const val BAND_DP = 14f
        private const val BAR_GAP_DP = 4f
        private const val BAR_MARGIN_DP = 10f
        private const val TOP_INSET_DP = 24f
        private const val OVERLAP_FRACTION = 0.25f
        private const val TAIL_HALF_FRACTION = 0.7f
    }
}
