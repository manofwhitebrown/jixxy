package app.jixxy.engine.api

/** User-controlled configuration. Persisted by the app via [SettingsRepository]. */
data class UserSettings(
    val roast: RoastLevel = RoastLevel.NORMAL,
    /** Package names the user chose for Pause. Max 3 (enforced by UI and brain). */
    val targetApps: Set<String> = emptySet(),
    val mutedApps: Set<String> = emptySet(),
    /** Quiet hours start, minutes after local midnight. Default 01:00. */
    val quietStartMin: Int = 60,
    /** Quiet hours end, minutes after local midnight. Default 07:00. */
    val quietEndMin: Int = 420,
    val stayDelaySec: Int = 3,
    val language: String = "en",
    /** Stops all detection and logging. */
    val blindMode: Boolean = false,
    /** Silence until this epoch-millis time; null = awake. */
    val napUntilTs: Long? = null,
    val pausesEnabled: Boolean = true
)

/**
 * Persistent brain state. Pure data; all transitions live in the brain.
 * Bond XP never decreases from absence. There are no streaks.
 */
data class BrainState(
    /** 0..100. Each spoken line costs a little; at 0 Jixxy is "on break". */
    val energy: Int = 100,
    val mood: Mood = Mood.CHILL,
    val bondXp: Int = 0,
    val firstSeenTs: Long? = null,
    val lastUnpromptedTs: Long? = null,
    /** Local epoch-day the [unpromptedToday] counter belongs to. */
    val unpromptedDayEpoch: Long? = null,
    val unpromptedToday: Int = 0
)

/** Read-only view of the brain for UI/debug. */
data class BrainSnapshot(
    val mood: Mood,
    val energy: Int,
    val bond: Bond,
    val bondXp: Int,
    val napping: Boolean
)

/** Kinds of facts that get logged for memory, callbacks and the weekly review. */
enum class UsageEventType {
    FOREGROUND, SESSION_MINUTES, PAUSE_SHOWN, PAUSE_STAY, PAUSE_LEAVE,
    LINE_SHOWN, LINE_REACTION, CHARGER_ON, NAP, LATE_NIGHT_OPEN
}

/**
 * One logged fact. Contains no message content or personal data, only package, counts and ids.
 * @property value type-specific number (e.g. minutes), or null
 * @property lineId set for LINE_SHOWN / LINE_REACTION
 */
data class UsageEvent(
    val ts: Long,
    val type: UsageEventType,
    val pkg: String? = null,
    val value: Int? = null,
    val lineId: String? = null
)
