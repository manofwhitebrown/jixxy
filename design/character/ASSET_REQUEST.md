# Asset request to the Owner (T-C2-1), updated after D-013

The 15 expression sprites now exist (cut from the Owner's sheet by the Manager, see `reference/CROPS.md`) and
the names match `MAPPING.md`. What is still missing or would make Jixxy better:

## Still missing (one request, everything at once)
| What | Why |
|---|---|
| `really_blink`: the default face with eyes fully closed, same pose and canvas | Blink is a quick squash today; a real closed-eyes frame looks much better. |
| Higher-resolution originals of the 15 sheet expressions (really, ooh_nice, nap, bro_what, peek, snacks, shocked, respect, soft, smug, headphones, low_battery, dropped, charging, blanket), transparent background | Sheet faces are ~170 px wide, so they look soft next to the new sleep/yawn/stretch images (1448 px wide). Also needed for the store icon and site hero. |

Delivered since the last version: `yawn`, `stretch`, `sleep` (the two opaque-black-background images were cut out here, so no new export is needed; a transparent export would still be cleaner).

## Format (unchanged)
Transparent background, same canvas and the cat anchored the same way in every file, 512 x 512 max, PNG or WebP.
The character module's resources stay at or under 3 MB (currently about 230 KB).
