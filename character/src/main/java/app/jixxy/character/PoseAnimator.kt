package app.jixxy.character

import app.jixxy.engine.api.CharacterState
import app.jixxy.engine.api.CharacterState.*
import kotlin.math.exp
import kotlin.random.Random

/**
 * Animation state machine behind [JixxyView]. Pure Kotlin so it can be unit-tested on the JVM.
 *
 * Priority: DRAGGED > POKED > scripted reactions > IDLE. A scripted request that arrives while a higher
 * priority state is running is remembered for [PENDING_TTL_MS] and played afterwards. Every state blends
 * into the next through exponential smoothing, so nothing snaps. Blinking is a separate overlay that is
 * scheduled every 3 to 6 seconds while the eyes are allowed to blink.
 *
 * Call [resume] before the first [step] and after any pause. Time is any monotonic clock in milliseconds.
 */
internal class PoseAnimator(private val random: Random = Random.Default) {

    /** The live, smoothed pose. */
    val pose = Pose()

    /** The state currently playing. */
    var state: CharacterState = IDLE
        private set

    /** Poke level 1..3 of the current or last POKED state. */
    var pokeLevel = 1
        private set

    /** 0 when the eyes are not blinking, up to 1 at the blink's peak. */
    var blinkEnvelope = 0f
        private set

    /** Free-running clock in seconds, wrapping hourly, for ongoing effects such as floating Zs. */
    var clockSec = 0f
        private set

    private val target = Pose()
    private var stateStartMs = 0L
    private var lastStepMs = 0L
    private var pending: CharacterState? = null
    private var pendingAtMs = 0L
    private var nextBlinkMs = 0L
    private var blinkStartMs = NONE
    private var doubleBlink = false

    /** Eye closure including the blink overlay, 0 = open, 1 = shut. */
    fun lid(): Float = pose[P.LID] + (1f - pose[P.LID]) * blinkEnvelope

    /** How long the host should wait before the next frame. Calm states need fewer frames. */
    val frameIntervalMs: Long
        get() = if (blinkEnvelope == 0f && state in CALM) CALM_FRAME_MS else ACTIVE_FRAME_MS

    /** Marks the start (or restart after a pause) of ticking at [nowMs]. */
    fun resume(nowMs: Long) {
        lastStepMs = nowMs
        if (nextBlinkMs < nowMs) nextBlinkMs = nowMs + random.nextLong(1_500L, 3_000L)
    }

    /**
     * A state requested from outside (the brain). DRAGGED is ignored because the view owns touch, and
     * POKED is ignored while already poked because the view shows pokes itself on every tap.
     */
    fun request(requested: CharacterState, nowMs: Long) {
        val current = priority(state)
        when {
            requested == BLINK -> if (canBlink() && blinkStartMs == NONE) startBlink(nowMs)
            requested == DRAGGED -> Unit
            requested == POKED -> if (current < 2) enter(POKED, nowMs)
            requested == IDLE -> if (current < 2) enter(IDLE, nowMs)
            current >= 2 -> {
                pending = requested
                pendingAtMs = nowMs
            }
            requested == state && durationMs(requested, pokeLevel) == null -> Unit
            else -> enter(requested, nowMs)
        }
    }

    /** A tap on the sprite; [level] (consecutive taps, capped at 3) sets how annoyed the reaction is. */
    fun poke(level: Int, nowMs: Long) {
        if (priority(state) >= 3) return
        pokeLevel = level.coerceIn(1, 3)
        enter(POKED, nowMs)
    }

    /** The finger started or stopped dragging. Letting go plays the landing. */
    fun setDragging(dragging: Boolean, nowMs: Long) {
        if (dragging) enter(DRAGGED, nowMs) else if (state == DRAGGED) enter(FALLING, nowMs)
    }

