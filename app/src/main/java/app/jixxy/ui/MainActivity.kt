package app.jixxy.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import app.jixxy.engine.api.AppCategory
import app.jixxy.platform.JixxyApp
import app.jixxy.platform.OverlayService
import app.jixxy.platform.signals.CategoryResolver
import app.jixxy.ui.nav.JixxyNavHost
import app.jixxy.ui.nav.Routes
import app.jixxy.ui.onboarding.AppChoice
import app.jixxy.ui.theme.JixxyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The real app entry (T-U2-1): hosts the Compose [JixxyNavHost] with PLATFORM's real repositories.
 * Wake sends PLATFORM's ACTION_WAKE, which clears the nap and shows the sprite, so
 * "Nap -> Stop -> Wake" brings Jixxy back.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as JixxyApp
        val prefs = getSharedPreferences(UI_PREFS, Context.MODE_PRIVATE)
        setContent {
            JixxyTheme {
                val apps by produceState(emptyList<AppChoice>()) { value = withContext(Dispatchers.Default) { launchableApps() } }
                JixxyNavHost(
                    startRoute = if (prefs.getBoolean(KEY_ONBOARDING_DONE, false)) Routes.HOME else Routes.ONBOARDING,
                    settings = app.settings,
                    store = app.stateStore,
                    apps = apps,
                    appLabel = remember { CategoryResolver(applicationContext)::label },
                    onOnboardingDone = {
                        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
                        wakeJixxy()
                    },
                    onNapRequested = { napJixxy() },
                    onWakeRequested = { wakeJixxy() },
                )
            }
        }
    }

    /** Launcher-intent app list (no broad package permission), without PROTECTED apps and without Jixxy itself. */
    private fun launchableApps(): List<AppChoice> {
        val resolver = CategoryResolver(applicationContext)
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(intent, 0)
            .map { it.activityInfo.packageName }
            .distinct()
            .filter { it != packageName && resolver.resolve(it) != AppCategory.PROTECTED }
            .map { AppChoice(it, resolver.label(it)) }
            .sortedBy { it.label.lowercase() }
    }

    /** Starts (or re-starts) the sprite service. Safe to call while it is already running. */
    private fun wakeJixxy() {
        val intent = Intent(this, OverlayService::class.java)
        try {
            val wake = intent.setAction(OverlayService.ACTION_WAKE)
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(wake) else startService(wake)
        } catch (e: IllegalStateException) { /* Android refused; Home still shows the true state next time. */ }
    }

    /** Tells a running service about the nap; if it is not running, the saved nap time is enough. */
    private fun napJixxy() {
        if (!OverlayService.wantedRunning(this)) return
        startService(Intent(this, OverlayService::class.java).setAction(OverlayService.ACTION_NAP))
    }

    private companion object {
        const val UI_PREFS = "ui_prefs"
        const val KEY_ONBOARDING_DONE = "onboarding_done"
    }
}
