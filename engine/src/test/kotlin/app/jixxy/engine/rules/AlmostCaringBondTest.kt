package app.jixxy.engine.rules

import app.jixxy.engine.api.*
import app.jixxy.engine.lines.Line
import app.jixxy.engine.lines.LineBank
import app.jixxy.engine.selector.BankLinePicker
import org.junit.Assert.*
import org.junit.Test
import java.time.ZoneOffset
import kotlin.random.Random

/** D-014: almost-caring uses each line's own bondMin (no global floor), at most once per 3 days. */
class AlmostCaringBondTest {
    private fun caring(id: String, bondMin: Int) = Line(
        id, Trigger.ALMOST_CARING, emptySet(), setOf(Mood.CHILL, Mood.SOFT), 1, bondMin, "en", "t", emptySet(), emptySet(), true
    )

    private val mild = "almost_caring_mild"
    private val heartfelt = "almost_caring_heartfelt"

    /** Runs [nights] consecutive late-night opens; returns (day, lineId) for every almost-caring line shown. */
    private fun run(xp: Int, nights: Int, lines: List<Line>): List<Pair<Int, String>> {
        val picker = BankLinePicker(LineBank(lines), Random(1))
        val b = DefaultJixxyBrain(BrainState(firstSeenTs = 1L, bondXp = xp), { UserSettings() }, picker, { it }, ZoneOffset.UTC)
        val shown = mutableListOf<Pair<Int, String>>()
        for (day in 0 until nights) {
            b.onSignal(Signal.ForegroundApp("n$day", AppCategory.SOCIAL, at(day, 23, 30)))
                .mapNotNull { it.lineId }.filter { it.startsWith("almost_caring") }.forEach { shown += day to it }
        }
        return shown
    }

    private val bank get() = listOf(caring(mild, 1), caring(heartfelt, 3))

    @Test fun mildLineFiresAtRoommateAndHeartfeltDoesNot() {
        val shown = run(BrainConfig.bondRoommateXp, 12, bank)
        assertTrue(shown.isNotEmpty())
        assertTrue(shown.all { it.second == mild })
    }

    @Test fun heartfeltLineWaitsForRideOrDie() {
        val shown = run(BrainConfig.bondRideOrDieXp, 30, bank)
        assertTrue(heartfelt in shown.map { it.second })
    }

    @Test fun nothingBeforeRoommate() {
        assertTrue(run(0, 12, bank).isEmpty())
    }

    @Test fun onlyHeartfeltLinesMeansNothingBeforeRideOrDie() {
        assertTrue(run(BrainConfig.bondFriendXp, 12, listOf(caring(heartfelt, 3))).isEmpty())
    }

    @Test fun capOfOncePerThreeDaysHolds() {
        val days = run(BrainConfig.bondRideOrDieXp, 30, bank).map { it.first }
        assertTrue(days.size >= 2)
        days.zipWithNext().forEach { (a, b) -> assertTrue("gap $a->$b", b - a >= BrainConfig.almostCaringGapDays) }
    }
}
