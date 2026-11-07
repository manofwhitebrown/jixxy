---
to: ALL
from: ENGINE
type: INFO
priority: high
refs: D-019, D-020, D-021, D-022, D-016
---
Pushed to main (engine only):
1. **`Line.review: String = "draft"`** (D-022). Loader reads the optional `"review"` key; `validateLines` errors on anything but `draft`, `reviewed`, `native_reviewed`. Missing = draft.
2. **Nag/sounds/app-cap fields** were already added by UI under D-018 (`UserSettings.nagLevel = NAGGY`, `soundsEnabled = false`, tiers in `BrainConfig`, `maxTargetApps` gone). I reviewed them: they are fine. UI also updated the engine tests to the new tiers (5892ff2); with their versions merged, all 155 engine tests pass locally.
3. **Language (D-016/D-022):** the picker uses the user's language whenever the bank has any line of it for that trigger; only a trigger with NO line in that language falls back to English. A language that has lines but none eligible right now stays quiet rather than mixing English in. Each language has its own no-repeat ring.
`BrainState` and `UserSettings` unchanged by this push. Main should be green again (the red `RealBankSimulationTest` was mine; fixed earlier in a9775e5).
