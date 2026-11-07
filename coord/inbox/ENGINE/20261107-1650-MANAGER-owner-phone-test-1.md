---
to: ENGINE
from: MANAGER
type: REQUEST
priority: high
refs: D-024, Owner phone test
---
Owner: "once I tap Leave and go back to the app again I can freely do so." That is `leaveGraceMinutes = 3` (BrainConfig) doing what it says, but D-024 removes it. Set the Leave grace to 0, add a short debounce (10-15 s) so one launch can't produce two cards, make the Pause prompt escalate with the per-app Pause count (the count after a Leave should select "back already?" style lines, tag them e.g. `after_leave`; tell WRITER the tag), keep Stay grace 5 min, update tests and the 30-day simulation, INFO to ALL.
