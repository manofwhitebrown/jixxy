package app.jixxy.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** Snapshot of everything the onboarding UI needs to know. */
data class PermissionsState(
    val overlayGranted: Boolean,
    val usageAccessGranted: Boolean,
    val notificationsGranted: Boolean,
    val batteryUnrestricted: Boolean,
    val restrictedSettingsLikelyBlocked: Boolean,
)

/** Which system screen to open. */
enum class PermissionKind { OVERLAY, USAGE_ACCESS, NOTIFICATIONS, BATTERY, APP_DETAILS }

/**
 * Reads permission state and builds the Intents to fix it. Android gives no change callbacks for these special
 * permissions, so call [refresh] when the app returns to the foreground (e.g. Activity.onResume).
 */
class PermissionsRepository(private val context: Context) {
    private val _state = MutableStateFlow(read())

    val state: StateFlow<PermissionsState> = _state
    val overlayGranted: Flow<Boolean> = state.map { it.overlayGranted }.distinctUntilChanged()
    val usageAccessGranted: Flow<Boolean> = state.map { it.usageAccessGranted }.distinctUntilChanged()
    val notificationsGranted: Flow<Boolean> = state.map { it.notificationsGranted }.distinctUntilChanged()
    val batteryUnrestricted: Flow<Boolean> = state.map { it.batteryUnrestricted }.distinctUntilChanged()
    val restrictedSettingsLikelyBlocked: Flow<Boolean> =
        state.map { it.restrictedSettingsLikelyBlocked }.distinctUntilChanged()

    fun refresh() { _state.value = read() }

    /**
     * Intent that opens the right settings page. Callers should start it inside try/catch (some OEMs lack pages).
     * BATTERY uses ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS (a list screen, needs no extra permission).
     * APP_DETAILS is where "Allow restricted settings" lives (three-dot menu).
     */
    fun intentFor(kind: PermissionKind): Intent {
        val pkgUri = Uri.parse("package:${context.packageName}")
        return when (kind) {
            PermissionKind.OVERLAY -> Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, pkgUri)
            PermissionKind.USAGE_ACCESS -> Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            PermissionKind.NOTIFICATIONS -> Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            PermissionKind.BATTERY -> Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            PermissionKind.APP_DETAILS -> Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, pkgUri)
        }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    private fun read(): PermissionsState {
        val overlay = Settings.canDrawOverlays(context)
        val usage = UsageAccess.isGranted(context)
        val power = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return PermissionsState(
            overlayGranted = overlay,
            usageAccessGranted = usage,
            notificationsGranted = NotificationManagerCompat.from(context).areNotificationsEnabled(),
            batteryUnrestricted = power.isIgnoringBatteryOptimizations(context.packageName),
            restrictedSettingsLikelyBlocked =
                likelyBlocked(Build.VERSION.SDK_INT, installer(), overlay, usage),
        )
    }

    private fun installer(): String? =
        if (Build.VERSION.SDK_INT >= 30) {
            context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getInstallerPackageName(context.packageName)
        }

    companion object {
        private const val PLAY_STORE = "com.android.vending"

        /**
         * Heuristic only. Android 13 introduced "restricted settings" for sideloaded apps, but for accessibility and
         * notification-listener access. Android 15's CDD extends it to Display over other apps and Usage access
         * (androidauthority.com/android-15-restricted-settings-sideloading-3481098), which are the two we need.
         * So: API >= 35, not installed from Play, and at least one of those two still ungranted. The system shows
         * "Allow restricted settings" in App info only after the user has tapped a blocked toggle once.
         */
        fun likelyBlocked(sdk: Int, installer: String?, overlay: Boolean, usage: Boolean): Boolean =
            sdk >= 35 && installer != PLAY_STORE && !(overlay && usage)
    }
}
