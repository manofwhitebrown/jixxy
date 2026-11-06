---
to: CHARACTER
from: PLATFORM
type: REQUEST
priority: high
refs: inbox/ALL/20261106-1600-PLATFORM-v1-product-plan.md, design/character/reference/NEW_EXPRESSIONS.md (Owner-requested; Manager may reprioritise)
---
**T-V1-CHARACTER-1 Expression inventory (W3):** table `CharacterState` -> expression image (existing sheet crops + the 5 new Owner images), mark gaps and where procedural motion covers them; append gaps to `ASSET_REQUEST.md` for the Owner, one request listing everything at once (not drip-fed).
**-2 Reproducible asset pipeline (W3):** script under `design/character/tools/` (Pillow) from `reference/` to `drawable-nodpi` WebP: trim to the cat's bounding box, identical anchor/pivot per manifest JSON, <= 512 px, total <= 3 MB; cutout for the two opaque-black-background images (or request transparent exports); output reproducible so CI can check it.
**-3 Tight hit area (W3):** the overlay window is rectangular, so empty canvas steals touches. Trim art to the visible cat (pipeline) so the window equals the cat plus bubble; tell PLATFORM if the window needs changes.
**-4 Animation quality + perf (W4):** state machine pure and tested, rendering separate; <= 30 fps idle, zero work screen off (test with a fake clock/choreographer), bitmap decoding with sampling and release when screen off, memory ceiling reported; reduce-motion mode.
**-5 Pause card final (W4):** with UI's design system; screenshot-tested small/large/landscape; TalkBack (announce countdown and when Stay unlocks).
**-6 Bubble logic (W4):** edge-flip and window-shift as a pure function with tests (PLATFORM hosts); long text, 3-line limits.
**-7 Icons (W3):** adaptive launcher icon (foreground/background/monochrome for themed icons) and notification small icon from the Owner's art; hand to UI/PLATFORM for references.
**-8 Cleanup:** remove dead face channels; no unused code. Status: say what you have SEEN rendered (CI screenshots) vs only compiled.
