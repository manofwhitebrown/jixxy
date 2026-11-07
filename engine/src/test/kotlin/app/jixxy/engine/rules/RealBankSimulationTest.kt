package app.jixxy.engine.rules

import app.jixxy.engine.api.*
import app.jixxy.engine.lines.Line
import app.jixxy.engine.lines.LineBank
import app.jixxy.engine.lines.LineBankLoader
import app.jixxy.engine.selector.BankLinePicker
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.time.ZoneOffset
import kotlin.random.Random

/** T-E3-4: a month of seeded behaviour against the REAL English bank. Skipped (passes) if the bank folder is not found. */
class RealBankSimulationTest {
    private val z = ZoneOffset.UTC
    private val minute = 60_000L

    private val bank: LineBank? = listOf(File("../app/src/main/assets/lines/en"), File("app/src/main/assets/lines/en"))
        .firstOrNull { it.isDirectory }?.let { LineBankLoader.fromDirectory(it) }

    private class Shown(val ts: Long, val r: Reaction, val line: Line?)

    private class Persona(
        val name: String, val seed: Int, val roast: RoastLevel, val leavePct: Int, val openPct: Int,
        val nightOwl: Boolean, val nag: NagLevel = NagLevel.NAGGY, val chargePerMille: Int = 8, val napsPerDay: Int = 1, val callsPerDay: Int = 2
    )

    private class Result(val shown: List<Shown>, val napWindows: List<LongRange>, val callWindows: List<LongRange>, val brainEvents: List<UsageEvent>, val days: Int)

    private val apps = listOf(
        "reels.app" to AppCategory.VIDEO, "chat.app" to AppCategory.MESSAGING, "game.app" to AppCategory.GAME,
        "insta.app" to AppCategory.SOCIAL, "shop.app" to AppCategory.SHOPPING, "web.app" to AppCategory.BROWSER,
        "notes.app" to AppCategory.PRODUCTIVITY, "bank.app" to AppCategory.PROTECTED
    )
    private val pauseTargets = setOf("reels.app", "insta.app", "game.app")

    private fun simulate(bank: LineBank, p: Persona, days: Int): Result {
        val settings = UserSettings(roast = p.roast, targetApps = pauseTargets, nagLevel = p.nag)
        val byId = bank.lines.associateBy { it.id }
        val brain = DefaultJixxyBrain(BrainState(), { settings }, BankLinePicker(bank, Random(p.seed)), { it }, z)
        val rnd = Random(p.seed)
        val shown = mutableListOf<Shown>()
        val naps = mutableListOf<LongRange>()
        val calls = mutableListOf<LongRange>()
        var fg: String? = null
        var sessionMin = 0
        var pausePkg: String? = null
        var napUntil = 0L
        var callUntil = 0L
        fun send(ts: Long, s: Signal) {
            for (r in brain.onSignal(s)) {
                if (r.kind == ReactionKindOut.PAUSE_PROMPT) pausePkg = r.pause!!.pkg
                shown += Shown(ts, r, r.lineId?.let { byId[it] })
            }
        }
        val start = at(0, 0)
        for (i in 0 until days * 24 * 60) {
            val ts = start + i * minute
            val hour = (i / 60) % 24
            val awake = if (p.nightOwl) hour >= 11 || hour < 4 else hour in 8..23
            send(ts, Signal.Heartbeat(ts))
            if (callUntil != 0L && ts >= callUntil) { send(ts, Signal.CallState(false, ts)); callUntil = 0L }
            if (callUntil == 0L && awake && rnd.nextInt(24 * 60 / maxOf(1, p.callsPerDay)) == 0) {
                callUntil = ts + 10 * minute; calls += ts..callUntil; send(ts, Signal.CallState(true, ts))
            }
            pausePkg?.let {
                pausePkg = null
                val choice = if (rnd.nextInt(100) < p.leavePct) PauseChoiceKind.LEAVE else PauseChoiceKind.STAY
                send(ts, Signal.PauseChoice(it, choice, ts))
            }
            if (!awake) { if (fg != null) { send(ts, Signal.ScreenState(false, ts)); fg = null }; continue }
            send(ts, Signal.ScreenState(true, ts))
            if (rnd.nextInt(100) < p.openPct) {
                val (pkg, cat) = apps[rnd.nextInt(apps.size)]
                fg = pkg; sessionMin = 0
                send(ts, Signal.ForegroundApp(pkg, cat, ts))
            } else if (fg != null) {
                sessionMin++
                send(ts, Signal.SessionTick(fg!!, sessionMin, ts))
            }
            if (rnd.nextInt(1000) < p.chargePerMille) send(ts, Signal.Charger(rnd.nextBoolean(), rnd.nextInt(5, 100), ts))
            if (rnd.nextInt(1000) < 6) send(ts, Signal.MusicActive(rnd.nextBoolean(), ts))
            if (rnd.nextInt(1000) < 3) send(ts, Signal.BatteryLow(rnd.nextInt(3, 15), ts))
            if (rnd.nextInt(1000) < 3) send(ts, Signal.SpriteTapped(rnd.nextInt(1, 5), ts))
            if (napUntil != 0L && ts >= napUntil) { send(ts, Signal.WakeRequested(ts)); napUntil = 0L }
            if (napUntil == 0L && p.napsPerDay > 0 && rnd.nextInt(24 * 60 / maxOf(1, p.napsPerDay)) == 0) {
                napUntil = ts + 30 * minute; naps += ts..napUntil; send(ts, Signal.NapRequested(30, ts))
            }
        }
        return Result(shown, naps, calls, brain.drainEvents(), days)
    }

