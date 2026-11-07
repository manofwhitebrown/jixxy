package app.jixxy.engine.rules

import app.jixxy.engine.api.*
import org.junit.Assert.*
import org.junit.Test
import java.time.ZoneOffset
import kotlin.random.Random

class SimulationTest {
    private val unprompted = setOf(
        Trigger.WELCOME, Trigger.APP_OPEN, Trigger.SESSION_LONG, Trigger.LATE_NIGHT,
        Trigger.CHARGER_ON, Trigger.BATTERY_LOW, Trigger.MUSIC
    )

    /** 30 synthetic days, one step per minute, seeded. Chaotic input; the hard rules must still hold. */
    @Test fun thirtyDaySimulationNeverBreaksCapsGapsOrQuietHours() {
        val settings = UserSettings(targetApps = setOf("t.app"), mutedApps = setOf("muted.app"))
        val rig = Rig(settings, BrainState())
        val rnd = Random(42)
        val apps = listOf(
            "a.one" to AppCategory.SOCIAL, "a.two" to AppCategory.VIDEO, "t.app" to AppCategory.SOCIAL,
            "muted.app" to AppCategory.GAME, "bank.app" to AppCategory.PROTECTED
        )
        val spoken = mutableListOf<Pair<Long, Reaction>>()
        var fg: String? = null
        var sessionMin = 0
        var pausePkg: String? = null
        fun collect(ts: Long, out: List<Reaction>) {
            for (r in out) {
                if (r.trigger() in unprompted) spoken += ts to r
                if (r.kind == ReactionKindOut.PAUSE_PROMPT) pausePkg = r.pause!!.pkg
            }
        }
        val start = at(0, 0)
        for (i in 0 until 30 * 24 * 60) {
            val ts = start + i * 60_000L
            collect(ts, rig.send(Signal.Heartbeat(ts)))
            pausePkg?.let {
                val choice = if (rnd.nextInt(100) < 60) PauseChoiceKind.LEAVE else PauseChoiceKind.STAY
                pausePkg = null
                collect(ts, rig.send(Signal.PauseChoice(it, choice, ts)))
            }
            if (rnd.nextInt(100) < 8) {
                val (pkg, cat) = apps[rnd.nextInt(apps.size)]
                fg = pkg; sessionMin = 0
                collect(ts, rig.send(Signal.ForegroundApp(pkg, cat, ts)))
            } else if (fg != null) {
                sessionMin++
                collect(ts, rig.send(Signal.SessionTick(fg!!, sessionMin, ts)))
            }
            if (rnd.nextInt(1000) < 10) collect(ts, rig.send(Signal.Charger(rnd.nextBoolean(), rnd.nextInt(5, 100), ts)))
            if (rnd.nextInt(1000) < 10) collect(ts, rig.send(Signal.MusicActive(rnd.nextBoolean(), ts)))
            if (rnd.nextInt(1000) < 3) collect(ts, rig.send(Signal.BatteryLow(rnd.nextInt(3, 15), ts)))
            if (rnd.nextInt(1000) < 5) {
                collect(ts, rig.send(Signal.ScreenState(rnd.nextBoolean(), ts)))
                if (rnd.nextBoolean()) fg = null
            }
            if (rnd.nextInt(1000) < 2) collect(ts, rig.send(Signal.SpriteTapped(rnd.nextInt(1, 5), ts)))
        }

        assertTrue("simulation too quiet to mean anything: ${spoken.size}", spoken.size > 60)
        val z = ZoneOffset.UTC
        val perDay = spoken.groupingBy { TimeUtil.epochDay(it.first, z) }.eachCount()
        assertTrue("daily cap exceeded: ${perDay.values.max()}", perDay.values.all { it <= BrainConfig.maxUnpromptedPerDay(NagLevel.NAGGY) })
        spoken.zipWithNext().forEach { (a, b) ->
            assertTrue("gap under the Naggy minimum", b.first - a.first >= BrainConfig.minGapMinutes(NagLevel.NAGGY) * 60_000L)
        }
        spoken.forEach { (ts, _) ->
            val m = TimeUtil.minuteOfDay(ts, z)
            assertFalse("unprompted line in quiet hours at $m", TimeUtil.inWindow(m, settings.quietStartMin, settings.quietEndMin))
        }
        assertTrue(rig.brain.drainEvents().none { it.pkg == "bank.app" })
    }

    @Test fun simulationIsDeterministic() {
        fun run(): List<String?> {
            val rig = Rig(UserSettings(), BrainState())
            val rnd = Random(7)
            val out = mutableListOf<String?>()
            for (i in 0 until 3000) {
                val ts = at(0, 8) + i * 60_000L
                if (rnd.nextInt(10) == 0) out += rig.send(Signal.ForegroundApp("a${rnd.nextInt(3)}", AppCategory.OTHER, ts)).map { it.lineId }
                if (rnd.nextInt(50) == 0) out += rig.send(Signal.Charger(true, 40, ts)).map { it.lineId }
            }
            return out
        }
        assertEquals(run(), run())
    }
}
