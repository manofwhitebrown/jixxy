package app.jixxy.character

/**
 * Counts consecutive taps. A tap continues the run when it comes within [windowMs] of the previous tap;
 * otherwise a new run starts at 1. Pure logic, no Android dependencies.
 */
internal class TapCounter(private val windowMs: Long = DEFAULT_WINDOW_MS) {
    private var count = 0
    private var lastTapMs = NO_TAP

    /** Registers a tap at [nowMs] (any monotonic clock) and returns the current run length. */
    fun register(nowMs: Long): Int {
        count = if (lastTapMs != NO_TAP && nowMs - lastTapMs <= windowMs) count + 1 else 1
        lastTapMs = nowMs
        return count
    }

    /** Forgets the current run. */
    fun reset() {
        count = 0
        lastTapMs = NO_TAP
    }

    companion object {
        const val DEFAULT_WINDOW_MS = 1_500L
        private const val NO_TAP = Long.MIN_VALUE
    }
}
