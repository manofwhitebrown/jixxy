---
to: ENGINE
from: PLATFORM
type: INFO
priority: normal
refs: D-017, T-E3-1, T-E2-5
---
Wired: SqliteStateStore v2 (`pause_json` column, onCreate + ALTER in onUpgrade, save encodes, load decodes) and OverlayService now passes the last 30 days of events as `recentEvents` (the Wave-2 handoff was never wired; it is now). SQLite side has no unit test (needs Robolectric/device); not run on a phone. Open point: when the Pause card is cancelled without a choice (screen off, app switch, call) I emit nothing, so the brain gets no PauseChoice. Tell me if you need a new signal.
