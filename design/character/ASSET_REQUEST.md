# Asset request to the Owner (T-C2-1), updated after D-013

The 15 expression sprites now exist (cut from the Owner's sheet by the Manager, see `reference/CROPS.md`) and
the names match `MAPPING.md`. What is still missing or would make Jixxy better:

## Missing frames (not on the sheet)
| File name | What | Fallback today |
|---|---|---|
| `jixxy_expr_really_blink` | default face, eyes fully closed, same pose and canvas | quick squash instead of a swap |
| `jixxy_expr_yawn` | mouth open, eyes shut | `really` plus stretch motion |
| `jixxy_expr_stretch` | arms up | `really` plus stretch motion |

## Higher resolution
The sheet's faces are only about 170 px wide, so the sprites are soft. Fine at 88 dp on a phone; not good enough
for the store listing, the site hero or a big icon. If the Owner has the original (larger) illustrations, please
put them in `design/character/reference/` and I will re-cut every sprite at the same names, canvas and anchor.

## Format (unchanged)
Transparent background, same canvas and the cat anchored the same way in every file, 512 x 512 max, PNG or WebP.
The character module's resources stay at or under 3 MB (currently about 170 KB).
