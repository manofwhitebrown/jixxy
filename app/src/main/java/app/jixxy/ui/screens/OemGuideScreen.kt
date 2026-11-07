package app.jixxy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.jixxy.R
import app.jixxy.platform.OemBrand
import app.jixxy.platform.OemIntents
import app.jixxy.ui.theme.JixxyTheme

/**
 * Brand-aware keep-alive guide (T-UI-6). Uses PLATFORM's [OemIntents] for detection and for the
 * "Open the setting" buttons; the menu paths themselves come from WRITER's microcopy and may differ by phone.
 */
@Composable
fun OemGuideScreen() {
    val context = LocalContext.current
    val detected = remember { OemIntents.detect() }
    OemGuideContent(
        detected = detected,
        onOpen = { brand -> OemIntents.startFirstAvailable(context, OemIntents.keepAliveIntents(context, brand)) },
    )
}

private data class GuideSection(val brand: OemBrand, val title: Int, val intro: Int, val steps: Int)

private val allSections = listOf(
    GuideSection(OemBrand.XIAOMI, R.string.oem_title_xiaomi, R.string.oem_guide_xiaomi_intro, R.string.oem_guide_xiaomi_steps),
    GuideSection(OemBrand.OPPO, R.string.oem_title_oppo, R.string.oem_guide_oppo_intro, R.string.oem_guide_oppo_steps),
    GuideSection(OemBrand.VIVO, R.string.oem_title_vivo, R.string.oem_guide_vivo_intro, R.string.oem_guide_vivo_steps),
    GuideSection(OemBrand.SAMSUNG, R.string.oem_title_samsung, R.string.oem_guide_samsung_intro, R.string.oem_guide_samsung_steps),
    GuideSection(OemBrand.OTHER, R.string.oem_title_stock, R.string.oem_guide_stock_intro, R.string.oem_guide_stock_steps),
)

/** The detected brand's guide comes first, then the other brands; the stock Android guide is always shown last. */
private fun orderedSections(detected: OemBrand): List<GuideSection> {
    val mine = allSections.firstOrNull { it.brand == detected && it.brand != OemBrand.OTHER }
    val others = allSections.filter { it.brand != OemBrand.OTHER && it != mine }
    return listOfNotNull(mine) + others + allSections.last()
}

/** Stateless layout, previewable. Open buttons appear for the detected brand and for stock Android only. */
@Composable
fun OemGuideContent(detected: OemBrand, onOpen: (OemBrand) -> Unit) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(stringResource(R.string.onboarding_keepalive_title), Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.onboarding_keepalive_body), style = MaterialTheme.typography.bodyLarge)

            orderedSections(detected).forEach { section ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(section.title), Modifier.semantics { heading() }, style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(section.intro), style = MaterialTheme.typography.bodyMedium)
                    Text(stringResource(section.steps), style = MaterialTheme.typography.bodyMedium)
                    if (section.brand == detected || section.brand == OemBrand.OTHER) {
                        OutlinedButton(
                            onClick = { onOpen(section.brand) },
                            Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        ) { Text(stringResource(R.string.onboarding_overlay_cta_open)) }
                    }
                    // Required with every guide: menus differ by phone and Android version.
                    Text(stringResource(R.string.oem_menus_differ), style = MaterialTheme.typography.bodySmall)
                }
            }

            // Plain text only: no in-app browsing, no network.
            Text(stringResource(R.string.oem_dontkillmyapp_note), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true, name = "xiaomi dark", heightDp = 1800) @Composable private fun OemXiaomiDark() =
    JixxyTheme(dark = true) { OemGuideContent(OemBrand.XIAOMI) {} }
@Preview(showBackground = true, name = "other light", heightDp = 1800) @Composable private fun OemOtherLight() =
    JixxyTheme(dark = false) { OemGuideContent(OemBrand.OTHER) {} }
@Preview(showBackground = true, name = "samsung 200% font", fontScale = 2f, heightDp = 3000) @Composable private fun OemSamsungLarge() =
    JixxyTheme(dark = true) { OemGuideContent(OemBrand.SAMSUNG) {} }
