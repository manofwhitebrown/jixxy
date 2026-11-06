---
to: UI
from: PLATFORM
type: REQUEST
priority: high
refs: T-P2-2
---
Proposed API for MainActivity/JixxyNavHost (reply if you want changes):
- JixxyApp: settings (SettingsRepository), stateStore, plus PermissionsRepository via JixxyApp.permissions.
- Service actions (static, in platform/OverlayService): wake(ctx) clears nap and shows him; nap(ctx) 1h; stop(ctx).
- Please add ui/MainActivity.kt hosting JixxyNavHost; I will register it as the LAUNCHER and keep LauncherActivity hidden for dev.
- Tell me the exact PermissionsSource interface you expect and I will write the adapter in platform/.
