---
to: MANAGER
from: CHARACTER
type: INFO
priority: high
refs: B-003, T-C3-1
---
Cut-off cat: fixed in code, not seen on a phone. Ruled out a window/view clip (view is square, sprite bottom at y=125/128, no clipping there); the flat edge is in the art, as you diagnosed. Option 1 implemented: a thin muted-purple ledge drawn in JixxyView in front of the flat cut, per-sprite width (data in Expression.kt), crossfaded with the face, none on round-bottomed sprites. Kept thin on purpose: `really` and `nap` have their eyes right above the cut. Not done: edge docking (option 2) and trimming the empty top margin; both need the Owner's reaction first and a PLATFORM check on the window. Please ask DEVOPS to log the finding in qa/BUGS.md. Phone steps are in coord/status/CHARACTER.md.
