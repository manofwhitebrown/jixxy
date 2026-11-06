package app.jixxy.ui.screens

import android.os.Build
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.jixxy.R
import app.jixxy.engine.api.RoastLevel
import app.jixxy.ui.onboarding.AppChoice
import app.jixxy.ui.onboarding.OnboardingFlow
import app.jixxy.ui.onboarding.OnboardingStep
import app.jixxy.ui.state.PermissionsUiState
import app.jixxy.ui.theme.JixxyTheme

/** Hooks the host (PLATFORM adapter) fills in. Defaults are no-ops so previews work. */
data class OnboardingActions(
    val openOverlaySettings: () -> Unit = {},
    val openUsageAccessSettings: () -> Unit = {},
    val openAppInfo: () -> Unit = {},
    val requestNotifications: () -> Unit = {},
    val openKeepAliveSettings: () -> Unit = {},
    val onTargetAppsChosen: (Set<String>) -> Unit = {},
    val onRoastChosen: (RoastLevel) -> Unit = {},
    val onLiteMode: () -> Unit = {},
)

/**
 * First-run flow (T-UI-1). Step and choices survive rotation and process death via rememberSaveable,
 * so the user can leave to system Settings and come back. Presentational: [permissions] is refreshed by the host on resume.
 */
@Composable
fun OnboardingScreen(
    onDone: () -> Unit,
    permissions: PermissionsUiState = PermissionsUiState(),
    availableApps: List<AppChoice> = emptyList(),
    actions: OnboardingActions = OnboardingActions(),
    sdkInt: Int = Build.VERSION.SDK_INT,
) {
    var step by rememberSaveable { mutableStateOf(OnboardingStep.HELLO) }
    var lite by rememberSaveable { mutableStateOf(false) }
    var picked by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var roast by rememberSaveable { mutableStateOf(RoastLevel.NORMAL) }

    fun advance() { step = OnboardingFlow.next(step, lite, sdkInt) }
    fun goBack() { step = OnboardingFlow.back(step, lite, sdkInt) }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (step) {
                OnboardingStep.HELLO -> StepBody(R.string.ob_hello_title, R.string.ob_hello_body)
                OnboardingStep.OVERLAY -> {
                    StepBody(R.string.ob_overlay_title, R.string.ob_overlay_body)
                    CantDoList()
                    GrantRow(permissions.overlayGranted, actions.openOverlaySettings)
                }
                OnboardingStep.USAGE -> {
                    StepBody(R.string.ob_usage_title, R.string.ob_usage_body)
                    CantDoList()
                    if (permissions.restrictedSettingsLikelyBlocked) RestrictedSettingsExplainer(actions.openAppInfo)
                    GrantRow(permissions.usageAccessGranted, actions.openUsageAccessSettings)
                }
                OnboardingStep.NOTIFICATIONS -> {
                    StepBody(R.string.ob_notif_title, R.string.ob_notif_body)
                    GrantRow(permissions.notificationsGranted, actions.requestNotifications)
                }
                OnboardingStep.KEEP_ALIVE -> {
                    StepBody(R.string.ob_keepalive_title, R.string.ob_keepalive_body)
                    GrantRow(permissions.batteryExempt, actions.openKeepAliveSettings)
                }
                OnboardingStep.PICK_APPS -> {
                    StepBody(R.string.ob_apps_title, R.string.ob_apps_body)
                    availableApps.forEach { app ->
                        val on = app.pkg in picked
                        FilterChip(
                            selected = on,
                            onClick = { picked = OnboardingFlow.toggleApp(picked.toSet(), app.pkg).toList(); actions.onTargetAppsChosen(picked.toSet()) },
                            label = { Text(app.label) },
                            modifier = Modifier.heightIn(min = 48.dp),
                        )
                    }
                }
                OnboardingStep.ROAST -> {
                    StepBody(R.string.ob_roast_title, R.string.ob_roast_body)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoastLevel.entries.forEach { level ->
                            FilterChip(
                                selected = roast == level,
                                onClick = { roast = level; actions.onRoastChosen(level) },
                                label = { Text(stringResource(roastLabel(level))) },
                                modifier = Modifier.heightIn(min = 48.dp),
                            )
                        }
                    }
                    Text(stringResource(R.string.ob_roast_sample_placeholder), style = MaterialTheme.typography.bodyMedium)
                }
                OnboardingStep.FINISH -> StepBody(R.string.ob_finish_title, R.string.ob_finish_body)
            }

            NavButtons(
                step = step,
                onPrimary = { if (step == OnboardingStep.FINISH) onDone() else advance() },
                onLite = { lite = true; actions.onLiteMode(); step = OnboardingStep.OVERLAY },
                onSkip = ::advance,
                onBack = ::goBack,
            )
        }
    }
}

