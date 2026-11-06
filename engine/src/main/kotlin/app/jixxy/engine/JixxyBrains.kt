package app.jixxy.engine

import app.jixxy.engine.api.BrainState
import app.jixxy.engine.api.UsageEvent
import app.jixxy.engine.api.UserSettings
import app.jixxy.engine.lines.LineBank
import app.jixxy.engine.rules.DefaultJixxyBrain
import app.jixxy.engine.selector.BankLinePicker
import java.time.ZoneId
import kotlin.random.Random

/** Entry point for the platform: builds a ready-to-use brain. */
object JixxyBrains {
    /**
     * Creates the real brain.
     *
     * @param lineBank lines parsed with `LineBankLoader.fromTexts(...)` from the app's assets
     * @param initialState result of `StateStore.load()`
     * @param settings read on every signal, so changes apply immediately
     * @param appLabel package name to human label (e.g. "com.instagram.android" to "Instagram")
     * @param zone user's time zone, for quiet hours, late-night and daily caps
     * @param recentEvents `StateStore.eventsBetween(now - 30 days, now)`; restores Pause state, counts and the
     *   no-repeat ring after a restart and feeds callbacks. Empty on first run.
     * @param random inject a seeded [Random] in tests
     *
     * Time comes only from `Signal.ts`, so the platform should stamp signals with its own clock.
     * After each `onSignal`, call `currentState()` and `drainEvents()` and persist them.
     */
    fun create(
        lineBank: LineBank,
        initialState: BrainState,
        settings: () -> UserSettings,
        appLabel: (String) -> String,
        zone: ZoneId = ZoneId.systemDefault(),
        random: Random = Random.Default,
        recentEvents: List<UsageEvent> = emptyList()
    ): DefaultJixxyBrain =
        DefaultJixxyBrain(initialState, settings, BankLinePicker(lineBank, random), appLabel, zone, recentEvents)
}
