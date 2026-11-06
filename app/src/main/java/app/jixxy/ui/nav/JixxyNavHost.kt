package app.jixxy.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.jixxy.engine.api.SettingsRepository
import app.jixxy.engine.api.StateStore
import app.jixxy.engine.fakes.InMemorySettingsRepository
import app.jixxy.engine.fakes.InMemoryStateStore
import app.jixxy.ui.onboarding.AppChoice
import app.jixxy.ui.screens.HomeScreen
import app.jixxy.ui.screens.OemGuideScreen
import app.jixxy.ui.screens.OnboardingRoute
import app.jixxy.ui.screens.SettingsScreen
import app.jixxy.ui.screens.WeeklyScreen
import app.jixxy.ui.screens.WhatSawScreen

/** Root navigation graph. [startRoute] is ONBOARDING until onboarding is done. */
@Composable
fun JixxyNavHost(
    startRoute: String = Routes.ONBOARDING,
    modifier: Modifier = Modifier,
    // Defaults are in-memory fakes until PLATFORM wires the real repositories (data/).
    settings: SettingsRepository = remember { InMemorySettingsRepository() },
    store: StateStore = remember { InMemoryStateStore() },
    // Launcher-intent app list from PLATFORM (PROTECTED apps removed); empty until wired.
    apps: List<AppChoice> = emptyList(),
    appLabel: (String) -> String = { it },
) {
    val nav = rememberNavController()
    NavHost(nav, startDestination = startRoute, modifier = modifier) {
        composable(Routes.ONBOARDING) {
            OnboardingRoute(
                settings = settings,
                apps = apps,
                onOpenKeepAliveGuide = { nav.navigate(Routes.OEM_GUIDE) },
                onDone = { nav.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } } },
            )
        }
        composable(Routes.HOME) { HomeScreen(settings = settings, store = store, onSettings = { nav.navigate(Routes.SETTINGS) }, onWeekly = { nav.navigate(Routes.WEEKLY) }) }
        composable(Routes.SETTINGS) { SettingsScreen(settings = settings, store = store, apps = apps, onWhatSaw = { nav.navigate(Routes.WHAT_SAW) }, onOemGuide = { nav.navigate(Routes.OEM_GUIDE) }) }
        composable(Routes.WHAT_SAW) { WhatSawScreen(store = store, appLabel = appLabel) }
        composable(Routes.WEEKLY) { WeeklyScreen() }
        composable(Routes.OEM_GUIDE) { OemGuideScreen() }
    }
}
