package app.jixxy.platform

/**
 * Pure nap/visibility rules (T-P2-1). No Android imports so it is fully unit-testable.
 *
 * Decisions (documented for the Manager):
 *  - A nap is "active" only while `napUntilTs` is strictly in the future. An expired value never hides the sprite,
 *    even if it was persisted before the service stopped.
 *  - An explicit user Wake always clears the nap.
 *  - Stop is a clean slate: it also clears the nap.
 *  - Blind mode stops detection and logging, but does not hide the sprite (he just stays quiet).
 */
object NapPolicy {
    const val NAP_MS = 60 * 60 * 1000L

    /** True while a nap is still running at [now]. */
    fun isNapping(napUntilTs: Long?, now: Long): Boolean = napUntilTs != null && napUntilTs > now

    /** Whether the sprite window should be visible. [blindMode] is accepted on purpose: it does not change visibility. */
    @Suppress("UNUSED_PARAMETER")
    fun shouldShowSprite(screenOn: Boolean, napUntilTs: Long?, now: Long, blindMode: Boolean): Boolean =
        screenOn && !isNapping(napUntilTs, now)

    /** New `napUntilTs` after the user asks for a nap of [ms] milliseconds. */
    fun napUntil(now: Long, ms: Long = NAP_MS): Long = now + ms

    /** New `napUntilTs` after an explicit Wake. */
    fun afterWake(): Long? = null

    /** New `napUntilTs` after Stop (clean slate). */
    fun afterStop(): Long? = null

    /** Milliseconds until the wake-up timer should fire, or 0 when no nap is active. */
    fun remainingMs(napUntilTs: Long?, now: Long): Long =
        if (isNapping(napUntilTs, now)) napUntilTs!! - now else 0L
}
