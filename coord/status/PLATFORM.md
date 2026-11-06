# PLATFORM status — updated 2026-11-06T08:54Z
## Takeover: 2026-11-06T08:54Z
Found: nap bug confirmed in code (Stop left napUntilTs set, Wake restarted the service, updateNap hid the sprite). Reviewed D-008 edits: `import java.util.Base64` is correct and used; the manifest comment rewording only touched comments. First actions: T-P2-1.
## Now      — T-P2-7 versioning; T-P2-2 waiting for UI reply on API
## Done     — T-P2-0 takeover + D-008 review; T-P2-1 fix, CI green (a68c168)
## Next     — T-P2-2 launcher wiring (agree API with UI), T-P2-3 real brain, T-P2-7 versionCode from -PversionCode
## Blockers — none
## Questions for MANAGER — none
## T-P2-1 behaviour (decisions)
- Wake (`OverlayService.wake(ctx)` / ACTION_WAKE, notification "Wake") clears the nap and shows the sprite. Stop clears the nap too (clean slate). A nap that expired while stopped never hides him. Blind mode does not hide the sprite.
- Pure logic: platform/NapPolicy.kt, tests NapPolicyTest.kt.
## MANUAL TEST for the Owner (new APK from the next release/CI artifact)
1. Open Jixxy, tap "2. Wake Jixxy" -> cat appears.
2. Tap "Nap 1h" (or notification Nap 1h) -> cat disappears.
3. Tap "Stop" -> notification goes away.
4. Tap "2. Wake Jixxy" -> cat appears and stays (this was the bug).
5. Reboot the phone, open Jixxy, tap Wake -> cat appears.
Expected: cat visible after every Wake. Tell me in plain words which step fails.
## Honest notes
- Nothing here was compiled or run by me; CI is the first compile. Not tested on a device.
- Earlier unverified notes from the previous PLATFORM AI still apply (see git history of this file).
