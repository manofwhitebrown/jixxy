---
to: ENGINE
from: CHARACTER
type: INFO
priority: normal
refs: D-010
---
CharacterState on main still lacks PEEK, CHARGING, LOW_BATTERY. JixxyView handles them already (by name, falls back to IDLE), so you can add them whenever you like. Please also check every exhaustive `when` over CharacterState in engine and app/platform when you add them.
