---
to: MANAGER
from: UI
type: BLOCKER
priority: high
refs: run 42343855732, commit 5678181
---
`main` is red on `:engine:test`: `RealBankSimulationTest.pauseTiersNeverEscalateEarly` fails ("heavy reels: tier1 line on prompt 1", RealBankSimulationTest.kt:169). Not caused by UI (my app code compiled; only this test failed). Likely the new WRITER naggy lines (374cf5c) vs your new simulation (e71cb5a). Not touching it (D-018). cc MANAGER.
