@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package app.jixxy.ui.screens

import android.app.TimePickerDialog
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.jixxy.R
import app.jixxy.engine.api.NagLevel
import app.jixxy.engine.api.RoastLevel
import app.jixxy.engine.api.SettingsRepository
import app.jixxy.engine.api.StateStore
import app.jixxy.engine.api.UserSettings
import app.jixxy.ui.onboarding.AppChoice
import app.jixxy.ui.settings.SettingsLogic
import app.jixxy.ui.state.DiagnosticsPort
import app.jixxy.ui.state.WeeklyPort
import app.jixxy.ui.theme.JixxyTheme
import kotlinx.coroutines.launch

/**
 * Stateful Settings (T-UI-3). Every change goes straight through [settings], so the rest of the app
 * sees it immediately. "Reset Jixxy" also clears logged events through [store].
 * [apps] is the launcher-intent app list from PLATFORM (PROTECTED apps already removed).
 */
@Composable
fun SettingsScreen(
    settings: SettingsRepository,
    store: StateStore,
    apps: List<AppChoice> = emptyList(),
    onWhatSaw: () -> Unit = {},
    onOemGuide: () -> Unit = {},
    diagnostics: DiagnosticsPort? = null,
    weekly: WeeklyPort? = null,
) {
    var weeklyOn by remember { mutableStateOf(weekly?.isEnabled()) }
    val current by settings.settings.collectAsStateWithLifecycle(initialValue = UserSettings())
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    fun change(transform: (UserSettings) -> UserSettings) { scope.launch { settings.update(transform) } }

    SettingsContent(
        s = current,
        apps = apps,
        onRoast = { r -> change { it.copy(roast = r) } },
        onToggleTarget = { pkg -> change { SettingsLogic.toggleTarget(it, pkg) } },
        onToggleMuted = { pkg -> change { SettingsLogic.toggleMuted(it, pkg) } },
        onQuietHours = { start, end -> change { SettingsLogic.withQuietHours(it, start, end) } },
        onStayDelay = { sec -> change { SettingsLogic.withStayDelay(it, sec) } },
        onLanguage = { code -> change { SettingsLogic.withLanguage(it, code) } },
        onNag = { level -> change { SettingsLogic.withNagLevel(it, level) } },
        onSounds = { on -> change { SettingsLogic.withSounds(it, on) } },
        onBlindMode = { on -> change { it.copy(blindMode = on) } },
        onReset = { scope.launch { store.clearEvents(); settings.update { SettingsLogic.reset() } } },
        onWhatSaw = onWhatSaw,
        onOemGuide = onOemGuide,
        onCopyDebug = diagnostics?.let { d -> { scope.launch { clipboard.setText(AnnotatedString(d.copyableReport())) } } },
        weeklyOn = weeklyOn,
        onWeeklyToggle = { on -> weekly?.setEnabled(on); weeklyOn = on },
        onClearDebug = diagnostics?.let { d -> { scope.launch { d.clear() } } },
    )
}

