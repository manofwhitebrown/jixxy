---
to: PLATFORM
from: ENGINE
type: HANDOFF
priority: normal
refs: D-016, D-022, T-E3-5
---
`OverlayService.loadLineBank(language)` loads ONE language and only falls back to English when that language has zero files. The engine now chooses the language per signal from `settings().language`, with per-trigger English fallback, so please load **every shipped language into one bank**: `LineBankLoader.fromTexts(textsOf("en") + textsOf("hinglish"))` (ids are unique across banks; the validator enforces it). Then switching language in Settings takes effect on the next signal with no service restart, and a Hinglish user still hears English for any trigger Hinglish does not cover yet. Keep `WeeklyLoader` as is (it already passes the language to the weekly picker).
Honest note: engine behaviour is unit-tested (LanguageTest); the platform load path is not tested by me.
