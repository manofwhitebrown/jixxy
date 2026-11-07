package app.jixxy.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings

/** Pure restart decision, so it can be unit-tested without Android. */
object RestartPolicy {
    /** Reboot and "app was updated" both kill the service; both are on Android's list of broadcasts that may start it again. */
    fun isRestartAction(action: String?): Boolean =
        action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED

    /** Restart only if the user had Jixxy running and the overlay permission is still there. */
    fun shouldRestart(action: String?, wantedRunning: Boolean, canDrawOverlays: Boolean): Boolean =
        isRestartAction(action) && wantedRunning && canDrawOverlays
}

/**
 * Best-effort restart after reboot or after the app is updated (an update kills the running service).
 * Docs: BOOT_COMPLETED, LOCKED_BOOT_COMPLETED and MY_PACKAGE_REPLACED receivers may start a foreground service from
 * the background (developer.android.com/develop/background-work/services/fgs/restrictions-bg-start).
 * Android 15 forbids BOOT_COMPLETED receivers from starting some foreground-service types
 * (developer.android.com/about/versions/15/behavior-changes-15); if specialUse were ever refused, the OS throws
 * ForegroundServiceStartNotAllowedException, which we swallow: the user then wakes him from the app.
 * Many OEMs block this unless autostart is allowed; failures are silent by design.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!RestartPolicy.shouldRestart(intent.action, OverlayService.wantedRunning(context), Settings.canDrawOverlays(context))) return
        try {
            context.startForegroundService(Intent(context, OverlayService::class.java))
        } catch (e: IllegalStateException) {
            // Background start refused by the OS. User must reopen the app.
        }
    }
}
