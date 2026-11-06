# Jixxy smoke checklist (phone-friendly, about 10 minutes)

**For:** the Owner, on the phone. **Build:** the newest `jixxy-latest.apk` from
https://github.com/manofwhitebrown/jixxy/releases/tag/latest (open it in the phone browser).
Note the "Built" time shown on that page: it tells us which build you tested.

Go in order. For each step answer **OK** or **BROKE** (and say what you saw).

1. **Update over the old one.** Download `jixxy-latest.apk`, tap it, Update. *No uninstall should be needed.* If Android refuses, copy its exact words.
2. **Open Jixxy.** You should see the real app (onboarding or home), not the 3-button screen. The icon should be the cat.
3. **Allow things.** Say yes to overlay, usage access and notifications when asked. Each ask should explain why. If Android says "restricted setting", follow the in-app help.
4. **Pick apps.** Choose up to 3 apps. A 4th should be refused. Pick a roast level.
5. **Cat shows up.** Tap Wake. The cat appears. Drag him around, let go. Rotate the phone: he stays on screen.
6. **Touches pass through.** Use another app normally; only the cat and his card catch touches.
7. **Pause card.** Open one of your chosen apps. A card with Leave and Stay appears.
8. **Leave / Stay.** Leave takes you home at once. On a new card, Stay unlocks after a few seconds and closes the card.
9. **Talks.** Switch between apps for a few minutes. Does he say anything? Is it funny, not annoying? (Max 6 a day.)
10. **Nap and wake.** Nap 1 hour: he goes quiet. Stop, then Wake: **he must come back** (see `REGRESSION_NAP_STOP_WAKE.md`).
11. **Stays silent where he must.** Open a banking app, the dialer or Maps; take a call. He says nothing.
12. **Reboot.** Restart the phone. Open Jixxy. Is he as you left him? (Not coming back by himself is OK on some phones: just tell us.)
13. **Blind mode.** Turn it on in Settings: he stops reacting. "What Jixxy saw" stops growing.
14. **Clear the log.** "What Jixxy saw" is empty afterwards.
15. **Update again later.** When a newer `latest` appears, install it over this one: settings must still be there.

## How to reply
Plain words are fine: "Steps 1-8 OK. Step 10 BROKE: after Wake the cat did not come back. Redmi Note 11, Android 13, build Built 09:27 UTC."
Add the phone model, Android version and build time. We turn each BROKE into a row in `qa/BUGS.md`.

## For QA records
Record: device, OEM skin, Android version, build (commit SHA from the release page), tester, date, PASS / FAIL / N/A per step. Matrix: `qa/DEVICE_MATRIX.md`.
