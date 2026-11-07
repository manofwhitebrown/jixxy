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
    private var onCancelled: (() -> Unit)? = null

    val isShowing get() = card != null

    fun show(spec: PauseSpec, text: String, onChoice: (PauseChoiceKind) -> Unit, onCancelled: () -> Unit = {}) {
        dismiss()
        this.onCancelled = onCancelled
        val dp = context.resources.displayMetrics.density
        val view = PauseCardView(context)
        view.bind(spec, text.ifBlank { PlatformStrings.PAUSE_NEUTRAL_TEXT }) { choice ->
            remove()  // a chosen card is not a cancelled card
            onChoice(choice)
        }
        val lp = WindowManager.LayoutParams(
            (300 * dp).toInt(), WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply { gravity = Gravity.CENTER }
        try {
            windowManager.addView(view, lp)
        } catch (e: RuntimeException) {
            // Overlay permission revoked or window refused: report "cancelled" so the dispatcher is not stuck.
            this.onCancelled = null
            onCancelled()
            return
        }
        card = view
    }

    /**
     * Removes the card without a choice; safe when nothing is showing (service stop, call, nap, screen off).
     * If a card was up, the owner of the Pause is told it was cancelled, so it can accept the next one.
     */
    fun dismiss() {
        val wasShowing = card != null
        val cb = onCancelled
        remove()
        if (wasShowing) cb?.invoke()
    }

    private fun remove() {
        card?.let { runCatching { windowManager.removeView(it) } }
        card = null
        onCancelled = null
    }
}
