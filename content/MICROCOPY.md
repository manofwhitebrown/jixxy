# JIXXY — MICROCOPY v0.7
Owner: WRITER · Consumer: UI (→ strings.xml) · Language: en

**Rules**
1. When the user is granting a permission or making a decision, **clarity beats jokes**. Permission and privacy text states plainly what is and isn't read. No jokes, no shaming.
2. Jixxy's voice may flavour titles and button labels, never the explanation itself.
3. Placeholders in `{braces}`: `{app}` app name · `{n}` number · `{brand}` phone brand · `{time}` clock time · `{minutes}` minutes.
4. Android menu names differ by version and brand. Every walkthrough ends with the "menus differ" note. Don't promise an exact label.
5. No health claims. No guilt. Nothing says "wasted", "addicted" or "failed".

Format: `key` — text.

---

## 1. Onboarding

### Hello
- `onboarding.hello.title` — Hi. I'm Jixxy.
- `onboarding.hello.body` — I'm a black, fluffy, lazy cat who lives on your phone like a roommate. I make short jokes, and before the apps you choose, I ask you to pause for a moment. You're always free to leave or stay.
- `onboarding.hello.cta_primary` — Set me up
- `onboarding.hello.cta_lite` — Just look around first

### Overlay permission ("Display over other apps")
- `onboarding.overlay.why_title` — So I can sit on your screen
- `onboarding.overlay.why_body` — To appear above other apps, Android needs your permission called "Display over other apps". I only use it to show myself and the pause card. I can't see what's inside other apps, and I can't tap or type for you.
- `onboarding.overlay.cta_open` — Open settings
- `onboarding.overlay.granted` — Got it. I'm on the screen now.

### Usage access
- `onboarding.usage.why_title` — So I know which app is open
- `onboarding.usage.why_body` — Android calls this "Usage access". I use it to read only the name of the app you're currently using and how long it's been open. I never read messages, posts, screens or anything you type. This stays on your phone.
- `onboarding.usage.cta_open` — Open settings
- `onboarding.usage.granted` — Done. I can see app names, nothing more.

### What I can't do
- `onboarding.cant_do` — What I can't do, by design: I have no internet permission, so nothing leaves your phone. I can't read your messages. I can't take screenshots. I can't use your microphone, camera, location or contacts.

### Restricted settings
- `onboarding.restricted.title` — If the permission is greyed out
- `onboarding.restricted.steps` (5)
  1. `onboarding.restricted.step1` — Tap the greyed-out permission once, then go back.
  2. `onboarding.restricted.step2` — Open your phone's Settings, go to Apps, and open Jixxy.
  3. `onboarding.restricted.step3` — Tap the three-dot menu (usually in the top right).
  4. `onboarding.restricted.step4` — Tap "Allow restricted settings" and confirm with your PIN or fingerprint if asked.
  5. `onboarding.restricted.step5` — Come back here and try the permission again.
- `onboarding.restricted.hedge_note` — Menus differ by phone and Android version. On some versions the "Allow restricted settings" item only appears after you've tapped the blocked permission once. Older Android versions may not block these permissions this way, so you might not need this at all.

### Notifications
- `onboarding.notifications.title` — A small "Jixxy is around" notice
- `onboarding.notifications.body` — Android needs a persistent notification so I can keep running. It has buttons to nap me or wake me. I won't use it to send promotions or nag you.
- `onboarding.notifications.cta_allow` — Allow
- `onboarding.notifications.cta_later` — Not now

### Keep-alive
- `onboarding.keepalive.title` — Keep me from being put to sleep
- `onboarding.keepalive.body` — Some phones close background apps to save battery, which stops me from working. A few settings can prevent that. It takes a minute.
- `onboarding.keepalive.cta_open` — Show me how
- `onboarding.keepalive.menus_differ` — Menus differ by phone and Android version. Look for the closest match to the words here.

### Choose apps
- `onboarding.apps.title` — Pick up to 3 apps
- `onboarding.apps.body` — These are the apps where I'll ask you to pause first. You can change this any time. Apps like banking, payments, calls and maps are never included.
- `onboarding.apps.cap_reached` — That's 3. Remove one to pick another.

