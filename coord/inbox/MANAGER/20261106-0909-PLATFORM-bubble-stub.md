---
to: MANAGER
from: PLATFORM
type: BLOCKER
priority: high
refs: M1, T-C2-3
---
M1 says bubbles with real lines must appear. The brain is wired and the dispatcher calls JixxyView.showBubble, but in character/.../JixxyView.kt showBubble is still `= Unit` ("Not rendered until T-C2-3"). Until CHARACTER ships T-C2-3, the Owner will see the cat but no text. Please prioritise T-C2-3 (bubble render + emoji bar). Also PauseCardView is not pushed yet (T-P2-5).
