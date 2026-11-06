# Owner test guide: Jixxy on your phone

One page. You are tester #1. Test on your real phone, then reply to the team in plain language ("step 4 failed, he never showed up"). Don't worry about technical words.

**Which build?** Install the newest APK from the Releases page (https://github.com/manofwhitebrown/jixxy/releases). Tell us the version name you installed. `v0.0.1-alpha` has the nap/wake bug you already found; a newer build should have a fix, but we haven't seen it run on a device.

**Note first (30 seconds):** phone brand and model, Android version, build/version name.

## The 8 steps

1. **Install and open.** Download the APK, install it, open it. Did it install without errors? What does the first screen look like? (Currently a temporary 3-button screen, or the real app once it's wired in.)
2. **Icon.** Look at the launcher icon. Is it the Jixxy icon or the Android robot?
3. **Permissions.** Allow *Display over other apps*. If a permission is greyed out on Android 13+, follow the "Allow restricted settings" steps in the README. Tell us exactly where you got stuck, if anywhere.
4. **Wake him.** Tap Wake Jixxy. Does a sprite appear? Is it the cat or a placeholder shape? Is the real cat art there yet?
5. **Move him.** Drag him around and let go. Does he stay where you dropped him? Can you still use other apps normally, with taps passing through?
6. **Nap / Stop / Wake (the bug).** Put him to nap, then press Stop, then Wake again. Does he come back? Try it twice.
7. **Does he talk?** Open a few apps, plug in the charger, then unplug. Does he say anything? Copy or screenshot any line. If you set up a Pause app, open it: does a card appear? Does Leave work immediately? Does Stay wait a few seconds?
8. **Survive.** Lock the phone for 10 minutes, then unlock. Then restart the phone. Is he still there, or does he need waking?

## What to screenshot
- The first screen, and the permission screen if you got stuck.
- The sprite on screen (any state).
- Any line he says, and the Pause card if it appears.
- Anything that looks broken.
Don't capture private messages or other people's content.

## How to report a bug
Send one message per bug with:
1. **Phone and Android version**
2. **Build/version name**
3. **What you did** (steps, in order)
4. **What you expected**
5. **What happened** (plus a screenshot)

DEVOPS keeps the bug list in `qa/`; the full per-device checklist is `qa/SMOKE_CHECKLIST.md`. This guide is the short version for you, not a replacement.

## Also tell us (opinions are data)
- Does he look like the character on your sheet? What's off?
- Anything that felt annoying, too much, or too personal?
