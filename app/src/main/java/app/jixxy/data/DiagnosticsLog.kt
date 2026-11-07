package app.jixxy.data

import java.io.File
import java.time.Instant

/**
 * Privacy filter for the local diagnostics log (T-P3-4).
 * Keeps Android/Kotlin/Jixxy class names (needed to read a stack trace) and the packages the USER chose to be paused in.
 * Every other package-looking token (someone else's app, an exception message that names one) becomes `<app>`.
 */
object DiagnosticsRedactor {
    private val packageLike = Regex("""\b[A-Za-z][A-Za-z0-9_]*(?:\.[A-Za-z0-9_]+){2,}""")
    private val safePrefixes = listOf(
        "app.jixxy.", "java.", "javax.", "kotlin.", "kotlinx.", "android.", "androidx.",
        "com.android.", "dalvik.", "libcore.", "sun.", "org.json", "org.jetbrains.",
    )

    fun redact(text: String, allowedPackages: Set<String> = emptySet()): String =
        packageLike.replace(text) { m ->
            val t = m.value
            if (t in allowedPackages || safePrefixes.any { t.startsWith(it) }) t else "<app>"
        }
}

/**
 * Small ring-buffer log in app-private storage. Never networked; it leaves the phone only when the user copies the report by hand.
 * Why file-based and line-trimmed: it must survive the process dying (that is exactly when we need it) and stay ~[capBytes].
 * Records: UTC timestamp, a KIND word, a short detail. Callers pass only kinds/ids/versions, never text from other apps;
 * [DiagnosticsRedactor] is the second line of defence.
 */
class DiagnosticsLog(
    private val file: File,
    private val capBytes: Int = 200_000,
    private val now: () -> Long = System::currentTimeMillis,
) {
    @Synchronized
    fun append(kind: String, detail: String = "", allowedPackages: Set<String> = emptySet()) {
        runCatching {
            val clean = DiagnosticsRedactor.redact(detail, allowedPackages).trimEnd()
            val record = "${Instant.ofEpochMilli(now())} $kind" + if (clean.isEmpty()) "\n" else " $clean\n"
            file.parentFile?.mkdirs()
            file.appendText(record)
            if (file.length() > capBytes) trim()
        }  // diagnostics must never crash the app
    }

    @Synchronized
    fun readAll(): String = runCatching { if (file.exists()) file.readText() else "" }.getOrDefault("")

    @Synchronized
    fun clear() { runCatching { file.delete() } }

    /** Drops the oldest lines until about 75% of the cap remains, so we do not rewrite the file on every append. */
    private fun trim() {
        val lines = file.readLines()
        var size = lines.sumOf { it.length + 1L }
        var from = 0
        while (from < lines.size && size > capBytes * 3L / 4) { size -= lines[from].length + 1; from++ }
        file.writeText(lines.drop(from).joinToString("\n", postfix = "\n"))
    }
}

/** Builds the text the user copies: a plain header plus the newest log lines, at most [maxChars] in total. */
object DiagnosticsReport {
    const val MAX_CHARS = 20_000

    fun build(header: List<String>, log: String, maxChars: Int = MAX_CHARS): String {
        val head = header.joinToString("\n", postfix = "\n--- log (oldest first, newest last) ---\n")
        val room = (maxChars - head.length).coerceAtLeast(0)
        return head + tail(log, room)
    }

    /** Keeps the newest whole lines that fit in [max] characters. */
    fun tail(text: String, max: Int): String {
        if (text.length <= max) return text
        val cut = text.substring(text.length - max)
        val firstBreak = cut.indexOf('\n')
        return if (firstBreak < 0) "" else cut.substring(firstBreak + 1)
    }
}
