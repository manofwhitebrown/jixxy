package app.jixxy.engine.api

import app.jixxy.engine.lines.JsonException
import app.jixxy.engine.lines.MiniJson

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
    val unpromptedToday: Int = 0,
    /** Pause runtime state (D-017). Additive and default-valued: saves made before it existed load as the default. */
    val pause: PauseRuntime = PauseRuntime()
)

/**
 * The part of Pause that must survive process death and reboot (D-017): Stay grants, the grace timer, the single
 * gentle second prompt, today's prompt counts and the Leave streak. Pure data; the brain fills it, the platform only
 * stores the [encode]d string and hands it back through [decode].
 *
 * @property savedTs time of the last signal the brain handled when this was captured (0 = never captured);
 *   usage events at or before it are already reflected here and are not replayed over it.
 * @property graceUntil per app: no new Pause card before this time (after Stay or Leave)
 * @property stayExpiry per app: when the Stay grant runs out and the one second prompt becomes due
 * @property secondPromptDone apps whose one second prompt was already shown for their current Stay grant
 * @property promptsToday per app: Pause prompts shown on [promptsDayEpoch]; drives the humour tier
 * @property promptsDayEpoch local epoch-day [promptsToday] belongs to
 * @property leaveStreak consecutive Leaves since the last Stay or earned praise
 */
data class PauseRuntime(
    val savedTs: Long = 0L,
    val graceUntil: Map<String, Long> = emptyMap(),
    val stayExpiry: Map<String, Long> = emptyMap(),
    val secondPromptDone: Set<String> = emptySet(),
    val promptsToday: Map<String, Int> = emptyMap(),
    val promptsDayEpoch: Long? = null,
    val leaveStreak: Int = 0
) {
    /** Compact JSON text for one TEXT column. Format version 1; unknown future fields are ignored by [decode]. */
    fun encode(): String = buildString {
        append("{\"v\":1,\"savedTs\":").append(savedTs)
        append(",\"graceUntil\":").append(longMap(graceUntil))
        append(",\"stayExpiry\":").append(longMap(stayExpiry))
        append(",\"secondPromptDone\":[").append(secondPromptDone.sorted().joinToString(",") { quote(it) }).append(']')
        append(",\"promptsToday\":").append(longMap(promptsToday.mapValues { it.value.toLong() }))
        append(",\"promptsDayEpoch\":").append(promptsDayEpoch ?: "null")
        append(",\"leaveStreak\":").append(leaveStreak)
        append('}')
    }

    companion object {
        /** Reads [encode]d text. Null, blank or damaged input yields the empty default; this never throws. */
        fun decode(text: String?): PauseRuntime {
            if (text.isNullOrBlank()) return PauseRuntime()
            return try {
                val root = MiniJson.parse(text) as? Map<*, *> ?: return PauseRuntime()
                PauseRuntime(
                    savedTs = (root["savedTs"] as? Double)?.toLong() ?: 0L,
                    graceUntil = readLongMap(root["graceUntil"]),
                    stayExpiry = readLongMap(root["stayExpiry"]),
                    secondPromptDone = (root["secondPromptDone"] as? List<*>)?.filterIsInstance<String>()?.toSet() ?: emptySet(),
                    promptsToday = readLongMap(root["promptsToday"]).mapValues { it.value.toInt() },
                    promptsDayEpoch = (root["promptsDayEpoch"] as? Double)?.toLong(),
                    leaveStreak = (root["leaveStreak"] as? Double)?.toInt()?.coerceAtLeast(0) ?: 0
                )
            } catch (e: JsonException) {
                PauseRuntime()
            }
        }

        private fun readLongMap(node: Any?): Map<String, Long> =
            (node as? Map<*, *>)?.entries
                ?.mapNotNull { (k, v) -> if (k is String && v is Double) k to v.toLong() else null }
                ?.toMap() ?: emptyMap()

        private fun longMap(m: Map<String, Long>): String =
            m.entries.sortedBy { it.key }.joinToString(",", "{", "}") { "${quote(it.key)}:${it.value}" }

        private fun quote(s: String): String = "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
    }
}

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
