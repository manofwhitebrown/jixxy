package app.jixxy.engine.tools

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.Trigger
import app.jixxy.engine.lines.Line
import org.junit.Assert.*
import org.junit.Test

class LineCoverageTableTest {
    private fun l(id: String, t: Trigger, cats: Set<AppCategory>, mood: Mood = Mood.CHILL, lang: String = "en") =
        Line(id, t, cats, setOf(mood), 1, 0, lang, "x", emptySet(), emptySet(), false)

    private val lines = listOf(
        l("a", Trigger.APP_OPEN, emptySet()),
        l("b", Trigger.APP_OPEN, setOf(AppCategory.SOCIAL), Mood.GRUMPY),
        l("c", Trigger.APP_OPEN, setOf(AppCategory.GAME), lang = "hinglish")
    )

    @Test fun anyLinesCountForEveryCategoryAndOtherLanguagesAreIgnored() {
        val thin = LineCoverageTable.thin(lines).filter { it.first == Trigger.APP_OPEN }.associate { it.second to it.third }
        assertEquals(2, thin[AppCategory.SOCIAL])
        assertEquals(1, thin[AppCategory.GAME])
    }

    @Test fun tableShowsMoodCounts() {
        val md = LineCoverageTable.render(lines)
        assertTrue(md.contains("## APP_OPEN (2 lines)"))
        assertTrue(md.contains("| SOCIAL | 1 | 0 | 1 | 0 | 0 | 0 | 0 |"))
        assertTrue(md.contains("Thin spots"))
    }
}