private fun roastLabel(level: RoastLevel) = when (level) {
    RoastLevel.GENTLE -> R.string.roast_gentle
    RoastLevel.NORMAL -> R.string.roast_normal
    RoastLevel.SAVAGE -> R.string.roast_savage
}

@Composable
private fun StepBody(title: Int, body: Int) {
    Text(stringResource(title), style = MaterialTheme.typography.headlineMedium)
    Text(stringResource(body), style = MaterialTheme.typography.bodyLarge)
}

/** What Jixxy cannot do. Shown on both scary-permission screens. Plain, no jokes. */
@Composable
private fun CantDoList() {
    Text(stringResource(R.string.ob_cant_do), style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun GrantRow(granted: Boolean, onOpen: () -> Unit) {
    if (granted) {
        Text(stringResource(R.string.ob_granted), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    } else {
        OutlinedButton(onClick = onOpen, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(stringResource(R.string.ob_open_setting)) }
    }
}

/** Sideload path on Android 13+: hedge honestly, wording differs by brand/version. */
@Composable
private fun RestrictedSettingsExplainer(onOpenAppInfo: () -> Unit) {
    Text(stringResource(R.string.ob_restricted_title), style = MaterialTheme.typography.titleMedium)
    Text(stringResource(R.string.ob_restricted_steps), style = MaterialTheme.typography.bodyMedium)
    Text(stringResource(R.string.ob_restricted_hedge), style = MaterialTheme.typography.bodySmall)
    TextButton(onClick = onOpenAppInfo, Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.ob_open_app_info)) }
}

@Composable
private fun NavButtons(step: OnboardingStep, onPrimary: () -> Unit, onLite: () -> Unit, onSkip: () -> Unit, onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onPrimary, Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Text(stringResource(if (step == OnboardingStep.FINISH) R.string.ob_wake_him else R.string.ob_continue))
        }
        if (step == OnboardingStep.HELLO) {
            TextButton(onClick = onLite, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(stringResource(R.string.ob_lite)) }
        }
        if (step != OnboardingStep.HELLO && step != OnboardingStep.FINISH) {
            TextButton(onClick = onSkip, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(stringResource(R.string.ob_not_now)) }
        }
        if (step != OnboardingStep.HELLO) {
            TextButton(onClick = onBack, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(stringResource(R.string.ob_back)) }
        }
    }
}

private val previewApps = listOf(AppChoice("pkg.a", "App A"), AppChoice("pkg.b", "App B"), AppChoice("pkg.c", "App C"), AppChoice("pkg.d", "App D"))

@Preview(showBackground = true, name = "hello dark") @Composable private fun OnboardingHelloDark() = JixxyTheme(dark = true) { OnboardingScreen(onDone = {}) }
@Preview(showBackground = true, name = "hello light") @Composable private fun OnboardingHelloLight() = JixxyTheme(dark = false) { OnboardingScreen(onDone = {}) }
@Preview(showBackground = true, name = "apps", fontScale = 2f) @Composable private fun OnboardingAppsLargeFont() =
    JixxyTheme(dark = true) { OnboardingScreen(onDone = {}, availableApps = previewApps, permissions = PermissionsUiState(restrictedSettingsLikelyBlocked = true)) }
