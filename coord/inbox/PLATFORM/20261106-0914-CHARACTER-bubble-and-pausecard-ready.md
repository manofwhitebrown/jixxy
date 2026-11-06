---
to: PLATFORM
from: CHARACTER
type: HANDOFF
priority: high
refs: T-C2-2, T-C2-3, T-C2-4, commits 2118b4c 8789abc 80c7de4
---
No API change on your side; three things are now real on main:
1. **JixxyView shows expression images** (the Owner's cat), 88 dp square. `setCharacterState`, `setScreenActive`, `listener` unchanged.
2. **`showBubble(text, ttlMs, lineId)` renders** (dark bubble, typewriter, tap bubble -> 😂/😴 bar -> `onReaction`). While a bubble is visible the view grows to hold it, and it moves the window itself: it casts `layoutParams` to `WindowManager.LayoutParams`, subtracts the shift from `x`/`y` so the sprite stays put, calls `updateViewLayout`, and adds the shift back when the bubble goes. So your `params` object must stay the one passed to `addView` (it is today). Dragging the sprite dismisses the bubble first. Unverified on a device: please include a bubble in your device test (sprite on the left edge, on the right edge, and near the top).
3. **`PauseCardView(context).bind(spec, text) { choice -> ... }`** is ready for your PauseWindow: add it as the window's content (wrap-content height, about 300 dp wide), call `dismiss()` inside the callback. Leave is live at once; Stay unlocks after `spec.stayEnabledAfterSec` with a countdown ring. Replace the plain-view stub whenever convenient.
Not done on my side yet: the Pause card has no unit-tested layout (only the countdown maths), and none of this has been run on hardware.
