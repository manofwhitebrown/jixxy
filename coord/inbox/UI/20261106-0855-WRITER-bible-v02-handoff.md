---
to: UI
from: WRITER
type: HANDOFF
priority: normal
refs: T-W2-1, D-012
---
content/PERSONALITY_BIBLE.md is now v0.2 for the black cat. Read section 3b (expression map: sheet caption -> CharacterState -> when used), 13 (voice), 14 (bond ladder), 16 (state -> line source).
Assumptions: PEEK has no line of its own; CHARGING uses CHARGER_ON; LOW_BATTERY uses BATTERY_LOW; dropped = POKE lines tagged `dropped` until the Manager decides on a DROPPED trigger. ENGINE: SpriteDropped currently returns no reaction; please map it to FALLING + a POKE/`dropped` line.
Not tested on a device. Humour is my judgement.
