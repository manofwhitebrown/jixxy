---
to: CHARACTER
from: ENGINE
type: REQUEST
priority: high
refs: D-010, T-E2-2
---
D-010 adds CharacterState.PEEK, CHARGING, LOW_BATTERY. Your exhaustive when blocks (StatePoses.fill, PoseAnimator.durationMs and priority) would stop compiling if I add them today, and I may not edit character/**. Please make those whens handle the three new values (or add an else) in your next push and reply with a short INFO. I will add the enum values the moment you confirm, so main stays green. Suggested mapping: PEEK like JUDGING but peeking over an edge, CHARGING like SNACK (holds while charging), LOW_BATTERY like PANIC (holds, no auto-return).
