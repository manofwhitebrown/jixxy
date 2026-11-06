@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package app.jixxy.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.jixxy.R
import app.jixxy.engine.api.RoastLevel
import app.jixxy.ui.onboarding.AppChoice
import app.jixxy.ui.components.CatImage
import app.jixxy.ui.components.CatPoses
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

    // System Back walks the flow backwards instead of leaving the app mid-onboarding.
    BackHandler(enabled = step != OnboardingStep.HELLO) { goBack() }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CatImage(CatPoses.forStep(step))
            when (step) {
                OnboardingStep.HELLO -> StepBody(R.string.onboarding_hello_title, R.string.onboarding_hello_body)
                OnboardingStep.OVERLAY -> {
                    StepBody(R.string.onboarding_overlay_why_title, R.string.onboarding_overlay_why_body)
                    CantDoList()
                    GrantRow(permissions.overlayGranted, R.string.onboarding_overlay_granted, R.string.onboarding_overlay_cta_open, actions.openOverlaySettings)
                }
                OnboardingStep.USAGE -> {
                    StepBody(R.string.onboarding_usage_why_title, R.string.onboarding_usage_why_body)
                    CantDoList()
                    if (permissions.restrictedSettingsLikelyBlocked) RestrictedSettingsExplainer(actions.openAppInfo)
                    GrantRow(permissions.usageAccessGranted, R.string.onboarding_usage_granted, R.string.onboarding_usage_cta_open, actions.openUsageAccessSettings)
                }
                OnboardingStep.NOTIFICATIONS -> {
                    StepBody(R.string.onboarding_notifications_title, R.string.onboarding_notifications_body)
                    GrantRow(permissions.notificationsGranted, R.string.onboarding_notifications_granted, R.string.onboarding_notifications_cta_allow, actions.requestNotifications)
                }
                OnboardingStep.KEEP_ALIVE -> {
                    StepBody(R.string.onboarding_keepalive_title, R.string.onboarding_keepalive_body)
                    GrantRow(permissions.batteryExempt, R.string.onboarding_keepalive_done, R.string.onboarding_keepalive_cta_open, actions.openKeepAliveSettings)
                    Text(stringResource(R.string.onboarding_keepalive_menus_differ), style = MaterialTheme.typography.bodySmall)
                }
                OnboardingStep.PICK_APPS -> {
                    StepBody(R.string.onboarding_apps_title, R.string.onboarding_apps_body)
                    availableApps.forEach { app ->
                        val on = app.pkg in picked
                        FilterChip(
                            selected = on,
                            onClick = { picked = OnboardingFlow.toggleApp(picked.toSet(), app.pkg).toList(); actions.onTargetAppsChosen(picked.toSet()) },
                            label = { Text(app.label) },
                            modifier = Modifier.heightIn(min = 48.dp),
                        )
                    }
                    if (picked.size >= OnboardingFlow.MAX_TARGET_APPS) {
                        Text(stringResource(R.string.onboarding_apps_cap_reached), style = MaterialTheme.typography.bodySmall)
                    }
                }
                OnboardingStep.ROAST -> {
                    StepBody(R.string.onboarding_roast_title, null)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoastLevel.entries.forEach { level ->
                            FilterChip(
                                selected = roast == level,
                                onClick = { roast = level; actions.onRoastChosen(level) },
                                label = { Text(stringResource(roastLabel(level))) },
                                modifier = Modifier.heightIn(min = 48.dp),
                            )
                        }
                    }
                    Text(
                        stringResource(roastSample(roast), stringResource(R.string.onboarding_roast_sample_app), SAMPLE_MINUTES),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(stringResource(R.string.onboarding_roast_footnote), style = MaterialTheme.typography.bodySmall)
                }
                OnboardingStep.FINISH -> StepBody(R.string.onboarding_finish_title, R.string.onboarding_finish_body)
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
    RoastLevel.GENTLE -> R.string.onboarding_roast_gentle
    RoastLevel.NORMAL -> R.string.onboarding_roast_normal
    RoastLevel.SAVAGE -> R.string.onboarding_roast_savage
}

