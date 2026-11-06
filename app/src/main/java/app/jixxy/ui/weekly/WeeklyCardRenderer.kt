package app.jixxy.ui.weekly

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import java.io.File
import app.jixxy.character.R as CharR

/** Text the card prints; all of it comes from strings.xml (WRITER's microcopy). */
data class WeeklyCardTexts(
    val headline: String,
    val pauses: String,
    val left: String,
    val stayed: String,
    val mark: String = "jixxy",
)

/**
 * Draws the 1080x1350 share card with plain Canvas calls (no window needed, so it works off-screen).
 * Jixxy-voiced headline, three counts, one roast, one earned compliment (if any), small "jixxy" mark.
 * Nothing leaves the phone: the PNG goes to cacheDir/share/ and is shared through FileProvider only on tap.
 */
object WeeklyCardRenderer {
    const val WIDTH = 1080
    const val HEIGHT = 1350
    private const val MARGIN = 72f

    private const val FUR = 0xFF0E0910.toInt()
    private const val SURFACE = 0xFF291E29.toInt()
    private const val CREAM = 0xFFFAE6E6.toInt()
    private const val PURPLE = 0xFFA080B0.toInt()

    fun render(context: Context, model: WeeklyCardModel, texts: WeeklyCardTexts): Bitmap {
        val bmp = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(FUR)
        var y = MARGIN

        BitmapFactory.decodeResource(context.resources, CharR.drawable.jixxy_expr_smug)?.let { cat ->
            val h = 360f
            val w = cat.width * h / cat.height
            c.drawBitmap(cat, null, RectF((WIDTH - w) / 2, y, (WIDTH + w) / 2, y + h), Paint(Paint.FILTER_BITMAP_FLAG))
            y += h + 24f
        }

        y = drawText(c, texts.headline, y, 64f, CREAM, bold = true)
        y += 36f

        val gap = 24f
        val boxW = (WIDTH - 2 * MARGIN - 2 * gap) / 3
        listOf(model.pauses to texts.pauses, model.left to texts.left, model.stayed to texts.stayed)
            .forEachIndexed { i, (value, label) -> drawStat(c, MARGIN + i * (boxW + gap), y, boxW, value, label) }
        y += 230f + 40f

        model.roast?.let { y = drawText(c, it, y, 46f, CREAM, italic = true) + 24f }
        model.compliment?.let { y = drawText(c, it, y, 46f, PURPLE, italic = true) }

        val mark = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { color = PURPLE; textSize = 40f; typeface = Typeface.DEFAULT_BOLD }
        c.drawText(texts.mark, WIDTH - MARGIN - mark.measureText(texts.mark), HEIGHT - MARGIN, mark)
        return bmp
    }

    private fun drawStat(c: Canvas, x: Float, y: Float, w: Float, value: Int, label: String) {
        c.drawRoundRect(RectF(x, y, x + w, y + 230f), 36f, 36f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = SURFACE })
        val num = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { color = CREAM; textSize = 96f; typeface = Typeface.DEFAULT_BOLD; textAlign = Paint.Align.CENTER }
        c.drawText(value.toString(), x + w / 2, y + 118f, num)
        val lab = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { color = PURPLE; textSize = 30f; textAlign = Paint.Align.CENTER }
        val layout = StaticLayout.Builder.obtain(label, 0, label.length, lab, (w - 24f).toInt())
            .setAlignment(Layout.Alignment.ALIGN_CENTER).setMaxLines(2).build()
        c.save(); c.translate(x + 12f, y + 140f); layout.draw(c); c.restore()
    }

    /** Draws wrapped text at [y] and returns the y below it. */
    private fun drawText(c: Canvas, text: String, y: Float, size: Float, color: Int, bold: Boolean = false, italic: Boolean = false): Float {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color; textSize = size
            typeface = Typeface.create(Typeface.DEFAULT, when { bold -> Typeface.BOLD; italic -> Typeface.ITALIC; else -> Typeface.NORMAL })
        }
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, (WIDTH - 2 * MARGIN).toInt())
            .setAlignment(Layout.Alignment.ALIGN_CENTER).setMaxLines(4).build()
        c.save(); c.translate(MARGIN, y); layout.draw(c); c.restore()
        return y + layout.height
    }

    /** Writes [bitmap] to cacheDir/share/ and returns a content Uri for ACTION_SEND. */
    fun saveForShare(context: Context, bitmap: Bitmap): android.net.Uri {
        val dir = File(context.cacheDir, "share").apply { mkdirs() }
        val file = File(dir, "jixxy-week.png")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
