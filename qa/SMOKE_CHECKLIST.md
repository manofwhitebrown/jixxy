# Jixxy smoke checklist (about 10 minutes, on your phone)

**Build to test:** `jixxy-latest.apk` from https://github.com/manofwhitebrown/jixxy/releases/tag/latest (open it in the phone browser). Write down the **Built** time on that page: it tells us which build you tested.
**How to answer:** for each step say **OK** or **BROKE** and what you saw. Short is fine. Details at the bottom.
Steps marked ★ need a feature the release notes say is in your build (Nag level, sounds). If it isn't there yet, say "not in this build".

## A. Install (1 min)
1. Download `jixxy-latest.apk`, tap it, choose **Update**. You should NOT need to uninstall. If Android refuses, copy its exact words.
2. Open the app drawer. The icon should be the Jixxy cat (not the green robot).
3. *(Debug builds)* There is a second icon, **Jixxy demo**. Open it: buttons for moods, a test bubble, a test Pause card. Tell us what you think of how he looks.

## B. Permissions (2 min)
4. Open **Jixxy**. You should see the real app (onboarding or Home), not the old 3-button screen.
5. Say yes to **Display over other apps**, **Usage access** and **Notifications** when asked. Each ask should say why in plain words.
6. If Android says **"Restricted setting"** or greys a switch out: follow the in-app help (usually app info, three dots, **Allow restricted settings**). Tell us if the help was unclear.

## C. Cat on screen (1 min)
7. Pick the apps you want Jixxy to stop you in. ★ There is no limit: try picking 10 or more. Pick a roast level. Tap **Wake**.
8. The cat appears. Drag him, let go, rotate the phone: he stays on screen.
9. Use another app normally. Only the cat himself catches touches.

## D. Talking and Pause (3 min)
10. Use your phone normally for a few minutes. Does a speech bubble show up now and then? Funny, or annoying? ★ Jixxy is naggy on purpose, so tell us if it's too much or too little.
11. Open one of your chosen apps. A card with **Leave** and **Stay** should appear.
12. **Leave** takes you to the home screen at once.
13. Open the app again, tap **Stay**: wait for the countdown, then it closes and you keep using the app.
(Full 10-minute Pause test: `qa/MILESTONE_M2_PAUSE.md`.)

★ **Too naggy? Turn him down.** Go to Settings and find **Nag level**. Pick **Calm**. Does he talk clearly less often? Pick **Chaos**: more? Put it back to **Naggy**. Also try Nap (he goes quiet at once) and Stop. Tell us how many taps it took.

## E. Nap, Wake, Stop (1 min)
14. Tap **Nap 1 hour** on the cat or in the app: he goes quiet.
15. Tap **Stop**, then **Wake**: **he must come back.** (See `qa/REGRESSION_NAP_STOP_WAKE.md`.)

## F. Silent places (1 min)
16. Open a banking app, the dialer or Maps; take a call. Jixxy says nothing there, even on **Chaos**.
17. Every card and bubble can be closed: Leave works at once, Stay works after the countdown. Nothing blocks you.
18. ★ **Sounds** (if your build has them): off by default. Turn sounds on: meows play. Put the phone on silent: no sound. During a call: no sound.

19. **Blind mode.** Turn it on in Settings: he stops reacting and **What Jixxy saw** stops growing.
20. **Quiet hours.** Set quiet hours to include the current time: no bubbles and no nags (a Pause card on a chosen app may still appear).
21. **Clear the log.** Clear **What Jixxy saw**: it is empty afterwards.

## G. Restart (1 min, optional)
22. Restart the phone, open Jixxy. Is he as you left him? Not coming back by himself is OK on some phones: just tell us.

## H. Update over this build (later)
23. When a newer `latest` appears, install it over this one. Your settings must still be there.

## How to reply
Plain words are fine: "A and B OK. Step 15 BROKE: after Wake the cat did not come back. Redmi Note 11, Android 13, Built 09:27 UTC."
Always add: phone model, Android version, **Built** time. We turn each BROKE into a row in `qa/BUGS.md`.

## For QA records
Device, OEM skin, Android version, build (commit SHA on the release page), tester, date, PASS / FAIL / N/A per step. Matrix: `qa/DEVICE_MATRIX.md`.
