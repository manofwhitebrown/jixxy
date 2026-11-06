package app.jixxy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DarkScheme = darkColorScheme(
    primary = Amber, onPrimary = NightBg,
    background = NightBg, onBackground = NightOnSurface,
    surface = NightSurface, onSurface = NightOnSurface,
)
private val LightScheme = lightColorScheme(
    primary = AmberDeep, onPrimary = DaySurface,
    background = DayBg, onBackground = DayOnSurface,
    surface = DaySurface, onSurface = DayOnSurface,
)
private val JixxyShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
)

/**
 * App theme. Dark-first: [dark] defaults to the system setting, but the
 * brand is designed in dark. Typography uses the system font until fonts
 * are bundled under res/font (no network, ever).
 */
@Composable
fun JixxyTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) DarkScheme else LightScheme,
        shapes = JixxyShapes,
        content = content,
    )
}
