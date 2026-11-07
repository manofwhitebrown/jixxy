package app.jixxy.ui.screens

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.jixxy.R
import app.jixxy.engine.api.StateStore
import app.jixxy.engine.api.UsageEventType
import app.jixxy.ui.theme.JixxyTheme
import app.jixxy.ui.whatsaw.SawDay
import app.jixxy.ui.whatsaw.SawRow
import app.jixxy.ui.whatsaw.WhatSawLogic
import java.util.Date
import kotlinx.coroutines.launch

/**
 * Transparency log (T-UI-4): what Jixxy recorded in the last 7 days, read from [store].
 * "Clear log" really calls [StateStore.clearEvents] after a confirmation.
 * [appLabel] maps a package name to its launcher label (PLATFORM); defaults to the package name.
 */
@Composable
fun WhatSawScreen(
    store: StateStore,
    appLabel: (String) -> String = { it },
    clock: () -> Long = System::currentTimeMillis,
) {
    var days by remember { mutableStateOf<List<SawDay>>(emptyList()) }
    var reload by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(reload) {
        val now = clock()
        days = WhatSawLogic.group(store.eventsBetween(WhatSawLogic.windowStart(now), now + 1), now, appLabel)
    }

    WhatSawContent(days, onClear = { scope.launch { store.clearEvents(); reload++ } })
}

/** Stateless layout, previewable. */
@Composable
fun WhatSawContent(days: List<SawDay>, onClear: () -> Unit) {
    var confirm by remember { mutableStateOf(false) }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(stringResource(R.string.whatsaw_title), Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.whatsaw_nothing_leaves_phone), style = MaterialTheme.typography.bodyMedium)

            if (days.isEmpty()) {
                Text(stringResource(R.string.whatsaw_empty), style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    days.forEach { day ->
                        item(key = "h${day.dayStartTs}") { DayHeader(day.dayStartTs) }
                        items(day.rows, key = { "${it.ts}-${it.type}-${it.appLabel}" }) { RowItem(it) }
                    }
                }
            }

            OutlinedButton(
                onClick = { confirm = true },
                enabled = days.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            ) { Text(stringResource(R.string.whatsaw_clear_button)) }
        }
    }

    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            text = { Text(stringResource(R.string.whatsaw_clear_confirm)) },
            confirmButton = { TextButton(onClick = { confirm = false; onClear() }) { Text(stringResource(R.string.whatsaw_clear_button)) } },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text(stringResource(R.string.common_cancel)) } },
        )
    }
}

@Composable
private fun DayHeader(dayStartTs: Long) {
    val text = DateFormat.getMediumDateFormat(LocalContext.current).format(Date(dayStartTs))
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
}

@Composable
private fun RowItem(row: SawRow) {
    val time = DateFormat.getTimeFormat(LocalContext.current).format(Date(row.ts))
    val what = eventText(row)
    Column(Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
        Text(what, style = MaterialTheme.typography.bodyLarge)
        Text(listOfNotNull(row.appLabel, time).joinToString(" · "), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun eventText(row: SawRow): String = when (row.type) {
    UsageEventType.FOREGROUND -> stringResource(R.string.whatsaw_event_foreground)
    UsageEventType.SESSION_MINUTES -> stringResource(R.string.whatsaw_event_session_minutes, row.minutes ?: 0)
    UsageEventType.PAUSE_SHOWN -> stringResource(R.string.whatsaw_event_pause_shown)
    UsageEventType.PAUSE_STAY -> stringResource(R.string.whatsaw_event_pause_stay)
    UsageEventType.PAUSE_LEAVE -> stringResource(R.string.whatsaw_event_pause_leave)
    UsageEventType.LINE_SHOWN -> stringResource(R.string.whatsaw_event_line_shown)
    UsageEventType.LINE_REACTION -> stringResource(R.string.whatsaw_event_line_reaction)
    UsageEventType.CHARGER_ON -> stringResource(R.string.whatsaw_event_charger_on)
    UsageEventType.NAP -> stringResource(R.string.whatsaw_event_nap)
    UsageEventType.LATE_NIGHT_OPEN -> stringResource(R.string.whatsaw_event_late_night_open)
}

private const val HOUR = 3_600_000L
private val previewDays = listOf(
    SawDay(1_800_000_000_000, listOf(
        SawRow(1_800_000_000_000 + 9 * HOUR, UsageEventType.PAUSE_SHOWN, "App A", null),
        SawRow(1_800_000_000_000 + 8 * HOUR, UsageEventType.SESSION_MINUTES, "App A", 25),
        SawRow(1_800_000_000_000 + 7 * HOUR, UsageEventType.CHARGER_ON, null, null),
    )),
)

@Preview(showBackground = true, name = "list dark") @Composable private fun WhatSawDark() = JixxyTheme(dark = true) { WhatSawContent(previewDays, {}) }
@Preview(showBackground = true, name = "empty light") @Composable private fun WhatSawEmptyLight() = JixxyTheme(dark = false) { WhatSawContent(emptyList(), {}) }
@Preview(showBackground = true, name = "list 200% font", fontScale = 2f) @Composable private fun WhatSawLargeFont() = JixxyTheme(dark = true) { WhatSawContent(previewDays, {}) }
