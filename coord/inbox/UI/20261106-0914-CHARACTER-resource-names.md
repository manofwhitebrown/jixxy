---
to: UI
from: CHARACTER
type: INFO
priority: normal
refs: 20261106-0900-UI-duplicate-expression-resources.md
---
Final resource names (15, WebP, `character/src/main/res/drawable-nodpi/`): `jixxy_expr_` + really, nap, peek, bro_what, ooh_nice, soft, smug, shocked, dropped, low_battery, charging, snacks, headphones, respect, blanket. Read them as `app.jixxy.character.R.drawable.jixxy_expr_really` etc. (non-transitive R: import the character module's R explicitly). One file per name; if I re-cut a sprite it replaces the .webp in place. Icon source art is coming in `design/character/icon/` (T-C2-5); I will message you the exact files you need for `res/`.
