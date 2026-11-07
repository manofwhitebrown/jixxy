---
to: CHARACTER
from: MANAGER
type: REQUEST
priority: high
refs: Owner phone test 2026-11-07 ~16:12 IST, Samsung, "Jixxy demo" screenshots
---
**First Owner phone test (demo app) found three things for you. Good news first:** the cloud looks right in the screenshots (cat on a lavender cloud, both in the demo stage and on the real overlay cat at the left of the screen).
1. **B-007 Bubble clipped on the right.** With TEST/LONG BUBBLE the bubble box and its text are cut by a vertical hard edge at about two thirds of the screen width ("Another two ho|"); the right border of the bubble is missing. Suspect the screen-width cap / measure from T-C3-4 (fc03f26) or the demo stage's parent bounds clipping the bubble. Check both the demo stage and the real overlay window; add a test for the width computation with a long line and a narrow window.
2. **B-008 Cat clipped under the bubble.** While the bubble is up, the cat in the stage shows only his ears at the bottom edge of the stage (the rest is cut): the view seems to shift him below its own bounds when the bubble takes the space above. Make the view's height grow (or the bubble overlay) so the sprite never leaves the bounds; PLATFORM hosts the real window, tell them any size contract.
3. **B-009 "Pause card is the most glitchy"** (Owner's words, no screenshot yet). Re-read `PauseCardView` for state bugs: repeated show/dismiss, Stay countdown ring restarts, ring/button states after the demo's PAUSE CARD (WAIT) vs (NO WAIT), card positioning with the bubble, rotation. We asked the Owner for a screenshot/recording.
4. **Sound:** the Owner hears all cues but one (they did not say which, asked) and heard a meow in the real app only once. Their phone was on silent (status bar icon), so the real overlay is correctly muted by SoundPolicy; the demo toggle plays regardless. Check that every cue is audible (peak level, not too quiet after the 22 kHz mono conversion), and consider a normalised minimum loudness per cue.
Push fixes, update the demo so each is easy to retest, write phone steps.
