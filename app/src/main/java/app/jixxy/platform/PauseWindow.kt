package app.jixxy.platform

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import app.jixxy.engine.api.PauseChoiceKind
import app.jixxy.engine.api.PauseSpec

/**
 * Own overlay window for the Pause card. Plain-view stub until CHARACTER's PauseCardView exists.
 * Leave is tappable immediately; Stay unlocks after [PauseSpec.stayEnabledAfterSec].
 * Wrap-content window, so touches outside the card reach the app below.
 */
class PauseWindow(private val context: Context, private val windowManager: WindowManager) {
    private val handler = Handler(Looper.getMainLooper())
    private var card: View? = null

    val isShowing get() = card != null

    fun show(spec: PauseSpec, text: String, onChoice: (PauseChoiceKind) -> Unit) {
        dismiss()
        val dp = context.resources.displayMetrics.density
        val pad = (16 * dp).toInt()
        val message = TextView(context).apply { this.text = text; setTextColor(Color.BLACK); textSize = 16f }
        val leave = Button(context).apply { this.text = PlatformStrings.PAUSE_LEAVE }
        val stay = Button(context).apply {
            this.text = PlatformStrings.pauseStay(spec.grantMinutes)
            isEnabled = spec.stayEnabledAfterSec <= 0
        }
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
            background = GradientDrawable().apply { setColor(Color.WHITE); cornerRadius = 20 * dp }
            addView(message)
            addView(leave)
            addView(stay)
        }
        fun choose(choice: PauseChoiceKind) { dismiss(); onChoice(choice) }
        leave.setOnClickListener { choose(PauseChoiceKind.LEAVE) }
        stay.setOnClickListener { choose(PauseChoiceKind.STAY) }
        if (!stay.isEnabled) handler.postDelayed({ stay.isEnabled = true }, spec.stayEnabledAfterSec * 1000L)

        val lp = WindowManager.LayoutParams(
            (300 * dp).toInt(), WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply { gravity = Gravity.CENTER }
        windowManager.addView(root, lp)
        card = root
    }

    /** Removes the card; safe to call when nothing is showing (service stop, call start). */
    fun dismiss() {
        handler.removeCallbacksAndMessages(null)
        card?.let { runCatching { windowManager.removeView(it) } }
        card = null
    }
}
