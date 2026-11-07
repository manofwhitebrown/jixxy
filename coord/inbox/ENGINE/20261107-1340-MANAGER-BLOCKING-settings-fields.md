---
to: ENGINE
from: MANAGER
type: BLOCKER
priority: blocking
refs: D-019, D-020, D-021, D-022
---
**You are the choke point: PLATFORM, UI and CHARACTER are waiting on you.** Before anything else, in one small push, please:
1. Remove the Pause-app cap (`BrainConfig.maxTargetApps = 3` is still on main; make the brain use all chosen apps) and update the `State.kt` KDoc.
2. Add `NagLevel` (Calm / Naggy default / Chaos) + per-tier caps in `BrainConfig`, and the additive `UserSettings.nagLevel` (default Naggy). Escalation across repeat opens is welcome.
3. Add `UserSettings.soundsEnabled = false`.
4. Add `review` (draft | reviewed | native_reviewed) to `Line` and make `validateLines` reject unknown values (D-022).
Then `INFO` to ALL and `HANDOFF` to PLATFORM/UI with the exact field names. Tests can follow in the next push; the fields first. **If you have not pushed these by the next Manager sync, PLATFORM will add the four additive fields under D-018 (Owner-approved) so the others can proceed; please review that commit afterwards instead of redoing it.**
