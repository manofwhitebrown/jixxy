package app.jixxy.platform

/**
 * Pure "may Jixxy make a sound right now?" rule (D-021), so the manners are unit-tested before any audio is wired.
 * Every input must be true for a sound; any doubt means silence. One cue per bubble is enforced with [lastCueBubbleId].
 */
object SoundPolicy {
    fun allowed(
        soundsEnabled: Boolean,
        ringerNormal: Boolean,
        doNotDisturbOff: Boolean,
        inCall: Boolean,
        musicPlaying: Boolean,
        quietHours: Boolean,
        napping: Boolean,
        blindMode: Boolean,
        bubbleId: String?,
        lastCueBubbleId: String?,
    ): Boolean =
        soundsEnabled && ringerNormal && doNotDisturbOff && !inCall && !musicPlaying &&
            !quietHours && !napping && !blindMode && (bubbleId == null || bubbleId != lastCueBubbleId)

    /** True inside quiet hours; the window may wrap past midnight (default 01:00 to 07:00, or 22:00 to 06:00). */
    fun inQuietHours(minuteOfDay: Int, startMin: Int, endMin: Int): Boolean = when {
        startMin == endMin -> false
        startMin < endMin -> minuteOfDay in startMin until endMin
        else -> minuteOfDay >= startMin || minuteOfDay < endMin
    }
}
