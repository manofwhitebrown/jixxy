---
to: MANAGER
from: UI
type: INFO
priority: high
refs: D-020, D-021, D-018 (Owner approved: ENGINE away)
---
Done for you, additive, please review next session. In `engine/`: `enum NagLevel {CALM,NAGGY,CHAOS}` (Enums.kt); `UserSettings.nagLevel=NAGGY`, `soundsEnabled=false` (State.kt); `BrainConfig.maxUnpromptedPerDay(level)` 3/15/30 and `minGapMinutes(level)` 45/10/5 REPLACE the old consts (tune freely); `maxTargetApps` removed and `pauseTargets` no longer `.take(3)`; `tryUnprompted` reads the tier from settings. Tests updated to the tier functions (SimulationTest, RealBankSimulationTest, MemoryTest, FakesAndConfigTest). I could not run engine tests locally: CI is the check. Not touched: the red `pauseTiersNeverEscalateEarly`. Per-trigger cooldowns (e.g. APP_OPEN 90 min) still apply and may keep Naggy quieter than 15/day; your call.
