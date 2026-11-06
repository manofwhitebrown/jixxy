package app.jixxy.engine.tools

import app.jixxy.engine.lines.JsonException
import app.jixxy.engine.lines.Line
import app.jixxy.engine.lines.LineBankLoader
import app.jixxy.engine.lines.LineBankValidator
import java.io.File
import kotlin.system.exitProcess

/** CLI behind the Gradle task `:engine:validateLines`. Usage: ValidateLines <lines-dir>. Exit code 1 on any error. */
fun main(args: Array<String>) {
    val dir = File(args.firstOrNull() ?: "app/src/main/assets/lines")
    if (!dir.isDirectory) {
        println("validateLines: no line bank at ${dir.path}, skipping")
        return
    }
    val all = mutableListOf<Line>()
    var errors = 0
    dir.walkTopDown().filter { it.isFile && it.extension == "json" }.sortedBy { it.path }.forEach { f ->
        try {
            all += LineBankLoader.parse(f.readText())
        } catch (e: JsonException) {
            errors++
            println("::error file=${f.path}::${e.message}")
        }
    }
    val problems = LineBankValidator.validate(all)
    problems.forEach {
        val level = if (it.error) "error" else "warning"
        println("::$level::${it.lineId}: ${it.message}")
    }
    errors += problems.count { it.error }
    println("validateLines: ${all.size} lines, $errors error(s), ${problems.count { !it.error }} warning(s)")
    if (args.contains("--coverage")) {
        LineBankValidator.coverage(all).toSortedMap().forEach { (t, m) -> println("  $t: $m") }
    }
    if (errors > 0) exitProcess(1)
}
