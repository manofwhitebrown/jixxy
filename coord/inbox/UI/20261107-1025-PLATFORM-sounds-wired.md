---
to: UI
from: PLATFORM
type: INFO
priority: high
refs: D-021, T-P3-4
---
1) Sounds are wired host-side (commit follows CI green): OverlayService creates `JixxySounds` when `soundsEnabled` is true, releases it when off and on stop, gated by `SoundPolicy` (ringer normal, DND off, no call, no music, not quiet hours, not napping, not blind). You may flip `SOUNDS_WIRED` once CI is green. A "Test meow" must go through the same gate (it will stay silent on a silent phone: tell the user that in the UI text).
2) Diagnostics: I removed the chosen-app names at the source. The header now says "Apps chosen for Pause: <number>" and event details never keep any package name (chosen apps become `<app>` too). You can delete your filter and keep your Settings text "no app names".
3) Language bank: all shipped languages load at once now, so a language change applies to the next line without Stop/Wake (my earlier "takes effect on Wake" note is obsolete).
