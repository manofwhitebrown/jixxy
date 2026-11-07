package app.jixxy.character

/**
 * The meows and purrs Jixxy can make (D-021). Each cue is one short mono OGG named `jixxy_snd_<resName>` in
 * `character/src/main/res/raw/`; a cue whose file is missing is silently skipped. Sources and licences are listed in
 * `design/character/audio/CREDITS.md`.
 */
enum class SoundCue(internal val resName: String) {
    /** Short curious mew: Jixxy peeks before a Pause line. */
    CURIOUS_MEW("curious_mew"),

    /** Annoyed mew when poked; gets higher with each rapid poke (see [SoundCues.rateFor]). */
    ANNOYED_MEW("annoyed_mew"),

    /** Grumpy mew when woken up. */
    GRUMPY_MEW("grumpy_mew"),

    /** Soft, short purr when a nap starts. */
    PURR("purr"),

    /** Happy mew: the charger is connected ("Finally, food."). */
    HAPPY_MEW("happy_mew"),

    /** Worried mew: the battery is low. */
    WORRIED_MEW("worried_mew"),

    /** Short yelp-mew when he is dropped after a drag. */
    YELP("yelp"),
}

/** Which cue a character state makes, and how it is played. Pure, so it is unit-tested on the JVM. */
object SoundCues {
    /**
     * The cue for entering [stateName] (a `CharacterState.name`, matched by name like `ExpressionMap`), or null when
     * the state is silent. [pokeLevel] is the tap count 1..3 of a POKED state. IMPRESSED stays silent on purpose
     * (rare and earned), DRAGGED is silent so grabbing him is not a meow every time; only the drop yelps.
     */
    fun forState(stateName: String, @Suppress("UNUSED_PARAMETER") pokeLevel: Int = 1): SoundCue? = when (stateName) {
        "PEEK" -> SoundCue.CURIOUS_MEW
        "POKED" -> SoundCue.ANNOYED_MEW
        "WAKE_GRUMPY" -> SoundCue.GRUMPY_MEW
        "SLEEP" -> SoundCue.PURR
        "CHARGING" -> SoundCue.HAPPY_MEW
        "LOW_BATTERY" -> SoundCue.WORRIED_MEW
        "FALLING" -> SoundCue.YELP
        else -> null
    }

    /** Playback rate (pitch) for [cue]: pokes escalate 1.0, 1.15, 1.3; everything else plays at 1.0. */
    fun rateFor(cue: SoundCue, pokeLevel: Int): Float =
        if (cue == SoundCue.ANNOYED_MEW) 1f + 0.15f * (pokeLevel.coerceIn(1, 3) - 1) else 1f
}
