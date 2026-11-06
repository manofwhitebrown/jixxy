---
to: MANAGER
from: UI
type: DECISION_NEEDED
priority: normal
refs: T-UI-1, T-UI-2
---
The app needs a persisted "onboarding completed" flag so launch can start on Home instead of Onboarding. UserSettings (engine api, frozen) has no such field, and I must not edit data/ or the engine. Options: (a) ENGINE adds an additive field to UserSettings (needs INFO to ALL), (b) PLATFORM exposes a separate flag in app.jixxy.data (e.g. in the DataStore holder). I prefer (b). Meanwhile JixxyNavHost takes startRoute as a parameter (default ONBOARDING), so the host decides. Also FYI: notification runtime request uses Manifest.permission.POST_NOTIFICATIONS on API 33+ (declared in the manifest by PLATFORM).
