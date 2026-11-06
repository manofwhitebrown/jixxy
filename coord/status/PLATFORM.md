# PLATFORM status — updated 2026-11-06T08:54Z
## Takeover: 2026-11-06T08:54Z
Found: nap bug confirmed in code (Stop left napUntilTs set, Wake restarted the service, updateNap hid the sprite). Reviewed D-008 edits: `import java.util.Base64` is correct and used; the manifest comment rewording only touched comments. First actions: T-P2-1.
## Now      — waiting on CHARACTER (showBubble stub T-C2-3, PauseCardView) and UI icons
## Done     — T-P2-2 launcher + T-P2-3 real brain (fe3c9ee, build/tests/lint green); T-P2-7 versionCode/versionName from -P props (0f59980); T-P2-0 takeover + D-008 review; T-P2-1 fix, CI green (a68c168)
## Next     — T-P2-2 launcher wiring (agree API with UI), T-P2-3 real brain, T-P2-7 versionCode from -PversionCode
## Blockers — CI step "Signing certificate must match" red for all commits (DEVOPS script, exit 4); PauseCardView and icon assets not pushed yet
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

## T-P2-2/3 notes
- Manifest: ui.MainActivity is the LAUNCHER; platform.LauncherActivity is exported=false and has no launcher filter (dev only).
- OverlayService now loads assets/lines/<lang>/*.json (fallback en), builds JixxyBrains.create(...), wraps it in PersistingBrain (state + events saved after every signal). NoopBrain moved to test sources.
- MANUAL TEST: install new build, finish onboarding, open Instagram/YouTube (a chosen app); expect Jixxy to say a real line within a few seconds. If silent, tell me which app and what time.
- Not verified on a device. Caps (6/day, 20 min apart) mean he may be silent after a few lines: that is by design.

- Signing check: verified by hand that the v0.0.1-alpha APK cert equals app/debug.keystore (ff57fdf9...3f1e). Red CI step is a DEVOPS script issue (see inbox/MANAGER, inbox/DEVOPS). Messages handled: 20261106-0903-MANAGER-signing-check-red.md, 20261106-0854-UI-launcher-and-wake.md, 20261106-0855-DEVOPS-versioncode-from-gradle-property.md, 20261106-0857-ENGINE-handoff-brain-factory.md

## Doc check (T-P2-5/8), source: developer.android.com/about/versions/15/behavior-changes-15
- Android 15, apps targeting 35+: the SYSTEM_ALERT_WINDOW exemption for starting a foreground service from the background is narrower; the app must already have a VISIBLE TYPE_APPLICATION_OVERLAY window first. Consequence: a start from a notification button or boot while no overlay is visible can throw ForegroundServiceStartNotAllowedException. OverlayService.enterForeground and BootReceiver already catch it; the Owner test is: reboot, then check whether Jixxy returns by himself (if not, he returns when the app is opened and Wake is tapped).
- Not found in the docs I retrieved: whether starting the HOME activity from a service is allowed with overlay permission only. Still UNVERIFIED; needs a real Android 14/15 phone. Test: open Instagram, wait for a Pause, tap Leave; expected result is the home screen.