    private val personas = listOf(
        Persona("heavy reels", 11, RoastLevel.NORMAL, 40, 9, false),
        Persona("night owl", 22, RoastLevel.SAVAGE, 60, 6, true),
        Persona("light user", 33, RoastLevel.GENTLE, 80, 2, false, napsPerDay = 0, callsPerDay = 1),
        Persona("student", 44, RoastLevel.NORMAL, 70, 5, false),
        Persona("calm reader", 55, RoastLevel.GENTLE, 70, 5, false, nag = NagLevel.CALM),
        Persona("chaos gremlin", 66, RoastLevel.SAVAGE, 50, 9, false, nag = NagLevel.CHAOS)
    )

    private val unpromptedTriggers = setOf(
        Trigger.WELCOME, Trigger.APP_OPEN, Trigger.SESSION_LONG, Trigger.LATE_NIGHT, Trigger.CHARGER_ON,
        Trigger.BATTERY_LOW, Trigger.MUSIC, Trigger.CALLBACK, Trigger.ALMOST_CARING
    )

    private fun forEachPersona(check: (Persona, Result) -> Unit) {
        val b = bank ?: return
        personas.forEach { check(it, simulate(b, it, 30)) }
    }

    private fun Shown.unprompted() = line?.trigger in unpromptedTriggers

    @Test fun capsGapsAndQuietHoursHold() = forEachPersona { p, res ->
        val settings = UserSettings(nagLevel = p.nag)
        val spoken = res.shown.filter { it.unprompted() }
        assertTrue("${p.name}: simulation too quiet (${spoken.size})", spoken.size > 20)
        spoken.groupingBy { TimeUtil.epochDay(it.ts, z) }.eachCount().forEach { (d, n) ->
            assertTrue("${p.name}: $n unprompted on day $d", n <= BrainConfig.maxUnpromptedPerDay(p.nag))
        }
        spoken.zipWithNext().forEach { (a, b) ->
            assertTrue("${p.name}: gap under ${BrainConfig.minGapMinutes(p.nag)} min", b.ts - a.ts >= BrainConfig.minGapMinutes(p.nag) * minute)
        }
        spoken.forEach {
            val m = TimeUtil.minuteOfDay(it.ts, z)
            assertFalse("${p.name}: unprompted line in quiet hours at minute $m", TimeUtil.inWindow(m, settings.quietStartMin, settings.quietEndMin))
        }
    }

    @Test fun calmNeverShowsNagOnlyLinesInUnpromptedSpeech() = forEachPersona { p, res ->
        if (p.nag != NagLevel.CALM) return@forEachPersona
        val bad = res.shown.filter { it.unprompted() && "nag_only" in it.line!!.tags }
        assertTrue("${p.name}: ${bad.size} nag_only lines at Calm", bad.isEmpty())
    }

