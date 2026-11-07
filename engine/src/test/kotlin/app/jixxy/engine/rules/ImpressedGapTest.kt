package app.jixxy.engine.rules

import app.jixxy.engine.api.*
import app.jixxy.engine.selector.LinePicker
import app.jixxy.engine.selector.LineQuery
import app.jixxy.engine.selector.PickedLine
import org.junit.Assert.*
import org.junit.Test
import java.time.ZoneOffset

/** Earned praise stays rare: at most one IMPRESSED line per BrainConfig.impressedGapDays. */
class ImpressedGapTest {
    private class RealIds : LinePicker {
        private var n = 0
        override fun pick(query: LineQuery): PickedLine? {
            n++
            val prefix = if (query.trigger == Trigger.IMPRESSED) "impressed_any_" else "other_${query.trigger}_"
            return PickedLine("$prefix$n", "t")
        }
        override fun onReaction(lineId: String, kind: ReactionKind) = Unit
    }

    private val t = "t.app"
    private fun brain() = DefaultJixxyBrain(
        BrainState(firstSeenTs = 1L), { UserSettings(targetApps = setOf(t)) }, RealIds(), { it }, ZoneOffset.UTC
    )

    /** Three Leaves in a row starting at [hour] on [day]; returns the triggers of the last Leave's reply. */
    private fun streak(b: DefaultJixxyBrain, day: Int, hour: Int): List<Trigger> {
        var last = emptyList<Reaction>()
        for (i in 0 until BrainConfig.impressedLeaveStreak) {
            b.onSignal(Signal.ForegroundApp("other$i", AppCategory.SOCIAL, at(day, hour, i * 5)))
            b.onSignal(Signal.ForegroundApp(t, AppCategory.SOCIAL, at(day, hour, i * 5 + 1)))
            last = b.onSignal(Signal.PauseChoice(t, PauseChoiceKind.LEAVE, at(day, hour, i * 5 + 2)))
        }
        return last.mapNotNull { r -> r.lineId?.let { id -> if (id.startsWith("impressed")) Trigger.IMPRESSED else Trigger.PAUSE_LEAVE } }
    }

    @Test fun secondStreakSoonAfterGetsNoPraiseButALaterOneDoes() {
        val b = brain()
        assertEquals(listOf(Trigger.IMPRESSED), streak(b, 0, 10))
        assertEquals(listOf(Trigger.PAUSE_LEAVE), streak(b, 0, 14))
        assertEquals(listOf(Trigger.PAUSE_LEAVE), streak(b, 1, 10))
        assertEquals(listOf(Trigger.IMPRESSED), streak(b, 3, 10))
    }
}
