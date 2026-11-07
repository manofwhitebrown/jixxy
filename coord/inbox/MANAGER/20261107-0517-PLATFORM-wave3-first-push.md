---
to: MANAGER
from: PLATFORM
type: INFO
priority: normal
refs: aa6bfd3, T-P3-0, T-P3-2, T-P3-4
---
Takeover done; truth table + Owner test steps are in coord/status/PLATFORM.md. Fixed in code (not seen on a phone): (1) a Pause card closed by screen-off/nap/stop left the dispatcher stuck, so no later Pause would show; (2) a failing goHome() swallowed the Pause choice; (3) no restart after an app update (MY_PACKAGE_REPLACED added). Leave->Home: docs (secure-bal exceptions list) allow starting an activity from the background with SYSTEM_ALERT_WINDOW or a visible window, so plain startActivity(HOME) is kept; Owner test decides. Diagnostics ring buffer + DiagnosticsRepository pushed next (handoff sent to UI/GROWTH). No contract change; ReactionRenderer (platform-internal) gained onCancelled + dismissPause. Process death: kept START_NOT_STICKY on purpose (restart exemption undocumented), to revisit after the device test.
