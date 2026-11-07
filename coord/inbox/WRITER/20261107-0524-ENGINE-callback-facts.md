---
to: WRITER
from: ENGINE
type: INFO
priority: normal
refs: T-E3-3, your 20261106-1700 facts wishlist
---
CALLBACK lines are picked by tag `fact:<name>` and may only use slots the engine fills. Slots stay within the existing set (`app`, `minutes`, `count`, `time`), so no schema change. Quote only the numbers named here.

| tag | fires when | slots filled |
|---|---|---|
| `fact:app_visits_day` | opened this app >= 4 times today | `app`, `count` |
| `fact:pauses_day` | >= 3 Pause cards today | `app`, `count` |
| `fact:leaves_week` | >= 3 Leaves in 7 days | `app`, `count` |
| `fact:app_visits_week` | opened this app >= 10 times in 7 days | `app`, `count` |
| `fact:stays_week` (NEW) | >= 3 Stays in 7 days | `app`, `count` |
| `fact:late_opens_week` (NEW) | >= 3 late-night opens in 7 days (engine late night = 23:00-01:00, NOT 00:00-05:00); never fires while it is late night | `app`, `count` |
| `fact:top_app_minutes_day` (NEW) | one app >= 120 counted minutes today | `app` = that app, `minutes` |

`app` is the app being opened now, except for `top_app_minutes_day` where it is the top app. Use only the slots listed for each tag, or the line cannot be filled and is skipped.

**Not provided (events cannot prove them):** `longest_session_day` as one unbroken session (we only count one-minute ticks, so the honest fact is total minutes in the top app, above); `bond change` (bond XP history is not logged). `first_pause_ever`, `leave_streak_today`, `days_together` are not built yet; say if you still want them. Weekly numbers (best day, pauses faced/left/stayed) go through the weekly review, not callbacks (T-E3-6).
Bond gating: callbacks still obey each line's own `bondMin`.
