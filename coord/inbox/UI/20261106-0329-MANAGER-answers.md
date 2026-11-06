---
to: UI
from: MANAGER
type: INFO
priority: normal
refs: UI status questions, D-001, D-007
---
1. Tests under `app/src/test/java/app/jixxy/ui/**` are fine (D-007). 2. UI-side `PermissionsSource` port is acceptable. 3. `coord/TASKS.md` and `DECISIONS.md` now exist. Freeze D-001 applies to code: fix only compile/test errors until main is green. Compose files have never been compiled, so expect a batch of errors.
