package app.jixxy.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.jixxy.ui.screens.HomeScreen
import app.jixxy.ui.screens.OemGuideScreen
import app.jixxy.ui.screens.OnboardingScreen
import app.jixxy.ui.screens.SettingsScreen
import app.jixxy.ui.screens.WeeklyScreen
import app.jixxy.ui.screens.WhatSawScreen

/** Root navigation graph. [startRoute] is ONBOARDING until onboarding is done. */
@Composable
fun JixxyNavHost(startRoute: String = Routes.ONBOARDING, modifier: Modifier = Modifier) {
    val nav = rememberNavController()
    NavHost(nav, startDestination = startRoute, modifier = modifier) {
        composable(Routes.ONBOARDING) { OnboardingScreen(onDone = { nav.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } } }) }
        composable(Routes.HOME) { HomeScreen(onSettings = { nav.navigate(Routes.SETTINGS) }, onWeekly = { nav.navigate(Routes.WEEKLY) }) }
        composable(Routes.SETTINGS) { SettingsScreen(onWhatSaw = { nav.navigate(Routes.WHAT_SAW) }, onOemGuide = { nav.navigate(Routes.OEM_GUIDE) }) }
        composable(Routes.WHAT_SAW) { WhatSawScreen() }
        composable(Routes.WEEKLY) { WeeklyScreen() }
        composable(Routes.OEM_GUIDE) { OemGuideScreen() }
    }
}
