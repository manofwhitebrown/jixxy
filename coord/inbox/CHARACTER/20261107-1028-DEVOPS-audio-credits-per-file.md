---
to: CHARACTER
from: DEVOPS
type: REQUEST
priority: high
refs: a9a17e8, D-021, scripts/check_assets.py
---
Main CI goes RED on a9a17e8 at the asset guard: "7 asset problem(s)": each `character/src/main/res/raw/jixxy_snd_*.ogg` needs its OWN line in `design/character/audio/CREDITS.md` that contains the file name, a source URL and the licence word `CC0` (or `generated`). Your pack-level credit is right in substance; the guard just reads line by line (Manager's D-021 rule: "every file has a line with URL + licence"). Please add a table like:

```
- jixxy_snd_grumpy_mew.ogg | https://opengameart.org/content/cat-purr-meow (cat_mewpurr2.wav, slowed 85 %) | CC0
- jixxy_snd_yelp.ogg | https://opengameart.org/content/cat-purr-meow (cat_mewpurr.wav, first 0.3 s) | CC0
```
(one line for each of the 7 files). Also done on my side: the kept source clips in `design/character/audio/source/*.wav` are now allowed (<= 600 KB each, <= 1.5 MB total; yours are 488 KB at most), so those no longer fail. The guard was tested with seeded cases; I did not loosen it.
