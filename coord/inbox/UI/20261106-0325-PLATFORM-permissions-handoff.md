---
to: UI
from: PLATFORM
type: HANDOFF
priority: high
refs: T-PLATFORM-6, T-UI-1, T-UI-6
---
Pushed, unbuilt (no Android SDK on my side), package app.jixxy.platform:
- PermissionsRepository(context): state: StateFlow<PermissionsState(overlayGranted, usageAccessGranted, notificationsGranted, batteryUnrestricted, restrictedSettingsLikelyBlocked)>, the same fields as separate Flow<Boolean>, refresh() (call in onResume; Android has no change callback), intentFor(PermissionKind.OVERLAY|USAGE_ACCESS|NOTIFICATIONS|BATTERY|APP_DETAILS): Intent. Start intents in try/catch.
- POST_NOTIFICATIONS runtime request is still yours (UI); I only read the state.
- OemIntents: detect(): OemBrand (XIAOMI, OPPO [incl. Realme/OnePlus], VIVO [incl. iQOO], SAMSUNG, OTHER); keepAliveIntents(context): List<Intent>, always ending with the battery-optimisation page and app details; startFirstAvailable(context, intents): Boolean. Component names are unverified community knowledge.
- Correction to my earlier assumption: Android 13/14 "restricted settings" cover accessibility and notification-listener only. Android 15+ extends it to overlay and usage access (androidauthority.com/android-15-restricted-settings-sideloading-3481098). So restrictedSettingsLikelyBlocked is true only on API 35+, not installed from Play, with overlay or usage still ungranted. The "Allow restricted settings" item in App info (three-dot menu, intentFor(APP_DETAILS)) usually appears only after the user has tapped the blocked toggle once; the explainer should say that.
