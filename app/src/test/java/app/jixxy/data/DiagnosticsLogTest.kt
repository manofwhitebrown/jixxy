package app.jixxy.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DiagnosticsLogTest {
    @get:Rule val tmp = TemporaryFolder()
    private fun log(cap: Int = 200_000) = DiagnosticsLog(tmp.newFile(), cap, now = { 0L })

    @Test fun redactsOtherAppsButKeepsChosenAndFramework() {
        val chosen = setOf("com.instagram.android")
        val out = DiagnosticsRedactor.redact(
            "chosen com.instagram.android other com.bank.secure at app.jixxy.platform.OverlayService.onCreate(X.kt:1) " +
                "android.os.Handler version 1.2.3", chosen,
        )
        assertTrue(out.contains("com.instagram.android"))
        assertFalse(out.contains("com.bank.secure"))
        assertTrue(out.contains("<app>"))
        assertTrue(out.contains("app.jixxy.platform.OverlayService.onCreate"))
        assertTrue(out.contains("android.os.Handler"))
        assertTrue(out.contains("1.2.3"))
    }

    @Test fun appendRedactsBeforeWriting() {
        val l = log()
        l.append("CRASH", "boom in com.whatsapp.w4b")
        assertFalse(l.readAll().contains("whatsapp"))
        assertTrue(l.readAll().startsWith("1970-01-01T00:00:00Z CRASH"))
    }

    @Test fun ringBufferStaysNearCapAndKeepsNewest() {
        val l = log(cap = 2_000)
        repeat(500) { l.append("EV", "n=$it") }
        val text = l.readAll()
        assertTrue(text.length <= 2_000)
        assertTrue(text.contains("n=499"))
        assertFalse(text.contains("n=0\n"))
    }

    @Test fun clearEmptiesLog() {
        val l = log()
        l.append("A")
        l.clear()
        assertEquals("", l.readAll())
    }

    @Test fun reportIsCappedAndKeepsNewestWholeLines() {
        val logText = (1..2000).joinToString("") { "line $it\n" }
        val report = DiagnosticsReport.build(listOf("head"), logText, maxChars = 500)
        assertTrue(report.length <= 500)
        assertTrue(report.endsWith("line 2000\n"))
        assertTrue(report.startsWith("head\n"))
        assertFalse(report.contains("ine 1\n"))
    }
}