    /** Advances the animation to [nowMs]. */
    fun step(nowMs: Long) {
        val dtMs = (nowMs - lastStepMs).coerceIn(0L, MAX_DT_MS)
        lastStepMs = nowMs
        val duration = durationMs(state, pokeLevel)
        if (duration != null && nowMs - stateStartMs >= duration) rest(nowMs)
        clockSec = (nowMs % CLOCK_WRAP_MS) / 1000f
        StatePoses.fill(state, (nowMs - stateStartMs) / 1000f, clockSec, pokeLevel, target)
        pose.approach(target, 1f - exp(-smoothing(state) * dtMs / 1000f))
        updateBlink(nowMs)
    }

    private fun enter(next: CharacterState, nowMs: Long) {
        state = next
        stateStartMs = nowMs
        if (!canBlink()) {
            blinkStartMs = NONE
            blinkEnvelope = 0f
        }
    }

    private fun rest(nowMs: Long) {
        val queued = pending
        pending = null
        enter(if (queued != null && nowMs - pendingAtMs <= PENDING_TTL_MS) queued else IDLE, nowMs)
    }

    private fun canBlink(): Boolean = when (state) {
        SLEEP, SNORE, PANIC, DRAGGED, IMPRESSED -> false
        else -> true
    }

    private fun startBlink(nowMs: Long) {
        blinkStartMs = nowMs
        doubleBlink = random.nextFloat() < DOUBLE_BLINK_CHANCE
    }

    private fun updateBlink(nowMs: Long) {
        if (blinkStartMs != NONE) {
            val x = (nowMs - blinkStartMs) / BLINK_MS.toFloat()
            if (x >= 1f) {
                blinkStartMs = NONE
                blinkEnvelope = 0f
                nextBlinkMs = if (doubleBlink) nowMs + 220L else nowMs + randomBlinkGap()
                doubleBlink = false
            } else {
                blinkEnvelope = if (x < 0.4f) x / 0.4f else (1f - x) / 0.6f
            }
        } else {
            blinkEnvelope = 0f
            if (canBlink() && nowMs >= nextBlinkMs) startBlink(nowMs)
        }
    }

    private fun randomBlinkGap(): Long =
        random.nextLong(3_000L, 6_000L) * (if (state == JUDGING) 2L else 1L)

    private fun priority(s: CharacterState): Int = when (s) {
        DRAGGED -> 3
        POKED -> 2
        IDLE, BLINK -> 0
        else -> 1
    }

    /** How long a state plays before returning to rest; null means it holds until replaced. */
    private fun durationMs(s: CharacterState, level: Int): Long? = when (s) {
        YAWN -> 1_800L
        WAKE_GRUMPY -> 1_400L
        SMUG -> 1_800L
        PANIC -> 2_400L
        HAPPY -> 1_500L
        IMPRESSED -> 2_800L
        POKED -> 600L + 250L * level
        FALLING -> 800L
        SNACK -> 2_200L
        STRETCH -> 1_600L
        JUDGING -> 8_000L
        PEEK -> 2_500L
        CHARGING -> 4_000L
        LOW_BATTERY -> 4_000L
        IDLE, BLINK, SLEEP, SNORE, DRAGGED, MUSIC, SOFT -> null
    }

    /** Smoothing speed per second: higher means the pose follows its target faster. */
    private fun smoothing(s: CharacterState): Float = when (s) {
        POKED -> 20f
        FALLING -> 22f
        DRAGGED, PANIC -> 14f
        WAKE_GRUMPY -> 12f
        SLEEP, SNORE -> 3.5f
        IMPRESSED, SOFT -> 4.5f
        else -> 8f
    }

    companion object {
        const val PENDING_TTL_MS = 2_000L
        const val ACTIVE_FRAME_MS = 33L
        const val CALM_FRAME_MS = 50L
        private const val BLINK_MS = 150L
        private const val MAX_DT_MS = 100L
        private const val CLOCK_WRAP_MS = 3_600_000L
        private const val DOUBLE_BLINK_CHANCE = 0.15f
        private const val NONE = Long.MIN_VALUE
        private val CALM = setOf(IDLE, SLEEP, SNORE, SOFT, JUDGING)
    }
}