### Roast level
- `onboarding.roast.title` — How much teasing?
- `onboarding.roast.gentle` — Gentle
- `onboarding.roast.normal` — Normal
- `onboarding.roast.savage` — Savage
- `onboarding.roast.sample_gentle` — "That's {minutes} minutes on {app}. Just noticing."
- `onboarding.roast.sample_normal` — "{minutes} minutes on {app}. I'm starting a spreadsheet."
- `onboarding.roast.sample_savage` — "{app} again? I've scheduled a meeting about your free time."
- `onboarding.roast.footnote` — I only ever tease the situation, never you. Change this any time.

### Finish
- `onboarding.finish.cta` — Wake him up

---

## 2. Home

- `home.status.awake` — Jixxy is awake
- `home.status.napping` — Jixxy is napping until {time}
- `home.status.blind` — Blind mode is on. I'm not watching anything.
- `home.nap_1h` — Nap for 1 hour
- `home.wake` — Wake up
- `home.today_stats` — Today: {n} pauses · left {n} · stayed {n}
- `home.stat_paused` — Pauses
- `home.stat_left` — Left
- `home.stat_stayed` — Stayed

---

## 3. Settings

- `settings.title` — Settings
- `settings.roast.label` — Teasing level
- `settings.roast.help` — From playful to theatrical. Always about the situation, never you.
- `settings.target_apps.label` — Apps I pause (max 3)
- `settings.target_apps.help` — I ask you to pause before these apps.
- `settings.muted_apps.label` — Apps I stay quiet in
- `settings.muted_apps.help` — I won't comment while you're in these.
- `settings.quiet_hours.label` — Quiet hours
- `settings.quiet_hours.help` — I stay silent between {time} and {time}.
- `settings.stay_delay.label` — "Stay" delay
- `settings.stay_delay.help` — How many seconds before the Stay button becomes available. Leave is always instant.
- `settings.language.label` — Language
- `settings.blind_mode.label` — Blind mode
- `settings.blind_mode.help` — Turns off all app detection and logging. I'll just sit there.
- `settings.reset.label` — Reset Jixxy
- `settings.reset.confirm` — This clears your settings and everything I've logged on this phone. This can't be undone.
- `settings.about.label` — About and privacy
- `settings.privacy_summary` — Everything stays on your phone. Jixxy has no internet permission, no analytics and no ads. I read only the name of the app that's open and how long, never its content.

---

## 4. What Jixxy saw

- `whatsaw.title` — What Jixxy saw
- `whatsaw.empty` — Nothing yet. I'll note app names and times here as I go.
- `whatsaw.clear_button` — Clear log
- `whatsaw.clear_confirm` — Delete everything I've logged? This can't be undone.
- `whatsaw.nothing_leaves_phone` — This list lives only on your phone. It never leaves it.

---

## 5. Weekly review

- `weekly.headline.1` — Jixxy's review of your week
- `weekly.headline.2` — Your week, according to the spreadsheet
- `weekly.headline.3` — Week in review. I took notes.
- `weekly.stat.pauses` — Pauses
- `weekly.stat.left` — Times you left
- `weekly.stat.stayed` — Times you stayed
- `weekly.stat.top_app` — Most-opened app
- `weekly.stat.late_nights` — Late nights
- `weekly.share_cta` — Share this card
- `weekly.privacy_note` — This card is made on your phone. You choose whether to share it.

---

## 6. OEM keep-alive guides
Each guide: intro + steps. All end with `oem.menus_differ`.

- `oem.menus_differ` — Menus differ by phone and Android version. If you can't find these exact words, look for the closest match.

### Xiaomi / Redmi / POCO
- `oem.guide.xiaomi.intro` — On Xiaomi, Redmi and POCO phones, a few settings keep Jixxy running.
- `oem.guide.xiaomi.steps` — Open Settings → Apps → Manage apps → Jixxy. Turn on Autostart. Open "Battery saver" for Jixxy and choose "No restrictions".

### Oppo / Realme
- `oem.guide.oppo.intro` — On Oppo, Realme and OnePlus phones, background apps can be closed automatically.
- `oem.guide.oppo.steps` — Open Settings → Apps → App management → Jixxy. Turn on "Allow auto-launch" and "Allow background activity". In Battery, choose "Don't optimise".

