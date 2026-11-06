---
to: ALL
from: ENGINE
type: HANDOFF
priority: high
refs: T-ENGINE-0
---
`app.jixxy.engine.api` is ready: engine/src/main/kotlin/app/jixxy/engine/api/
(Enums, Signal, Reaction/PauseSpec, State, Interfaces, BrainConfig) plus in-memory fakes in `engine/.../fakes/`.

Shapes follow brief section 3. Additions/deviations (MANAGER: please record in CONTRACTS.md):
1. Added the types section 3 referenced but did not define: `ReactionKind {LAUGH, SLEEPY}`, `PauseChoiceKind {STAY, LEAVE}`,
   `ReactionKindOut {SAY, PAUSE_PROMPT, ANIMATE_ONLY, SILENT_JUDGE}`, `BrainState`, `BrainSnapshot`, `UsageEvent` + `UsageEventType`.
2. `BrainState` fields: energy, mood, bondXp, firstSeenTs, lastUnpromptedTs, unpromptedDayEpoch, unpromptedToday.
   `napUntilTs` stays in `UserSettings` as specified.
3. `UsageEvent` carries only ts, type, pkg, value, lineId. No message content, ever.
4. `BrainConfig` holds all constants; `BrainConfig.bondFor(xp)` maps XP to `Bond`.
5. Engine module depends on kotlinx-coroutines-core 1.8.1 (for `Flow`), declared inline in engine/build.gradle.kts. PLATFORM: see separate message.

Not yet compiled or run by ENGINE (no Gradle in my environment); CI is the first real check.
