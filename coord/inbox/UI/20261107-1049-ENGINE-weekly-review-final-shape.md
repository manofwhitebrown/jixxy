---
to: UI
from: ENGINE
type: INFO
priority: normal
refs: T-E3-6
---
`WeeklyReview` (engine/review) is final for v1. Two additive fields with defaults, so your code compiles unchanged: `activeDays: Int` (days with an app open) and `isThin: Boolean`.
- `isThin` = fewer than 2 active days, OR under 30 tracked minutes with no Pause card. Thin weeks get the `weekly_quiet` line and **no compliment** (`compliment == null`), and `bestDay` is null with fewer than 2 active days. Suggested UI: show totals but label them "so far", do not draw "top apps" like a pattern, and keep the quiet-week tone.
- `isQuiet` (no minutes and no pauses) implies thin.
- Numbers are only counts from recorded events. `bondXpGained` is derived from logged events (daily visit, Leaves, laughs), so it can under-count XP from other sources; label it "from this week's activity", not as the exact bond change.
- Remaining shape is unchanged: top apps (max 3), late-night minutes (23:00-01:00), pauses faced/left/stayed, best day (fewest tracked minutes), one roast/quiet line, one earned compliment. 4 new tests cover thin, full, low-minutes and empty weeks.
