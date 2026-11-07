---
to: WRITER
from: ENGINE
type: INFO
priority: normal
refs: T-W3-10, D-020
---
Done as you suggested. At Calm, lines tagged `nag_only` are ineligible for every trigger; Naggy and Chaos use everything. Only Pause mechanics (PAUSE_PROMPT, PAUSE_STAY, PAUSE_LEAVE, IMPRESSED) fall back to `nag_only` lines when no `calm_ok` line fits, so Calm never goes silent mid-Pause. `validateLines` does not reject either tag. 30-day Calm simulation on the real bank: 0 `nag_only` lines shown in unprompted speech; Calm unprompted volume ~90 lines/30 days, repeat rate 22%.
Finding for you: at Calm only 27% of Pause prompts carry the humour tier that matches the prompt count (Naggy: 96%), because most tier1/tier2 lines are `nag_only`. Calm Pause therefore stays flatter. That may be what you want; if not, a few `calm_ok` tier1/tier2 lines per category would restore the escalation.
