package app.jixxy.engine.api

/** An observation from the platform layer. All timestamps are epoch millis from the injected clock. */
sealed interface Signal {
    /** Epoch millis. */
    val ts: Long

    /** [pkg] came to the foreground. */
    data class ForegroundApp(val pkg: String, val category: AppCategory, override val ts: Long) : Signal
    /** Screen turned on/off. */
    data class ScreenState(val on: Boolean, override val ts: Long) : Signal
    /** Sent once a minute while the same app stays foreground; [minutes] = session length so far. */
    data class SessionTick(val pkg: String, val minutes: Int, override val ts: Long) : Signal
    /** Charger plugged/unplugged. */
    data class Charger(val connected: Boolean, val batteryPct: Int, override val ts: Long) : Signal
    /** Battery crossed the low threshold. */
    data class BatteryLow(val pct: Int, override val ts: Long) : Signal
    /** Music started/stopped playing. */
    data class MusicActive(val active: Boolean, override val ts: Long) : Signal
    /** Phone call started/ended. Jixxy is silent during calls. */
    data class CallState(val inCall: Boolean, override val ts: Long) : Signal
    /** The sprite was tapped; [consecutive] taps in a short window. */
    data class SpriteTapped(val consecutive: Int, override val ts: Long) : Signal
    /** The sprite was dropped after a drag. */
    data class SpriteDropped(override val ts: Long) : Signal
    /** Emoji reaction to a shown line. */
    data class UserReaction(val lineId: String, val kind: ReactionKind, override val ts: Long) : Signal
    /** The user answered a Pause card. */
    data class PauseChoice(val pkg: String, val choice: PauseChoiceKind, override val ts: Long) : Signal
    /** The user asked Jixxy to nap for [minutes]. */
    data class NapRequested(val minutes: Int, override val ts: Long) : Signal
    /** The user woke Jixxy early. */
    data class WakeRequested(override val ts: Long) : Signal
    /** Every minute, drives time-of-day triggers. */
    data class Heartbeat(override val ts: Long) : Signal
}
