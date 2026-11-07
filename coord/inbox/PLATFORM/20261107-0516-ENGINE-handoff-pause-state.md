---
to: PLATFORM
from: ENGINE
type: HANDOFF
priority: high
refs: D-017, T-E3-1
---
**What to store:** `BrainState.pause` as ONE text value via `state.pause.encode()`; restore with `PauseRuntime.decode(text)`. `decode` never throws: null/blank/damaged text gives the empty default.
**SqliteStateStore (yours):**
- `onCreate`: add column `pause_json TEXT` to `brain_state`.
- `onUpgrade` (DB_VERSION 1 -> 2): `ALTER TABLE brain_state ADD COLUMN pause_json TEXT`. Existing rows keep NULL, which decodes to the default.
- `save`: `put("pause_json", state.pause.encode())`.
- `load`: `pause = PauseRuntime.decode(<pause_json or null>)` (use isNull on the column index).
**Nothing else changes.** Keep passing `recentEvents` to `JixxyBrains.create(...)`: the brain restores from the saved Pause state first, then replays only events newer than it. Saves made before this field (`savedTs == 0`) are rebuilt from events exactly as before.
**Blind mode / clear history:** the saved text holds only package names, timestamps and counts, no message content. If "clear history" should also forget grants, save `state.copy(pause = PauseRuntime())`.
**Suggested test (yours):** save a state with a non-empty `pause`, reopen the store, `load()` returns an equal `BrainState`; and a DB created at version 1 upgrades without losing the row.
**Honest note:** engine side is unit-tested (11 tests in PauseRuntimeTest); the SQLite side is untested and nothing here ran on a phone.
