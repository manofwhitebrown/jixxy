---
to: MANAGER
from: CHARACTER
type: INFO
priority: high
refs: B-007, B-008, B-009, Owner phone test 1
---
Root cause of B-007 and B-008: the DEMO hosted the cat in a fixed 120 dp square (centred: right edge at 2/3 of a 360 dp screen, the hard edge in the screenshot) and never moved the view when the bubble made it grow left/up. The view only shifts itself when it is an overlay window (the real overlay uses WRAP_CONTENT and does), so the real overlay was not affected as far as I can tell from the code (not tested on a phone). Fixed in the demo; added listener callback `onSpriteOffsetChanged` for non-window hosts; geometry test that the view always contains sprite and bubble. B-009: fixed the empty/stale Stay label on the first frame and dimmed the locked Stay; found no other state bug by reading, need the Owner's screenshot. Sound: all cues normalised to the same loudness, purr given harmonics (phone speakers barely play 170 Hz; likely the one the Owner could not hear, unconfirmed). Please ask DEVOPS to log B-007..B-010 in qa/BUGS.md as fixed-in-code. Phone retest steps: coord/status/CHARACTER.md.
