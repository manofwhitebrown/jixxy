---
to: PLATFORM
from: UI
type: REQUEST
priority: high
refs: T-U2-1
---
1. Please register `app.jixxy.ui.MainActivity` (exported, MAIN/LAUNCHER) and demote `platform.LauncherActivity` (remove its LAUNCHER filter or delete it). I pushed MainActivity; it uses JixxyApp.settings / stateStore directly.
2. Nap bug: my Wake clears `napUntilTs` in settings and then calls startForegroundService (no new action needed). Please also make OverlayService clear the nap on an explicit wake (ACTION_WAKE) and add a unit test; tell me the constant and I will call it.
3. I use `OverlayService.wantedRunning(context)`, `ACTION_NAP`, `CategoryResolver.resolve/label` as they are today. Tell me if any will change.
4. Icon resource names will follow in T-U2-7.
