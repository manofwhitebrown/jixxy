package app.jixxy.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.jixxy.engine.api.NagLevel
import app.jixxy.engine.api.RoastLevel
import app.jixxy.engine.api.SettingsRepository
import app.jixxy.engine.api.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "jixxy_settings")

/** [SettingsRepository] backed by Preferences DataStore. Stays on-device; absent keys fall back to [UserSettings] defaults. */
class DataStoreSettingsRepository(context: Context) : SettingsRepository {
    private val store = context.applicationContext.settingsStore

    override val settings: Flow<UserSettings> = store.data.map { it.toSettings() }

    override suspend fun update(transform: (UserSettings) -> UserSettings) {
        store.edit { prefs -> prefs.write(transform(prefs.toSettings())) }
    }

    private fun Preferences.toSettings(): UserSettings {
        val d = UserSettings()
        return UserSettings(
            roast = this[ROAST]?.let { name -> RoastLevel.values().firstOrNull { it.name == name } } ?: d.roast,
            targetApps = this[TARGET_APPS] ?: d.targetApps,
            mutedApps = this[MUTED_APPS] ?: d.mutedApps,
            quietStartMin = this[QUIET_START] ?: d.quietStartMin,
            quietEndMin = this[QUIET_END] ?: d.quietEndMin,
            stayDelaySec = this[STAY_DELAY] ?: d.stayDelaySec,
            language = this[LANGUAGE] ?: d.language,
            blindMode = this[BLIND] ?: d.blindMode,
            napUntilTs = this[NAP_UNTIL],
            pausesEnabled = this[PAUSES] ?: d.pausesEnabled,
            nagLevel = this[NAG]?.let { name -> NagLevel.values().firstOrNull { it.name == name } } ?: d.nagLevel,
            soundsEnabled = this[SOUNDS] ?: d.soundsEnabled,
        )
    }

    private fun androidx.datastore.preferences.core.MutablePreferences.write(s: UserSettings) {
        this[ROAST] = s.roast.name
        this[TARGET_APPS] = s.targetApps
        this[MUTED_APPS] = s.mutedApps
        this[QUIET_START] = s.quietStartMin
        this[QUIET_END] = s.quietEndMin
        this[STAY_DELAY] = s.stayDelaySec
        this[LANGUAGE] = s.language
        this[BLIND] = s.blindMode
        this[PAUSES] = s.pausesEnabled
        this[NAG] = s.nagLevel.name
        this[SOUNDS] = s.soundsEnabled
        val nap = s.napUntilTs
        if (nap != null) this[NAP_UNTIL] = nap else remove(NAP_UNTIL)
    }

    private companion object {
        val ROAST = stringPreferencesKey("roast")
        val TARGET_APPS = stringSetPreferencesKey("target_apps")
        val MUTED_APPS = stringSetPreferencesKey("muted_apps")
        val QUIET_START = intPreferencesKey("quiet_start_min")
        val QUIET_END = intPreferencesKey("quiet_end_min")
        val STAY_DELAY = intPreferencesKey("stay_delay_sec")
        val LANGUAGE = stringPreferencesKey("language")
        val BLIND = booleanPreferencesKey("blind_mode")
        val NAG = stringPreferencesKey("nag_level")
        val SOUNDS = booleanPreferencesKey("sounds_enabled")
        val NAP_UNTIL = longPreferencesKey("nap_until_ts")
        val PAUSES = booleanPreferencesKey("pauses_enabled")
    }
}