    @Test fun napsCallsAndProtectedAppsStaySilent() = forEachPersona { p, res ->
        assertTrue("${p.name}: no protected-app events", res.brainEvents.none { it.pkg == "bank.app" })
        // Replies to something the user just did (NAP/WAKE, Stay/Leave, praise) are allowed; everything else must stay silent.
        val replies = setOf(Trigger.NAP, Trigger.WAKE, Trigger.PAUSE_STAY, Trigger.PAUSE_LEAVE, Trigger.IMPRESSED)
        res.shown.filter { it.line != null && it.line.trigger !in replies }.forEach { x ->
            res.callWindows.forEach { w -> assertFalse("${p.name}: spoke during a call at ${x.ts}", x.ts in w.first until w.last) }
            // A line in the same minute as the nap tap was spoken before the tap, so the window opens just after it.
            res.napWindows.forEach { w -> assertFalse("${p.name}: spoke during a nap at ${x.ts}", x.ts > w.first && x.ts < w.last) }
        }
    }

    /** Honest guard, not the 7-day goal: the unprompted repeat rate measured on the current picker must not get worse. */
    @Test fun unpromptedRepeatsWithinAWeekStayBelowMeasuredBaseline() = forEachPersona { p, res ->
        val last = mutableMapOf<String, Long>()
        var repeats = 0
        val spoken = res.shown.filter { it.unprompted() }
        for (x in spoken) {
            val id = x.line!!.id
            last[id]?.let { if (x.ts - it < 7 * 86_400_000L) repeats++ }
            last[id] = x.ts
        }
        val rate = repeats.toDouble() / spoken.size
        println("SIM ${p.name}: unprompted=${spoken.size} repeats<7d=$repeats rate=${"%.2f".format(rate)}")
        assertTrue("${p.name}: repeat rate $rate", rate <= REPEAT_RATE_BASELINE)
    }

    @Test fun pauseTiersRarelyEscalateEarly() = forEachPersona { p, res ->
        val events = res.brainEvents
        val shownEvents = events.withIndex().filter { it.value.type == UsageEventType.PAUSE_SHOWN }
        val cards = res.shown.filter { it.r.kind == ReactionKindOut.PAUSE_PROMPT }
        assertEquals(shownEvents.size, cards.size)
        var match = 0
        var early = 0
        var total = 0
        shownEvents.zip(cards).forEach { (ev, card) ->
            val e = ev.value
            val followUp = events.none { it.type == UsageEventType.FOREGROUND && it.pkg == e.pkg && it.ts == e.ts }
            val l = card.line ?: return@forEach
            if (followUp) return@forEach
            // D-024 return prompts are chosen by "came straight back after a Leave", not by the day's count, so they
            // carry no tier tag and say nothing about whether the tier ladder holds.
            if ("after_leave" in l.tags) return@forEach
            val expected = minOf((e.value ?: 1) - 1, 2)
            val shownTier = (0..2).firstOrNull { "tier$it" in l.tags }
            if (shownTier != null && shownTier > expected) early++
            total++
            if (shownTier == expected) match++
        }
        println("SIM ${p.name}: pause tier match $match/$total, early escalation $early")
        assertTrue("${p.name}: early escalation $early/$total", early <= total * EARLY_TIER_CEILING)
        // At Calm the pool has few higher-tier lines (most are nag_only), so escalation is deliberately flatter there.
        if (p.nag != NagLevel.CALM) assertTrue("${p.name}: tier match too low $match/$total", match >= total * TIER_MATCH_FLOOR)
    }

    @Test fun praiseStaysRare() = forEachPersona { p, res ->
        val n = res.shown.count { it.line?.trigger == Trigger.IMPRESSED }
        println("SIM ${p.name}: IMPRESSED $n in ${res.days} days")
        assertTrue("${p.name}: $n IMPRESSED lines", n <= res.days / BrainConfig.impressedGapDays + 1)
    }

    private companion object {
        /** Measured worst case 0.55 (light user) on 2026-11-07. The goal (no repeat within 7 days) is NOT met; see coord/status/ENGINE.md. */
        /** Raised from 0.6 when D-020 made Jixxy naggy (more lines from the same bank); WRITER to grow the bank, ENGINE to re-tighten. */
        const val REPEAT_RATE_BASELINE = 0.85
        const val TIER_MATCH_FLOOR = 0.5
        /** A higher tier than the prompt count allows is only served when no lower-tier line fits; keep that rare. */
        const val EARLY_TIER_CEILING = 0.05
    }
}
