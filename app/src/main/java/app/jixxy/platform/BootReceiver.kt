package app.jixxy.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings

/**
 * Best-effort restart after reboot. Only restarts if the user had Jixxy running (see [OverlayService.wantedRunning])
 * and the overlay permission is still granted. Many OEMs block this unless autostart is allowed; failures are silent.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        if (!OverlayService.wantedRunning(context) || !Settings.canDrawOverlays(context)) return
        try {
            context.startForegroundService(Intent(context, OverlayService::class.java))
        } catch (e: IllegalStateException) {
            // Background start refused by the OS (ForegroundServiceStartNotAllowedException). User must reopen the app.
        }
    }
}
