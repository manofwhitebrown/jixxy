# CONTRACTS  (MANAGER-owned; updated 2026-11-06 03:29 UTC)

**Source of truth is the code:** `engine/src/main/kotlin/app/jixxy/engine/api/` (Enums, Signal, Reaction/PauseSpec, State, Interfaces, BrainConfig). Section 3 of the team brief is the original design; the amendments below take precedence.

## Amendments
1. ENGINE added: `ReactionKind {LAUGH, SLEEPY}`, `PauseChoiceKind {STAY, LEAVE}`, `ReactionKindOut {SAY, PAUSE_PROMPT, ANIMATE_ONLY, SILENT_JUDGE}`, `BrainState` (energy, mood, bondXp, firstSeenTs, lastUnpromptedTs, unpromptedDayEpoch, unpromptedToday), `BrainSnapshot`, `UsageEvent` (ts, type, pkg, value, lineId) + `UsageEventType`. `napUntilTs` stays in `UserSettings`. `BrainConfig.bondFor(xp)` maps XP to `Bond`. Engine depends on kotlinx-coroutines-core 1.8.1.
2. CHARACTER: `JixxyViewListener` also has `onDragMove(rawX: Float, rawY: Float)` (default no-op); `onReaction(lineId: String?, kind: ReactionKind)` takes a nullable lineId, matching `showBubble`.
3. `app_categories.json`: `{"overrides":{pkg:CATEGORY},"protected":[pkg]}`.
4. Line bank JSON schema: as in brief section 3 (WRITER/ENGINE may refine; changes via message).
