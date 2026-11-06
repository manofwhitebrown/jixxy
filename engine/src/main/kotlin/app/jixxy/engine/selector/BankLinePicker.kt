package app.jixxy.engine.selector

import app.jixxy.engine.api.BrainConfig
import app.jixxy.engine.api.ReactionKind
import app.jixxy.engine.lines.Line
import app.jixxy.engine.lines.LineBank
import kotlin.random.Random

/**
 * Picks lines from a [LineBank].
 *
 * Eligibility (hard): trigger, language (falls back to "en"), roast <= user level, bond, category
 * (a line with no categories fits any app), every declared slot available in the query.
 * Preference (soft, relaxed in order): mood match, then `on_break` / `follow_up` tags.
 * Repeats: a global ring of the last [BrainConfig.dedupeWindow] ids; for small pools the block
 * window shrinks to half the pool so a 10-line trigger can still speak.
 * Reactions: 😂 lines gain weight, 😴 lines lose it (never to zero by itself; the ring still applies).
 */
class BankLinePicker(
    private val bank: LineBank,
    private val random: Random
) : LinePicker {

    private val ring = ArrayDeque<String>()
    private val weights = mutableMapOf<String, Int>()
    private val byId: Map<String, Line> = bank.lines.associateBy { it.id }

    override fun pick(query: LineQuery): PickedLine? {
        var pool = eligible(query, query.language)
        if (pool.isEmpty() && query.language != "en") pool = eligible(query, "en")
        if (pool.isEmpty()) return null

        pool = preferTag(pool, "on_break", query.onBreak, excludeWhenOff = true)
        pool = preferTag(pool, "follow_up", query.followUp, excludeWhenOff = false)
        val moodMatched = pool.filter { query.mood in it.moods }
        if (moodMatched.isNotEmpty()) pool = moodMatched

        val window = minOf(BrainConfig.dedupeWindow, maxOf(1, pool.size / 2))
        val blocked = ring.takeLast(window).toSet()
        val fresh = pool.filter { it.id !in blocked }
        if (fresh.isEmpty()) return null

        val line = weighted(fresh)
        remember(line.id)
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

    /** Ids recently shown, oldest first (for tests and debugging). */
    fun recentIds(): List<String> = ring.toList()

    private fun eligible(q: LineQuery, lang: String): List<Line> =
        bank.forTrigger(q.trigger).filter { l ->
            l.lang == lang &&
                l.roast <= q.roast.n &&
                l.bondMin <= q.bond.ordinal &&
                (l.categories.isEmpty() || (q.category != null && q.category in l.categories)) &&
                l.slots.all { it in q.slots }
        }

    private fun preferTag(pool: List<Line>, tag: String, wanted: Boolean, excludeWhenOff: Boolean): List<Line> {
        val tagged = pool.filter { tag in it.tags }
        return when {
            wanted && tagged.isNotEmpty() -> tagged
            !wanted && excludeWhenOff -> pool.filter { tag !in it.tags }.ifEmpty { pool }
            else -> pool
        }
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

    private fun remember(id: String) {
        ring.remove(id)
        ring.addLast(id)
        while (ring.size > BrainConfig.dedupeWindow) ring.removeFirst()
    }

    private fun fill(text: String, slots: Map<String, String>): String =
        slots.entries.fold(text) { acc, (k, v) -> acc.replace("{$k}", v) }
}