/** Stateless Settings layout, previewable. */
@Composable
fun SettingsContent(
    s: UserSettings,
    apps: List<AppChoice>,
    onRoast: (RoastLevel) -> Unit,
    onToggleTarget: (String) -> Unit,
    onToggleMuted: (String) -> Unit,
    onQuietHours: (startMin: Int, endMin: Int) -> Unit,
    onStayDelay: (Int) -> Unit,
    onLanguage: (String) -> Unit,
    onNag: (NagLevel) -> Unit,
    onSounds: (Boolean) -> Unit,
    onBlindMode: (Boolean) -> Unit,
    onReset: () -> Unit,
    onWhatSaw: () -> Unit,
    onOemGuide: () -> Unit,
    onCopyDebug: (() -> Unit)? = null,
    onClearDebug: (() -> Unit)? = null,
    weeklyOn: Boolean? = null,
    onWeeklyToggle: (Boolean) -> Unit = {},
) {
    var confirmReset by remember { mutableStateOf(false) }
    var debugNote by remember { mutableStateOf<Int?>(null) }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(stringResource(R.string.settings_title), Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineMedium)

            Section(R.string.settings_roast_label, R.string.settings_roast_help) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    RoastLevel.entries.forEach { level ->
                        FilterChip(
                            selected = s.roast == level,
                            onClick = { onRoast(level) },
                            label = { Text(stringResource(roastName(level))) },
                            modifier = Modifier.heightIn(min = 48.dp),
                        )
                    }
                }
                // Live sample: changes instantly with the dial.
                Text(
                    stringResource(roastSampleRes(s.roast), stringResource(R.string.onboarding_roast_sample_app), SAMPLE_MINUTES),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Section(R.string.settings_nag_label, R.string.settings_nag_help) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    NagLevel.entries.forEach { level ->
                        FilterChip(
                            selected = s.nagLevel == level,
                            onClick = { onNag(level) },
                            label = { Text(stringResource(nagName(level))) },
                            modifier = Modifier.heightIn(min = 48.dp),
                        )
                    }
                }
                Text(stringResource(nagHint(s.nagLevel)), style = MaterialTheme.typography.bodyMedium)
            }

            if (weeklyOn != null) {
                Section(R.string.settings_weekly_label, R.string.settings_weekly_help) {
                    SwitchRow(stringResource(R.string.settings_weekly_label), weeklyOn, onWeeklyToggle)
                }
            }

            // PLATFORM plays sounds in the overlay behind SoundPolicy (silent mode, DND, calls, music win), so the switch is real.
            if (SOUNDS_WIRED) {
                Section(R.string.settings_sounds_label, R.string.settings_sounds_help) {
                    SwitchRow(stringResource(R.string.settings_sounds_label), s.soundsEnabled, onSounds)
                }
            }

            Section(R.string.settings_target_apps_label, R.string.settings_target_apps_help) {
                if (apps.isEmpty() || s.targetApps.isEmpty()) {
                    if (s.targetApps.isEmpty()) Text(stringResource(R.string.empty_apps_none_selected), style = MaterialTheme.typography.bodyMedium)
                }
                apps.forEach { app ->
                    val picked = app.pkg in s.targetApps
                    CheckRow(app.label, picked, enabled = true) { onToggleTarget(app.pkg) }
                }
            }

            Section(R.string.settings_muted_apps_label, R.string.settings_muted_apps_help) {
                apps.forEach { app -> CheckRow(app.label, app.pkg in s.mutedApps, enabled = true) { onToggleMuted(app.pkg) } }
            }

            Section(R.string.settings_quiet_hours_label, null) {
                val start = clockText(s.quietStartMin)
                val end = clockText(s.quietEndMin)
                Text(stringResource(R.string.settings_quiet_hours_help, start, end), style = MaterialTheme.typography.bodyMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TimeButton(start, s.quietStartMin) { onQuietHours(it, s.quietEndMin) }
                    TimeButton(end, s.quietEndMin) { onQuietHours(s.quietStartMin, it) }
                }
            }

            Section(R.string.settings_stay_delay_label, R.string.settings_stay_delay_help) {
                Text("${s.stayDelaySec} s", style = MaterialTheme.typography.titleMedium)
                Slider(
                    value = s.stayDelaySec.toFloat(),
                    onValueChange = { onStayDelay(it.toInt()) },
                    valueRange = SettingsLogic.STAY_DELAY_MIN_SEC.toFloat()..SettingsLogic.STAY_DELAY_MAX_SEC.toFloat(),
                    steps = SettingsLogic.STAY_DELAY_MAX_SEC - SettingsLogic.STAY_DELAY_MIN_SEC - 1,
                )
            }

            Section(R.string.settings_language_label, R.string.settings_language_help) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsLogic.SUPPORTED_LANGUAGES.forEach { code ->
                        FilterChip(
                            selected = s.language == code,
                            onClick = { onLanguage(code) },
                            label = { Text(stringResource(languageName(code))) },
                            modifier = Modifier.heightIn(min = 48.dp),
                        )
                    }
                }
                if (s.language == SettingsLogic.LANGUAGE_HINGLISH) {
                    Text(stringResource(R.string.settings_language_hinglish_note), style = MaterialTheme.typography.bodySmall)
                }
            }

            Section(R.string.settings_blind_mode_label, R.string.settings_blind_mode_help) {
                SwitchRow(stringResource(R.string.settings_blind_mode_label), s.blindMode, onBlindMode)
            }

            Section(R.string.settings_about_label, null) {
                Text(stringResource(R.string.settings_privacy_summary), style = MaterialTheme.typography.bodyMedium)
                OutlinedButton(onClick = onWhatSaw, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(stringResource(R.string.whatsaw_title)) }
                OutlinedButton(onClick = onOemGuide, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(stringResource(R.string.onboarding_keepalive_cta_open)) }
            }

            if (onCopyDebug != null && onClearDebug != null) {
                Section(R.string.settings_debug_label, R.string.settings_debug_help) {
                    OutlinedButton(onClick = { onCopyDebug(); debugNote = R.string.settings_debug_copied }, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(stringResource(R.string.settings_debug_copy)) }
                    debugNote?.let { Text(stringResource(it), Modifier.semantics { liveRegion = LiveRegionMode.Polite }, style = MaterialTheme.typography.bodyMedium) }
                    OutlinedButton(onClick = { onClearDebug(); debugNote = R.string.settings_debug_cleared }, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(stringResource(R.string.settings_debug_clear)) }
                }
            }

            OutlinedButton(onClick = { confirmReset = true }, Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(stringResource(R.string.settings_reset_label))
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            text = { Text(stringResource(R.string.settings_reset_confirm)) },
            confirmButton = { TextButton(onClick = { confirmReset = false; onReset() }) { Text(stringResource(R.string.settings_reset_label)) } },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text(stringResource(R.string.common_cancel)) } },
        )
    }
}

