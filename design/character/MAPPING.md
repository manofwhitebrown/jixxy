# Jixxy expression mapping (T-C2-0)

Source of truth for which image `JixxyView` shows for each `CharacterState`. Art is the Owner's character
sheet (D-012, cut into sprites per D-013); this file only decides *which* picture plays *when*. File names are Android resource names in
`character/src/main/res/drawable-nodpi/` (`.webp` or `.png`, 512 px max, same canvas and anchor for all).

| CharacterState | Resource (`jixxy_expr_*`) | Sheet caption | Notes |
|---|---|---|---|
| IDLE | `really` | ...really? | Default face. Breathing scale only. |
| BLINK | `really_blink` | (needed frame) | Fallback until supplied: 120 ms vertical squash, no swap. |
| JUDGING | `really` | ...really? | Same face as IDLE, but motion almost stops. That is the joke. |
| HAPPY | `ooh_nice` | Ooh nice! | Small hop. |
| SLEEP | `nap` | Nap time... | Slow Zs overlay, slower breathing. |
| SNORE | `sleep` | (Owner image, lying down) | Deeper sleep: same Zs overlay. `sleep_zzz` is not shipped: its dark Zs vanish on dark screens, the overlay Zs stay readable. |
| WAKE_GRUMPY | `bro_what` | Bro what?! | Short shake, then back to IDLE. |
| POKED | level 1: `really`, level 2-3: `bro_what` | | Squash on every tap; escalating shake at x3. |
| PEEK | `peek` | (peeking over an edge) | Shown before a Pause line. Also top of the Pause card. |
| SNACK | `snacks` | Snacks. Now. | |
| PANIC | `shocked` | WHAT?! | Rare. Quick shake, then back to IDLE. |
| IMPRESSED | `respect` | Respect. | Rare and earned. Slow, clean, no wobble. |
| SOFT | `soft` | ...you good? | Late night or caring moment. Never mocks. |
| SMUG | `smug` | Hehe. | |
| MUSIC | `headphones` | (headphones) | Beat bob plus notes overlay. |
| LOW_BATTERY | `low_battery` | 5%?! bro charge. | D-010 value. |
| CHARGING | `charging` | Finally, food. | D-010 value. |
| DRAGGED | `dropped` | YOU DROPPED ME?! Rude. | Stretch plus bob while held. |
| FALLING | `dropped` | YOU DROPPED ME?! Rude. | Landing squash, then IDLE. |
| YAWN | `yawn` | (Owner image) | Cutout from the opaque-black original. Motion adds only a small stretch. |
| STRETCH | `stretch` | (Owner image) | Cutout from the opaque-black original. Motion adds only a small stretch. |
| late-night SLEEPY/SOFT | `blanket` | We should both sleep... | Brain asks for SOFT late at night; view swaps to `blanket` between 00:00 and 05:00 local time. |

**Rules**
- A missing image never crashes: the view falls back to `really`, and if that is missing too it draws nothing.
- Unknown or future `CharacterState` values fall back to IDLE's image (D-010 values are looked up by name until ENGINE adds them).
- Captions stay on the sheet. The app never draws the captions; speech comes from the line bank.
- Never edit `design/character/reference/`. Derived sprites go in `character/src/main/res/drawable-nodpi/` (one file per name, `.webp`; a second file with the same name and another extension breaks the build).

## Expression inventory and gaps (T-V1-CHARACTER-1)
- **Every `CharacterState` (21) has an image** except BLINK, which still uses the 120 ms squash on `really` (needs `really_blink`, see `ASSET_REQUEST.md`).
- Images from the sheet (15) are soft (source ~170 px wide). `sleep`, `yawn`, `stretch` come from the Owner's larger images via `tools/build_expressions.py` (one shared scale and anchor; they show the lying cat larger than the sheet busts, which is how the pose is drawn).
- Unused originals (kept in `reference/`, not shipped): `jixxy-sleep-zzz.png`, `jixxy-yawn-zzz.png`. Say if the Owner wants either in the app.
