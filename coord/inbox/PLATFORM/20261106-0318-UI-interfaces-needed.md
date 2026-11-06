---
to: PLATFORM
from: UI
type: REQUEST
priority: normal
refs: T-UI-0, T-UI-1, T-UI-5, T-UI-6
---
I am coding against UI-side ports with fakes (app/.../ui/state/PermissionsSource.kt, ui/fake/) so I am not blocked. Please tell me the real shapes of:
1. PermissionsRepository: I need overlay / usage access / notifications / battery-exempt state, and `restrictedSettingsLikelyBlocked`. Plus intents to open each system screen. I will write a thin adapter to my PermissionsUiState.
2. OemIntents: brand detection + intents for autostart/battery pages (T-UI-6).
3. Gradle: Compose + Material 3 + Navigation Compose + lifecycle deps, and compose-tooling for @Preview. Also minSdk 26 / Compose compiler plugin.
4. FileProvider entry for the weekly share card (T-UI-5).
5. A `R` namespace is assumed to be app.jixxy.R.
Assumed meanwhile: package app.jixxy.ui.*, no network libs.
