package app.jixxy.engine.api

/** Tunable constants for the brain. Keep all magic numbers here. */
object BrainConfig {
    /** Hard cap on unprompted comments per local day. Pauses are user-opted-in and not counted. */
    const val maxUnpromptedPerDay = 6
    /** Minimum minutes between unprompted comments. */
    const val minGapMinutes = 20
    /** Number of recently shown line ids the selector will not repeat. */
    const val dedupeWindow = 200
    /** Default minutes of grace after the user taps Stay (PauseSpec.grantMinutes can override). */
    const val stayGraceMinutes = 5
    /** Quiet minutes granted after the user taps Leave. */
    const val leaveGraceMinutes = 3
    /** Late-night window start, minutes after local midnight (23:00). */
    const val lateNightStartMin = 23 * 60
    /** Late-night window end, minutes after local midnight (01:00). */
    const val lateNightEndMin = 60
    /** Max spoken line length in characters (validator). */
    const val maxLineChars = 90
    /** Max user-chosen Pause apps. */
    const val maxTargetApps = 3
    /** Memory retention for usage events. */
    const val retentionDays = 30
    /** Minimum days between ALMOST_CARING lines. */
    const val almostCaringGapDays = 3
    /** Minutes between two spoken reactions to being dropped (the falling animation always plays). */
    const val droppedLineCooldownMin = 2
    const val maxEnergy = 100
    /** Cumulative bond XP needed to reach each level (never lost). */
    const val bondRoommateXp = 100
    const val bondFriendXp = 400
    const val bondRideOrDieXp = 1200


    /** Energy a spoken (non-Pause) line costs. At 0 Jixxy is "on break". */
    const val energyPerLine = 8
    /** Minutes of real time per +1 energy point. */
    const val regenMinutesPerPoint = 6
    /** Energy restored when the charger is plugged in. */
    const val chargerEnergyBonus = 10
    /** Max energy restored by a full nap. */
    const val napEnergyBonus = 30
    /** Energy below this makes the mood SLEEPY. */
    const val lowEnergy = 20
    /** Leaving an app after at least this many minutes makes the next app change comment-worthy. */
    const val longSessionMin = 20
    /** Session length (minutes) at which SESSION_LONG remarks begin. */
    const val sessionLongMin = 30
    /** Minutes between repeated SESSION_LONG remarks within one session. */
    const val sessionLongRepeatMin = 30
    /** Minutes a temporary mood (GRUMPY, SMUG, IMPRESSED) lasts. */
    const val moodHoldMin = 10
    /** Consecutive taps that make Jixxy GRUMPY. */
    const val pokeGrumpyThreshold = 3
    /** Consecutive Leaves that earn an IMPRESSED line. */
    const val impressedLeaveStreak = 3
    const val bondXpDaily = 3
    const val bondXpLaugh = 2
    const val bondXpLeave = 5
    const val bondXpImpressed = 10

    /** Callback thresholds: a counted fact is only quoted once it is at least this big. */
    const val callbackVisitsDay = 4
    const val callbackVisitsWeek = 10
    const val callbackPausesDay = 3
    const val callbackLeavesWeek = 3
    const val callbackStaysWeek = 3
    const val callbackLateOpensWeek = 3
    /** Minutes in one app on one day before `fact:top_app_minutes_day` may be quoted. */
    const val callbackTopAppMinutesDay = 120
    /** Distinct late nights in the last 7 days before the almost-caring line may appear. */
    const val almostCaringLateNights = 3

    /** Minimum minutes between two unprompted lines of the same [trigger]. */
    fun unpromptedCooldownMin(trigger: Trigger): Int = when (trigger) {
        Trigger.APP_OPEN -> 90
        Trigger.SESSION_LONG -> 45
        Trigger.LATE_NIGHT -> 360
        Trigger.CHARGER_ON -> 180
        Trigger.BATTERY_LOW -> 240
        Trigger.MUSIC -> 240
        Trigger.CALLBACK -> 180
        else -> 0
    }

    /** How long a speech bubble stays for [text]; 0 for no text. */
    fun bubbleMs(text: String?): Long =
        if (text.isNullOrEmpty()) 0L else minOf(7000L, 3000L + 45L * text.length)

    /** Maps bond XP to a [Bond] level. */
    fun bondFor(xp: Int): Bond = when {
        xp >= bondRideOrDieXp -> Bond.RIDE_OR_DIE
        xp >= bondFriendXp -> Bond.FRIEND
        xp >= bondRoommateXp -> Bond.ROOMMATE
        else -> Bond.STRANGER
    }
}
