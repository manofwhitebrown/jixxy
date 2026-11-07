package app.jixxy.engine.selector

import app.jixxy.engine.api.*
import app.jixxy.engine.lines.Line
import app.jixxy.engine.lines.LineBank
import app.jixxy.engine.rules.DefaultJixxyBrain
import app.jixxy.engine.rules.at
import org.junit.Assert.*
import org.junit.Test
import java.time.ZoneOffset
import kotlin.random.Random

/** D-016: language per signal, English fallback only when a trigger has no lines in the language, rings per language. */
class LanguageTest {
    private fun line(id: String, lang: String, trigger: Trigger = Trigger.APP_OPEN, roast: Int = 1) =
        Line(id, trigger, emptySet(), setOf(Mood.CHILL), roast, 0, lang, "$lang $id {app}", setOf("app"), emptySet(), false)

    private fun query(lang: String, trigger: Trigger = Trigger.APP_OPEN, roast: RoastLevel = RoastLevel.NORMAL) =
        LineQuery(trigger, AppCategory.SOCIAL, Mood.CHILL, roast, Bond.STRANGER, lang, mapOf("app" to "Insta"))

    private fun picker(vararg l: Line, seed: Int = 1) = BankLinePicker(LineBank(l.toList()), Random(seed))

    @Test fun languageWithLinesNeverMixesInEnglish() {
        val p = picker(line("e1", "en"), line("e2", "en"), line("h1", "hi"), line("h2", "hi"), line("h3", "hi"))
        repeat(30) { assertTrue(p.pick(query("hi"))?.id?.startsWith("h") ?: true) }
    }

    @Test fun triggerWithoutLinesInTheLanguageFallsBackToEnglish() {
        val p = picker(line("e", "en", Trigger.CHARGER_ON), line("h", "hi", Trigger.APP_OPEN))
        assertEquals("e", p.pick(query("hi", Trigger.CHARGER_ON))!!.id)
    }

    @Test fun languageWithLinesButNoneEligibleStaysQuietNotEnglish() {
        val p = picker(line("e", "en"), line("spicy", "hi", roast = 3))
        assertNull(p.pick(query("hi", roast = RoastLevel.GENTLE)))
    }

    @Test fun ringsAreSeparatePerLanguage() {
        val p = picker(line("e1", "en"), line("e2", "en"), line("h1", "hi"), line("h2", "hi"))
        p.restoreRecent(listOf("e1", "h1", "e2"))
        assertEquals(listOf("e1", "e2"), p.recentIds("en"))
        assertEquals(listOf("h1"), p.recentIds("hi"))
    }

    @Test fun englishTrafficDoesNotTouchTheHinglishRing() {
        val p = picker(*((1..60).map { line("e$it", "en") } + (1..6).map { line("h$it", "hi") }).toTypedArray())
        val first = p.pick(query("hi"))!!.id
        repeat(100) { p.pick(query("en")) }
        assertEquals(listOf(first), p.recentIds("hi"))
        assertTrue(p.recentIds("en").isNotEmpty() && p.recentIds("en").all { it.startsWith("e") })
    }

    @Test fun switchingLanguageMidDayUsesTheRightBankAndSharedCaps() {
        var lang = "en"
        val bank = LineBank((1..9).map { line("e$it", "en") } + (1..9).map { line("h$it", "hi") })
        val b = DefaultJixxyBrain(
            BrainState(firstSeenTs = 1L), { UserSettings(language = lang) }, BankLinePicker(bank, Random(3)), { it }, ZoneOffset.UTC
        )
        val said = mutableListOf<Pair<String, String>>()
        var t = at(0, 9)
        for (i in 0 until 40) {
            if (i == 10) lang = "hi"
            if (i == 20) lang = "en"
            t += 25 * 60_000L
            b.onSignal(Signal.ForegroundApp("a$i", AppCategory.SOCIAL, t)).forEach { r -> r.lineId?.let { said += lang to it } }
        }
        assertTrue(said.isNotEmpty())
        said.forEach { (l, id) -> assertEquals(if (l == "hi") "h" else "e", id.take(1)) }
        assertTrue("caps shared across languages", said.size <= BrainConfig.maxUnpromptedPerDay(UserSettings().nagLevel))
    }
}
