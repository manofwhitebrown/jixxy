# Jixxy Privacy Notice

> **DRAFT for Owner review.** This is a plain-language description of what the app is built to do, not legal advice. The permissions table was re-checked against `app/src/main/AndroidManifest.xml` on 2026-11-06 (Wave 2) and must be re-checked before every release. Jixxy is at alpha: some features described here (the weekly card, the "What Jixxy saw" screen) may not be reachable in the current build yet.

*Last updated: 2026-11-06 (draft). Retention and erase behaviour were checked by reading the code, not by running the app. Nothing here has been verified on a device.*

## The short version

Jixxy is a black cat who lives on your screen. Everything he does happens on your phone. The app is built **without the internet permission**, so it has no way to send data off your device. There are no accounts, no ads and no analytics or crash-reporting tools.

## What Jixxy processes on your phone

- **Which app is in the foreground**, and for how long (the app's package name and timestamps). Used to react and to show a pause for the apps you chose.
- **Time of day**, to stay quiet in your quiet hours and to be softer late at night.
- **Screen on/off state**, to stop animations when the screen is off.
- **Battery level and charger state**, so he can react to plugging in or a low battery.
- **Whether music is playing** (a yes/no), so he can react to it. Not what is playing.
- **Whether you are in a call** (a yes/no), so he stays silent during calls.
- **Your taps and choices inside Jixxy**: poking the sprite, 😂/😴 reactions to lines, Leave/Stay on pause cards, naps and settings.

## What Jixxy stores

- Your **settings**: chosen apps (max 3), muted apps, roast level, quiet hours, and similar.
- Jixxy's own **state** (mood, energy, how well you two know each other).
- A simple **event log** (for example, "opened an app at 23:40", "chose Leave"). Events older than **30 days** are deleted automatically. The in-app "What Jixxy saw" screen shows the **last 7 days** of it, and the log powers the weekly review card.

All of this is stored on your phone only. You can **view the last 7 days** and **erase the whole log** (all stored events, not just the 7 days shown) inside the app. "Blind mode" stops detection and logging entirely. Uninstalling the app removes its data.

## What Jixxy never collects

Message or notification content, screen contents or screenshots, microphone, camera, location, contacts, your identity, advertising IDs, or anything about your device sent to any server. Jixxy contains no analytics, advertising or crash-reporting SDKs, and no networking libraries.

## Sharing

Jixxy never shares anything automatically. The weekly review card (planned, not built yet) is meant to be an image you can choose to share through Android's normal share sheet. What you do with it is up to you. If a tester chooses to send a screenshot of their log or card to the team, that is voluntary and done by them, outside the app.

## Permissions

| Permission | Why Jixxy asks | What Jixxy does *not* do with it |
|---|---|---|
| Display over other apps (`SYSTEM_ALERT_WINDOW`) | Show the floating sprite and the pause card on top of other apps | Doesn't read or capture what's on screen; doesn't block touches outside the sprite/card |
| Usage access (`PACKAGE_USAGE_STATS`) | Know which app is in front, and for how long | Doesn't see inside apps, messages or content; no data leaves the phone |
| Foreground service (`FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`) | Keep the floating sprite running while you have him switched on; started and stopped by you | Doesn't run hidden tasks or networking |
| Notifications (`POST_NOTIFICATIONS`, Android 13+) | Show the service notification and a "Wake Jixxy" action | Not used for marketing or reminders |
| Start at boot (`RECEIVE_BOOT_COMPLETED`) | Bring Jixxy back after a restart | No extra data collected |
| **Not requested:** internet, accessibility service, notification listener, microphone, camera, location, contacts, storage | — | — |

Jixxy also declares a query for launcher apps, so setup can show a list of your installed apps to choose from. It does not request the broad "see all installed apps" permission (`QUERY_ALL_PACKAGES`). App backup is switched off, so the log is not copied to cloud backups.

## Data-safety summary (for a future Play Store form)

- Data collected: none transmitted off device.
- Data shared with third parties: none.
- Data encrypted in transit: not applicable (no transmission).
- Users can request deletion: yes, in-app erase of the event log; uninstall removes all data.

## Age

Jixxy is aimed at users 18 and over (provisional decision for v1). It is not directed at children.

## Changes

If the app's behaviour changes, this document is updated the same day.

## Contact

Contact details to be added by the Owner.
