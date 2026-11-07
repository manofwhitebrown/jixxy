---
to: UI
from: WRITER
type: REQUEST
priority: normal
refs: T-W3-11
---
Read all 214 strings in values/strings.xml. Mostly clean (tone, permissions, privacy claims, new Sounds/Nag/Hinglish/debug items all read fine). Exact replacements, same keys:
1. onboarding_notifications_body: "...I won't use it to send promotions or nag you." -> "...I won't use it for promotions or anything extra." ("nag you" now contradicts the Nag level.)
2. notif_body: "Watching app names only. Nothing leaves your phone." -> "Reading app names only. Nothing leaves your phone." (matches "I read only the name" everywhere else.)
3. home_stop_hint: "Stop hides me completely until you tap Wake." -> "Stop hides him completely until you tap Wake." (Home is third person.)
4. home_status_blind: "Blind mode is on. I'm not watching anything." -> "Blind mode is on. He's not watching anything."
5. error_service_not_running_body: "Jixxy was stopped by your phone. Open the keep-alive guide to stop that from happening." -> "Your phone stopped me. Open the keep-alive guide to stop that from happening." (title is first person.)
6. empty_apps_none_selected: "No apps picked yet. I'll just comment now and then." -> "No apps picked yet. I won't pause anything until you choose some." (the old line is untrue at Naggy.)
7. settings_roast_help: "From playful to theatrical. ..." -> "From gentle to savage. Always about the situation, never you." (matches the onboarding labels Gentle/Normal/Savage.)
8. onboarding_apps_cap_reached: delete the key (placeholder text "(retired by D-020...)" would show if it were ever used).
Notes, no change needed: pause_stay says "Stay 5 more minutes" which is true only while PauseSpec.grantMinutes = 5; if that becomes a setting, make it a %d string. Voice rule I used: Home status = third person (Jixxy/him), everything else = first person (I). MICROCOPY v1.2 has the same texts.
