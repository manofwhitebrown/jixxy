package app.jixxy.engine.tools

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.Trigger
import app.jixxy.engine.lines.Line

/**
 * Trigger x category x mood shopping list for new lines. A cell is the number of lines that could be picked for that
 * combination (lines with no category fit every category). Only English or whichever [lang] is asked for.
 */
object LineCoverageTable {
    /** Triggers whose pick depends on the foreground app's category. */
    val categoryTriggers = setOf(Trigger.APP_OPEN, Trigger.SESSION_LONG, Trigger.LATE_NIGHT, Trigger.PAUSE_PROMPT, Trigger.CALLBACK)
    private val moods = Mood.values().toList()
    private val categories = AppCategory.values().filter { it != AppCategory.PROTECTED }

    /** Combinations (trigger, category) of [categoryTriggers] that have fewer than [min] usable lines. */
    fun thin(lines: List<Line>, lang: String = "en", min: Int = 3): List<Triple<Trigger, AppCategory, Int>> =
        categoryTriggers.flatMap { t ->
            categories.map { c -> Triple(t, c, usable(lines, lang, t, c).size) }.filter { it.third < min }
        }

    /** Markdown: one table per trigger, categories as rows, moods as columns. */
    fun render(lines: List<Line>, lang: String = "en"): String = buildString {
        append("# Line coverage ($lang)\n\nCells = lines that fit that trigger, app category and mood (no-category lines count everywhere). ")
        append("Only trigger/category pairs the engine picks by category are listed per category; others show one row.\n")
        for (t in Trigger.values()) {
            val mine = lines.filter { it.lang == lang && it.trigger == t }
            append("\n## $t (${mine.size} lines)\n\n| category | ").append(moods.joinToString(" | ")).append(" |\n")
            append("|---|").append(moods.joinToString("") { "---|" }).append('\n')
            val rows = if (t in categoryTriggers) categories.map { it.name to it } else listOf("ANY" to null)
            for ((label, cat) in rows) {
                val fit = if (cat == null) mine else usable(lines, lang, t, cat)
                append("| $label | ").append(moods.joinToString(" | ") { m -> fit.count { m in it.moods }.toString() }).append(" |\n")
            }
        }
        val thin = thin(lines, lang)
        append("\n## Thin spots (fewer than 3 usable lines)\n\n")
        if (thin.isEmpty()) append("None.\n") else thin.forEach { (t, c, n) -> append("- $t / $c: $n\n") }
    }

    private fun usable(lines: List<Line>, lang: String, t: Trigger, c: AppCategory) =
        lines.filter { it.lang == lang && it.trigger == t && (it.categories.isEmpty() || c in it.categories) }
}
