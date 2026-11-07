package app.jixxy.engine.selector

import app.jixxy.engine.api.*
import app.jixxy.engine.lines.Line
import app.jixxy.engine.lines.LineBank
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

/** D-020: at Calm, `nag_only` lines are ineligible; Naggy and Chaos use everything. */
class NagTierTest {
    private fun line(id: String, tag: String, trigger: Trigger = Trigger.APP_OPEN) =
        Line(id, trigger, emptySet(), setOf(Mood.CHILL), 1, 0, "en", "t $id", emptySet(), setOf(tag), false)

    private fun query(calm: Boolean, trigger: Trigger = Trigger.APP_OPEN) =
        LineQuery(trigger, AppCategory.SOCIAL, Mood.CHILL, RoastLevel.NORMAL, Bond.STRANGER, "en", emptyMap(), calm = calm)

    private fun picker(vararg l: Line) = BankLinePicker(LineBank(l.toList()), Random(5))

    @Test fun calmNeverPicksNagOnly() {
        val p = picker(line("ok", "calm_ok"), line("nag", "nag_only"))
        repeat(40) { assertEquals("ok", p.pick(query(calm = true))?.id ?: "ok") }
    }

    @Test fun naggyAndChaosUseBothKinds() {
        val seen = (1..60).mapNotNull { BankLinePicker(LineBank(listOf(line("ok", "calm_ok"), line("nag", "nag_only"))), Random(it)).pick(query(calm = false)) }.map { it.id }.toSet()
        assertEquals(setOf("ok", "nag"), seen)
    }

    @Test fun calmWithOnlyNagOnlyUnpromptedLinesStaysQuiet() {
        assertNull(picker(line("nag", "nag_only")).pick(query(calm = true)))
    }

    @Test fun calmPauseMechanicsMayFallBackToNagOnlyRatherThanSilence() {
        val p = picker(line("nag", "nag_only", Trigger.PAUSE_PROMPT))
        assertEquals("nag", p.pick(query(calm = true, trigger = Trigger.PAUSE_PROMPT))!!.id)
    }

    @Test fun calmPauseMechanicsStillPreferCalmOk() {
        val p = picker(line("ok", "calm_ok", Trigger.PAUSE_PROMPT), line("nag", "nag_only", Trigger.PAUSE_PROMPT))
        repeat(30) { assertEquals("ok", p.pick(query(calm = true, trigger = Trigger.PAUSE_PROMPT))?.id ?: "ok") }
    }
}
