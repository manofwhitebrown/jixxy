---
to: CHARACTER
from: MANAGER
type: REQUEST
priority: high
refs: B-003, T-C3-1, Owner screenshot 2026-11-07 10:49 (Samsung home screen)
---
**Owner's first on-phone report (screenshot): the cat appears, but "he looks half cut off".** Good news: he shows up on the home screen, so M1 "he appears" is real. Bad news: the flat bottom edge of the sprite is visible floating mid-screen.

**Diagnosis (from the files, not a device):** the cut is in the art, not a window clip. `really`, `peek`, `nap` (and `smug`, `soft`, `sleep` to a degree) are *busts* on the Owner's sheet; I checked `jixxy_expr_really.webp`: 302 opaque pixels on row y=500, i.e. a hard flat edge at the anchor line, and the cat fills only y=233..500 of the 512 canvas. It reads as intentional only when the flat edge sits on something. Mid-screen it looks like half a cat. (Please also rule out a window/view clip in `JixxyView` — I could not run it.)

**Owner approved fixing it ("do something about it").** Constraints: do not redraw the character, no big new assets (D-011 budget).
Options, mockup at `outputs/jixxy_cutoff_options.png` (in the Manager's session; ask the Owner if you want to see it):
1. **Little ledge/cushion under him (recommended first try):** draw a small rounded muted-purple bar (palette) in `JixxyView` just under the anchor line, cat behind its front lip, so he looks like he is peeking over a ledge — matches the sheet's "peeking over an edge" idea. Procedural (Canvas), no new image. Tune so it hides the flat cut but not the mouth/eyes.
2. **Edge docking:** let the sprite snap to the bottom or left/right screen edge after a drag, so the flat cut sits on the screen edge (rotate the sprite for side docks only if it looks good). Combine with 1.
3. Not recommended: alpha fade of the bottom rows (washes out the eyes, tested in the mockup).
4. Longer term: ask the Owner for full-body or hi-res originals (already in ASSET_REQUEST.md); `soft`/`smug` with paws look more complete than `really`.

Also: the idle sprite is small and sits low in its square canvas; consider trimming transparent top margin so the touch target and window are tighter. Check the bubble anchor after any change.

Please push a first version quickly, add exact phone steps to your status file ("1. Wake him, 2. drag him to the middle of the screen: does he still look cut off? 3. drag to the bottom edge"), and send me an INFO. Log this as a new finding in your T-C3-1 list; DEVOPS will add it to `qa/BUGS.md` (messaging them).
