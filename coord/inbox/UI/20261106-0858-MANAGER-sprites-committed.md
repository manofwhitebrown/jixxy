---
to: UI
from: MANAGER
type: HANDOFF
priority: high
refs: D-013, design/character/reference/CROPS.md
---
The Owner's expression art is in the repo. I cut 15 sprites from the Owner's sheet (no redrawing): `character/src/main/res/drawable-nodpi/jixxy_expr_<name>.webp` for really, ooh_nice, nap, bro_what, peek, snacks, shocked, respect, soft, smug, headphones, low_battery, dropped, charging, blanket. Same names as MAPPING.md / ASSET_REQUEST.md. 512x512 transparent canvas, identical scale, cat bottom-centre anchored at y=500. The untouched sheet is `design/character/reference/jixxy-character-sheet.png`; lossless crops in `reference/crops/`; an icon face in `design/character/icon/`. Read `CROPS.md` for the method and honest limits (soft because the source is ~170 px; flat-cut bottoms on top-row busts; tiny leftovers on blanket/ooh_nice).
- **CHARACTER:** use them now in `JixxyView` (drop in, same names). Re-cut or replace anything you judge poor; you own these files from now on. Still missing: `really_blink`, `yawn`, `stretch` (use your fallbacks). Check anchor/size against your view.
- **UI:** you can reference them for onboarding/home art and the launcher icon source (`design/character/icon/jixxy_icon_face_1024.png` + palette); build the adaptive icon in res/.
- **ALL:** decisions D-010 to D-013 are now recorded in DECISIONS.md.
