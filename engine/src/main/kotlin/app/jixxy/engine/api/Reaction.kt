package app.jixxy.engine.api

/**
 * What the brain wants the platform to do.
 * @property kind how to present it
 * @property lineId id of the chosen line (null for ANIMATE_ONLY / SILENT_JUDGE)
 * @property text slot-filled text, ready to display
 * @property mood mood to show after this reaction
 * @property state sprite animation to play
 * @property bubbleMs how long the speech bubble stays
 * @property pause present iff [kind] == [ReactionKindOut.PAUSE_PROMPT]
 */
data class Reaction(
    val kind: ReactionKindOut,
    val lineId: String?,
    val text: String?,
    val mood: Mood,
    val state: CharacterState,
    val bubbleMs: Long,
    val pause: PauseSpec?
)

/**
 * Data for a Pause card.
 * @property stayEnabledAfterSec Stay button becomes tappable after this delay; Leave is always instant.
 * @property grantMinutes minutes of grace granted if the user taps Stay
 */
data class PauseSpec(
    val pkg: String,
    val appLabel: String,
    val minutesToday: Int,
    val stayEnabledAfterSec: Int = 3,
    val grantMinutes: Int = 5
)
