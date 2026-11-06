package app.jixxy.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Routes notification buttons (Nap 1h / Stop) to [OverlayService]. */
class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        context.startService(Intent(context, OverlayService::class.java).setAction(action))
    }
}
