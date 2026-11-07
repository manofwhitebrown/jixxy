package app.jixxy.engine.lines

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.Trigger
import java.io.File

/**
 * One line of the bank (schema owned by WRITER, see coord/CONTRACTS.md).
 * @property categories empty means "any category"
 * @property bondMin 0 stranger, 1 roommate, 2 friend, 3 ride-or-die
 */
data class Line(
    val id: String,
    val trigger: Trigger,
    val categories: Set<AppCategory>,
    val moods: Set<Mood>,
    val roast: Int,
    val bondMin: Int,
    val lang: String,
    val text: String,
    val slots: Set<String>,
    val tags: Set<String>,
    val almostCaring: Boolean,
    /** Review state (D-022): "draft" (default), "reviewed" or "native_reviewed". Checked by validateLines. */
    val review: String = "draft"
)

/** An immutable set of lines indexed by trigger. */
class LineBank(val lines: List<Line>) {
    private val byTrigger: Map<Trigger, List<Line>> = lines.groupBy { it.trigger }

    /** All lines for [trigger] in bank order. */
    fun forTrigger(trigger: Trigger): List<Line> = byTrigger[trigger].orEmpty()

    val size: Int get() = lines.size
}

/** Parses line-bank JSON. Pure Kotlin: the platform reads asset files and hands the text in. */
object LineBankLoader {
    private val slotPattern = Regex("\\{([a-z]+)\\}")

    /** Placeholders `{x}` used in [text], in order. */
    fun placeholders(text: String): List<String> = slotPattern.findAll(text).map { it.groupValues[1] }.toList()

    /** Parses one JSON array of lines. Throws [JsonException] on malformed input or unknown enum names. */
    fun parse(json: String): List<Line> {
        val root = MiniJson.parse(json) as? List<*> ?: throw JsonException("line file must be a JSON array")
        return root.mapIndexed { idx, raw ->
            val m = raw as? Map<*, *> ?: throw JsonException("entry $idx is not an object")
            fun str(k: String) = m[k] as? String ?: throw JsonException("entry $idx: missing string '$k'")
            fun strs(k: String): List<String> =
                (m[k] as? List<*> ?: emptyList<Any?>()).map { it as? String ?: throw JsonException("entry $idx: '$k' must hold strings") }
            fun int(k: String, def: Int) = (m[k] as? Double)?.toInt() ?: def
            try {
                Line(
                    id = str("id"),
                    trigger = Trigger.valueOf(str("trigger")),
                    categories = strs("categories").map { AppCategory.valueOf(it) }.toSet(),
                    moods = strs("moods").map { Mood.valueOf(it) }.toSet(),
                    roast = int("roast", 1),
                    bondMin = int("bondMin", 0),
                    lang = (m["lang"] as? String) ?: "en",
                    text = str("text"),
                    slots = strs("slots").toSet(),
                    tags = strs("tags").toSet(),
                    almostCaring = (m["almostCaring"] as? Boolean) ?: false,
                    review = (m["review"] as? String) ?: "draft"
                )
            } catch (e: IllegalArgumentException) {
                throw JsonException("entry $idx: ${e.message}")
            }
        }
    }

    /** Builds a bank from several JSON texts (one per trigger file). */
    fun fromTexts(texts: List<String>): LineBank = LineBank(texts.flatMap { parse(it) })

    /** Loads every `*.json` under [dir] recursively (JVM tools and tests; Android reads assets instead). */
    fun fromDirectory(dir: File): LineBank =
        fromTexts(dir.walkTopDown().filter { it.isFile && it.extension == "json" }.sortedBy { it.path }.map { it.readText() }.toList())
}
