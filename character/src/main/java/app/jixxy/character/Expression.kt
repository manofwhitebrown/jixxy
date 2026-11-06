package app.jixxy.character

/**
 * The expression images Jixxy can show (one per file `jixxy_expr_<resName>` in `drawable-nodpi`).
 * See `design/character/MAPPING.md` for the caption each one carries on the Owner's character sheet.
 */
internal enum class Expression(val resName: String) {
    REALLY("really"),
    NAP("nap"),
    PEEK("peek"),
    BRO_WHAT("bro_what"),
    OOH_NICE("ooh_nice"),
    SOFT("soft"),
    SMUG("smug"),
    SHOCKED("shocked"),
    DROPPED("dropped"),
    LOW_BATTERY("low_battery"),
    CHARGING("charging"),
    SNACKS("snacks"),
    HEADPHONES("headphones"),
    RESPECT("respect"),
    BLANKET("blanket"),
}

/** Decides which [Expression] a character state shows. Pure, so it is unit-tested on the JVM. */
internal object ExpressionMap {
    /** Hours (local, 0..4) in which a SOFT Jixxy is wrapped in the purple blanket. */
    private val BLANKET_HOURS = 0..4

    /**
     * [stateName] is `CharacterState.name`. States are matched by name so the D-010 values (PEEK, CHARGING,
     * LOW_BATTERY) work whether or not the engine enum already has them; unknown names show the default face.
     * [pokeLevel] is the tap count 1..3 of a POKED state, [hourOfDay] the local hour 0..23.
     */
    fun forState(stateName: String, pokeLevel: Int, hourOfDay: Int): Expression = when (stateName) {
        "HAPPY" -> Expression.OOH_NICE
        "SLEEP", "SNORE" -> Expression.NAP
        "WAKE_GRUMPY" -> Expression.BRO_WHAT
        "POKED" -> if (pokeLevel >= 2) Expression.BRO_WHAT else Expression.REALLY
        "PEEK" -> Expression.PEEK
        "SNACK" -> Expression.SNACKS
        "PANIC" -> Expression.SHOCKED
        "IMPRESSED" -> Expression.RESPECT
        "SOFT" -> if (hourOfDay in BLANKET_HOURS) Expression.BLANKET else Expression.SOFT
        "SMUG" -> Expression.SMUG
        "MUSIC" -> Expression.HEADPHONES
        "LOW_BATTERY" -> Expression.LOW_BATTERY
        "CHARGING" -> Expression.CHARGING
        "DRAGGED", "FALLING" -> Expression.DROPPED
        else -> Expression.REALLY // IDLE, BLINK, JUDGING, YAWN, STRETCH and anything new
    }
}

/** Crossfades from the previous expression to the current one. Time is any monotonic clock in ms. */
internal class ExpressionFader(initial: Expression = Expression.REALLY) {
    /** The expression being shown (fully, once [progress] reaches 1). */
    var current: Expression = initial
        private set

    /** The expression fading out underneath, or null when no fade is running. */
    var previous: Expression? = null
        private set

    /** 0..1: how far the current expression has faded in over the previous one. */
    var progress = 1f
        private set

    val isFading: Boolean get() = progress < 1f

    private var startMs = 0L

    /** Starts fading to [next]. Asking for the expression already shown does nothing. */
    fun show(next: Expression, nowMs: Long) {
        if (next == current) return
        previous = current
        current = next
        startMs = nowMs
        progress = 0f
    }

    /** Advances the fade to [nowMs]. */
    fun step(nowMs: Long) {
        if (!isFading) return
        progress = ((nowMs - startMs) / FADE_MS.toFloat()).coerceIn(0f, 1f)
        if (progress >= 1f) previous = null
    }

    companion object {
        const val FADE_MS = 140L
    }
}

/** Picks a BitmapFactory `inSampleSize` (power of two) that keeps the decoded image at least [targetPx] wide. */
internal fun sampleSizeFor(sourcePx: Int, targetPx: Int): Int {
    var sample = 1
    while (targetPx > 0 && sourcePx / (sample * 2) >= targetPx) sample *= 2
    return sample
}