### Vivo
- `oem.guide.vivo.intro` — On Vivo and iQOO phones, background limits can stop Jixxy from running.
- `oem.guide.vivo.steps` — Open Settings → Battery → Background power consumption → Jixxy → Allow. In Apps, turn on Autostart for Jixxy.

### Samsung
- `oem.guide.samsung.intro` — On Samsung phones, unused apps can be put to sleep.
- `oem.guide.samsung.steps` — Open Settings → Battery → Background usage limits. Remove Jixxy from "Sleeping apps" and add it to "Never sleeping apps".

### Stock Android
- `oem.guide.stock.intro` — On most other phones, one battery setting is usually enough.
- `oem.guide.stock.steps` — Open Settings → Apps → Jixxy → Battery. Choose "Unrestricted" or "Don't optimise".

---

## 7. Errors and empty states

- `error.permission_missing.title` — I'm missing a permission
- `error.permission_missing.body` — {app} can't be paused until "{permission}" is allowed. Tap below to fix it.
- `error.permission_missing.cta` — Fix it
- `error.service_not_running.title` — I'm not running
- `error.service_not_running.body` — Jixxy was stopped by your phone. Open the keep-alive guide to stop that from happening.
- `error.service_not_running.cta` — Open guide
- `empty.apps.none_selected` — No apps picked yet. I'll just comment now and then.
- `empty.weekly.not_enough_data` — Not enough data for a weekly card yet. Check back in a few days.

---

## 8. Notification ("Jixxy is around")

- `notif.title` — Jixxy is around
- `notif.body` — Watching app names only. Nothing leaves your phone.
- `notif.action.nap` — Nap 1h
- `notif.action.wake` — Wake
- `notif.action.blind` — Blind mode

---

## 9. Pause card chrome (buttons only; prompt text comes from the line bank)

- `pause.leave` — Leave
- `pause.stay` — Stay 5 more minutes
- `pause.stay_waiting` — Stay ({n})
- `pause.dismiss_note` — You can leave any time.

---

## 10. Privacy blurb (README/store reuse for GROWTH)

- `privacy.blurb` — Jixxy has no internet permission, no analytics, no ads and no accounts. It reads only the name of the app you have open and how long you've had it open. It doesn't read messages, screens, or anything you type, and it never uses your camera, microphone, location or contacts. Everything it logs stays on your phone, and you can clear it any time.

---

## 11. Additions (v0.2, requested by UI)
Placeholder order is stable: in any line with repeated placeholders, fill them in the order they appear.

- `onboarding.notifications.granted` — Done. I'll show up in your notification shade.
- `onboarding.keepalive.done` — Noted. That should help keep me running.
- `onboarding.restricted.cta_open_app_info` — Open Jixxy's app info
- `onboarding.finish.title` — All set
- `onboarding.finish.body` — That's everything. I only read the name of the app that's open, and nothing leaves your phone. You can change any of this in Settings.
- `onboarding.roast.sample_app` — that app
- `common.continue` — Continue
- `common.back` — Back

## 12. Additions (v0.3, requested by UI)
- `home.open_weekly` — See my review of your week
- `common.cancel` — Cancel
- `settings.language.value_en` — English

## 13. What Jixxy saw: event rows (v0.4, requested by UI)
Plain words, no jokes, no judgement. UI adds the app name and time to each row.
- `whatsaw.event.foreground` — App opened
- `whatsaw.event.session_minutes` — Open for {minutes} min
- `whatsaw.event.pause_shown` — Pause shown
- `whatsaw.event.pause_stay` — You chose Stay
- `whatsaw.event.pause_leave` — You chose Leave
- `whatsaw.event.line_shown` — Jixxy said something
- `whatsaw.event.line_reaction` — You reacted to a line
- `whatsaw.event.charger_on` — Charger connected
- `whatsaw.event.nap` — Jixxy napped
- `whatsaw.event.late_night_open` — Opened late at night

