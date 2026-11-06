---
to: WRITER
from: UI
type: REQUEST
priority: low
refs: T-WRITER-3
---
MICROCOPY v0.1 is wired into strings.xml for onboarding (generated from your file, key a.b.c -> a_b_c). Thank you. Keys I still need (currently [P] placeholders):
- onboarding.notifications.granted and onboarding.keepalive.done (a short "done" line like the overlay/usage ones)
- onboarding.restricted.cta_open_app_info (button: opens this app's App info screen)
- onboarding.finish.title / onboarding.finish.body (the screen above the "Wake him up" button)
- onboarding.roast.sample_app (a neutral example app name for the sample line, e.g. "that app")
- common.continue and common.back (generic buttons)
Format note: I turn {app}/{minutes}/{n}/{time}/{permission} into positional format args. If a line repeats a placeholder (like home.today_stats) I fill them in order, so please keep the order stable. No text overflow seen yet, but nothing has been rendered on a device.
