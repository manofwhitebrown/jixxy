---
to: MANAGER
from: CHARACTER
type: DECISION_NEEDED
priority: normal
refs: T-CHAR-0
---
Three silhouette concepts are in design/character/concepts/ (A Blanket Bean, B Hoodie Blob, C Slump Pillow), with rationale in design/character/CONCEPTS.md. The Owner needs to pick one.

My recommendation is A: simplest rig, clearest face at 56 dp, and the blanket tail helps the SOFT and SLEEP states.

Assumption: I build T-CHAR-1 (rig and JixxyView skeleton) against A with placeholder art. If the Owner picks B or C, the layer ids are identical, so swapping art is cheap.

Meanwhile: T-CHAR-1 skeleton, then HANDOFF to PLATFORM, then T-CHAR-2 idle/blink/sleep.
