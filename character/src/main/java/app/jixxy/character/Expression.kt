package app.jixxy.character

/**
 * The expression images Jixxy can show (one per file `jixxy_expr_<resName>` in `drawable-nodpi`).
 * See `design/character/MAPPING.md` for the caption each one carries on the Owner's character sheet.
 */
internal enum class Expression(val resName: String, val ledgeLeft: Float = -1f, val ledgeRight: Float = -1f) {
    REALLY("really", 18f, 110f),
    NAP("nap", 15f, 114f),
    PEEK("peek", 22f, 107f),
    BRO_WHAT("bro_what", 27f, 107f),
    OOH_NICE("ooh_nice", 21f, 103f),
    SOFT("soft", 30f, 91f),
    SMUG("smug", 36f, 88f),
    SHOCKED("shocked"),
    DROPPED("dropped", 29f, 111f),
    LOW_BATTERY("low_battery", 33f, 105f),
    CHARGING("charging"),
    SNACKS("snacks", 25f, 106f),
    HEADPHONES("headphones"),
    RESPECT("respect", 20f, 111f),
    BLANKET("blanket", 9f, 119f),
    /** Lying down, eyes shut (Owner image, built by `design/character/tools/build_expressions.py`). */
    SLEEP("sleep"),
    /** Big yawn, eyes shut (same pipeline). */
    YAWN("yawn"),
    /** Paws up stretch (same pipeline). */
    STRETCH("stretch", 19f, 124f),
    ;

    /**
     * True when the sprite's bottom edge is a flat cut (a bust on the Owner's sheet) and needs the cloud under
     * it. [ledgeLeft]..[ledgeRight] are viewbox units (0..128) measured from each image's bottom rows, plus ~5
     * units either side ([CloudShape] adds its own 2). Blanket and stretch are trimmed so the cloud fits the view. Round-bottomed sprites (shocked, charging, headphones, sleep, yawn) have none.
     */
    val hasCloud: Boolean get() = ledgeLeft >= 0f
}

/** The cloud to draw this frame: span of the sprite's flat edge in viewbox units, and opacity 0..1. */
internal data class Cloud(val left: Float, val right: Float, val alpha: Float)

/**
 * Blends the cloud across a crossfade from [previous] to [current] ([progress] 0..1), so it grows, shrinks or
 * slides with the sprite instead of popping. Null when neither expression has one.
 */
internal fun cloudFor(previous: Expression?, current: Expression, progress: Float): Cloud? {
    val p = progress.coerceIn(0f, 1f)
    val before = previous?.takeIf { it.hasCloud }
    val now = current.takeIf { it.hasCloud }
    return when {
        now != null && before != null -> Cloud(
            before.ledgeLeft + (now.ledgeLeft - before.ledgeLeft) * p,
            before.ledgeRight + (now.ledgeRight - before.ledgeRight) * p,
            1f,
        )
        now != null -> Cloud(now.ledgeLeft, now.ledgeRight, if (previous == null) 1f else p)
        before != null -> Cloud(before.ledgeLeft, before.ledgeRight, 1f - p)
        else -> null
    }
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
        "SLEEP" -> Expression.NAP
        "SNORE" -> Expression.SLEEP
        "YAWN" -> Expression.YAWN
        "STRETCH" -> Expression.STRETCH
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
        else -> Expression.REALLY // IDLE, BLINK, JUDGING and anything new
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
