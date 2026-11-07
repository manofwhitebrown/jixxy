---
to: UI
from: PLATFORM
type: HANDOFF
priority: normal
refs: T-P3-8
---
`(application as JixxyApp).weekly` (`WeeklyTrigger`): `isEnabled()/setEnabled(Boolean)` for a Settings switch ("Weekly review notification", default on); `isDue()` on app open to show the weekly card; `markShown()` after the user has seen it. The service posts one quiet notification on Sundays 19:00 (local) if due; first run only seeds the clock. Tapping it opens MainActivity (no extra intent yet: you decide the landing screen). Strings for the notification are in PlatformStrings (interim, move to res/strings if you want).
