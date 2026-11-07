package app.jixxy.platform

import android.app.Application
import app.jixxy.data.DataStoreSettingsRepository
import app.jixxy.data.SqliteStateStore
import app.jixxy.platform.signals.SignalBus

/** Process-wide holder for the on-device singletons the service and UI share. */
class JixxyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        diagnostics.installCrashHandler()
        diagnostics.record("APP_START")
    }

    /** Mirrors UserSettings.blindMode; read by the store so logging stops the instant blind mode is on. */
    @Volatile var blindMode: Boolean = false

    val bus = SignalBus()
    val settings by lazy { DataStoreSettingsRepository(this) }
    val permissions by lazy { PermissionsRepository(this) }
    /** UI-facing view of [permissions]. */
    val permissionsSource by lazy { PermissionsSourceAdapter(permissions) }
    /** Chosen Pause apps, mirrored from settings so crash/diagnostic writes can keep exactly those package names. */
    @Volatile var chosenApps: Set<String> = emptySet()
    val diagnostics by lazy { DiagnosticsRepository(this, { chosenApps }, { permissions.state.value }) }
    val weekly by lazy { WeeklyTrigger(this) }
    val stateStore by lazy { SqliteStateStore(this, isBlind = { blindMode }) }
}
