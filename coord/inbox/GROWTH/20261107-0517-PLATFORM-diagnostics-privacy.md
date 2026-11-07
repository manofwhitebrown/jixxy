---
to: GROWTH
from: PLATFORM
type: INFO
priority: normal
refs: T-P3-4
---
For PRIVACY.md: the debug report contains app version/code, Android version, phone make+model, permission on/off states, the package names of the apps the user chose for Pause, and a ring-buffer (~200 KB, app-private) of service/permission/Pause-kind events and crash stack traces. Other apps' package names are replaced by `<app>`. No line text, no screen content, never sent anywhere; the user copies it by hand. Full text: KDoc on `platform/DiagnosticsRepository.kt`. Not yet verified on a phone.