private fun roastSample(level: RoastLevel) = when (level) {
    RoastLevel.GENTLE -> R.string.onboarding_roast_sample_gentle
    RoastLevel.NORMAL -> R.string.onboarding_roast_sample_normal
    RoastLevel.SAVAGE -> R.string.onboarding_roast_sample_savage
}

private const val SAMPLE_MINUTES = 25

@Composable
private fun StepBody(title: Int, body: Int?) {
    Text(stringResource(title), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
    if (body != null) Text(stringResource(body), style = MaterialTheme.typography.bodyLarge)
}

/** What Jixxy cannot do. Shown on both scary-permission screens. Plain, no jokes. */
@Composable
private fun CantDoList() {
    Text(stringResource(R.string.onboarding_cant_do), style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun GrantRow(granted: Boolean, grantedText: Int, openText: Int, onOpen: () -> Unit) {
    if (granted) {
        Text(stringResource(grantedText), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    } else {
        OutlinedButton(onClick = onOpen, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(stringResource(openText)) }
    }
}

/** Sideload path on Android 13+: hedge honestly, wording differs by brand/version. */
@Composable
private fun RestrictedSettingsExplainer(onOpenAppInfo: () -> Unit) {
    Text(stringResource(R.string.onboarding_restricted_title), style = MaterialTheme.typography.titleMedium)
    listOf(
        R.string.onboarding_restricted_step1, R.string.onboarding_restricted_step2, R.string.onboarding_restricted_step3,
        R.string.onboarding_restricted_step4, R.string.onboarding_restricted_step5,
    ).forEachIndexed { i, step -> Text("${i + 1}. ${stringResource(step)}", style = MaterialTheme.typography.bodyMedium) }
    Text(stringResource(R.string.onboarding_restricted_hedge_note), style = MaterialTheme.typography.bodySmall)
    TextButton(onClick = onOpenAppInfo, Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.onboarding_restricted_cta_open_app_info)) }
}

@Composable
private fun NavButtons(step: OnboardingStep, onPrimary: () -> Unit, onLite: () -> Unit, onSkip: () -> Unit, onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onPrimary, Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Text(stringResource(primaryLabel(step)))
        }
        if (step == OnboardingStep.HELLO) {
            TextButton(onClick = onLite, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(stringResource(R.string.onboarding_hello_cta_lite)) }
        }
        if (step != OnboardingStep.HELLO && step != OnboardingStep.FINISH) {
            TextButton(onClick = onSkip, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(stringResource(R.string.onboarding_notifications_cta_later)) }
        }
        if (step != OnboardingStep.HELLO) {
            TextButton(onClick = onBack, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text(stringResource(R.string.common_back)) }
        }
    }
}

private fun primaryLabel(step: OnboardingStep) = when (step) {
    OnboardingStep.HELLO -> R.string.onboarding_hello_cta_primary
    OnboardingStep.FINISH -> R.string.onboarding_finish_cta
    else -> R.string.common_continue
}

private val previewApps = listOf(AppChoice("pkg.a", "App A"), AppChoice("pkg.b", "App B"), AppChoice("pkg.c", "App C"), AppChoice("pkg.d", "App D"))

@Preview(showBackground = true, name = "hello dark") @Composable private fun OnboardingHelloDark() = JixxyTheme(dark = true) { OnboardingScreen(onDone = {}) }
@Preview(showBackground = true, name = "hello light") @Composable private fun OnboardingHelloLight() = JixxyTheme(dark = false) { OnboardingScreen(onDone = {}) }
@Preview(showBackground = true, name = "apps", fontScale = 2f) @Composable private fun OnboardingAppsLargeFont() =
    JixxyTheme(dark = true) { OnboardingScreen(onDone = {}, availableApps = previewApps, permissions = PermissionsUiState(restrictedSettingsLikelyBlocked = true)) }
