package app.jixxy.platform

import android.app.Application
import app.jixxy.data.DataStoreSettingsRepository
import app.jixxy.data.SqliteStateStore
import app.jixxy.platform.signals.SignalBus

/** Process-wide holder for the on-device singletons the service and UI share. */
class JixxyApp : Application() {
    /** Mirrors UserSettings.blindMode; read by the store so logging stops the instant blind mode is on. */
    @Volatile var blindMode: Boolean = false

    val bus = SignalBus()
    val settings by lazy { DataStoreSettingsRepository(this) }
    val stateStore by lazy { SqliteStateStore(this, isBlind = { blindMode }) }
}
