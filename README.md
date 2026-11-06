# Jixxy

**A tiny, lazy, dramatic, secretly caring roommate who lives on your Android screen, and pauses you, with jokes, before the apps *you* chose.**

![status: pre-release](https://img.shields.io/badge/status-pre--release-orange)
![platform: Android 8+](https://img.shields.io/badge/Android-8%2B-green)
![internet permission: none](https://img.shields.io/badge/internet%20permission-none-blue)

- **You pick up to 3 apps.** When you open one, Jixxy shows a short, funny pause card with a **Leave** button and a **Stay** button. You are always in control.
- **He reacts, he doesn't nag.** He is silent by default, comments on moments rather than constantly, and has a hard daily cap on unprompted comments.
- **Everything stays on your phone.** The app ships with no internet permission, no analytics, no ads and no accounts. Anyone can verify this in the app's permission list.

> Status: pre-release. First tester build (`v0.1.0`) is being prepared. Nothing here is a finished product yet.

---

## What he does

Jixxy is a small sprite that floats on your screen. He dozes, stretches, gets grumpy when woken, and reacts to a few things: opening an app, a long session, a late night, plugging in the charger, low battery. His reactions are short lines, for example:

> "Third night on Instagram at 2am. I'm starting a spreadsheet."

(Example line, pending the final line bank. Apps are named only as neutral examples; Jixxy is not affiliated with or endorsed by any app or company.)

Once a week he produces a shareable **"Jixxy's review of your week"** card.

He has a mood and energy that respond to your choices. There are no lost streaks, no punishment for being away, and no pet death. You can put him to sleep with one tap.

## How the Pause works

1. In setup you choose **up to 3 apps** you want a moment of friction for, and how roasty he should be (Gentle, Normal, Savage).
2. When you open one of those apps, Jixxy shows a pause card with a line and two buttons.
3. **Leave** closes the card immediately. **Stay** becomes available after a short delay (3 seconds by default) and gives you 5 minutes.
4. That's it. He jokes about the *situation*, never about you. Late at night he gets softer.

Banking, payments, passwords, dialer and maps apps are protected: Jixxy never reacts to them or pauses them. During calls and in your quiet hours (default 01:00 to 07:00) he stays silent. "Blind mode" stops all detection and logging.

Jixxy does not make health or medical claims and is not a treatment for anything. He is a small nudge with a sense of humour.

---

## Install (sideloading)

Jixxy is not on the Play Store yet. Until then you install the APK directly.

1. On your Android phone, open the [Releases page](https://github.com/manofwhitebrown/jixxy/releases) and download the latest `.apk`.
2. Open the downloaded file. If Android asks, allow your browser or file manager to **install unknown apps** (Settings → Apps → special access → Install unknown apps).
3. **Android 13 and newer: "Restricted settings".** Because Jixxy is installed outside the Play Store, Android may grey out a permission. If it does: tap the greyed-out permission once, then go back. Open Settings → Apps → Jixxy, tap the three-dot menu (usually top right), choose **Allow restricted settings** (confirm with your PIN or fingerprint if asked), then return and try the permission again. On some versions that menu item only appears after you've tapped the blocked permission once, and older Android versions may not block it at all. Menus differ by phone and Android version.
4. Follow Jixxy's in-app setup. He will ask for two special permissions:
   - **Display over other apps** (overlay), so he can float on screen.
   - **Usage access**, so he can tell which app is in front.
5. **Keep him alive.** Some phone brands aggressively close background apps. See the per-brand notes below.

### Keep-alive by brand (steps match the in-app guide; not yet verified on real devices)

Menu names differ between versions. The goal is always the same: allow Jixxy to run in the background, set battery usage to *unrestricted* / *no restrictions*, and enable *autostart* where the brand has it.

| Brand | What to look for |
|---|---|
| Stock Android / Pixel | Settings → Apps → Jixxy → Battery → Unrestricted (or "Don't optimise") |
| Samsung | Settings → Battery → Background usage limits → remove Jixxy from "Sleeping apps" and add it to "Never sleeping apps" |
| Xiaomi / Redmi / POCO | Settings → Apps → Manage apps → Jixxy → Autostart ON; Battery saver for Jixxy → No restrictions |
| Oppo / Realme | Settings → Apps → App management → Jixxy → Allow auto-launch and Allow background activity; Battery → Don't optimise |
| Vivo | Settings → Battery → Background power consumption → Jixxy → Allow; Apps → Autostart ON for Jixxy |

Jixxy's setup screens show the same brand-specific steps, so you can follow them in the app.

> These steps follow the in-app guide, which the team wrote from general knowledge. They have **not yet been verified** on real devices; testers will confirm or correct them.

---

## FAQ

**Why does Jixxy need the overlay permission?**
The overlay ("display over other apps") is how a floating character and the pause card can appear on top of other apps. Without it he can't show up. The overlay doesn't block touches outside the sprite and card.

**Why does he need Usage access?**
To know which app is currently in front, so he can react to the apps you chose. It reports the app's name and time only. It does not give him the contents of any app.

**Does he read my messages?**
No. Jixxy has no accessibility service and no notification-listener, and never reads message content. He does not take screenshots and has no microphone, camera, location or contacts permission.

**Why no internet?**
On purpose. The app is built without the internet permission, so it technically cannot send your data anywhere. You can check this in Android's app info.

**What does he remember?**
Simple events (for example, "opened an app at 23:40"), kept on your phone and deleted automatically after 30 days. The in-app "What Jixxy saw" screen shows the last 7 days, and you can erase the whole log. See [PRIVACY.md](PRIVACY.md).

**Is it free?**
Yes. No ads, no accounts, no payments.

**Which phones?**
Android 8.0 or newer.

---

## Known limitations

- Android only. No iOS version is planned.
- Phones from brands with aggressive battery management may close Jixxy in the background unless you follow the keep-alive steps.
- English first. A Hinglish line pack is planned for later.
- Pre-release software: expect rough edges. See [CHANGELOG.md](CHANGELOG.md) for known issues per release.

## Privacy

Short version: everything is processed on your phone, nothing is sent anywhere. Full details, including a permissions table, are in [PRIVACY.md](PRIVACY.md).

## Licence

To be decided by the project Manager. No licence file is included yet; until one is added, all rights are reserved by default.

## Contributing / feedback

Jixxy is currently in a closed tester phase. If you are a tester, use the feedback channel you were given.
