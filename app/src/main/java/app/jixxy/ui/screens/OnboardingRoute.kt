package app.jixxy.ui.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.jixxy.engine.api.SettingsRepository
import app.jixxy.platform.PermissionKind
import app.jixxy.platform.PermissionsRepository
import app.jixxy.ui.onboarding.AppChoice
import app.jixxy.ui.onboarding.OnboardingFlow
import app.jixxy.ui.state.toUi
import kotlinx.coroutines.launch

/**
 * Connects [OnboardingScreen] to PLATFORM's [PermissionsRepository] and to [settings].
 * Permission state is re-read every time the app returns to the foreground (Android has no change callback),
 * so leaving to system Settings and coming back updates the screen. Every system screen is started inside
 * try/catch because some phones lack some pages; the user can always carry on with "Not now".
 */
@Composable
fun OnboardingRoute(
    settings: SettingsRepository,
    apps: List<AppChoice>,
    onOpenKeepAliveGuide: () -> Unit,
    onDone: () -> Unit,
) {
    val context = LocalContext.current
    val repo = remember { PermissionsRepository(context.applicationContext) }
    val permissions by repo.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { repo.refresh() }

    fun open(kind: PermissionKind) {
        try { context.startActivity(repo.intentFor(kind)) } catch (e: ActivityNotFoundException) { } catch (e: SecurityException) { }
    }
    val notificationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { repo.refresh() }

    // Remember the step so a swiped-away app resumes where the user was (never a dead end: bad values restart at Hello).
    val prefs = remember { context.getSharedPreferences("ui_prefs", android.content.Context.MODE_PRIVATE) }
    val savedLite = remember { prefs.getBoolean(KEY_STEP_LITE, false) }
    val startStep = remember { OnboardingFlow.resume(prefs.getString(KEY_STEP, null), savedLite, Build.VERSION.SDK_INT) }

    OnboardingScreen(
        initialStep = startStep,
        initialLite = savedLite,
        onDone = { prefs.edit().remove(KEY_STEP).remove(KEY_STEP_LITE).apply(); onDone() },
        permissions = permissions.toUi(),
        availableApps = apps,
        actions = OnboardingActions(
            openOverlaySettings = { open(PermissionKind.OVERLAY) },
            openUsageAccessSettings = { open(PermissionKind.USAGE_ACCESS) },
            openAppInfo = { open(PermissionKind.APP_DETAILS) },
            requestNotifications = {
                if (Build.VERSION.SDK_INT >= 33) notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                else open(PermissionKind.NOTIFICATIONS)
            },
            openKeepAliveSettings = onOpenKeepAliveGuide,
            onTargetAppsChosen = { chosen -> scope.launch { settings.update { it.copy(targetApps = chosen) } } },
            onProgress = { step, lite -> prefs.edit().putString(KEY_STEP, step.name).putBoolean(KEY_STEP_LITE, lite).apply() },
            onRoastChosen = { level -> scope.launch { settings.update { it.copy(roast = level) } } },
        ),
    )
}

private const val KEY_STEP = "onboarding_step"
private const val KEY_STEP_LITE = "onboarding_lite"
