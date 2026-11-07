package app.jixxy.platform

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.jixxy.R
import app.jixxy.ui.MainActivity
import java.time.ZoneId

/**
 * Weekly-review trigger without WorkManager: the service's 60 s heartbeat (screen on only, so no extra wake-ups) and the UI's
 * app-open check both call [check]. At most ONE gentle notification per week, and the user can switch it off ([setEnabled]).
 * State: two values in app-private prefs. First run only seeds "last shown = now", so a fresh install never fires at once.
 */
class WeeklyTrigger(private val context: Context, private val zone: () -> ZoneId = { ZoneId.systemDefault() }) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, true)
    fun setEnabled(on: Boolean) { prefs.edit().putBoolean(KEY_ENABLED, on).apply() }

    /** True if this week's review has not been shown yet. UI uses this on app open to offer the card even if the service was off. */
    fun isDue(now: Long = System.currentTimeMillis()): Boolean = WeeklySchedule.isDue(prefs.getLong(KEY_LAST, 0L), now, zone())

    /** UI calls this after the user has opened the weekly card, so the notification does not repeat. */
    fun markShown(now: Long = System.currentTimeMillis()) { prefs.edit().putLong(KEY_LAST, now).apply() }

    /** Posts the one weekly notification if due, enabled and notifications are allowed. Returns true if it posted. */
    @SuppressLint("MissingPermission")  // guarded by areNotificationsEnabled()
    fun check(now: Long = System.currentTimeMillis()): Boolean {
        if (prefs.getLong(KEY_LAST, 0L) == 0L) { markShown(now); return false }
        if (!isEnabled() || !isDue(now)) return false
        val nm = NotificationManagerCompat.from(context)
        if (!nm.areNotificationsEnabled()) return false
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(CHANNEL_ID, PlatformStrings.WEEKLY_CHANNEL, NotificationManager.IMPORTANCE_LOW)
        )
        val open = PendingIntent.getActivity(
            context, 5, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        nm.notify(
            NOTIFICATION_ID,
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_jixxy)
                .setContentTitle(PlatformStrings.WEEKLY_TITLE)
                .setContentIntent(open)
                .setAutoCancel(true)
                .build(),
        )
        markShown(now)
        return true
    }

    private companion object {
        const val PREFS = "platform_weekly"
        const val KEY_LAST = "last_shown"
        const val KEY_ENABLED = "enabled"
        const val CHANNEL_ID = "jixxy_weekly"
        const val NOTIFICATION_ID = 3
    }
}
