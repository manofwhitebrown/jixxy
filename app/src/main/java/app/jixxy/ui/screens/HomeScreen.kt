package app.jixxy.ui.screens

import android.text.format.DateFormat
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.jixxy.R
import app.jixxy.engine.api.SettingsRepository
import app.jixxy.engine.api.StateStore
import app.jixxy.engine.api.UserSettings
import app.jixxy.ui.components.CatImage
import app.jixxy.ui.components.CatPoses
import app.jixxy.ui.home.HomeLogic
import app.jixxy.ui.home.HomeUiState
import app.jixxy.ui.home.JixxyStatus
import app.jixxy.ui.home.TodayStats
import app.jixxy.ui.theme.JixxyTheme
import java.util.Date
import kotlinx.coroutines.launch

/**
 * Stateful Home (T-UI-2): reads settings + today's logged events and writes nap/wake through [settings].
 * [onNapRequested] / [onWakeRequested] let the host also tell the engine (Signal.NapRequested / WakeRequested).
 * [clock] is injectable for tests and previews.
 */
@Composable
fun HomeScreen(
    settings: SettingsRepository,
    store: StateStore,
    onSettings: () -> Unit = {},
    onWeekly: () -> Unit = {},
    onNapRequested: (minutes: Int) -> Unit = {},
    onWakeRequested: () -> Unit = {},
    clock: () -> Long = System::currentTimeMillis,
) {
    val current by settings.settings.collectAsStateWithLifecycle(initialValue = UserSettings())
    var ui by remember { mutableStateOf(HomeUiState()) }
    val scope = rememberCoroutineScope()

    // Recompute on settings change; events are re-read at the same time so numbers are fresh on each visit.
    LaunchedEffect(current) {
        val now = clock()
        val events = store.eventsBetween(HomeLogic.startOfLocalDay(now), now + 1)
        ui = HomeLogic.uiState(current, events, now)
    }

    HomeContent(
        ui = ui,
        onNap = {
            scope.launch {
                settings.update { it.copy(napUntilTs = HomeLogic.napUntil(clock())) }
                onNapRequested(HomeLogic.NAP_MINUTES)
            }
        },
        onWake = {
            scope.launch {
                settings.update { it.copy(napUntilTs = null) }
                onWakeRequested()
            }
        },
        onSettings = onSettings,
        onWeekly = onWeekly,
    )
}

/** Stateless Home layout. Calm, no shaming: numbers are plain counts. */
@Composable
fun HomeContent(
    ui: HomeUiState,
    onNap: () -> Unit,
    onWake: () -> Unit,
    onSettings: () -> Unit,
    onWeekly: () -> Unit,
) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            CatImage(CatPoses.forStatus(ui.status))
            Text(statusText(ui), style = MaterialTheme.typography.headlineMedium)

            when (ui.status) {
                JixxyStatus.AWAKE -> Button(onClick = onNap, Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                    Text(stringResource(R.string.home_nap_1h))
                }
                JixxyStatus.NAPPING -> Button(onClick = onWake, Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                    Text(stringResource(R.string.home_wake))
                }
                JixxyStatus.BLIND -> Unit // Blind mode is turned off in Settings; nothing to nap.
            }

            TodayRow(ui.today)

            OutlinedButton(onClick = onWeekly, Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(stringResource(R.string.home_open_weekly))
            }
            TextButton(onClick = onSettings, Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(stringResource(R.string.settings_title))
            }
        }
    }
}

@Composable
private fun statusText(ui: HomeUiState): String = when (ui.status) {
    JixxyStatus.AWAKE -> stringResource(R.string.home_status_awake)
    JixxyStatus.BLIND -> stringResource(R.string.home_status_blind)
    JixxyStatus.NAPPING -> {
        val until = ui.napUntilTs
        val time = if (until != null) DateFormat.getTimeFormat(LocalContext.current).format(Date(until)) else ""
        stringResource(R.string.home_status_napping, time)
    }
}

@Composable
private fun TodayRow(stats: TodayStats) {
    val summary = stringResource(R.string.home_today_stats, stats.paused, stats.left, stats.stayed)
    Row(
        Modifier.fillMaxWidth().semantics(mergeDescendants = true) { contentDescription = summary },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard(R.string.home_stat_paused, stats.paused, Modifier.weight(1f))
        StatCard(R.string.home_stat_left, stats.left, Modifier.weight(1f))
        StatCard(R.string.home_stat_stayed, stats.stayed, Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: Int, value: Int, modifier: Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value.toString(), style = MaterialTheme.typography.headlineLarge)
            Text(stringResource(label), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private val sampleStats = TodayStats(paused = 5, left = 3, stayed = 2)

@Preview(showBackground = true, name = "awake dark") @Composable private fun HomeAwakeDark() =
    JixxyTheme(dark = true) { HomeContent(HomeUiState(JixxyStatus.AWAKE, today = sampleStats), {}, {}, {}, {}) }
@Preview(showBackground = true, name = "napping light") @Composable private fun HomeNappingLight() =
    JixxyTheme(dark = false) { HomeContent(HomeUiState(JixxyStatus.NAPPING, napUntilTs = 1_800_000_000_000, today = sampleStats), {}, {}, {}, {}) }
@Preview(showBackground = true, name = "blind, 200% font", fontScale = 2f) @Composable private fun HomeBlindLarge() =
    JixxyTheme(dark = true) { HomeContent(HomeUiState(JixxyStatus.BLIND), {}, {}, {}, {}) }
