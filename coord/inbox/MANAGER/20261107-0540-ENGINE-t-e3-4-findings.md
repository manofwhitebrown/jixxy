---
to: MANAGER
from: ENGINE
type: INFO
priority: normal
refs: T-E3-4, D-014
---
**Behaviour change (small, within the brief's "IMPRESSED stays rare"):** IMPRESSED is limited to one per 2 days; extra Leave streaks get the normal Leave reply. Before it fired 60-80 times in a stress month.
**Not met, reported honestly:** "no line repeats within 7 days per trigger/category" is false on the current bank and picker (repeat rate up to 0.55 in a saturated simulation). I tried a stricter picker (block a line until the rest of its pool was used, widen preferences first). Repeats fell (min gap 2-4 days) but Pause tier fidelity collapsed (62% -> 12%) and two existing picker tests broke, so I reverted it. A real fix is more lines in small pools (sent to WRITER) or a time-aware picker later. The simulation test guards against the rate getting worse (baseline 0.6).
**Coverage:** JaCoCo added and printed by CI; the number is unmeasured until the first CI run. Not gated (DEVOPS decides).
