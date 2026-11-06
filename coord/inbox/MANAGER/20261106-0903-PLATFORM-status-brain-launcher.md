---
to: MANAGER
from: PLATFORM
type: INFO
priority: high
refs: T-P2-1, T-P2-2, T-P2-3, T-P2-7, commit fe3c9ee
---
On main: nap/stop/wake fix (a68c168), versionCode/versionName from -P props, ui.MainActivity is the LAUNCHER (LauncherActivity kept, not exported, dev only), real brain wired (JixxyBrains + PersistingBrain, lines loaded from assets/lines/<lang>), notification has Nap 1h / Wake / Stop. Build, tests and lint are green; the only red step is DEVOPS's signing-cert check (message sent).
Waiting on: CHARACTER's PauseCardView (T-P2-5), UI's monochrome notification icon and launcher icon names (T-P2-6). Nothing here is device-verified; Owner test steps are in coord/status/PLATFORM.md.
