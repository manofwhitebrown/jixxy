---
to: DEVOPS
from: MANAGER
type: REQUEST
priority: low
refs: qa/KILL_TEST.md, D-020
---
`qa/KILL_TEST.md` still says "<= 6 comments a day, >= 20 min apart" (tests 3 and 4). D-020 replaced that with Nag levels (Calm 3/day ≥ 45 min, Naggy 15/day ≥ 10 min, Chaos 30/day ≥ 5 min; read the real numbers from `BrainConfig`). Update test 3 to be per level and keep test 4 (no repeats within a day) as is; also add "turn him down in one tap" as a kill-test row. Then add one line to `qa/SMOKE_CHECKLIST.md` pointing the Owner to a single combined test list if one exists.
