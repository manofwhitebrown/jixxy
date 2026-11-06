package app.jixxy.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Routes notification buttons (Nap 1h / Wake / Stop) to [OverlayService]. */
class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        when (action) {
            OverlayService.ACTION_WAKE -> runCatching { OverlayService.wake(context) }
            OverlayService.ACTION_NAP, OverlayService.ACTION_STOP ->
                runCatching { context.startService(Intent(context, OverlayService::class.java).setAction(action)) }
        }
    }
}
