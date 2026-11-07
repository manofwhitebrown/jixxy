package app.jixxy.engine.review

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.Bond
import app.jixxy.engine.api.BrainConfig
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.RoastLevel
import app.jixxy.engine.api.Trigger
import app.jixxy.engine.api.UsageEvent
import app.jixxy.engine.api.UsageEventType
import app.jixxy.engine.rules.TimeUtil
import app.jixxy.engine.selector.LinePicker
import app.jixxy.engine.selector.LineQuery
import app.jixxy.engine.selector.PickedLine
import java.time.ZoneId

/** Minutes spent in one app during the week, from logged `SESSION_MINUTES` events. */
data class AppMinutes(val pkg: String, val label: String, val minutes: Int)

/** Minutes on one local day (`epochDay` is the local day number). */
data class DayMinutes(val epochDay: Long, val minutes: Int)

/**
 * The numbers and words behind the shareable weekly card. Every number comes from recorded events.
 * @property topApps up to three apps by minutes, most first
 * @property lateNightMinutes tracked minutes inside the late-night window (23:00 to 01:00)
 * @property pausesFaced Pause cards shown; [pausesLeft] and [pausesStayed] are the user's answers
 * @property bestDay the active day with the fewest tracked minutes (null if fewer than two active days)
 * @property bondXpGained XP earned from logged events (daily visit, Leaves, laughs); [bondLevelUp] if that crossed a level
 * @property roast one weekly line (roast or quiet-week) with `{count}` = pauses faced; null if none eligible
 * @property compliment one earned compliment, only when the week supports it; null otherwise
 * @property activeDays distinct local days with at least one app open
 * @property isThin too little data to say much: fewer than [BrainConfig.weeklyMinActiveDays] active days, or under
 *   [BrainConfig.weeklyMinMinutes] tracked minutes with no Pause card. Thin weeks get the quiet-week line and no
 *   compliment; UI should present totals as "so far" rather than as a pattern.
 */
data class WeeklyReview(
    val fromTs: Long,
    val toTs: Long,
    val totalMinutes: Int,
    val topApps: List<AppMinutes>,
    val lateNightMinutes: Int,
    val pausesFaced: Int,
    val pausesLeft: Int,
    val pausesStayed: Int,
    val bestDay: DayMinutes?,
    val bondXpGained: Int,
    val bondLevelUp: Boolean,
    val roast: PickedLine?,
    val compliment: PickedLine?,
    val activeDays: Int = 0,
    val isThin: Boolean = false
) {
    /** True if there was nothing to report (no tracked time and no pauses). */
    val isQuiet: Boolean get() = totalMinutes == 0 && pausesFaced == 0
}

/** Builds a [WeeklyReview] from events. Pure and deterministic given the picker's randomness. */
class WeeklyReviewBuilder(
    private val picker: LinePicker,
    private val zone: ZoneId,
    private val appLabel: (String) -> String
) {
    /**
     * @param events events with `fromTs <= ts < toTs` (as returned by `StateStore.eventsBetween`)
     * @param bondXpNow current bond XP, to tell whether the week crossed a bond level
     * @param roast the user's roast level; the lines are filtered by it
     */
    fun build(
        events: List<UsageEvent>,
        fromTs: Long,
        toTs: Long,
        bondXpNow: Int,
        roast: RoastLevel,
        language: String = "en"
    ): WeeklyReview {
        val inRange = events.filter { it.ts in fromTs until toTs }

        val perApp = inRange.filter { it.type == UsageEventType.SESSION_MINUTES && it.pkg != null }
            .groupBy { it.pkg!! }.mapValues { (_, v) -> v.sumOf { it.value ?: 1 } }
        val top = perApp.entries.sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .take(3).map { AppMinutes(it.key, appLabel(it.key), it.value) }
        val totalMinutes = perApp.values.sum()

        val lateNight = inRange.filter { it.type == UsageEventType.SESSION_MINUTES }.sumOf {
            if (isLateNight(it.ts)) it.value ?: 1 else 0
        }

        val faced = inRange.count { it.type == UsageEventType.PAUSE_SHOWN }
        val left = inRange.count { it.type == UsageEventType.PAUSE_LEAVE }
        val stayed = inRange.count { it.type == UsageEventType.PAUSE_STAY }

        val byDay = inRange.filter { it.type == UsageEventType.SESSION_MINUTES }
            .groupBy { TimeUtil.epochDay(it.ts, zone) }.mapValues { (_, v) -> v.sumOf { it.value ?: 1 } }
        val best = if (byDay.size >= 2) byDay.entries.minWith(compareBy({ it.value }, { it.key })).let { DayMinutes(it.key, it.value) } else null

        val activeDays = inRange.filter { it.type == UsageEventType.FOREGROUND }.map { TimeUtil.epochDay(it.ts, zone) }.toSet().size
        val laughs = inRange.count { it.type == UsageEventType.LINE_REACTION && it.value == 1 }
        val xp = activeDays * BrainConfig.bondXpDaily + left * BrainConfig.bondXpLeave + laughs * BrainConfig.bondXpLaugh
        val levelUp = BrainConfig.bondFor(bondXpNow) != BrainConfig.bondFor((bondXpNow - xp).coerceAtLeast(0))

        val bond = BrainConfig.bondFor(bondXpNow)
        fun pickTagged(tag: String): PickedLine? = picker.pick(
            LineQuery(Trigger.WEEKLY, AppCategory.OTHER, Mood.CHILL, roast, bond, language,
                mapOf("count" to faced.toString(), "time" to ""), requiredTag = tag)
        )
        val thin = activeDays < BrainConfig.weeklyMinActiveDays || (totalMinutes < BrainConfig.weeklyMinMinutes && faced == 0)
        val roastLine = if (faced == 0) pickTagged("weekly_quiet") else pickTagged("weekly_roast")
        val earned = !thin && left >= 1 && left >= stayed
        val compliment = if (earned) pickTagged("weekly_praise") else null

        return WeeklyReview(
            fromTs, toTs, totalMinutes, top, lateNight, faced, left, stayed, best, xp, levelUp, roastLine, compliment,
            activeDays = activeDays, isThin = thin
        )
    }

    private fun isLateNight(ts: Long): Boolean =
        TimeUtil.inWindow(TimeUtil.minuteOfDay(ts, zone), BrainConfig.lateNightStartMin, BrainConfig.lateNightEndMin)
}
