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
| SNACK | `snacks` | Snacks. Now. | Emitted when he is poked at 0 energy ("on break"), instead of the poke squash. |
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

## Audit, 2026-11-07 (T-C3-6, done by GROWTH under the Owner's approval, D-018; read-only, no code changed)
Checked `Expression.kt` (`ExpressionMap.forState`), `ExpressionTest`, the `CharacterState` enum and every `CharacterState.` use in `engine/src/main`. Not run; CI covers the existing tests.
- **No wrong faces:** all 21 states resolve to the table above; unknown names fall back to `really`. 18 images exist in `character/src/main/res/drawable-nodpi/`, one per table row, so no missing-resource fallback is hit.
- **Rare states stay rare:** the engine emits `IMPRESSED` (Respect) from 2 places and `PANIC` (shocked) from 1; neither is in the common path.
- **Late night:** `SOFT` becomes `blanket` between 00:00 and 05:00 (`BLANKET_HOURS`), as the table says. `FALLING` and `DRAGGED` both show `dropped`, as required.
- **States the engine never emits** (found by searching `engine/src/main`): `SNACK` (the "Snacks. Now." face), `SNORE` (the lying-down `sleep` face), `BLINK` (view-internal, expected), `DRAGGED` (view-internal, expected). So two shipped images, `snacks` and `sleep`, can only appear if the view or another role asks for them. This is a **question for ENGINE/MANAGER, not a bug**: either emit `SNACK` (for example on a hunger-style moment or the charger) and `SNORE` (deep sleep after a long nap), or accept that those faces are spare.
- **Resolved 2026-11-07 (Owner request: use the spare faces).** `snacks` is now reachable: poking him at 0 energy (the existing `on_break` lines, including the owner-canon "Snacks. Now.") sends `SNACK` instead of `POKED` (`DefaultJixxyBrain.onTap`, tests in `MoodStateTimeTest`). `sleep` is now shown on Home while a nap is running (`CatPoses.forStatus(NAPPING)`, the new `CatPose.SLEEP`); a stopped service keeps the sitting `nap` face. The overlay sprite is hidden during a nap and `SNORE` holds until replaced, so the engine still does not emit `SNORE`: a held lying-down cat on someone's screen with no way to end it would be a new bug, not a feature. Revisit if the platform ever shows the sprite during a nap.
- **Still open, needs a compiler or the Owner:** dead face channels (BROW/PUPIL/SMILE) in `Pose.kt` and `StatePoses.kt` are left in on purpose because removing them touches tested code and nobody here has a compile; the closed-eyes blink frame needs the Owner's art (`ASSET_REQUEST.md`).
