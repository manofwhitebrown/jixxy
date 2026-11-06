package app.jixxy.engine.lines

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.BrainConfig
import app.jixxy.engine.api.Trigger

/** One validator finding. [error] findings fail the build; the rest are advice. */
data class Problem(val lineId: String, val message: String, val error: Boolean = true)

/**
 * Checks a parsed bank against the content rules (bible section 10, CONTRACTS line schema):
 * unique ids, text length, declared and allowed slots, slots the engine can actually fill for that trigger,
 * roast 1..3, bond 0..3, banned topics and phrases, affectionate-insult gating, PROTECTED never targeted.
 */
object LineBankValidator {
    val allowedSlots = setOf("app", "minutes", "count", "time", "battery")

    /** Affectionate insults permitted only at roast >= 2 and bondMin >= 1. */
    val affectionateInsults = setOf("idiot", "gremlin", "dummy", "goblin", "menace", "muppet")

    /** Banned word stems and phrases (matched case-insensitively on word boundaries). */
    val bannedPhrases = listOf(
        // self-harm and death idioms
        "kill me", "kill myself", "i'm dead", "im dead", "dying", "end it", "suicide", "die", "death",
        // mental health and diagnoses
        "depressed", "depression", "anxiety", "anxious", "therapy", "therapist", "diagnos", "addict", "mental",
        // body, intelligence, worth
        "fat", "ugly", "stupid", "dumb", "loser", "worthless", "pathetic", "trash",
        // guilt
        "wasting your life", "waste of your life", "disappointed in you", "you always", "you never",
        // profanity (short list; extend as needed)
        "damn", "hell", "crap", "shit", "fuck", "ass", "bitch", "bastard",
        // sensitive topics
        "religion", "god", "politic", "election", "sex", "porn"
    )

    /** Slots the brain can fill when [trigger] fires (time is always available). */
    fun slotsAvailable(trigger: Trigger): Set<String> = when (trigger) {
        Trigger.APP_OPEN, Trigger.LATE_NIGHT -> setOf("app", "time")
        Trigger.SESSION_LONG -> setOf("app", "minutes", "time")
        Trigger.PAUSE_PROMPT -> setOf("app", "minutes", "count", "time")
        Trigger.PAUSE_STAY, Trigger.PAUSE_LEAVE -> setOf("app", "minutes", "count", "time")
        Trigger.IMPRESSED -> setOf("app", "count", "time")
        Trigger.CHARGER_ON, Trigger.BATTERY_LOW -> setOf("battery", "time")
        Trigger.CALLBACK, Trigger.WEEKLY, Trigger.ALMOST_CARING -> allowedSlots
        Trigger.WELCOME, Trigger.NAP, Trigger.WAKE, Trigger.POKE, Trigger.MUSIC -> setOf("time")
    }

    fun validate(lines: List<Line>): List<Problem> {
        val out = mutableListOf<Problem>()
        lines.groupBy { it.id }.filter { it.value.size > 1 }.forEach { (id, v) ->
            out += Problem(id, "duplicate id (${v.size} lines)")
        }
        lines.forEach { out += check(it) }
        return out
    }

    private fun check(l: Line): List<Problem> {
        val out = mutableListOf<Problem>()
        fun err(m: String) { out += Problem(l.id, m) }
        if (l.id.isBlank()) err("blank id")
        if (l.text.isBlank()) err("empty text")
        if (l.text.length > BrainConfig.maxLineChars) err("text is ${l.text.length} chars (max ${BrainConfig.maxLineChars})")
        val used = LineBankLoader.placeholders(l.text).toSet()
        if ((used - l.slots).isNotEmpty()) err("uses undeclared slots ${(used - l.slots).sorted()}")
        if ((l.slots - used).isNotEmpty()) out += Problem(l.id, "declares unused slots ${(l.slots - used).sorted()}", error = false)
        if ((l.slots - allowedSlots).isNotEmpty()) err("disallowed slots ${(l.slots - allowedSlots).sorted()}")
        val unfillable = used - slotsAvailable(l.trigger)
        if (unfillable.isNotEmpty()) err("${l.trigger} never provides ${unfillable.sorted()}, so this line can never be shown")
        if (l.roast !in 1..3) err("roast ${l.roast} outside 1..3")
        if (l.bondMin !in 0..3) err("bondMin ${l.bondMin} outside 0..3")
        if (l.moods.isEmpty()) out += Problem(l.id, "no moods listed (matches any mood only as a fallback)", error = false)
        if (AppCategory.PROTECTED in l.categories) err("PROTECTED apps must never be targeted")
        if (l.almostCaring && l.trigger != Trigger.ALMOST_CARING) out += Problem(l.id, "almostCaring flag on a ${l.trigger} line", error = false)

        val lower = l.text.lowercase().replace('\u2019', '\'')
        bannedPhrases.filter { containsWord(lower, it) }.forEach { err("banned word or phrase \"$it\"") }
        val insults = affectionateInsults.filter { containsWord(lower, it) }
        if (insults.isNotEmpty() && (l.roast < 2 || l.bondMin < 1)) {
            err("affectionate insult ${insults.sorted()} needs roast >= 2 and bondMin >= 1")
        }
        return out
    }

    private val stems = setOf("diagnos", "politic")

    /** True if [needle] occurs as a whole word or phrase ("die" does not match "diet"); stems also match longer words. */
    private fun containsWord(haystack: String, needle: String): Boolean {
        val tail = if (needle in stems) "\\w*" else "\\b"
        return Regex("\\b" + Regex.escape(needle) + tail).containsMatchIn(haystack)
    }

    /** Lines per trigger and category, for coverage reports. Key "ANY" is a line with no category. */
    fun coverage(lines: List<Line>): Map<Trigger, Map<String, Int>> =
        lines.groupBy { it.trigger }.mapValues { (_, v) ->
            v.flatMap { l -> if (l.categories.isEmpty()) listOf("ANY") else l.categories.map { it.name } }
                .groupingBy { it }.eachCount().toSortedMap()
        }
}
