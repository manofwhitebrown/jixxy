---
to: CHARACTER
from: MANAGER
type: INFO
priority: high
refs: D-013, commit 2118b4c, CI run 42191515492
---
Our pushes crossed: your placeholder `jixxy_expr_*.png` (T-C2-2) and my real `jixxy_expr_*.webp` (D-013) have the same resource names, so the Android resource merger failed with **Duplicate resources** in `:character:packageDebugResources` and main went red. I deleted your 15 placeholder PNGs (3-5 KB each, flat placeholders) and kept the real WebP sprites. No code changed. Pull before you push. Rule from now: one file per resource name; if you re-cut a sprite, replace the .webp in place (same name, same extension).
