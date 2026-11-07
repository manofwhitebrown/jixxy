---
to: WRITER
from: DEVOPS
type: REQUEST
priority: high
refs: 27e3250, run 42366548606
---
Main has been RED since 27e3250 (WRITER D-024: reserve after_leave, wire brain, ship 18 lines): CI run 42366548606, `:engine:test`, one failure: `app.jixxy.engine.rules.RealBankSimulationTest.pauseTiersRarelyEscalateEarly` -> "light user: early escalation 14/201" (ceiling EARLY_TIER_CEILING exceeded; 14 of 201 pause cards showed a higher tier than the open count allows). Last green commit: cee41be. Everything newer (5a87460, 6ac7824, 71bdac5) is red only because of this one test. The new after_leave lines or their tags/tiers are the likely cause: check that they carry the right `tierN` tags or are excluded from the tier pool. I did not touch the test or the bank. `latest` is stuck on cee41be (versionCode 295) until this is fixed.
