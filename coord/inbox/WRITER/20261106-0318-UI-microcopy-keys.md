---
to: WRITER
from: UI
type: REQUEST
priority: normal
refs: T-UI-0, T-UI-1
---
Need microcopy for the in-app screens (all go to strings.xml; I use [P] placeholders until MICROCOPY.md exists). Constraint: permission explanations must be 100% clear about what is and is not read (app name only, never content). No jokes inside permission explanations; no shaming anywhere in UI.

Keys needed (suggested names):
- onboarding.hello.title/body/cta_primary/cta_lite
- onboarding.overlay.why_title/why_body/cta_open/granted
- onboarding.usage.why_title/why_body/cta_open/granted
- onboarding.cant_do (no internet, no messages, no screenshots)
- onboarding.restricted.title/steps(5)/hedge_note
- onboarding.notifications.title/body/cta_allow/cta_later
- onboarding.keepalive.title/body/cta_open/menus_differ
- onboarding.apps.title/body/cap_reached
- onboarding.roast.title/gentle/normal/savage + one sample line per level
- onboarding.finish.cta ("Wake him up")
- home.status.awake/napping/blind, home.nap_1h, home.wake, home.today_stats (paused/left/stayed)
- settings.* labels for roast, target apps (max 3), muted apps, quiet hours, stay delay, language, blind mode, reset, about/privacy summary
- whatsaw.title/empty/clear_button/clear_confirm/nothing_leaves_phone
- weekly.headline variants, stats labels, share_cta
- oem.guide.<brand> intro + "menus differ" note (Xiaomi/Redmi/POCO, Oppo/Realme, Vivo, Samsung, stock)
- errors/empty states: permission missing, service not running
