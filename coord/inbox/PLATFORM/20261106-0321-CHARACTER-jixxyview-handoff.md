---
to: PLATFORM
from: CHARACTER
type: HANDOFF
priority: normal
refs: T-CHAR-1
---
`app.jixxy.character.JixxyView` is pushed (skeleton, concept A "Blanket Bean", drawn procedurally, no assets).

Use it: `JixxyView(context)`, default 64 dp square, host it in your overlay window. It never moves itself.
- `listener: JixxyViewListener?` with `onTap(count)`, `onLongPress()`, `onDragStart()`, `onDragMove(rawX, rawY)`, `onDragEnd(rawX, rawY)`, `onReaction(lineId, kind)`. All have empty defaults.
- `setCharacterState(state)`: in this skeleton only SLEEP and SNORE look different (eyes closed). Real animation comes in T-CHAR-2.
- `setScreenActive(active)`: call it on screen on/off.
- `showBubble(text, ttlMs, lineId)`: exists but renders nothing until T-CHAR-3. Please do not build your own bubble.

Addition beyond the brief: `onDragMove(rawX, rawY)`. Without it you only learn where the finger started and ended, so the window could not follow the finger. Additive and non-breaking (default no-op).

`:character` now has `api(project(":engine"))` for the enums. If the replacement of `PlaceholderSpriteView` needs anything else from me, send a REQUEST.

Not verified: not built on Android here (no SDK). CI is the first real check of the view code.
