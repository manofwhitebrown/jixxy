# CHARACTER status: updated 2026-11-07T05:25Z (Wave 3, fresh AI)
## Takeover: 2026-11-07T05:20Z
Found: main green (CI, Emulator, Latest at 0550d84). Module = JixxyView, SpeechBubble, PauseCardView, PoseAnimator/StatePoses, 15 expression WebPs, 5 JVM test files. Demo activity already lists every CharacterState via values() (21). 5 new Owner images (sleep, sleep+zzz, yawn+zzz, yawn/stretch opaque black) sit in reference/ unprocessed. Nothing verified on a phone.
Done: T-C3-0 demo gaps, new sleep/yawn/stretch sprites. Next: tight hit area (T-V1-CHARACTER-3), motion budget (T-C3-3), bubble/Pause polish.
## Wave-3 findings list (Owner reports, with SHAs)
1. 2026-11-07 "he looks half cut off" (Owner screenshot, Samsung home screen; B-003). Cause: flat bottom cut in the sheet busts, not a window clip (view is 88 dp square, bottom anchor at y=125/128, nothing clips it). Fixed in code: small purple ledge in front of the cut (JixxyView.drawLedge, per-sprite span in Expression.kt, fades with the crossfade). Commit: see git log "[CHARACTER] T-C3-1". NOT verified on a phone.
   PHONE STEPS: 1. Wake him. 2. Drag him to the middle of the screen: is the flat cut gone, does he look like he peeks over a small purple ledge? 3. Drag to the bottom edge and the sides. 4. Open "Jixxy demo", press SMUG, SOFT, NAP/SLEEP, STRETCH, SHOCKED: round-bottomed ones (shocked, charging, headphones, yawn) should have no ledge. Reply: "looks fine" / "ledge too big/small/ugly" / "still cut off".
## Now
T-C2-5 done. T-C2-6 reviewed (GROWTH built it). T-C2-7/dead face channels deferred (see notes).
## Takeover: 2026-11-06T08:55Z
Found: T-CHAR-2 (procedural Blanket Bean, 18 states, PoseAnimator + StatePoses + 20 JVM tests) is merged on main. Nothing was ever run on a device. No bubble, no PauseCardView, no icon. `design/character/reference/` is missing (Owner has not added the art). Engine CharacterState lacks PEEK/CHARGING/LOW_BATTERY.
Plan: keep PoseAnimator (state machine, priorities, tested) and StatePoses motion channels; replace the vector drawing in JixxyView with expression images and crossfades.
## Done
- T-C3-4/5 (part): TalkBack: bubble text is a polite live region only while a bubble is up (announced once, not when it returns to the base text); Pause card announces "Stay is available now" once when the countdown ends (not when it starts unlocked). Fixed in code, TalkBack never run.
- T-C3-3 (part): JixxyView honours the system "remove animations" / battery saver (ValueAnimator.areAnimatorsEnabled, API 26): crossfades only, no bob/shake/squash/Zs/notes/sparkles. Hidden/screen-off/detached already stop the frame loop (shouldAnimate + onVisibilityAggregated), checked by reading, not measured.
- T-V1-CHARACTER-1/-2: inventory in MAPPING.md; design/character/tools/build_expressions.py makes sleep, yawn, stretch WebP (cutout of the black-background images), manifest JSON; wired into ExpressionMap/JixxyView + tests
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

## Motion budget (counted from code, not measured on a device)
Idle (calm state, no blink, no bubble, no fade): one frame every CALM_FRAME_MS; during a blink, fade, bubble or active state: 33 ms (~30 fps, the cap). Screen off / window hidden / detached: zero frames, zero timers (the ticker is removed). Reduce-motion on: same frame rate for crossfades, no transform or overlay work. Allocation in onDraw not audited yet.
QUESTION FOR THE OWNER: does he feel too fidgety when idle? (too much movement / about right / too still)
