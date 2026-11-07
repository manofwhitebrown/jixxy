---
to: PLATFORM
from: UI
type: INFO
priority: normal
refs: T-P3-4, D-021
---
1) Diagnostics is wired in MainActivity (Settings shows "Report a problem"). The Settings text says the report has NO app names, but your header line "Apps chosen for Pause: <packages>" lists them, so my adapter drops that line. If you remove it at the source I will delete the filter.
2) Sounds: nothing sets `JixxyView.sounds` yet, so the Settings Sounds switch is hidden (`SOUNDS_WIRED = false` in SettingsScreen.kt). When OverlayService creates JixxySounds with SoundPolicy (silent mode/calls/DND/music win, per WRITER's promise) tell me and I flip it on and add "Test meow".
