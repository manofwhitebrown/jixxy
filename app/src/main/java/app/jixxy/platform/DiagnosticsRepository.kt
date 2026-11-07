package app.jixxy.platform

import android.content.Context
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import app.jixxy.data.DiagnosticsLog
import app.jixxy.data.DiagnosticsReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant

/**
 * Local-only bug report (T-P3-4). UI adapts this to its `DiagnosticsPort`.
 *
 * WHAT THE REPORT CONTAINS (GROWTH: copy into PRIVACY.md): app version and versionCode, Android version, phone make/model,
 * the current permission on/off states, how many apps the user chose to be paused in (a number, no names), and a ring-buffer of events: service start/stop, permission changes, Pause shown/chosen/cancelled (kind only),
 * and crash stack traces. It never contains: line text, any app's name or package (redacted to `<app>`, chosen apps included), screen content, messages,
 * contacts, location or anything typed. It is stored in app-private storage and leaves the phone only when the user copies it.
 */
class DiagnosticsRepository(
    private val context: Context,
    private val chosenApps: () -> Set<String>,
    private val permissions: () -> PermissionsState,
    private val log: DiagnosticsLog = DiagnosticsLog(File(context.filesDir, "diagnostics.log")),
) {
    /** Safe from any thread; never throws. */
    fun record(kind: String, detail: String = "") = log.append(kind, detail)  // no allow-list: even chosen apps' package names are hidden

    suspend fun copyableReport(): String = withContext(Dispatchers.IO) {
        val p = permissions()
        val info = runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull()
        val header = listOf(
            "Jixxy debug info. It stays on this phone until you paste it somewhere yourself.",
            "App: ${info?.versionName ?: "?"} (code ${info?.let { PackageInfoCompat.getLongVersionCode(it) } ?: "?"})",
            "Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            "Phone: ${Build.MANUFACTURER} ${Build.MODEL}",
            "Made: ${Instant.now()}",
            "Permissions: overlay=${p.overlayGranted} usageAccess=${p.usageAccessGranted} " +
                "notifications=${p.notificationsGranted} batteryUnrestricted=${p.batteryUnrestricted}",
            "Apps chosen for Pause: ${chosenApps().size}",
        )
        DiagnosticsReport.build(header, log.readAll())
    }

    suspend fun clear() = withContext(Dispatchers.IO) { log.clear() }

    /** Writes uncaught exceptions to the log, then lets the previous handler (the system's) run. Call once. */
    fun installCrashHandler() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            runCatching { record("CRASH", "thread=${thread.name}\n" + error.stackTraceToString()) }
            previous?.uncaughtException(thread, error)
        }
    }
}
