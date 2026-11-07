package app.jixxy.ui.settings

import app.jixxy.engine.api.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsLogicTest {
    @Test fun stayDelayIsClampedToZeroTen() {
        assertEquals(0, SettingsLogic.clampStayDelay(-4))
        assertEquals(10, SettingsLogic.clampStayDelay(99))
        assertEquals(3, SettingsLogic.clampStayDelay(3))
    }

    @Test fun minutesWrapIntoOneDay() {
        assertEquals(60, SettingsLogic.normalizeMinutes(1500))
        assertEquals(1380, SettingsLogic.normalizeMinutes(-60))
        assertEquals("01:05", SettingsLogic.formatMinutes(65))
        assertEquals("07:00", SettingsLogic.formatMinutes(420))
    }

    @Test fun targetsHaveNoCapAndMutedAppsAreDropped() {
        var s = UserSettings(mutedApps = setOf("d"))
        listOf("a", "b", "c", "d", "e").forEach { s = SettingsLogic.toggleTarget(s, it) }
        assertEquals(setOf("a", "b", "c", "d", "e"), s.targetApps)
        assertEquals(emptySet<String>(), s.mutedApps)
    }

    @Test fun mutingATargetRemovesItFromTargets() {
        val s = SettingsLogic.toggleMuted(UserSettings(targetApps = setOf("a", "b")), "a")
        assertEquals(setOf("b"), s.targetApps)
        assertEquals(setOf("a"), s.mutedApps)
        assertEquals(emptySet<String>(), SettingsLogic.toggleMuted(s, "a").mutedApps)
    }

    @Test fun addingATargetUnmutesIt() {
        val s = SettingsLogic.toggleTarget(UserSettings(mutedApps = setOf("a")), "a")
        assertEquals(setOf("a"), s.targetApps)
        assertEquals(emptySet<String>(), s.mutedApps)
    }

    @Test fun quietHoursAreNormalized() {
        val s = SettingsLogic.withQuietHours(UserSettings(), 1500, -30)
        assertEquals(60, s.quietStartMin)
        assertEquals(1410, s.quietEndMin)
    }

    @Test fun resetGivesFactorySettings() = assertEquals(UserSettings(), SettingsLogic.reset())

    @Test fun languageAcceptsOnlyKnownCodes() {
        val base = UserSettings()
        assertEquals("en", base.language)
        assertEquals("hinglish", SettingsLogic.withLanguage(base, "hinglish").language)
        assertEquals("en", SettingsLogic.withLanguage(base.copy(language = "hinglish"), "klingon").language)
    }
}
