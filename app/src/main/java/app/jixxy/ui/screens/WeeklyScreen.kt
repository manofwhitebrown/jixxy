package app.jixxy.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.jixxy.R
import app.jixxy.engine.api.SettingsRepository
import app.jixxy.engine.api.StateStore
import app.jixxy.engine.api.UserSettings
import app.jixxy.ui.components.CatImage
import app.jixxy.ui.components.CatPose
import app.jixxy.ui.theme.JixxyTheme
import app.jixxy.ui.weekly.WeeklyCardLogic
import app.jixxy.ui.weekly.WeeklyCardModel
import app.jixxy.ui.weekly.WeeklyCardRenderer
import app.jixxy.ui.weekly.WeeklyCardTexts
import app.jixxy.ui.weekly.WeeklyLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Weekly review: this week's counts, Jixxy's line, and a Share button that renders the 1080x1350 card on the phone. */
@Composable
fun WeeklyScreen(settings: SettingsRepository, store: StateStore, appLabel: (String) -> String = { it }, onShown: () -> Unit = {}) {
    val context = LocalContext.current
    val current by settings.settings.collectAsStateWithLifecycle(initialValue = UserSettings())
    var model by remember { mutableStateOf<WeeklyCardModel?>(null) }

    LaunchedEffect(current.roast, current.language) {
        model = withContext(Dispatchers.Default) {
            runCatching { WeeklyCardLogic.model(WeeklyLoader.load(context, store, current, appLabel)) }.getOrNull()
        }
    }

    // The weekly notification stops nagging once the person has actually seen the card.
    LaunchedEffect(model != null) { if (model != null) onShown() }

    val texts = WeeklyCardTexts(
        headline = stringResource(R.string.weekly_headline_1),
        pauses = stringResource(R.string.weekly_stat_pauses),
        left = stringResource(R.string.weekly_stat_left),
        stayed = stringResource(R.string.weekly_stat_stayed),
    )
    val scope = rememberCoroutineScope()
    WeeklyContent(model, texts, onShare = { model?.let { m -> scope.launch { shareCard(context, m, texts) } } })
}

/** Draws and saves the card off the UI thread, frees the bitmap, then opens the share sheet. A failure just does nothing. */
private suspend fun shareCard(context: Context, model: WeeklyCardModel, texts: WeeklyCardTexts) {
    val uri = withContext(Dispatchers.Default) {
        runCatching {
            val bitmap = WeeklyCardRenderer.render(context, model, texts)
            try { WeeklyCardRenderer.saveForShare(context, bitmap) } finally { bitmap.recycle() }
        }.getOrNull()
    } ?: return
    val send = Intent(Intent.ACTION_SEND).setType("image/png").putExtra(Intent.EXTRA_STREAM, uri)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    runCatching { context.startActivity(Intent.createChooser(send, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
}

/** Stateless layout. A quiet or still-loading week shows the "not enough data" line and no Share button. */
@Composable
fun WeeklyContent(model: WeeklyCardModel?, texts: WeeklyCardTexts, onShare: () -> Unit) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CatImage(CatPose.SMUG)
            Text(texts.headline, Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineMedium)
            if (model == null || model.quiet) {
                Text(stringResource(R.string.empty_weekly_not_enough_data), style = MaterialTheme.typography.bodyLarge)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Stat(model.pauses, texts.pauses, Modifier.weight(1f))
                    Stat(model.left, texts.left, Modifier.weight(1f))
                    Stat(model.stayed, texts.stayed, Modifier.weight(1f))
                }
                model.roast?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
                model.compliment?.let { Text(it, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary) }
                Button(onClick = onShare, Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                    Text(stringResource(R.string.weekly_share_cta))
                }
                Text(stringResource(R.string.weekly_privacy_note), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun Stat(value: Int, label: String, modifier: Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value.toString(), style = MaterialTheme.typography.headlineLarge)
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private val previewTexts = WeeklyCardTexts("Jixxy's review of your week", "Pauses", "Times you left", "Times you stayed")
private val previewModel = WeeklyCardModel(9, 6, 3, "Nine pauses. I'm keeping a spreadsheet.", "You left more than you stayed. Respect.", false)

@Preview(showBackground = true, name = "dark") @Composable private fun WeeklyPreviewDark() =
    JixxyTheme(dark = true) { WeeklyContent(previewModel, previewTexts) {} }
@Preview(showBackground = true, name = "light, quiet") @Composable private fun WeeklyPreviewQuiet() =
    JixxyTheme(dark = false) { WeeklyContent(null, previewTexts) {} }
