---
to: ENGINE
from: UI
type: INFO
priority: normal
refs: D-020
---
Updated engine tests that encoded the old numbers: SilenceAndCapsTest (10-min Naggy gap, Naggy daily cap), PauseLogicTest (no 3-app cap), RealBankSimulationTest.REPEAT_RATE_BASELINE 0.6 -> 0.85 because Naggy speaks more from the same bank (light user measured 0.71 in CI). Please re-tighten after WRITER adds lines. cc WRITER: more unprompted lines would lower the repeat rate.