@Composable
private fun Section(title: Int, help: Int?, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(title), Modifier.semantics { heading() }, style = MaterialTheme.typography.titleMedium)
        if (help != null) Text(stringResource(help), style = MaterialTheme.typography.bodySmall)
        content()
    }
}

/** Whole row is the tap target (>= 48dp) and reads as one checkbox to TalkBack. */
@Composable
private fun CheckRow(label: String, checked: Boolean, enabled: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 48.dp)
            .toggleable(value = checked, enabled = enabled, role = Role.Checkbox, onValueChange = { onToggle() }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = null, enabled = enabled)
        Text(label, Modifier.padding(start = 12.dp), style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 48.dp).toggleable(value = checked, role = Role.Switch, onValueChange = onChange),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = null)
    }
}

/** Opens the framework time picker, honouring the device's 12/24 h setting. */
@Composable
private fun TimeButton(text: String, minutes: Int, onPicked: (Int) -> Unit) {
    val context = LocalContext.current
    OutlinedButton(
        onClick = {
            TimePickerDialog(context, { _, h, m -> onPicked(h * 60 + m) }, minutes / 60, minutes % 60, DateFormat.is24HourFormat(context)).show()
        },
        Modifier.heightIn(min = 48.dp),
    ) { Text(text) }
}

@Composable
private fun clockText(minutes: Int): String {
    val m = SettingsLogic.normalizeMinutes(minutes)
    val cal = java.util.Calendar.getInstance().apply { set(java.util.Calendar.HOUR_OF_DAY, m / 60); set(java.util.Calendar.MINUTE, m % 60) }
    return DateFormat.getTimeFormat(LocalContext.current).format(cal.time)
}

private fun nagName(level: NagLevel) = when (level) {
    NagLevel.CALM -> R.string.settings_nag_calm
    NagLevel.NAGGY -> R.string.settings_nag_naggy
    NagLevel.CHAOS -> R.string.settings_nag_chaos
}

private fun nagHint(level: NagLevel) = when (level) {
    NagLevel.CALM -> R.string.settings_nag_calm_hint
    NagLevel.NAGGY -> R.string.settings_nag_naggy_hint
    NagLevel.CHAOS -> R.string.settings_nag_chaos_hint
}

private fun languageName(code: String) =
    if (code == SettingsLogic.LANGUAGE_HINGLISH) R.string.settings_language_value_hinglish else R.string.settings_language_value_en

private fun roastName(level: RoastLevel) = when (level) {
    RoastLevel.GENTLE -> R.string.onboarding_roast_gentle
    RoastLevel.NORMAL -> R.string.onboarding_roast_normal
    RoastLevel.SAVAGE -> R.string.onboarding_roast_savage
}

private fun roastSampleRes(level: RoastLevel) = when (level) {
    RoastLevel.GENTLE -> R.string.onboarding_roast_sample_gentle
    RoastLevel.NORMAL -> R.string.onboarding_roast_sample_normal
    RoastLevel.SAVAGE -> R.string.onboarding_roast_sample_savage
}

private const val SAMPLE_MINUTES = 25

private val previewApps = listOf(AppChoice("pkg.a", "App A"), AppChoice("pkg.b", "App B"), AppChoice("pkg.c", "App C"), AppChoice("pkg.d", "App D"))

@Preview(showBackground = true, name = "default dark", heightDp = 1600) @Composable private fun SettingsDark() =
    JixxyTheme(dark = true) { SettingsContent(UserSettings(targetApps = setOf("pkg.a")), previewApps, {}, {}, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, {}) }
@Preview(showBackground = true, name = "cap reached light", heightDp = 1600) @Composable private fun SettingsCapLight() =
    JixxyTheme(dark = false) { SettingsContent(UserSettings(targetApps = setOf("pkg.a", "pkg.b", "pkg.c"), blindMode = true), previewApps, {}, {}, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, {}) }
@Preview(showBackground = true, name = "200% font", fontScale = 2f, heightDp = 2400) @Composable private fun SettingsLargeFont() =
    JixxyTheme(dark = true) { SettingsContent(UserSettings(language = "hinglish"), previewApps, {}, {}, {}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}) }

private const val SOUNDS_WIRED = true
