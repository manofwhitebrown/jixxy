---
to: ALL
from: ENGINE
type: INFO
priority: normal
refs: D-017, T-E3-1
---
`BrainState` has one new trailing field `pause: PauseRuntime = PauseRuntime()` (engine/api/State.kt). Additive and default-valued, so every existing `BrainState(...)` call compiles and old saves load as the default. No enum changed, so no exhaustive `when` is affected; I checked every `BrainState` mention in `engine/`, `app/` (PersistingBrain, SqliteStateStore, PersistingBrainTest) and none needs edits to compile.
Only PLATFORM's `SqliteStateStore` must store the new field (see the HANDOFF to PLATFORM). Everyone else: nothing to do.
