package app.jixxy.ui.settings

import app.jixxy.engine.api.NagLevel
import app.jixxy.engine.api.UserSettings
import app.jixxy.ui.onboarding.OnboardingFlow

/** Pure rules behind the Settings screen. No Android imports; unit-tested. */
object SettingsLogic {
    const val STAY_DELAY_MIN_SEC = 0
    const val STAY_DELAY_MAX_SEC = 10
    private const val MINUTES_PER_DAY = 24 * 60

    /** Language codes stored in [UserSettings.language] (D-016: English default, Hinglish opt-in beta). */
    const val LANGUAGE_EN = "en"
    const val LANGUAGE_HINGLISH = "hinglish"
    val SUPPORTED_LANGUAGES = listOf(LANGUAGE_EN, LANGUAGE_HINGLISH)

    /** Stay delay is clamped to 0..10 s. Leave is always instant, so this never delays an exit. */
    fun clampStayDelay(sec: Int): Int = sec.coerceIn(STAY_DELAY_MIN_SEC, STAY_DELAY_MAX_SEC)

    /** Minutes after local midnight, wrapped into one day (e.g. 1500 -> 60). */
    fun normalizeMinutes(min: Int): Int = ((min % MINUTES_PER_DAY) + MINUTES_PER_DAY) % MINUTES_PER_DAY

    /** 24-hour "HH:mm" for a minutes-after-midnight value. The screen may use the device's own format instead. */
    fun formatMinutes(min: Int): String {
        val m = normalizeMinutes(min)
        return "%02d:%02d".format(m / 60, m % 60)
    }

    /** Adds/removes [pkg] from the pause targets (no cap, D-020). A target is never also muted. */
    fun toggleTarget(s: UserSettings, pkg: String): UserSettings {
        val targets = OnboardingFlow.toggleApp(s.targetApps, pkg)
        return s.copy(targetApps = targets, mutedApps = s.mutedApps - targets)
    }

    /** Adds/removes [pkg] from the muted apps. Muting an app removes it from the pause targets. */
    fun toggleMuted(s: UserSettings, pkg: String): UserSettings {
        val muted = if (pkg in s.mutedApps) s.mutedApps - pkg else s.mutedApps + pkg
        return s.copy(mutedApps = muted, targetApps = s.targetApps - muted)
    }

    fun withQuietHours(s: UserSettings, startMin: Int, endMin: Int): UserSettings =
        s.copy(quietStartMin = normalizeMinutes(startMin), quietEndMin = normalizeMinutes(endMin))

    fun withStayDelay(s: UserSettings, sec: Int): UserSettings = s.copy(stayDelaySec = clampStayDelay(sec))

    /** Sets the line language; an unknown code falls back to English so a bad value can never silence Jixxy. */
    fun withLanguage(s: UserSettings, code: String): UserSettings =
        s.copy(language = if (code in SUPPORTED_LANGUAGES) code else LANGUAGE_EN)

    fun withNagLevel(s: UserSettings, level: NagLevel): UserSettings = s.copy(nagLevel = level)

    /** One-tap "turn him down" from Home: always lands on Calm. */
    fun turnedDown(s: UserSettings): UserSettings = s.copy(nagLevel = NagLevel.CALM)

    fun withSounds(s: UserSettings, on: Boolean): UserSettings = s.copy(soundsEnabled = on)

    /** Factory settings for "Reset Jixxy". The caller must also clear logged events. */
    fun reset(): UserSettings = UserSettings()
}
