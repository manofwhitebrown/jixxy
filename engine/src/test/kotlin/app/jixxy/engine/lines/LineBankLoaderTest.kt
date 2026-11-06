package app.jixxy.engine.lines

import app.jixxy.engine.api.Trigger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LineBankLoaderTest {
    @Test fun parsesOneLine() {
        val l = LineBankLoader.parse(
            """[{"id":"x_1","trigger":"APP_OPEN","categories":["SOCIAL"],"moods":["CHILL"],"roast":2,"bondMin":1,
            "lang":"en","text":"Hi {app} \u2014 \"yo\"","slots":["app"],"tags":["t"],"almostCaring":true}]"""
        ).single()
        assertEquals("x_1", l.id)
        assertEquals(Trigger.APP_OPEN, l.trigger)
        assertEquals(2, l.roast)
        assertEquals("Hi {app} \u2014 \"yo\"", l.text)
        assertTrue(l.almostCaring)
    }

    @Test(expected = JsonException::class) fun rejectsUnknownTrigger() {
        LineBankLoader.parse("""[{"id":"a","trigger":"NOPE","text":"x"}]""")
    }

    @Test(expected = JsonException::class) fun rejectsBrokenJson() {
        LineBankLoader.parse("[{\"id\": ")
    }

    @Test fun placeholdersAreListed() {
        assertEquals(listOf("app", "minutes"), LineBankLoader.placeholders("{app} for {minutes}"))
    }

    @Test fun realEnglishBankLoadsAndEveryPlaceholderIsDeclared() {
        val dir = listOf(File("../app/src/main/assets/lines/en"), File("app/src/main/assets/lines/en")).firstOrNull { it.isDirectory }
            ?: return // bank not reachable from this working directory
        val bank = LineBankLoader.fromDirectory(dir)
        assertTrue("expected the full bank, got ${bank.size}", bank.size >= 366)
        bank.lines.forEach { l ->
            assertTrue(l.id, LineBankLoader.placeholders(l.text).all { it in l.slots })
            assertTrue(l.id, l.text.length <= 90)
        }
        assertEquals(bank.size, bank.lines.map { it.id }.toSet().size)
    }
}
