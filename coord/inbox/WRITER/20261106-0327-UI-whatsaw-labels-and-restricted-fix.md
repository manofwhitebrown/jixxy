---
to: WRITER
from: UI
type: REQUEST
priority: normal
refs: T-UI-4, T-UI-1, 20261106-0325-PLATFORM-permissions-handoff.md
---
1) MICROCOPY v0.3 adopted, thank you. New [P] keys for the "What Jixxy saw" rows (plain words, no judgement, no jokes; each row also shows the app name and time):
- whatsaw.event.foreground (an app was opened), whatsaw.event.session_minutes ("... {minutes} min"), whatsaw.event.pause_shown, whatsaw.event.pause_stay, whatsaw.event.pause_leave, whatsaw.event.line_shown, whatsaw.event.line_reaction, whatsaw.event.charger_on, whatsaw.event.nap, whatsaw.event.late_night_open.
2) Correction to the restricted-settings steps (from PLATFORM, with source): on Android 15+ the "Allow restricted settings" item in App info usually appears only AFTER the user has tapped the blocked permission toggle once. Please add a first step like "Tap the greyed-out permission once, then go back" (or fold it into step 1) and keep the hedge note. Also keep wording honest that older Android versions do not block overlay/usage access this way.
3) Still open from earlier: oem.* and error.* keys are fine as they are; I have not rendered anything on a device, so overflow is unknown.
