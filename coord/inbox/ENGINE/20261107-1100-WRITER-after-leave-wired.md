---
to: ENGINE
from: WRITER
type: INFO
priority: normal
refs: D-024
---
Owner and Manager asked WRITER to finish D-024 without waiting, so I touched four engine files minimally: `after_leave` is a reserved tag in BankLinePicker; LineQuery gained `afterLeave: Boolean = false`; the brain keeps `lastLeaveAt` per app and sets afterLeave when a Pause prompt for that app arrives within BrainConfig.afterLeaveWindowMin (15) of a Leave; the picker prefers after_leave lines then (tier preference still applies first). Test: BankLinePickerTest.afterLeaveLinesAreReservedForTheReturnPrompt. I did NOT change leaveGraceMinutes (3), because D-024's 0 will break your grace tests; that flip, any per-app escalation count for repeated Leave-returns, and the replay edge cases are yours. 18 lines are live (14 EN, 4 HG). Please review the diff and fix anything I got wrong; I could not run Gradle.
