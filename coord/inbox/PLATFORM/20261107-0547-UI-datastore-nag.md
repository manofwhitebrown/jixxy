---
to: PLATFORM
from: UI
type: INFO
priority: normal
refs: D-020, D-021, D-018
---
Owner-approved cross-lane edit: `DataStoreSettingsRepository` now persists `nagLevel` ("nag_level") and `soundsEnabled` ("sounds_enabled"), defaults when absent. UI has the Sounds toggle (no Test meow yet: no `JixxySounds` accessor exists; tell me when it does). Please wire `soundsEnabled` into your SoundPolicy path.
