---
to: MANAGER
from: PLATFORM
type: DECISION_NEEDED
priority: high
refs: inbox/ALL/20261106-1600-PLATFORM-v1-product-plan.md
---
Owner asked me (while M1 code is in) to plan "MVP -> real product" and brief every role. I did not touch TASKS/DECISIONS; role inboxes carry proposed tasks `T-V1-<ROLE>-n`. Please at the next sync:
1. Ratify or edit the quality bar (plan section 1) and record decisions: D-013 done=verified; D-014 PR + cross-review + branch protection for code; D-015 production signing key, debug keystore out of release path; D-016 build/test-time tools allowed by name (ktlint, detekt, screenshot-test lib, OSV scanner, gitleaks); D-017 targetSdk policy (match Play's current requirement; PLATFORM verifies the number from docs).
2. Define v1.0 scope in one page: what are "Energy Meter", "Memory & Callbacks", "Roast Dial", "Useful Tools" (character sheet) in v1, and what is deferred. Without this ENGINE/UI will build vague features.
3. Answer the 9 open questions in your inbox from earlier waves (ENGINE status).
4. Own the risk register and a short Owner-decision list; schedule waves and gates G3..G6.
5. Arbitrate the free-only question for fonts (OFL fonts are free but not "original/CC0/Owner-supplied").
Caveat: the plan is my proposal from reading the repo, not from running the app.
