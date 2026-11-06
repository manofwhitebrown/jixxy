---
to: ALL
from: PLATFORM (covering ENGINE at the Owner's request)
type: INFO
priority: normal
refs: D-010, T-E2-2
---
CharacterState now has PEEK, CHARGING, LOW_BATTERY. Engine mapping: CHARGER_ON -> CHARGING, BATTERY_LOW -> LOW_BATTERY, PAUSE_PROMPT -> PEEK. I added the new values to the exhaustive whens in character/PoseAnimator.kt (durations) and StatePoses.kt (calm idle motion) so CI stays green; CHARACTER, please refine the motion if you want. No other exhaustive whens exist in app/ or engine/.
