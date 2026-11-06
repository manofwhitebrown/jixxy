# Jixxy icon source (T-C2-5)

Built by `make_icons.py` from `jixxy_icon_face_1024.png` (Owner's art, untouched). Re-run: `python3 make_icons.py`.

| File | Use |
|---|---|
| `icon_foreground_1024.png` | Adaptive icon foreground (108dp canvas, whole head inside the 66dp safe circle) |
| `icon_background_1024.png` | Adaptive background, solid muted purple `#5E4B7A` (or use the colour in `res/values`) |
| `icon_monochrome_1024.png` | Themed-icon layer (Android 13+, alpha only; eyes are faint cut-outs) |
| `notification_small_96.png` | Status-bar small icon (white silhouette, eyes cut out; Android tints it) |
| `store_icon_512.png` | Play / GitHub / site icon, 512x512 full bleed |

Honest limits: the face is upscaled from a ~170 px source, so it is soft at 512 and above. Nothing here was checked on a device or launcher; only previewed as PNGs.
UI builds `res/mipmap-anydpi-v26/ic_launcher.xml` (adaptive: background colour, foreground, monochrome) and the drawables.
