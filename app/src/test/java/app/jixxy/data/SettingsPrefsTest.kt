package app.jixxy.data

import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.emptyPreferences
import app.jixxy.engine.api.NagLevel
import app.jixxy.engine.api.RoastLevel
import app.jixxy.engine.api.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsPrefsTest {
    @Test fun oldInstallWithNoKeysGetsDefaults() {
        assertEquals(UserSettings(), SettingsPrefs.read(emptyPreferences()))
    }

    @Test fun everyFieldRoundTrips() {
        val s = UserSettings(
            roast = RoastLevel.values().last(), targetApps = (1..40).map { "com.example.app$it" }.toSet(),
            mutedApps = setOf("com.example.muted"), quietStartMin = 30, quietEndMin = 400, stayDelaySec = 5,
            language = "hinglish", blindMode = true, napUntilTs = 123456789L, pausesEnabled = false,
            nagLevel = NagLevel.values().first(), soundsEnabled = true,
        )
        val prefs = mutablePreferencesOf()
        SettingsPrefs.write(prefs, s)
        assertEquals(s, SettingsPrefs.read(prefs))
    }

    @Test fun noCapOnChosenApps() {
        val many = (1..500).map { "pkg.number$it.x" }.toSet()
        val prefs = mutablePreferencesOf()
        SettingsPrefs.write(prefs, UserSettings(targetApps = many))
        assertEquals(500, SettingsPrefs.read(prefs).targetApps.size)
    }

    @Test fun clearingNapRemovesKey() {
        val prefs = mutablePreferencesOf()
        SettingsPrefs.write(prefs, UserSettings(napUntilTs = 5L))
        SettingsPrefs.write(prefs, UserSettings(napUntilTs = null))
        assertNull(SettingsPrefs.read(prefs).napUntilTs)
    }

    @Test fun unknownEnumNameFallsBackToDefault() {
        val prefs = mutablePreferencesOf()
        SettingsPrefs.write(prefs, UserSettings())
        prefs[androidx.datastore.preferences.core.stringPreferencesKey("nag_level")] = "FUTURE_LEVEL"
        assertEquals(UserSettings().nagLevel, SettingsPrefs.read(prefs).nagLevel)
    }
}
