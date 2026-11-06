package app.jixxy.platform

import app.jixxy.ui.state.PermissionsSource
import app.jixxy.ui.state.PermissionsUiState

/** Maps [PermissionsState] to the UI's [PermissionsSource] port (T-P2-2). Re-reads the system on every call. */
class PermissionsSourceAdapter(private val repo: PermissionsRepository) : PermissionsSource {
    override fun current(): PermissionsUiState {
        repo.refresh()
        return repo.state.value.toUi()
    }
}

/** Pure mapping, unit-tested. */
fun PermissionsState.toUi() = PermissionsUiState(
    overlayGranted = overlayGranted,
    usageAccessGranted = usageAccessGranted,
    notificationsGranted = notificationsGranted,
    batteryExempt = batteryUnrestricted,
    restrictedSettingsLikelyBlocked = restrictedSettingsLikelyBlocked,
)
