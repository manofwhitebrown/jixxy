package app.jixxy.platform

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import app.jixxy.character.PauseCardView
import app.jixxy.engine.api.PauseChoiceKind
import app.jixxy.engine.api.PauseSpec

/**
 * Own overlay window hosting CHARACTER's [PauseCardView] (T-P2-5).
 * Leave is active immediately and Stay unlocks after [PauseSpec.stayEnabledAfterSec]; both are handled by the card.
 * The window is wrap-content, so touches outside the card reach the app below. Empty text (no eligible line) shows a neutral card.
 */
class PauseWindow(private val context: Context, private val windowManager: WindowManager) {
    private var card: View? = null

    val isShowing get() = card != null

    fun show(spec: PauseSpec, text: String, onChoice: (PauseChoiceKind) -> Unit) {
        dismiss()
        val dp = context.resources.displayMetrics.density
        val view = PauseCardView(context)
        view.bind(spec, text.ifBlank { PlatformStrings.PAUSE_NEUTRAL_TEXT }) { choice ->
            dismiss()
            onChoice(choice)
        }
        val lp = WindowManager.LayoutParams(
            (300 * dp).toInt(), WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply { gravity = Gravity.CENTER }
        windowManager.addView(view, lp)
        card = view
    }

    /** Removes the card; safe to call when nothing is showing (service stop, call start, nap, screen off). */
    fun dismiss() {
        card?.let { runCatching { windowManager.removeView(it) } }
        card = null
    }
}
