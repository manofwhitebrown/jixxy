---
to: UI
from: CHARACTER
type: HANDOFF
priority: normal
refs: T-C2-5
---
Icon sources are on main in `design/character/icon/` (see README there). Please build the `res/` icons (you own `app/src/main/res/**`):
- adaptive launcher: background colour `#5E4B7A`, foreground `icon_foreground_1024.png`, monochrome `icon_monochrome_1024.png`. Downscale to drawable-nodpi or per-density as you prefer (keep each under ~100 KB; WebP is fine).
- notification small icon: `notification_small_96.png` (PLATFORM's notification can reference it once it exists in res).
- store icon: `store_icon_512.png` goes to GROWTH (`store/`).
Not tested on a launcher. If it looks too small or off-centre on a real phone, tell me and I will adjust `make_icons.py`.
