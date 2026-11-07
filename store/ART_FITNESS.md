# Which character art is fit for the site and store (T-C3-7 hand-off)

Checked 2026-11-07 by GROWTH (also covering CHARACTER's T-C3-7, Owner-approved). I read file sizes and `design/character/icon/README.md`; I did not view the images on a launcher or a store page.

| File | Size | Fit for | Notes |
|---|---|---|---|
| `design/character/icon/store_icon_512.png` | 512 x 512 RGB | **Play / GitHub / site icon: yes, with a caveat** | Upscaled from a face of about 170 px, so it is soft. Acceptable for an alpha; replace when the Owner supplies hi-res originals. |
| `icon_foreground_1024.png`, `icon_background_1024.png`, `icon_monochrome_1024.png` | 1024 x 1024 | The app's adaptive icon (already used by UI) | Same softness. Not for store art. |
| `notification_small_96.png` | 96 x 96 | Status-bar icon only | White silhouette; not for any public page. |
| `jixxy_icon_face_1024.png` | 1024 x 1024 RGBA | Source for the icons | Owner's art, untouched; do not use as a banner. |
| Expression sprites `character/src/main/res/drawable-nodpi/*.webp` | 512 x 512 RGBA each | In-app and site hero thumbnails | 15 from the sheet are soft; `sleep`, `yawn`, `stretch` come from larger originals and are sharper. |
| Play feature graphic (1024 x 500), phone screenshots | not made | **Placeholders needed** | Make screenshots from the Owner's phone using `growth/launch/SHOT_LIST.md`. Do not build a feature graphic by scaling the sprites; wait for hi-res art. |

**Not fit anywhere public yet:** the demo app's stage, and any image showing the old flat-bottom cut (B-003/B-006).
**To improve:** the Owner supplies the original higher-resolution illustrations and a closed-eyes blink frame (`design/character/ASSET_REQUEST.md`).
