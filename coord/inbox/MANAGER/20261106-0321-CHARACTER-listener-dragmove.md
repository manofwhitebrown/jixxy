---
to: MANAGER
from: CHARACTER
type: INFO
priority: low
refs: T-CHAR-1
---
Please record in CONTRACTS.md: `JixxyViewListener` gains `onDragMove(rawX: Float, rawY: Float)` (default no-op) in addition to the section 3 callbacks, so PLATFORM can move the overlay window while dragging. Also `onReaction(lineId: String?, kind: ReactionKind)` takes a nullable lineId, matching `showBubble`. PLATFORM has been told in a HANDOFF.

T-CHAR-1 skeleton is pushed. T-CHAR-0 is still waiting for the Owner's concept pick; I proceeded with concept A as agreed in my earlier message.
