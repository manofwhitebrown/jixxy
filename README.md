# Jixxy

> *Your lazy, sarcastic, but loyal roommate.*

Jixxy is a black, fluffy, heavy-lidded cat who is meant to live on your Android screen. He is lazy, dramatic and sarcastic, a bit naggy, and he secretly has your back. The plan: he makes you **pause, with jokes,** before the apps *you* chose.

*A little chaos. A lot of care.*

![status: alpha](https://img.shields.io/badge/status-alpha-orange)
![platform: Android 8+](https://img.shields.io/badge/Android-8%2B-green)
![internet permission: none](https://img.shields.io/badge/internet%20permission-none-blue)

> **Alpha, honestly.** The only numbered release is `v0.0.1-alpha`, the one build the Owner has run on a phone: it installs and opens, and a placeholder sprite can be switched on, but Jixxy does not talk and the Pause card does not work in it. A rolling **`latest`** pre-release is rebuilt from `main` after every green check and contains the real cat, the real app screens, speech bubbles, the Pause card and more. **None of that has been seen on a phone yet.** Read [What works today](#what-works-today) before you install.

---

## What works today

Two columns, so nobody is surprised. **In `v0.0.1-alpha`** is what the Owner saw on a real phone. **On `main`, not released yet** means the code was changed (per the team's bug list, `qa/BUGS.md`) but nobody has seen it working on a phone, so treat it as "may or may not work".

| | In `v0.0.1-alpha` (seen on a phone) | On `main`, not released yet (not seen on a phone) |
|---|---|---|
| Installs and opens | Works | n/a |
| First screen | Temporary 3-button screen | The real app is now the launcher in code |
| Launcher icon | Default Android robot | Jixxy cat face icon in code |
| Floating sprite | Placeholder shape ("potato") | Real cat expression images from the Owner's sheet |
| Jixxy's lines | Nothing shows | Real brain connected and a speech bubble in code; not seen on a phone |
| Pause card (Leave / Stay) | Not working | A real Pause card view exists in code; not confirmed end to end |
| Nap, Stop, Wake | Jixxy never came back | Fixed in code, needs a phone test |
| Weekly review card | Not built | A weekly review screen and share card exist in code |

The one to try is the rolling **`latest`** build: <https://github.com/manofwhitebrown/jixxy/releases/download/latest/jixxy-latest.apk>. Updates install over an older Jixxy without uninstalling. Tell us what you see using [`growth/OWNER_TEST_GUIDE.md`](growth/OWNER_TEST_GUIDE.md).

## Progress checklist

`[x]` = done. `[ ]` = not done yet. "Written" means the code exists and the computer's tests pass; **only the Owner seeing it work on a phone counts as "seen on a phone".** Last checked 2026-11-07 against commit `5892ff2`.

### Seen working on a real phone
- [x] Installs and opens (`v0.0.1-alpha`)
- [x] Floating placeholder sprite can be switched on (alpha)
- [ ] Real app screens and setup
- [x] The real cat appears on the home screen (Owner screenshot, 2026-11-07)
- [ ] The cat art looks whole (he looks cut off at the bottom; a fix is being written, not seen)
- [ ] Speech bubbles and lines
- [ ] Pause card: Leave and Stay
- [ ] Nap, Stop, Wake (fix written, not seen)
- [ ] Survives a restart or app update
- [ ] Keep-alive on Samsung, Xiaomi, Oppo, Vivo
- [ ] Weekly review card
- [ ] TalkBack (screen reader) check

### Written, in the `latest` build or on `main`, not yet seen on a phone
- [x] Real app as the launcher, guided setup, Home, Settings, "What Jixxy saw"
- [x] 18 cat expression images, speech bubble, Pause card
- [x] No limit on Pause apps (cap removed in the screens and the brain; code only)
- [x] Calm / Naggy / Chaos nag level in Settings, default Naggy (code only)
- [x] Line bank (700+ English lines, more being added) connected to the brain
- [x] Weekly review screen and share card
- [x] Hinglish as an opt-in beta
- [x] Local debug report and crash log (hidden "Report a problem" screen until wired in)
- [x] Offline checks: no internet permission, signing key match, no secrets

### Decided, not written yet
- [ ] Optional meows and blips (off by default)
- [ ] Sharper art, plus a closed-eyes blink frame (yawn, stretch and sleep now exist)

### Docs and launch prep
- [x] README, CHANGELOG, privacy notice, brand guide
- [x] Landing page (static, no outside requests)
- [x] Owner test guide and tester kit (WhatsApp invite, 5 questions)
- [x] Distribution memo and licence memo
- [x] Launch scripts and phone-recording shot list (on hold)
- [ ] Landing page speed score (Lighthouse) checked
- [ ] Landing page opened and checked by the Owner (GitHub Pages is on and has deployed)
- [ ] README, CHANGELOG and site matched to a build the Owner confirmed

### Before testers join
- [ ] The Owner has tested a build on their phone
- [ ] About 20 testers (18+) recruited
- [ ] Seven days of tester feedback collected

---

## What he's meant to do

- **You pick the apps.** When you open one, Jixxy shows a short pause card with **Leave** and **Stay** buttons. You stay in control.
- **He is naggy, on purpose, and you set how much.** Persistent and dramatic ("again?"), but every card and bubble can be dismissed, and a one-tap nap is always there. A Calm / Naggy / Chaos dial is in Settings in the newest build (written, **not yet seen on a phone**); Naggy is the default.
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

1. On your Android phone, open the [`latest` APK link](https://github.com/manofwhitebrown/jixxy/releases/download/latest/jixxy-latest.apk) (all builds are on the [Releases page](https://github.com/manofwhitebrown/jixxy/releases)) and download it.
2. Open the downloaded file. If Android asks, allow your browser or file manager to **install unknown apps** (Settings, Apps, Special access, Install unknown apps).
3. Open Jixxy. The newest builds show the real app with a guided setup; `v0.0.1-alpha` shows a temporary screen with three buttons.
4. Grant **Display over other apps** when the setup asks.
5. **"Allow restricted settings" (mostly Android 15 and newer; Android 13 and 14 block some other permissions).** Because Jixxy is installed outside the Play Store, Android may grey out overlay or usage access. If so: tap the greyed-out permission once, then go back. Open Settings, Apps, Jixxy, tap the three-dot menu (usually top right) and choose **Allow restricted settings** (confirm with your PIN or fingerprint if asked). Then return and try the permission again. On some versions the menu item only appears after you have tapped the blocked permission once, and older Android versions may not block it at all. Menus differ by phone and Android version.
6. Grant **Usage access** when asked (it lets Jixxy tell which app is in front; see [PRIVACY.md](PRIVACY.md)). The newest builds guide you through this; the alpha does not.
7. Tap **Wake**. **Stop** (in the notification or Home) switches him off.
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
Simple events (for example, "opened an app at 23:40"), kept on your phone and deleted after 30 days. A "What Jixxy saw" screen is written to show the last 7 days and let you erase the log; it is in the newest builds, not in the alpha. See [PRIVACY.md](PRIVACY.md).

**Is it free?**
Yes. No ads, no accounts, no payments.

**Which phones?**
Android 8.0 or newer.

---

## Known limitations

- **Alpha.** Expect rough edges. See [CHANGELOG.md](CHANGELOG.md) and the team's bug list [`qa/BUGS.md`](qa/BUGS.md).
- **Nothing in the newest builds has been seen on a phone.** That includes the Pause flow (especially Leave going Home on Android 14 and 15), the speech bubble, the overlay with the bigger cat, starting again after a restart or update, and keep-alive on Samsung, Xiaomi, Oppo and Vivo.
- **Art is soft.** The cat sprites come from a small source image, and the closed-eyes blink frame is missing (yawn, stretch and sleep now exist); a simple squash stands in for it.
- Phones with **aggressive battery managers** may close Jixxy in the background.
- **Android only.** No iOS version is planned.
- **English first.** Hinglish is an opt-in beta (off by default) and has not had a native-speaker read-through.

## Privacy

Everything is processed on your phone; nothing is sent anywhere. Full details and a permissions table: [PRIVACY.md](PRIVACY.md).

## Licence

No licence file is included yet; until one is added, all rights are reserved by default. The licence is an Owner decision.

## Feedback

Jixxy is in a closed tester phase. Testers: use the feedback channel you were given. Jixxy is not affiliated with or endorsed by any app or company named as an example.
