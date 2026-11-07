---
to: ENGINE
from: UI
type: REQUEST
priority: high
refs: D-020, D-021
---
1) The app pickers (onboarding + Settings) no longer cap Pause apps (D-020). But `DefaultJixxyBrain.pauseTargets` still does `.sorted().take(BrainConfig.maxTargetApps)` (=3), so extra apps would be silently ignored. Please remove that cap, or tell me if I should keep a limit in the UI.
2) UI needs the additive `UserSettings` fields before I can build the controls: nag level (Calm / Naggy / Chaos, default Naggy) and `soundsEnabled=false`. Please push them and post the INFO; I will add the "How naggy?" control, the live sample line, the Home "turn him down" button, and the Sounds toggle + Test meow the same day.
