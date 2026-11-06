package app.jixxy.engine.lines

/** Thrown when line-bank JSON is malformed. */
class JsonException(message: String) : RuntimeException(message)

/**
 * Tiny dependency-free JSON reader (the engine module may not use android.* or org.json).
 * Produces Map<String, Any?>, List<Any?>, String, Double, Boolean or null.
 */
object MiniJson {
    fun parse(text: String): Any? {
        val p = Parser(text)
        p.ws()
        val v = p.value()
        p.ws()
        if (p.i != text.length) throw JsonException("trailing data at ${p.i}")
        return v
    }

    private class Parser(val s: String) {
        var i = 0

        fun ws() {
            while (i < s.length && s[i].isWhitespace()) i++
        }

        fun value(): Any? {
            if (i >= s.length) throw JsonException("unexpected end")
            return when (val c = s[i]) {
                '{' -> obj()
                '[' -> arr()
                '"' -> str()
                't' -> lit("true", true)
                'f' -> lit("false", false)
                'n' -> lit("null", null)
                else -> if (c == '-' || c.isDigit()) num() else throw JsonException("unexpected '$c' at $i")
            }
        }

        fun lit(word: String, v: Any?): Any? {
            if (!s.startsWith(word, i)) throw JsonException("bad literal at $i")
            i += word.length
            return v
        }

        fun num(): Double {
            val st = i
            if (s[i] == '-') i++
            while (i < s.length && (s[i].isDigit() || s[i] in ".eE+-")) i++
            return s.substring(st, i).toDoubleOrNull() ?: throw JsonException("bad number at $st")
        }

        fun str(): String {
            i++ // opening quote
            val sb = StringBuilder()
            while (true) {
                if (i >= s.length) throw JsonException("unterminated string")
                val c = s[i++]
                when (c) {
                    '"' -> return sb.toString()
                    '\\' -> {
                        if (i >= s.length) throw JsonException("bad escape")
                        when (val e = s[i++]) {
                            '"', '\\', '/' -> sb.append(e)
                            'n' -> sb.append('\n')
                            't' -> sb.append('\t')
                            'r' -> sb.append('\r')
                            'b' -> sb.append('\b')
                            'f' -> sb.append('\u000C')
                            'u' -> {
                                if (i + 4 > s.length) throw JsonException("bad unicode escape")
                                sb.append(s.substring(i, i + 4).toInt(16).toChar())
                                i += 4
                            }
                            else -> throw JsonException("bad escape \\$e")
                        }
                    }
                    else -> sb.append(c)
                }
            }
        }

        fun arr(): List<Any?> {
            i++
            val out = mutableListOf<Any?>()
            ws()
            if (s.getOrNull(i) == ']') { i++; return out }
            while (true) {
                ws()
                out += value()
                ws()
                when (s.getOrNull(i++)) {
                    ',' -> continue
                    ']' -> return out
                    else -> throw JsonException("expected , or ] at ${i - 1}")
                }
            }
        }

        fun obj(): Map<String, Any?> {
            i++
            val out = linkedMapOf<String, Any?>()
            ws()
            if (s.getOrNull(i) == '}') { i++; return out }
            while (true) {
                ws()
                if (s.getOrNull(i) != '"') throw JsonException("expected key at $i")
                val k = str()
                ws()
                if (s.getOrNull(i++) != ':') throw JsonException("expected : at ${i - 1}")
                ws()
                out[k] = value()
                ws()
                when (s.getOrNull(i++)) {
                    ',' -> continue
                    '}' -> return out
                    else -> throw JsonException("expected , or } at ${i - 1}")
                }
            }
        }
    }
}
