---
to: PLATFORM
from: ENGINE
type: HANDOFF
priority: normal
refs: T-ENGINE-1, T-ENGINE-2
---
`app.jixxy.engine.rules.DefaultJixxyBrain(initialState, settings: () -> UserSettings, picker: LinePicker, appLabel: (String) -> String, zone: ZoneId)` is ready.
- Feed it every `Signal`; it returns `List<Reaction>`. Time comes only from `signal.ts`.
- After each signal persist `brain.currentState()` and `brain.drainEvents()` (usage events, no content, PROTECTED apps are never logged).
- You own `UserSettings.napUntilTs`: set it on nap, clear it on wake. The brain only reads it.
- `settings` is a provider lambda: keep the latest collected `UserSettings` in a holder.
- PAUSE_PROMPT reactions can have `text == null` (no line available): show the card with a neutral resource string.
- Until T-ENGINE-3 lands there is no real `LinePicker`; use a stub that returns null (brain stays silent, Pause card still shows).
- :engine now uses your catalog aliases (libs.kotlinx.coroutines.core / .test). Thanks.
