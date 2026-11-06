package app.jixxy.character

import app.jixxy.engine.api.CharacterState
import app.jixxy.engine.api.CharacterState.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.sin

/**
 * The target pose of every [CharacterState] as a function of time. Pure maths, no Android.
 * Restraint is the product: small motions, understatement, one idea per state.
 */
internal object StatePoses {
    private const val TAU = (2.0 * PI).toFloat()

    /**
     * Fills [o] with the pose [state] wants at [t] seconds after entering it. [clock] is a free-running
     * clock (seconds, wraps hourly) for ongoing cycles such as breathing. [level] is the poke level 1..3.
     */
    fun fill(state: CharacterState, t: Float, clock: Float, level: Int, o: Pose) {
        o.setNeutral()
        when (state) {
            IDLE, BLINK, PEEK, CHARGING, LOW_BATTERY -> {  // D-010 states: image swap carries the look, motion stays calm
                breathe(o, clock, 3.2f, 0.018f)
                o[P.TAIL] = 2.5f * wave(0.2f, clock)
            }
            YAWN -> {
                val e = bump(t / 1.8f)
                o[P.OPEN] = 0.9f * e
                o[P.LID] = 0.5f + 0.4f * e
                o[P.SY] = 1f + 0.06f * e
                o[P.SX] = 1f - 0.03f * e
                o[P.BROW_L] = 1.5f * e
                o[P.BROW_R] = 1.5f * e
                o[P.SMILE] = 1f - 1.4f * e
                o[P.ARM_L] = 14f * e
                o[P.ARM_R] = 14f * e
            }
            SLEEP -> {
                breathe(o, clock, 4.2f, 0.03f)
                sleepy(o)
                o[P.TAIL] = 1.5f * wave(0.1f, clock)
            }
            SNORE -> {
                breathe(o, clock, 2.6f, 0.05f)
                sleepy(o)
                o[P.OPEN] = 0.3f + 0.25f * wave(1f / 2.6f, clock)
                o[P.ZS] = 1f
            }
            WAKE_GRUMPY -> {
                val decay = exp(-t * 4f)
                o[P.LID] = 0.55f
                o[P.BROW_TILT] = -14f
                o[P.BROW_L] = -1.5f
                o[P.BROW_R] = -1.5f
                o[P.SMILE] = -1f
                o[P.DX] = 1.2f * wave(11f, t) * exp(-t * 5f)
                o[P.SY] = 1f + 0.05f * decay
                o[P.ROT] = -2f
                o[P.TAIL] = -6f
                o[P.CHEEK] = 1f
            }
            SMUG -> {
                val e = ramp(t / 0.4f)
                o[P.LID] = 0.58f
                o[P.BROW_R] = 3.5f * e
                o[P.SMILE] = 1f + 1.4f * e
                o[P.ROT] = -4f * e
                o[P.PUPIL_DX] = -1.5f * e
                o[P.SY] = 1f + 0.02f * e
                o[P.TAIL] = 10f * wave(0.8f, t) * exp(-t * 1.2f)
                o[P.CHEEK] = 0.9f
            }
            PANIC -> {
                o[P.DX] = 1.4f * wave(9f, t)
                o[P.DY] = -1.2f * abs(wave(4.5f, t))
                o[P.LID] = 0.04f
                o[P.PUPIL_R] = 2.4f
                o[P.BROW_L] = 4f
                o[P.BROW_R] = 4f
                o[P.BROW_TILT] = 12f
                o[P.SMILE] = -0.5f
                o[P.OPEN] = 0.55f
                o[P.ARM_L] = 38f + 8f * wave(6f, t)
                o[P.ARM_R] = 38f + 8f * wave(6f, t + 0.05f)
                o[P.SWEAT] = ramp(t / 0.3f)
                o[P.CHEEK] = 0.3f
                o[P.SX] = 1.03f
                o[P.SY] = 0.98f
            }
            HAPPY -> {
                val hop = abs(wave(1.2f, t))
                val decay = exp(-t * 0.5f)
                o[P.DY] = -5f * hop * decay
                o[P.SY] = 1f + 0.05f * hop - 0.04f * (1f - hop) * decay
                o[P.LID] = 0.7f
                o[P.SMILE] = 2.5f
                o[P.CHEEK] = 1f
                o[P.BROW_L] = 1f
                o[P.BROW_R] = 1f
                o[P.TAIL] = 12f * wave(3f, t)
                o[P.ARM_L] = 18f + 8f * wave(2.4f, t)
                o[P.ARM_R] = 18f + 8f * wave(2.4f, t + 0.1f)
            }
            IMPRESSED -> {
                // Rare, so slow and clean: eyes open, a small stretch taller, a few sparkles. No wobble.
                val e = min(ramp(t / 0.7f), ramp((2.8f - t) / 0.6f))
                o[P.LID] = 0.5f - 0.4f * e
                o[P.PUPIL_R] = 3.6f + 0.8f * e
                o[P.BROW_L] = 3f * e
                o[P.BROW_R] = 3f * e
                o[P.SMILE] = 1f + 0.8f * e
                o[P.SY] = 1f + 0.06f * e
                o[P.DY] = -2f * e
                o[P.CHEEK] = 1f
                o[P.TAIL] = 8f * e
                o[P.ARM_L] = 6f * e
                o[P.ARM_R] = 6f * e
                o[P.SPARKLE] = e
            }
            POKED -> poked(o, t, level.coerceIn(1, 3))
            DRAGGED -> {
                o[P.SY] = 1.10f
                o[P.SX] = 0.94f
                o[P.LID] = 0.12f
                o[P.PUPIL_R] = 3.1f
                o[P.BROW_L] = 2f
                o[P.BROW_R] = 2f
                o[P.SMILE] = 0.2f
                o[P.OPEN] = 0.25f
                o[P.TAIL] = 18f + 6f * wave(3f, t)
                o[P.ARM_L] = 26f + 8f * wave(2.5f, t)
                o[P.ARM_R] = 26f + 8f * wave(2.5f, t + 0.15f)
                o[P.ROT] = 2.5f * wave(1.6f, t)
            }
            FALLING -> falling(o, t)
            SNACK -> {
                val chew = if (t > 0.3f) 0.5f + 0.5f * wave(3f, t) else 0f
                o[P.SNACK] = ramp(t / 0.25f) * (1f - 0.55f * ramp((t - 0.4f) / 1.4f)) * ramp((2.2f - t) / 0.2f)
                o[P.OPEN] = 0.3f * chew
                o[P.LID] = 0.5f + 0.15f * ramp(t / 0.5f)
                o[P.CHEEK] = 1f
                o[P.DY] = 0.7f * wave(3f, t)
                o[P.ARM_R] = 20f * ramp(t / 0.25f)
            }
            STRETCH -> {
                val e = bump(t / 1.6f)
                o[P.ARM_L] = 75f * e
                o[P.ARM_R] = 75f * e
                o[P.SY] = 1f + 0.10f * e
                o[P.SX] = 1f - 0.04f * e
                o[P.LID] = 0.5f + 0.4f * e
                o[P.OPEN] = 0.35f * e
                o[P.BROW_L] = 1.5f * e
                o[P.BROW_R] = 1.5f * e
                o[P.SMILE] = 1f - 0.5f * e
                o[P.ROT] = -2f * e
            }
            MUSIC -> {
                val beat = wave(1.7f, clock)
                o[P.SY] = 1f + 0.03f * beat
                o[P.DY] = -1.2f * abs(beat)
                o[P.ROT] = 3f * wave(0.85f, clock)
                o[P.NOTES] = 1f
                o[P.LID] = 0.5f
                o[P.SMILE] = 2f
                o[P.CHEEK] = 0.9f
                o[P.TAIL] = 8f * beat
                o[P.ARM_L] = 8f + 8f * beat
                o[P.ARM_R] = 8f - 8f * beat
            }
            JUDGING -> {
                // The silent stare: almost nothing moves. That is the joke.
                breathe(o, clock, 4f, 0.009f)
                o[P.LID] = 0.6f
                o[P.BROW_L] = -0.5f
                o[P.BROW_R] = -0.5f
                o[P.BROW_TILT] = 0f
                o[P.PUPIL_DY] = -1.2f
                o[P.PUPIL_R] = 3.2f
                o[P.SMILE] = 0.1f
                o[P.CHEEK] = 0.4f
            }
            SOFT -> {
                breathe(o, clock, 4.5f, 0.02f)
                o[P.LID] = 0.62f
                o[P.BROW_TILT] = 9f
                o[P.BROW_L] = 0.5f
                o[P.BROW_R] = 0.5f
                o[P.SMILE] = 1.7f
                o[P.CHEEK] = 1f
                o[P.TAIL] = 4f * wave(0.17f, clock)
                o[P.ROT] = 2f
                o[P.DY] = 1f
            }
        }
    }

