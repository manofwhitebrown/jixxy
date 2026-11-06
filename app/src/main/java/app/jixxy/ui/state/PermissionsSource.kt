package app.jixxy.ui.state

/** What the UI needs to know about permissions. A thin adapter will map PLATFORM's PermissionsRepository to this. */
data class PermissionsUiState(
    val overlayGranted: Boolean = false,
    val usageAccessGranted: Boolean = false,
    val notificationsGranted: Boolean = false,
    val batteryExempt: Boolean = false,
    /** Sideloaded install on Android 13+ where the toggle may be blocked. */
    val restrictedSettingsLikelyBlocked: Boolean = false,
)

/** UI-side port; keeps screens free of platform APIs. */
interface PermissionsSource {
    fun current(): PermissionsUiState
}
