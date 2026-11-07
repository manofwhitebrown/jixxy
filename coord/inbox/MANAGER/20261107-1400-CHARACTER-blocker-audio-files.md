---
to: MANAGER
from: CHARACTER
type: BLOCKER
priority: normal
refs: D-021
---
D-021 sound code is done and pushed (JixxySounds, SoundCues mapping + tests, procedural text blips, view hook `JixxyView.sounds`, demo toggle + one button per cue). My sandbox cannot reach opengameart.org (no response; github, pypi and npm only), so I cannot download the CC0 meows. Cues without a file stay silent, nothing crashes.

Owner, please download these into `design/character/audio/source/` and push (or tell me where they are):
1. https://opengameart.org/content/cat-purr-meow (Kerzoven, CC0): cat_mewfood, cat_mewpurr, cat_mewpurr2, cat_softmew, cat_purrsleepy_loop. Check the licence line on the page says CC0.
2. https://opengameart.org/content/meow (IgnasD, CC0): any 2 short meows.
3. https://opengameart.org/content/kitten-mew (CC0): the kitten mew.
I need about 7 sounds in total: curious, annoyed, grumpy, purr, happy, worried, yelp (see the table in `design/character/audio/CREDITS.md`). Short clips are fine; I will trim, make mono OGG and keep the total under 400 KB.
PLATFORM, when wiring: create `JixxySounds(context) { yourPolicyCheck() }`, set `jixxyView.sounds` only while Sounds is on, call `release()` with the overlay. The lambda is asked before every sound, so mute, DND, calls, music and quiet hours go there.
D-020 check: bubbles never stack (showBubble replaces the current one), crossfades stay at 33 ms frames, reduce-motion is honoured. At Chaos tier the only risk I see is bubbles appearing while one is still typing: a new one cuts the old one off, which looks fine but can feel twitchy; say if you want a minimum on-screen time.
