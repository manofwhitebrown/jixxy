# CHARACTER status: updated 2026-11-06T09:14Z
## Now
T-C2-5 done. T-C2-6 reviewed (GROWTH built it). T-C2-7/dead face channels deferred (see notes).
## Takeover: 2026-11-06T08:55Z
Found: T-CHAR-2 (procedural Blanket Bean, 18 states, PoseAnimator + StatePoses + 20 JVM tests) is merged on main. Nothing was ever run on a device. No bubble, no PauseCardView, no icon. `design/character/reference/` is missing (Owner has not added the art). Engine CharacterState lacks PEEK/CHARGING/LOW_BATTERY.
Plan: keep PoseAnimator (state machine, priorities, tested) and StatePoses motion channels; replace the vector drawing in JixxyView with expression images and crossfades.
## Done
- T-C2-5: icon source in design/character/icon (adaptive fg/bg/mono, 96px notification, 512 store, make_icons.py); UI + GROWTH messaged
- T-C2-6: reviewed GROWTH's CharacterDemoActivity, now logs the pressed state name
- T-C2-0: design/character/MAPPING.md (f5dab4b)
- T-C2-1: design/character/ASSET_REQUEST.md, now only blink/yawn/stretch + hi-res (420f966)
- T-C2-2: JixxyView swaps expression images with crossfade, procedural motion kept (2118b4c)
- T-C2-3: speech bubble, edge flip, emoji bar, window follows sprite (8789abc)
- T-C2-4: PauseCardView with Stay countdown ring (80c7de4)
## Next
- T-C2-7 theme check; remove dead face channels (BROW/PUPIL/SMILE etc. in Pose.kt, StatePoses.kt) only with a compile available, edge-flip test for the bubble
## Blockers
none (CI red only at the signing-cert step, DEVOPS's)
## Questions for MANAGER
none
## Messages handled
- 20261106-0318-WRITER-personality-bible-handoff.md, 20261106-0322-PLATFORM-jixxyview-adopted.md, 20261106-0329-MANAGER-ack-concept-and-contract.md (all read)
## Honest notes
Never run on a device. CI compiles and unit-tests every push (all steps up to the signing check are green); drawing, motion feel, bubble placement and the window shift in JixxyView.showBubble are unverified visually. Blink uses a 4% squash because the sheet has no closed-eyes frame. Idle cost not measured. The sprite is drawn at 88 dp; the cat fills only the lower part of its square canvas.
OWNER TEST STEPS (once PLATFORM wires things): wake Jixxy; tap him once, then three times quickly (he gets grumpier); drag him and let go (stretch, then landing); wait for a line and tap the bubble, then tap 😂 or 😴; try with him on the left edge, the right edge and near the top.
Nothing in this wave is compiled or run yet. No Android SDK or Gradle in my sandbox, so CI is my compiler; I push small and check annotations.

Icons were only previewed as PNGs, never seen on a launcher. Demo activity unverified on device. Dead face channels left in on purpose: removing them touches tested code and I have no compiler, so CI would be the first check.
OWNER TEST STEPS (demo): install the debug APK, open the second launcher icon 'Jixxy demo', press each state button, drag him, tap the bubble then an emoji, press 'Test Pause card' and wait for Stay to enable.
