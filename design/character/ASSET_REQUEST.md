# Asset request to the Owner (T-C2-1)

Status: `design/character/reference/` is empty in the repo as of this commit. Until the files land, the view
uses clearly-labelled placeholder sprites (named exactly like the real ones, so swapping is "drop in file, same name").

## What I need
One image per file below. **Transparent background. Same canvas size and the same pose anchor (cat sits
in the same place) in every file. 512 x 512 px max. PNG or WebP.** Keep the character exactly as on the sheet.

| File name | Caption on sheet | Priority |
|---|---|---|
| `jixxy_expr_really` | ...really? (default face) | must |
| `jixxy_expr_nap` | Nap time... | must |
| `jixxy_expr_peek` | peeking over an edge | must (Pause card) |
| `jixxy_expr_bro_what` | Bro what?! | must |
| `jixxy_expr_ooh_nice` | Ooh nice! | must |
| `jixxy_expr_soft` | ...you good? | must |
| `jixxy_expr_smug` | Hehe. | should |
| `jixxy_expr_shocked` | WHAT?! | should |
| `jixxy_expr_dropped` | YOU DROPPED ME?! Rude. | should |
| `jixxy_expr_low_battery` | 5%?! bro charge. | should |
| `jixxy_expr_charging` | Finally, food. | should |
| `jixxy_expr_snacks` | Snacks. Now. | should |
| `jixxy_expr_headphones` | headphones | should |
| `jixxy_expr_respect` | Respect. | nice |
| `jixxy_expr_blanket` | purple hooded blanket | nice |

## Missing frames (not on the sheet)
| File name | What | Fallback if you cannot supply |
|---|---|---|
| `jixxy_expr_really_blink` | default face, eyes fully closed | quick squash instead of a swap |
| `jixxy_expr_yawn` | mouth open, eyes shut | `really` plus stretch motion |
| `jixxy_expr_stretch` | arms up | `really` plus stretch motion |

## Also for the app icon (T-C2-5)
Head only, front-facing, on transparent background, as large as you have it (ideally 1024 px), with and without
the gold crown doodle. If only the full sheet exists, I will crop from it and record the crop in `design/character/CROPS.md`.

## How to deliver
Put originals in `design/character/reference/` (untouched). If you already cleaned/resized versions, drop them
in `character/src/main/res/drawable-nodpi/` with the exact names above; I will check size and anchor.
Budget: the whole character module's resources stay at or under 3 MB.
