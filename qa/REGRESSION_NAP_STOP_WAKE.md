# Regression test: Nap, Stop, Wake (bug B-004)

**For:** the Owner on a phone, and PLATFORM. **Time:** about 2 minutes. **Needs:** a build newer than `v0.0.1-alpha` (the `latest` release, or a CI APK), installed, overlay permission already granted.

Do the steps in order. After each step, look at the screen and note PASS or FAIL.

| # | Do this | You should see |
|---|---|---|
| 1 | Open Jixxy, tap **Wake** | The cat appears on screen. |
| 2 | Tap **Nap** (1 hour). If there is no Nap button, use the notification button **Nap 1h** | The cat disappears. |
| 3 | Tap **Stop** (button or notification) | The cat stays gone. The service is stopped. |
| 4 | Tap **Wake** again | **The cat appears and stays.** This was the bug. |
| 5 | Wait 30 seconds without touching the phone | The cat is still there. |
| 6 | Tap **Nap** again, then **Wake** straight away (without Stop) | The cat disappears, then comes back. |
| 7 | Lock the screen, unlock it after 10 seconds | The cat is there. |
| 8 | (Optional) Reboot the phone, open Jixxy, tap **Wake** | The cat appears. If he does not come back by himself after the reboot, that is expected on some phones: say so in your reply. |

**PASS:** steps 4, 5, 6 and 7 show the cat. **FAIL:** the cat is missing after any Wake.

## What to tell us
Reply in plain words, for example: "Step 4 failed, cat never came back. Phone: Redmi Note 11, Android 13." Include the phone model and Android version, and the name of the build you installed. DEVOPS will add it to `qa/BUGS.md` and send it to PLATFORM.

## For PLATFORM: what the test covers
- `Stop` clears `napUntilTs` (clean slate) and `Wake` clears it too (`NapPolicy.afterStop()`, `afterWake()`); a nap that expired while stopped never hides the sprite. These are unit-tested in `NapPolicyTest`; the steps above check the part unit tests cannot: the real service, the real overlay and the real settings store on a phone.
- If step 4 fails, the useful facts are: was the notification still showing, did the cat appear after force-stopping the app, and does it appear after 1 hour.
