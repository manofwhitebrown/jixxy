package app.jixxy.character

import android.graphics.Color

/**
 * Jixxy's colours, from the Owner's character sheet: near-black fur, charcoal, muted purple (blanket, UI accents),
 * cream (bubble text), pink only for inner ears. Everything the character module draws takes its colours from here,
 * so skins can swap this one object later (no skin features yet, T-C2-7).
 */
internal data class CharacterPalette(
    val ink: Int,
    val charcoal: Int,
    val purple: Int,
    val cream: Int,
    val pink: Int,
    val cloudBody: Int,
    val cloudShade: Int,
) {
    companion object {
        val Default = CharacterPalette(
            ink = Color.parseColor("#15121A"),
            charcoal = Color.parseColor("#2B2833"),
            purple = Color.parseColor("#6B5B95"),
            cream = Color.parseColor("#F3EBDD"),
            pink = Color.parseColor("#E8A0B4"),
            cloudBody = Color.parseColor("#DDD5F0"),
            cloudShade = Color.parseColor("#A89BCF"),
        )
    }
}
