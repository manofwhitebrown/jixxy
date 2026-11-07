package app.jixxy.engine.selector

import app.jixxy.engine.api.BrainConfig
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.Trigger
import app.jixxy.engine.api.ReactionKind
import app.jixxy.engine.lines.Line
import app.jixxy.engine.lines.LineBank
import kotlin.random.Random

/**
 * Picks lines from a [LineBank].
 *
 * Language (D-016): the query's language is used when the bank has any line of it for the trigger; only when it has
 * none does the pick fall back to English. A language that has lines but none eligible right now stays quiet instead
 * of mixing in English. Each language keeps its own no-repeat ring.
 *
 * Eligibility (hard): trigger, roast <= user level, bond, category
 * (a line with no categories fits any app), every declared slot available in the query.
 * Tags (WRITER's vocabulary): `on_break`, `dropped`, `escalation`, `followup` lines are reserved: they are used only when
 * the query asks for them. `tierN` (Pause prompts), `soft` (late night) are preferences. A `requiredTag` is a hard filter.
 * Preference (soft, relaxed in order): tags above, then mood match.
 * Repeats: a ring per language of the last [BrainConfig.dedupeWindow] ids; for small pools the block
 * window shrinks to half the pool so a 10-line trigger can still speak.
 * Reactions: 😂 lines gain weight, 😴 lines lose it (never to zero by itself; the ring still applies).
 */
class BankLinePicker(
    private val bank: LineBank,
    private val random: Random
) : LinePicker {

    private val rings = mutableMapOf<String, ArrayDeque<String>>()
    private val weights = mutableMapOf<String, Int>()
    private val byId: Map<String, Line> = bank.lines.associateBy { it.id }
    private val reservedTags = setOf("on_break", "dropped", "escalation", "followup", "after_leave")

    override fun pick(query: LineQuery): PickedLine? {
        val lang = languageFor(query)
        val ring = rings.getOrPut(lang) { ArrayDeque() }
        var pool = eligible(query, lang)
        // Calm hides `nag_only` lines; only Pause mechanics may fall back to them rather than go silent (WRITER, D-020).
        if (pool.isEmpty() && query.calm && query.trigger in PAUSE_MECHANICS) pool = eligible(query.copy(calm = false), lang)
        if (pool.isEmpty()) return null

        query.requiredTag?.let { tag -> pool = pool.filter { tag in it.tags }; if (pool.isEmpty()) return null }
        pool = withoutReserved(pool, query)
        if (pool.isEmpty()) return null
        if (query.onBreak) pool = prefer(pool, "on_break")
        if (query.followUp) pool = prefer(pool, "followup")
        if (query.escalation) pool = prefer(pool, "escalation")
        if (query.trigger == Trigger.PAUSE_PROMPT && !query.followUp) pool = preferTier(pool, minOf(query.tier, 2))
        if (query.afterLeave) pool = prefer(pool, "after_leave")
        if (query.lateNight || query.mood == Mood.SOFT) pool = prefer(pool, "soft")
        val moodMatched = pool.filter { query.mood in it.moods }
        if (moodMatched.isNotEmpty()) pool = moodMatched

        val window = minOf(BrainConfig.dedupeWindow, maxOf(1, pool.size / 2))
        val blocked = ring.takeLast(window).toSet()
        val fresh = pool.filter { it.id !in blocked }
        if (fresh.isEmpty()) return null

        val line = weighted(fresh)
        remember(line)
        return PickedLine(line.id, fill(line.text, query.slots))
    }

    override fun onReaction(lineId: String, kind: ReactionKind) {
        if (lineId !in byId) return
        val w = weights[lineId] ?: 2
        weights[lineId] = when (kind) {
            ReactionKind.LAUGH -> minOf(6, w + 2)
            ReactionKind.SLEEPY -> maxOf(1, w - 1)
        }
    }

    override fun restoreRecent(ids: List<String>) {
        ids.mapNotNull { byId[it] }.forEach { remember(it) }
    }

    /** Ids recently shown in [lang], oldest first (for tests and debugging). */
    fun recentIds(lang: String = "en"): List<String> = rings[lang]?.toList().orEmpty()

    /** The language to pick from: the query's own if the bank has lines of it for the trigger, else English. */
    private fun languageFor(q: LineQuery): String =
        if (bank.forTrigger(q.trigger).any { it.lang == q.language }) q.language else "en"

    private fun eligible(q: LineQuery, lang: String): List<Line> =
        bank.forTrigger(q.trigger).filter { l ->
            l.lang == lang &&
                l.roast <= q.roast.n &&
                l.bondMin <= q.bond.ordinal &&
                (l.categories.isEmpty() || (q.category != null && q.category in l.categories)) &&
                l.slots.all { it in q.slots } &&
                !(q.calm && NAG_ONLY in l.tags)
        }

    /** Drops reserved-tag lines the query did not ask for. */
    private fun withoutReserved(pool: List<Line>, q: LineQuery): List<Line> {
        val wanted = buildSet {
            if (q.onBreak) add("on_break")
            if (q.followUp) add("followup")
            if (q.escalation) add("escalation")
            if (q.afterLeave) add("after_leave")
            q.requiredTag?.let { add(it) }
        }
        return pool.filter { l -> reservedTags.none { it in l.tags && it !in wanted } }
    }

    /** Narrows to lines carrying [tag] if there are any, else leaves the pool alone. */
    private fun prefer(pool: List<Line>, tag: String): List<Line> = pool.filter { tag in it.tags }.ifEmpty { pool }

    /**
     * Pause prompts climb `tier0` -> `tier2` with the day's prompt count. Prefers lines of exactly tier [want]. When none
     * fit (tier0 lines only exist for some categories), falls back to untagged or lower-tier lines before it would ever
     * serve a HIGHER tier than the count allows: the first prompt of the day must not read like the fifth. Only when
     * nothing lower fits does the whole pool stay in play, so he still speaks.
     */
    private fun preferTier(pool: List<Line>, want: Int): List<Line> {
        val exact = pool.filter { "tier$want" in it.tags }
        if (exact.isNotEmpty()) return exact
        val notHigher = pool.filter { l -> ((want + 1)..2).none { "tier$it" in l.tags } }
        return notHigher.ifEmpty { pool }
    }

    private fun weighted(pool: List<Line>): Line {
        val total = pool.sumOf { weights[it.id] ?: 2 }
        var r = random.nextInt(total)
        for (l in pool) {
            r -= weights[l.id] ?: 2
            if (r < 0) return l
        }
        return pool.last()
    }

    private fun remember(line: Line) {
        val id = line.id
        val ring = rings.getOrPut(line.lang) { ArrayDeque() }
        ring.remove(id)
        ring.addLast(id)
        while (ring.size > BrainConfig.dedupeWindow) ring.removeFirst()
    }

    private fun fill(text: String, slots: Map<String, String>): String =
        slots.entries.fold(text) { acc, (k, v) -> acc.replace("{$k}", v) }

    private companion object {
        const val NAG_ONLY = "nag_only"
        val PAUSE_MECHANICS = setOf(Trigger.PAUSE_PROMPT, Trigger.PAUSE_STAY, Trigger.PAUSE_LEAVE, Trigger.IMPRESSED)
    }
}
