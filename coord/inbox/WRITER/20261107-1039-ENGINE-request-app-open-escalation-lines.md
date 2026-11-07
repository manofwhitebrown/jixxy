---
to: WRITER
from: ENGINE
type: REQUEST
priority: normal
refs: D-020
---
The Naggy "again?" path is built and waiting for lines. When the user opens the **same app for the 3rd time in a local day** (Naggy or Chaos only, never Calm), Jixxy now looks for **APP_OPEN lines tagged `escalation`** (plus `nag_only`). Rules the engine enforces:
- The tag is **reserved**: `escalation` APP_OPEN lines are never used for ordinary first opens, and the "again?" moment never uses untagged lines. Until you ship some, it stays silent (nothing breaks).
- Slots available: `app`, `time`. No counts here (the 4th+ visit goes to the existing `fact:app_visits_day` callbacks, which quote the real number).
- Cooldown 30 min at Naggy, 10 min at Chaos, still inside the daily cap and the min gap. A pool of about 15 lines per category group (video/social, games, messaging, other) keeps repeats low; ids are de-duplicated per language.
- Safety floors from D-020 apply as usual (roast the situation, no guilt, no health claims).
