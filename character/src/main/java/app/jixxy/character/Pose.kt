package app.jixxy.character

/** Indices into [Pose]. Units are viewBox units (128 x 128) and degrees unless noted. */
internal object P {
    const val SX = 0          // horizontal scale about the feet
    const val SY = 1          // vertical scale about the feet
    const val DX = 2
    const val DY = 3
    const val ROT = 4         // degrees about the feet
    const val LID = 5         // 0 = eyes wide open, 1 = shut
    const val PUPIL_DX = 6
    const val PUPIL_DY = 7
    const val PUPIL_R = 8
    const val BROW_L = 9      // lift in units, positive = up
    const val BROW_R = 10
    const val BROW_TILT = 11  // degrees, positive = inner end up (worried/soft), negative = grumpy
    const val SMILE = 12      // 1 = default slight smile, 0 = flat, negative = frown
    const val OPEN = 13       // mouth open 0..1
    const val ARM_L = 14      // degrees raised outward
    const val ARM_R = 15
    const val TAIL = 16       // blanket tail swing, degrees
    const val CHEEK = 17      // cheek opacity 0..1
    const val ZS = 18         // effect amounts 0..1
    const val NOTES = 19
    const val SPARKLE = 20
    const val SWEAT = 21
    const val SNACK = 22      // snack scale 0..1
    const val COUNT = 23
}

/** A bag of animatable channels, stored flat so blending allocates nothing. */
internal class Pose {
    private val v = FloatArray(P.COUNT)

    init { setNeutral() }

    operator fun get(i: Int): Float = v[i]
    operator fun set(i: Int, x: Float) { v[i] = x }

    fun setNeutral() = NEUTRAL.copyInto(v)

    /** Moves every channel a fraction [alpha] (0..1) of the way towards [target]. */
    fun approach(target: Pose, alpha: Float) {
        for (i in 0 until P.COUNT) v[i] += (target.v[i] - v[i]) * alpha
    }

    private companion object {
        val NEUTRAL = floatArrayOf(
            1f, 1f, 0f, 0f, 0f,       // SX SY DX DY ROT
            0.5f, 0f, 0f, 3.6f,       // LID PUPIL_DX PUPIL_DY PUPIL_R
            0f, 0f, 4f, 1f, 0f,       // BROW_L BROW_R BROW_TILT SMILE OPEN
            0f, 0f, 0f, 0.7f,         // ARM_L ARM_R TAIL CHEEK
            0f, 0f, 0f, 0f, 0f        // ZS NOTES SPARKLE SWEAT SNACK
        )
    }
}