    private fun sleepy(o: Pose) {
        o[P.LID] = 1f
        o[P.SMILE] = 0.4f
        o[P.DY] = 2f
        o[P.ROT] = 4f
        o[P.BROW_L] = -0.5f
        o[P.BROW_R] = -0.5f
        o[P.CHEEK] = 0.8f
    }

    private fun poked(o: Pose, t: Float, level: Int) {
        val e = exp(-t * 5.5f)
        val squash = (0.07f + 0.03f * level) * e
        o[P.SX] = 1f + squash
        o[P.SY] = 1f - squash
        o[P.LID] = 0.2f + 0.25f * ramp(t / 0.5f)
        val grumpy = level == 3
        o[P.SMILE] = when (level) { 1 -> 0.6f; 2 -> 0f; else -> -1.2f }
        o[P.BROW_TILT] = when (level) { 1 -> 4f; 2 -> -6f; else -> -16f }
        o[P.BROW_L] = if (grumpy) -1.5f else 1f
        o[P.BROW_R] = if (grumpy) -1.5f else 1f
        o[P.ARM_L] = if (grumpy) 28f + 10f * wave(8f, t) else 8f
        o[P.ARM_R] = if (grumpy) 28f + 10f * wave(8f, t + 0.06f) else 8f
        if (grumpy) {
            o[P.DX] = 1.5f * wave(12f, t) * exp(-t * 3f)
            o[P.CHEEK] = 1f
        }
    }

