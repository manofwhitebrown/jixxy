# Jixxy

> *Your lazy, sarcastic, but loyal roommate.*

Jixxy is a black, fluffy, heavy-lidded cat who is meant to live on your Android screen. He is lazy, dramatic and sarcastic, and he secretly has your back. The plan: he makes you **pause, with jokes,** before the apps *you* chose.

*A little chaos. A lot of care.*

![status: alpha](https://img.shields.io/badge/status-alpha-orange)
![platform: Android 8+](https://img.shields.io/badge/Android-8%2B-green)
![internet permission: none](https://img.shields.io/badge/internet%20permission-none-blue)

> **Alpha, honestly.** The only published release so far is `v0.0.1-alpha`. It installs and opens, and a floating placeholder sprite can be switched on. Jixxy does not talk in that build and the Pause card does not work in it. Newer work on `main` adds the real cat, the real app screens and more, but none of it has been seen on a phone yet. Read [What works today](#what-works-today) before you install.

---

## What works today

Two columns, so nobody is surprised. **In `v0.0.1-alpha`** is what the Owner saw on a real phone. **On `main`, not released yet** means the code was changed (per the team's bug list, `qa/BUGS.md`) but nobody has seen it working on a phone, so treat it as "may or may not work".

| | In `v0.0.1-alpha` (seen on a phone) | On `main`, not released yet (not seen on a phone) |
|---|---|---|
| Installs and opens | Works | n/a |
| First screen | Temporary 3-button screen | The real app is now the launcher in code |
| Launcher icon | Default Android robot | Jixxy cat face icon in code |
| Floating sprite | Placeholder shape ("potato") | Real cat expression images from the Owner's sheet |
| Jixxy's lines | Nothing shows | Real brain connected; the speech bubble is still being finished |
| Pause card (Leave / Stay) | Not working | A real Pause card view exists in code; not confirmed end to end |
| Nap, Stop, Wake | Jixxy never came back | Fixed in code, needs a phone test |
| Weekly review card | Not built | A weekly review screen and share card exist in code |

If a newer APK appears on the [Releases page](https://github.com/manofwhitebrown/jixxy/releases) (including a rolling pre-release called "latest", if one is published), it is the one to try. Tell us what you see using `growth/OWNER_TEST_GUIDE.md`.

## What he's meant to do

- **You pick up to 3 apps.** When you open one, Jixxy shows a short pause card with **Leave** and **Stay** buttons. You stay in control.
- **He reacts, he doesn't nag.** Silent by default, comments on moments, hard daily cap on unprompted comments, one-tap nap.
- **He roasts the situation, never you.** Late at night he gets softer.
- **Weekly,** a shareable "Jixxy's review of your week" card.
- **No guilt mechanics.** No lost streaks, no punishment for being away, no pet death.

Banking, payments, passwords, dialer and maps apps are protected: he never reacts to or pauses them. During calls and in your quiet hours (default 01:00 to 07:00) he stays silent. "Blind mode" stops all detection and logging.

Jixxy makes no health or medical claims and is not a treatment for anything. He is a small nudge with a sense of humour.

## Everything stays on your phone

The app is built **without the internet permission**, with no analytics, ads or accounts. He has no accessibility service, notification listener, microphone, camera, location or contacts permission, and never reads message content. Anyone can check the permission list in Android's app info. Details: [PRIVACY.md](PRIVACY.md).

---

## Install (sideloading)

Jixxy is not on the Play Store. You install the APK directly.

1. On your Android phone, open the [Releases page](https://github.com/manofwhitebrown/jixxy/releases) and download the `.apk` from `v0.0.1-alpha` or newer.
2. Open the downloaded file. If Android asks, allow your browser or file manager to **install unknown apps** (Settings, Apps, Special access, Install unknown apps).
3. Open Jixxy. In the current alpha you will see a temporary screen with three buttons.
4. Tap **Allow overlay** and grant **Display over other apps**.
5. **Android 13 and newer: "Allow restricted settings".** Because Jixxy is installed outside the Play Store, Android may grey out a permission. If so: tap the greyed-out permission once, then go back. Open Settings, Apps, Jixxy, tap the three-dot menu (usually top right) and choose **Allow restricted settings** (confirm with your PIN or fingerprint if asked). Then return and try the permission again. On some versions the menu item only appears after you have tapped the blocked permission once, and older Android versions may not block it at all. Menus differ by phone and Android version.
6. Grant **Usage access** when asked (it lets Jixxy tell which app is in front; see [PRIVACY.md](PRIVACY.md)). In the alpha, the guided setup for this is not in the first screen yet.
7. Tap **Wake Jixxy**. **Stop** switches him off.
8. **Keep him alive.** Some phone brands aggressively close background apps. See below.

### Keep-alive by brand (not yet verified on real devices)

Menu names differ between versions. The goal is always the same: allow Jixxy to run in the background, set battery usage to *unrestricted* / *no restrictions*, and enable *autostart* where the brand has it.

| Brand | What to look for |
|---|---|
| Stock Android / Pixel | Settings, Apps, Jixxy, Battery, Unrestricted (or "Don't optimise") |
| Samsung | Settings, Battery, Background usage limits: remove Jixxy from "Sleeping apps", add to "Never sleeping apps" |
| Xiaomi / Redmi / POCO | Settings, Apps, Manage apps, Jixxy: Autostart ON; Battery saver for Jixxy: No restrictions |
| Oppo / Realme | Settings, Apps, App management, Jixxy: allow auto-launch and background activity; Battery: Don't optimise |
| Vivo | Settings, Battery, Background power consumption, Jixxy: Allow; Apps: Autostart ON for Jixxy |

These steps come from general knowledge and the in-app guide text. Testers will confirm or correct them.

---

## FAQ

**Why does he need the overlay permission?**
It is how a floating character and the pause card can appear on top of other apps. The overlay is not meant to block touches outside the sprite and card.

**Why Usage access?**
To know which app is currently in front. It gives the app's name and time only, not the contents of any app.

**Does he read my messages?**
No. There is no accessibility service or notification listener, and no message content is read. No screenshots, microphone, camera, location or contacts.

**Why no internet?**
On purpose. Without the permission, the app technically cannot send your data anywhere.

**What does he remember?**
Simple events (for example, "opened an app at 23:40"), kept on your phone and deleted after 30 days. A "What Jixxy saw" screen is written to show the last 7 days and let you erase the log; it is not yet reachable in the alpha. See [PRIVACY.md](PRIVACY.md).

**Is it free?**
Yes. No ads, no accounts, no payments.

**Which phones?**
Android 8.0 or newer.

---

## Known limitations

- **Alpha.** Expect rough edges. See [CHANGELOG.md](CHANGELOG.md) for known issues per release.
- **Sprite art is in progress.** The floating sprite is a placeholder shape. The real cat art is being added.
- **Temporary first screen** instead of the real app.
- **Jixxy doesn't speak yet** and the **Pause flow is unverified on devices.**
- Phones with **aggressive battery managers** may close Jixxy in the background.
- **Android only.** No iOS version is planned.
- English first. A Hinglish line pack comes later.

## Privacy

Everything is processed on your phone; nothing is sent anywhere. Full details and a permissions table: [PRIVACY.md](PRIVACY.md).

## Licence

No licence file is included yet; until one is added, all rights are reserved by default. The licence is an Owner decision.

## Feedback

Jixxy is in a closed tester phase. Testers: use the feedback channel you were given. Jixxy is not affiliated with or endorsed by any app or company named as an example.
