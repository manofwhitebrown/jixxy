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
    const val maxEnergy = 100
    /** Cumulative bond XP needed to reach each level (never lost). */
    const val bondRoommateXp = 100
    const val bondFriendXp = 400
    const val bondRideOrDieXp = 1200

    /** Maps bond XP to a [Bond] level. */
    fun bondFor(xp: Int): Bond = when {
        xp >= bondRideOrDieXp -> Bond.RIDE_OR_DIE
        xp >= bondFriendXp -> Bond.FRIEND
        xp >= bondRoommateXp -> Bond.ROOMMATE
        else -> Bond.STRANGER
    }
}
