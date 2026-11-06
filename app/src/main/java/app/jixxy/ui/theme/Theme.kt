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
    primary = Purple, onPrimary = Fur,
    secondary = Cream, onSecondary = Fur,
    tertiary = EarPink, onTertiary = Fur,
    background = Fur, onBackground = Cream,
    surface = Charcoal, onSurface = Cream,
    surfaceVariant = CharcoalHigh, onSurfaceVariant = Cream,
)
private val LightScheme = lightColorScheme(
    primary = PurpleDeep, onPrimary = CreamSurface,
    secondary = InkOnLight, onSecondary = CreamSurface,
    tertiary = EarPinkDeep, onTertiary = CreamSurface,
    background = CreamBg, onBackground = InkOnLight,
    surface = CreamSurface, onSurface = InkOnLight,
    surfaceVariant = CreamBg, onSurfaceVariant = InkOnLight,
)
private val JixxyShapes = Shapes(
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
)

/**
 * App theme. Dark-first: the brand is the black cat, so [dark] defaults to the system setting but light mode is the
 * cream sheet and stays readable. System font until fonts are bundled under res/font (no network, ever).
 */
@Composable
fun JixxyTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) DarkScheme else LightScheme,
        shapes = JixxyShapes,
        content = content,
    )
}
