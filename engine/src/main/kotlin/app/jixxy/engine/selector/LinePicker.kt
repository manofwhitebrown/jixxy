package app.jixxy.engine.selector

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.Bond
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.ReactionKind
import app.jixxy.engine.api.RoastLevel
import app.jixxy.engine.api.Trigger

/**
 * Everything the selector needs to choose a line.
 * @property slots values for `{app} {minutes} {count} {time} {battery}`; a line whose slot is missing here must be dropped
 * @property tier Pause escalation: 0 for the first prompt of the day for that app, +1 each time (funnier, never meaner)
 * @property followUp true for the single gentle prompt after a granted Stay expires
 * @property lateNight true inside the late-night window (softer register)
 * @property onBreak true when a poke arrives at energy 0: pick a POKE line tagged `on_break` ("I'm on break")
 * @property requiredTag only lines carrying this tag are eligible (e.g. `dropped` for a drop reaction)
 * @property escalation true when the user is poking repeatedly: prefer POKE lines tagged `escalation`
 * @property calm true at the Calm nag level: lines tagged `nag_only` are not eligible (D-020); Pause mechanics fall back to them only if nothing else fits
 */
data class LineQuery(
    val trigger: Trigger,
    val category: AppCategory?,
    val mood: Mood,
    val roast: RoastLevel,
    val bond: Bond,
    val language: String,
    val slots: Map<String, String>,
    val tier: Int = 0,
    val followUp: Boolean = false,
    val lateNight: Boolean = false,
    val onBreak: Boolean = false,
    val requiredTag: String? = null,
    val escalation: Boolean = false,
    val calm: Boolean = false
)

/** A chosen line with its slots already filled. */
data class PickedLine(val id: String, val text: String)

/** Chooses lines for the brain. The real implementation arrives with T-ENGINE-3; tests use a fake. */
interface LinePicker {
    /** Returns a line, or null if nothing truthful and eligible exists (the brain then stays quiet or animates only). */
    fun pick(query: LineQuery): PickedLine?
    /** The user reacted to a shown line; implementations may learn from it. */
    fun onReaction(lineId: String, kind: ReactionKind) {}

    /** Seeds the no-repeat memory with ids shown before a restart, oldest first. */
    fun restoreRecent(ids: List<String>) {}
}
