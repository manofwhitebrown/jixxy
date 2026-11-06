package app.jixxy.ui.state

import app.jixxy.platform.PermissionsState

/** Maps PLATFORM's [PermissionsState] to the UI's own [PermissionsUiState] so screens stay free of platform types. */
fun PermissionsState.toUi(): PermissionsUiState = PermissionsUiState(
    overlayGranted = overlayGranted,
    usageAccessGranted = usageAccessGranted,
    notificationsGranted = notificationsGranted,
    batteryExempt = batteryUnrestricted,
    restrictedSettingsLikelyBlocked = restrictedSettingsLikelyBlocked,
)
