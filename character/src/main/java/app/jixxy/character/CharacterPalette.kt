package app.jixxy.character

import android.graphics.Color

/** The five colours Jixxy is drawn with, plus a shadow tint. Kept in one place so skins can swap it later. */
internal data class CharacterPalette(
    val outline: Int,
    val body: Int,
    val blanket: Int,
    val cheek: Int,
    val eye: Int
) {
    companion object {
        val Default = CharacterPalette(
            outline = Color.parseColor("#2D2A3E"),
            body = Color.parseColor("#F4B97A"),
            blanket = Color.parseColor("#6C7BD9"),
            cheek = Color.parseColor("#E8806F"),
            eye = Color.parseColor("#FFF6E8")
        )
    }
}
