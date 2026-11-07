# Owner test guide: Jixxy on your phone

One page. You are tester #1. Test on your real phone, then reply in plain words ("step 5 failed, he never showed up"). No technical terms needed.

**Build you test:** the rolling `latest` build, not `v0.0.1-alpha`.
Download link (open it in your phone browser): https://github.com/manofwhitebrown/jixxy/releases/download/latest/jixxy-latest.apk
Release page (shows the "Built" time, so tell us which build you tested): https://github.com/manofwhitebrown/jixxy/releases/tag/latest

**Status:** everything below was written and passed automatic tests, but **nobody has seen it run on a phone yet**. Your report is the first proof.

## Before you start (2 minutes)

1. **Install over the old one.** Tap the downloaded file, then **Update**. You should not need to uninstall. If Android refuses, send us its exact words.
2. **"Install unknown apps".** If Android asks, allow your browser (or file manager) to install apps once: Settings, Apps, Special access, Install unknown apps.
3. **Permissions.** The app asks for three things and explains each: **Display over other apps**, **Usage access**, **Notifications**.
4. **If a permission is greyed out ("Restricted setting").** Android 15 and newer can block overlay and usage access for apps installed outside Play (Android 13 and 14 mostly block other permissions; sources: [Android Authority](https://androidauthority.com/android-15-restricted-settings-sideloading-3481098)). Do this: tap the greyed-out permission once and press OK, then Settings, Apps, Jixxy, three-dot menu (top right), **Allow restricted settings** (PIN or fingerprint), then go back and try again. The menu item only appears after you tapped the blocked permission once. Menus differ by phone. The app also has its own help for this step. Tell us exactly where you got stuck.
5. **Write down:** phone brand and model, Android version, and the build's "Built" time.

## 8 things to try

1. **First screen.** Open Jixxy. You should see the real app (onboarding or home), not a 3-button screen. Is the icon the cat, not the Android robot?
2. **Look at him.** Is he the cat on your sheet? What's off (face, size, colour)?
3. **Pick apps.** Choose some habit apps and a roast level. (The current build may still stop you at 3; the plan is no limit. Tell us what happens.) A nag-level dial is planned but not in the build yet.
4. **Wake and move him.** Tap Wake. Drag him, let go, rotate the phone. Does he stay on screen? Can you use other apps normally, with taps passing through around him?
5. **The Pause.** Open one of your chosen apps. A card with **Leave** and **Stay** should appear. Leave should take you home at once. On a new card, Stay should unlock after a few seconds. (This is the riskiest step on Android 14 and 15, so please try it twice.)
6. **Talk.** Switch between apps for a few minutes, plug in and unplug the charger. Does he say anything? Funny or annoying? Screenshot any line.
7. **Nap, Stop, Wake.** Nap 1 hour, press Stop, then Wake. He must come back. Try it twice.
8. **Survive.** Lock the phone for 10 minutes and unlock. Then restart the phone and open Jixxy. Is he still there, or does he need waking? Either answer is useful.

## What to screenshot
The first screen, any permission screen where you got stuck, the cat on screen, any line he says, the Pause card, anything that looks broken. Skip private messages and other people's content.

## How to report
One message per problem, in plain words, with:
1. **Phone and Android version**
2. **Build** (the "Built" time)
3. **What you did** (steps, in order)
4. **What you expected**
5. **What happened** (plus a screenshot)
6. **Debug info:** once the app has a **Copy debug info** button (Settings), tap it and paste the text. It is not in the app yet. It stays on your phone until you paste it.

Example: "Steps 1-4 OK. Step 5 BROKE: Leave did nothing. Redmi Note 11, Android 13, built 09:27 UTC."

## Also tell us (opinions are data)
He is meant to be **naggy on purpose**. Is it funny, or too much? Anything annoying, too much or too personal? Did a line make you laugh?

The full 15-step phone checklist is [`qa/SMOKE_CHECKLIST.md`](../qa/SMOKE_CHECKLIST.md) and the bug list is [`qa/BUGS.md`](../qa/BUGS.md). This page is the short version; it does not replace them.