    private fun falling(o: Pose, t: Float) {
        // Stretch while falling, then a damped squash on landing, blended in over 0.1 s so the impact is fast but not a cut.
        val u = (t - 0.18f).coerceAtLeast(0f)
        val land = ramp(u / 0.1f)
        val squash = exp(-u * 7f) * cos(TAU * 2.2f * u)
        o[P.SY] = 1.08f + ((1f - 0.18f * squash) - 1.08f) * land
        o[P.SX] = 0.96f + ((1f + 0.13f * squash) - 0.96f) * land
        o[P.LID] = 0.1f + 0.4f * land
        o[P.OPEN] = 0.25f * (1f - land)
        o[P.SMILE] = 1f + (0.4f - 1f) * land
        o[P.ARM_L] = 30f * (if (t < 0.18f) 1f else exp(-u * 8f))
        o[P.ARM_R] = o[P.ARM_L]
        o[P.TAIL] = 22f * (if (t < 0.18f) 1f else exp(-u * 5f))
    }

    private fun breathe(o: Pose, clock: Float, periodSec: Float, amount: Float) {
        val b = wave(1f / periodSec, clock)
        o[P.SY] = 1f + amount * b
        o[P.SX] = 1f - amount * 0.55f * b
    }

    private fun wave(hz: Float, t: Float): Float = sin(TAU * ((hz * t) % 1f))
    private fun bump(x: Float): Float = sin(PI.toFloat() * x.coerceIn(0f, 1f)).coerceAtLeast(0f)
    private fun ramp(x: Float): Float = x.coerceIn(0f, 1f)
}
