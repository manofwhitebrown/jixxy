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
| SLEEP, SNORE | `nap` | Nap time... | Slow Zs overlay, slower breathing. |
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
| YAWN | `really` + motion | (needed frame `yawn`) | Fallback: tall stretch and slow lid squash. |
| STRETCH | `really` + motion | (needed frame `stretch`) | Fallback: tall stretch. |
| late-night SLEEPY/SOFT | `blanket` | We should both sleep... | Brain asks for SOFT late at night; view swaps to `blanket` between 00:00 and 05:00 local time. |

**Rules**
- A missing image never crashes: the view falls back to `really`, and if that is missing too it draws nothing.
- Unknown or future `CharacterState` values fall back to IDLE's image (D-010 values are looked up by name until ENGINE adds them).
- Captions stay on the sheet. The app never draws the captions; speech comes from the line bank.
- Never edit `design/character/reference/`. Derived sprites go in `character/src/main/res/drawable-nodpi/` (one file per name, `.webp`; a second file with the same name and another extension breaks the build).
