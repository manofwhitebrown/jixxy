package app.jixxy.engine.lines

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.Trigger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LineBankValidatorTest {
    private fun line(
        id: String = "a_1", text: String = "Hello there.", trigger: Trigger = Trigger.APP_OPEN,
        slots: Set<String> = emptySet(), roast: Int = 1, bondMin: Int = 0,
        cats: Set<AppCategory> = setOf(AppCategory.SOCIAL), moods: Set<Mood> = setOf(Mood.CHILL)
    ) = Line(id, trigger, cats, moods, roast, bondMin, "en", text, slots, emptySet(), false)

    private fun errors(vararg l: Line) = LineBankValidator.validate(l.toList()).filter { it.error }.map { it.message }

    @Test fun goodLinePasses() {
        assertTrue(errors(line()).isEmpty())
        assertTrue(errors(line(text = "{app} again?", slots = setOf("app"))).isEmpty())
    }

    @Test fun rejectsDuplicateIds() {
        assertTrue(errors(line("x"), line("x")).any { it.contains("duplicate") })
    }

    @Test fun rejectsTooLongText() {
        assertTrue(errors(line(text = "x".repeat(91))).any { it.contains("91 chars") })
        assertTrue(errors(line(text = "x".repeat(90))).isEmpty())
    }

    @Test fun rejectsUndeclaredAndDisallowedSlots() {
        assertTrue(errors(line(text = "Hi {app}")).any { it.contains("undeclared") })
        assertTrue(errors(line(text = "Hi {name}", slots = setOf("name"))).any { it.contains("disallowed") })
    }

    @Test fun rejectsSlotsTheTriggerCannotFill() {
        assertTrue(errors(line(text = "{battery}%", slots = setOf("battery"))).any { it.contains("never provides") })
        assertTrue(errors(line(text = "{battery}%", slots = setOf("battery"), trigger = Trigger.CHARGER_ON)).isEmpty())
    }

    @Test fun rejectsBadRoastAndBond() {
        assertTrue(errors(line(roast = 0)).any { it.contains("roast") })
        assertTrue(errors(line(roast = 4)).any { it.contains("roast") })
        assertTrue(errors(line(bondMin = 4)).any { it.contains("bondMin") })
    }

    @Test fun rejectsBannedTopicsButNotInnocentWords() {
        assertTrue(errors(line(text = "You are so stupid.")).any { it.contains("stupid") })
        assertTrue(errors(line(text = "I'm dead inside.")).any { it.contains("dead") })
        assertTrue(errors(line(text = "Wasting your life, huh.")).any { it.contains("wasting your life") })
        assertTrue(errors(line(text = "Go see a therapist.")).any { it.contains("therapist") })
        assertTrue(errors(line(text = "A diet of reels. Hmm.")).isEmpty())
        assertTrue(errors(line(text = "Shellfish and hello.")).isEmpty())
    }

    @Test fun affectionateInsultsNeedRoastAndBond() {
        assertTrue(errors(line(text = "Go to sleep, idiot.", roast = 1)).any { it.contains("affectionate") })
        assertTrue(errors(line(text = "Go to sleep, idiot.", roast = 2, bondMin = 0)).any { it.contains("affectionate") })
        assertTrue(errors(line(text = "Go to sleep, idiot.", roast = 2, bondMin = 1)).isEmpty())
    }

    @Test fun protectedCategoryIsNeverTargeted() {
        assertTrue(errors(line(cats = setOf(AppCategory.PROTECTED))).any { it.contains("PROTECTED") })
    }

    @Test fun coverageCountsLinesPerTriggerAndCategory() {
        val cov = LineBankValidator.coverage(listOf(line("a"), line("b"), line("c", cats = emptySet())))
        assertEquals(mapOf("ANY" to 1, "SOCIAL" to 2), cov[Trigger.APP_OPEN])
    }

    @Test fun realBankHasNoErrors() {
        val dir = listOf(File("../app/src/main/assets/lines"), File("app/src/main/assets/lines")).firstOrNull { it.isDirectory } ?: return
        val bank = LineBankLoader.fromDirectory(dir)
        val bad = LineBankValidator.validate(bank.lines).filter { it.error }
        assertTrue(bad.joinToString("\n") { "${it.lineId}: ${it.message}" }, bad.isEmpty())
    }

    @Test fun reviewFieldDefaultsToDraftAndAcceptsOnlyKnownValues() {
        val base = line()
        assertEquals("draft", base.review)
        listOf("draft", "reviewed", "native_reviewed").forEach { assertTrue(errors(base.copy(review = it)).isEmpty()) }
        assertTrue(errors(base.copy(review = "approved")).any { it.contains("review") })
    }

    @Test fun loaderReadsTheReviewField() {
        val json = """[{"id":"a_1","trigger":"APP_OPEN","categories":["SOCIAL"],"moods":["CHILL"],"roast":1,"text":"Hi.","review":"reviewed"},
            {"id":"a_2","trigger":"APP_OPEN","categories":["SOCIAL"],"moods":["CHILL"],"roast":1,"text":"Yo."}]"""
        val lines = LineBankLoader.fromTexts(listOf(json)).lines.associateBy { it.id }
        assertEquals("reviewed", lines["a_1"]!!.review)
        assertEquals("draft", lines["a_2"]!!.review)
    }
}
