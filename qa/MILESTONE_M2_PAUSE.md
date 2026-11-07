# Milestone M2: the 10-minute Pause test

**Why:** the Pause (Jixxy stops you before a habit app) has never been seen on a real phone. This test says whether it works.
**Needs:** the newest `jixxy-latest.apk` installed, overlay + usage access + notifications allowed, and **Wake** tapped so the cat is on screen. Pick one habit app you really use (call it APP). Note the **Built** time on the release page.

Do the steps in order. Answer each as **"step N worked"** or **"step N didn't: what I saw"**.

1. **Pick APP.** In Jixxy, choose APP as one of your 3 apps. *Expect:* it is saved; a 4th app is refused.
2. **Trigger.** Go to the home screen, wait 10 seconds, open APP. *Expect:* within about 2 seconds a card appears on top of APP with a joke line, a **Leave** button and a **Stay** button.
3. **Look.** *Expect:* the card is readable, the cat peeks out, nothing is cut off at the screen edges. Tell us if the text is too small or too big.
4. **Leave.** Tap **Leave**. *Expect:* you land on the home screen **at once** and the card is gone. (Biggest risk on Android 14/15: if you stay in APP or see a blank screen, say so.)
5. **Again.** Open APP again straight away. *Expect:* either a new card or a short quiet time after Leave. Tell us which.
6. **Stay.** When a card shows, tap **Stay** (it may be greyed with a ring for about 3 seconds first). *Expect:* the ring counts down, then **Stay** works, the card closes and APP stays open.
7. **Grace.** Keep using APP for 5 minutes. *Expect:* no card during that time. After about 5 minutes Jixxy may ask once more.
8. **Back button.** Open the card again and press the phone's Back button. *Expect:* the card does not trap you; tell us what happens.
9. **Other apps.** Open an app you did NOT choose (and a banking app or the dialer). *Expect:* no card.
10. **Nap.** Nap Jixxy for 1 hour, open APP. *Expect:* no card while he naps.
11. **After restart (optional).** Restart the phone, open Jixxy once, then APP. *Expect:* the card works again. If it doesn't, say so.

## How to reply (copy and fill in)
```
Phone + Android version:
Built time:
Step 1: worked / didn't: ...
Step 2: worked / didn't: ...
... (up to 11)
Anything annoying or funny:
```
We turn every "didn't" into a bug in `qa/BUGS.md`. Only you can mark a bug **verified**.

## For QA records
Pause is considered verified for M2 when steps 2, 4 and 6 worked on at least one real phone on Android 14 or newer, and 8 does not trap the user.