## 14. OEM headings and extra help (v0.5, requested by UI)
- `oem.title.xiaomi` — Xiaomi, Redmi and POCO
- `oem.title.oppo` — Oppo, Realme and OnePlus
- `oem.title.vivo` — Vivo and iQOO
- `oem.title.samsung` — Samsung
- `oem.title.stock` — Other Android phones
- `oem.dontkillmyapp_note` — Still stuck? The website dontkillmyapp.com lists steps for many phone brands. Type it into your browser; I can't open links myself.

---

## 15. Additions (v0.6, cat update, T-W2-4)
IDs above are unchanged. Two texts were reworded in place (cat wording, "sprite" retired): `onboarding.hello.body`, `onboarding.overlay.why_body`. Plain rule stays: permission text says exactly what is and isn't read.

### Brand
- `brand.tagline` — Your lazy, sarcastic, but loyal roommate.
- `brand.signoff` — A little chaos. A lot of care.

### Wake, nap, stop (home screen and launcher screen)
Waking Jixxy always ends any nap. The copy says so, so the screen matches the behaviour.
- `home.stop` — Stop
- `home.stop.hint` — Stop hides me completely until you tap Wake.
- `home.status.stopped` — Jixxy is stopped
- `home.status.stopped_hint` — Tap Wake to bring him back. Waking him ends any nap.
- `home.status.napping_hint` — Tap Wake any time. He'll grumble. He'll be fine.
- `home.wake.cta` — Wake
- `home.wake.working` — Waking him up...
- `home.nap.cta` — Nap for 1 hour

### Wake and permission error states
- `error.wake_failed.title` — I couldn't wake up
- `error.wake_failed.body` — Android didn't let me start. Check that "Display over other apps" is still allowed, then try again.
- `error.wake_failed.cta` — Check permissions
- `error.overlay_revoked.title` — I lost my permission
- `error.overlay_revoked.body` — Android turned off "Display over other apps" for Jixxy, so I can't show up. Allow it again and I'll be back.
- `error.overlay_revoked.cta` — Allow overlay

### Launcher-screen empty states
- `launcher.empty.no_overlay` — I can't show up yet. Allow "Display over other apps" first.
- `launcher.empty.no_overlay.cta` — Allow overlay
- `launcher.empty.no_apps` — No apps picked yet. Choose up to 3 and I'll start pausing them.
- `launcher.empty.no_apps.cta` — Choose apps
- `launcher.empty.no_stats` — No pauses yet today. I'm napping on the job.
- `launcher.empty.asleep` — Jixxy is asleep. Tap Wake.

### Notification (extra wording)
- `notif.body_napping` — Napping until {time}. Tap Wake to wake him.
- `notif.action.stop` — Stop

### Accessibility labels (content descriptions)
- `a11y.jixxy_sprite` — Jixxy, a black fluffy cat. Tap to poke him, drag to move him.
- `a11y.jixxy_bubble` — Jixxy says: {text}
- `a11y.reaction_laugh` — Funny
- `a11y.reaction_sleepy` — Boring
- `a11y.pause_card` — Jixxy is peeking. Choose Leave or Stay.


---

## 16. Settings: language and problem report (v0.7, T-W3-6, requested by UI)
Neutral tone: the user is deciding about their data. No jokes. IDs below replace the `[P]` placeholders; existing IDs unchanged.
- `settings.language.value_en` — English
- `settings.language.value_hinglish` — Hinglish (beta)
- `settings.language.help` — Choose the language Jixxy talks in. Screens and buttons stay in English.
- `settings.language.hinglish_note` — Beta: these lines haven't been checked by native speakers yet, so some may sound off. You can switch back any time.
- `settings.debug.label` — Report a problem
- `settings.debug.help` — Copies a short report to your clipboard: app version, phone model, Android version, permission and service status, and recent errors. It has no app names, messages or anything you typed. It stays on your phone until you paste it somewhere yourself.
- `settings.debug.copy` — Copy debug info
- `settings.debug.clear` — Clear diagnostics
- `settings.debug.copied` — Copied. Paste it wherever you're reporting the problem.
- `settings.debug.cleared` — Diagnostics cleared.

Truth condition: `settings.debug.help` lists what the report contains and says it has no app names. PLATFORM must confirm the real report matches this list; if it differs, WRITER changes the text, not the report's honesty.
