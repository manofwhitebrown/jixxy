# Audio credits (D-021)

Rules: CC0 recordings or procedural sound only. No CC-BY, no custom "free" licences, no paid packs. Every bundled
file is listed here with source URL, author, licence and the date it was checked. Budget: all audio <= 400 KB.

## Bundled
All seven cues are cut from clips of one pack, built by `build_audio.py` (cut points, re-pitch and fades are in its table).
- Pack: "Cat purr / meow" by Kerzoven, https://opengameart.org/content/cat-purr-meow
- Licence: CC0 (public domain), as listed by the Manager for D-021. **CHARACTER could not open the page** (the sandbox cannot reach opengameart.org); the Owner downloaded the files on 2026-11-07. **Owner/Manager: please open the page once and confirm it says CC0**, then delete this sentence.
- Used source clips (kept in `source/`): cat_softmew, cat_mewpurr, cat_mewpurr2, cat_mewfood, cat_purrsleepy_loop. Not used and not kept: cat_purractive_loop.
- Derived: grumpy_mew (cat_mewpurr2, slowed to 85 %), yelp (cat_mewpurr, first 0.3 s, pitched up 30 %), worried_mew (cat_softmew second mew with a 5.5 Hz wobble). Pitch-shifted or wobbled CC0 audio is still CC0.
- Total bundled audio: about 69 KB (budget 400 KB).

## Procedural (no files, nothing to credit)
- Text blips: `BlipSynth` generates short sine blips at runtime (28 ms, four pitches).

## Cue to file map (files in `character/src/main/res/raw/`)
| Cue | File | Made from |
|---|---|---|
| CURIOUS_MEW | jixxy_snd_curious_mew.ogg | cat_softmew, the rising mew |
| ANNOYED_MEW | jixxy_snd_annoyed_mew.ogg | cat_mewpurr, first 0.75 s |
| GRUMPY_MEW | jixxy_snd_grumpy_mew.ogg | cat_mewpurr2, slowed and lowered |
| PURR | jixxy_snd_purr.ogg | cat_purrsleepy_loop, 1.8 s with long fades |
| HAPPY_MEW | jixxy_snd_happy_mew.ogg | cat_mewfood (the "food" mew) |
| WORRIED_MEW | jixxy_snd_worried_mew.ogg | cat_softmew second mew + wobble |
| YELP | jixxy_snd_yelp.ogg | cat_mewpurr start, 0.23 s, higher |
