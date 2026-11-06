# Crops of the Owner's character sheet (made by MANAGER, 2026-11-06 08:58 UTC)

**Source:** `jixxy-character-sheet.png` (Owner-supplied, 1254x1254, untouched). Nothing was redrawn.

**Method:** each expression was cut out of the sheet with a script: crop box, flood-fill removal of the light panel background connected to the crop border, 1 px edge erosion + 0.7 px feather (removes the pink fringe), small decorations dropped (sparkles, hearts, exclamation marks), then upscaled 2x (Lanczos) and placed on a transparent **512x512 canvas** with the **same scale** for every sprite and the cat's **bottom-centre anchored at y=500**. Lossless PNGs are in `crops/`; the 88-quality WebP copies the app uses are in `character/src/main/res/drawable-nodpi/` (15 files, about 170 KB total).

**Honest limits**
- The sheet's expressions are only about 170 px wide, so after 2x upscaling the sprites are **soft**. Fine at 72-96 dp on a phone; not good enough for a big hero image or a store graphic. Ask the Owner for higher-resolution originals for those.
- The top-row cats (`really`, `ooh_nice`, `nap`, `bro_what`, `peek`) are busts on the sheet, so their bottom edge is a flat cut. That works for a sprite sitting on a window edge and for `peek` over the Pause card.
- `blanket` still has its small "zz" baked in; `ooh_nice` has a few tiny sparkle specks. CHARACTER may clean these.
- **Not on the sheet, so missing:** `really_blink` (closed eyes), `yawn`, `stretch`. Use the fallbacks in MAPPING.md.
- `design/character/icon/jixxy_icon_face_1024.png` is the default face centred on 1024x1024 transparent; it is upscaled from the same small source.

**Files (15):** really, ooh_nice, nap, bro_what, peek, snacks, shocked, respect, soft, smug, headphones, low_battery, dropped, charging, blanket (all `jixxy_expr_<name>`).
