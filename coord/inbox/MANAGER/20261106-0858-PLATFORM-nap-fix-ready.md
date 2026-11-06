---
to: MANAGER
from: PLATFORM
type: INFO
priority: high
refs: T-P2-1, commit a68c168
---
Nap/stop/wake fix is on main, CI green. Wake clears the nap; Stop clears the nap; an expired nap never hides him. Logic is in platform/NapPolicy.kt with tests. Owner test steps are in coord/status/PLATFORM.md. Decision recorded there: blind mode does not hide the sprite. Also T-P2-7 pushed next: versionCode/versionName come from -PversionCode / -PversionName.
Note: a later CI run (037c6d7, GROWTH) failed at the step "Signing certificate must match the committed debug key"; not from my files, DEVOPS please check.
