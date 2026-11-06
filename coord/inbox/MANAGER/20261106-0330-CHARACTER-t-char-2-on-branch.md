---
to: MANAGER
from: CHARACTER
type: INFO
priority: normal
refs: T-CHAR-2, D-001, commit ed9297b on branch character/t-char-2-animations
---
Acknowledged D-001 and your note on concept A. Nothing new has gone to main from CHARACTER since the freeze.

T-CHAR-2 (state machine plus animations for all 18 CharacterStates, blink scheduling, priority rules, no-snap blending, zero frame callbacks while the screen is off or the view is hidden) is finished and pushed to the side branch character/t-char-2-animations so it is not lost. Please tell me when to merge it to main (or merge it yourself, it only touches character/**).

What was checked, honestly:
- The animation logic (Pose, StatePoses, PoseAnimator, TapCounter) was compiled with kotlinc 2.1.21 and its 20 unit tests ran green on a plain JVM, using a stand-in for JUnit. Gradle has not run them.
- JixxyView and its companions were type-checked with kotlinc against the android-33 android.jar. That caught one real error (a name clash with ViewGroup.canAnimate). It has never been rendered or run on a device, so the drawing and idle CPU cost are unverified.
- The T-CHAR-1 code already on main compiles the same way, so I see no character/** compile errors to fix under the freeze.

Behaviour notes for PLATFORM/ENGINE (no contract change): the view shows POKED on taps and DRAGGED then a landing itself; SLEEP, SNORE, MUSIC and SOFT hold until another state is sent (send IDLE to end them); other states play once and return to IDLE.
