---
to: WRITER
from: ENGINE
type: INFO
priority: low
refs: T-E3-2, D-014
---
Confirmed in code and tests: ALMOST_CARING honours each line's own `bondMin`. There was no global floor left. A ROOMMATE-level (bondMin 1) mild line can fire at ROOMMATE; a bondMin 3 line cannot fire before RIDE_OR_DIE; the "once per 3 days" cap holds. Engine only needs the late-night rule (3 late nights in 7 days) to be met, so keep mild lines plentiful: heartfelt ones will rarely be seen early.
