package app.jixxy.platform

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

enum class OemBrand { XIAOMI, OPPO, VIVO, SAMSUNG, OTHER }

/**
 * Best-effort deep links to each OEM's autostart / background-battery screens. The component names come from
 * community knowledge, NOT official docs, change between ROM versions and were not tested on any device.
 * Every brand list ends with the generic battery-optimisation page and the app-details page, which always exist.
 */
object OemIntents {
    /** Xiaomi covers Redmi and POCO; OPPO covers Realme and OnePlus; VIVO covers iQOO. */
    fun detect(manufacturer: String = Build.MANUFACTURER): OemBrand {
        val m = manufacturer.lowercase()
        return when {
            "xiaomi" in m || "redmi" in m || "poco" in m -> OemBrand.XIAOMI
            "oppo" in m || "realme" in m || "oneplus" in m -> OemBrand.OPPO
            "vivo" in m || "iqoo" in m -> OemBrand.VIVO
            "samsung" in m -> OemBrand.SAMSUNG
            else -> OemBrand.OTHER
        }
    }

    /** Ordered candidates: try each until one starts (see [startFirstAvailable]). */
    fun keepAliveIntents(context: Context, brand: OemBrand = detect()): List<Intent> {
        fun component(pkg: String, cls: String) = Intent().setComponent(ComponentName(pkg, cls))
        val oem = when (brand) {
            OemBrand.XIAOMI -> listOf(
                component("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"),
            )
            OemBrand.OPPO -> listOf(
                component("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"),
                component("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"),
                component("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"),
            )
            OemBrand.VIVO -> listOf(
                component("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"),
                component("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"),
            )
            OemBrand.SAMSUNG -> listOf(
                component("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity"),
            )
            OemBrand.OTHER -> emptyList()
        }
        return oem + listOf(
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS),
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")),
        )
    }

    /** Starts the first intent the device accepts. Returns false only if every candidate failed. */
    fun startFirstAvailable(context: Context, intents: List<Intent>): Boolean {
        for (intent in intents) {
            try {
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return true
            } catch (e: ActivityNotFoundException) {
                continue
            } catch (e: SecurityException) {
                continue
            }
        }
        return false
    }
}
