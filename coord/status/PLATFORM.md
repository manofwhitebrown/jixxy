# PLATFORM status — updated 2026-11-06T08:54Z
## Takeover: 2026-11-06T08:54Z
Found: nap bug confirmed in code (Stop left napUntilTs set, Wake restarted the service, updateNap hid the sprite). Reviewed D-008 edits: `import java.util.Base64` is correct and used; the manifest comment rewording only touched comments. First actions: T-P2-1.
## Now      — T-P2-5/6 pushed (real PauseCardView, icons); next: device checks for T-P2-4/5
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

## T-P2-5/6 notes
- PauseWindow hosts CHARACTER's PauseCardView (300 dp wide, centred, wrap-content). Null/blank text shows "Quick pause?". Leave still calls goHome(): UNVERIFIED on Android 14/15.
- Manifest icon + roundIcon set; notification uses R.drawable.ic_stat_jixxy. Messages handled: 20261106-0907-UI-icon-resources.md
- MANUAL TEST: (1) launcher icon is the Jixxy icon, not the Android robot; (2) pull down the notification shade: small icon is the cat silhouette, buttons Nap 1h / Wake / Stop; (3) choose Instagram as a Pause app, open it, wait: a card with a peeking cat, Leave and a Stay countdown appears; Leave -> home screen; Stay -> card closes.

## T-P2-4 (2026-11-06, later) — sprite hosting review
- Reviewed OverlayService against CHARACTER's handoff: `params` is still the object passed to addView (CHARACTER's bubble shift depends on it); WRAP_CONTENT window; drag clamps to screen; PEEK/CHARGING/LOW_BATTERY pass through Dispatcher -> setCharacterState unchanged (no `when` in platform code).
- Fix: sprite remembers which edge it snapped to (`snappedRight`, saved in prefs) and stays on that edge after rotation. Before, a right-edge sprite could end up mid-screen after portrait<->landscape.
- NOT tested on a device. MANUAL TEST: drag cat to the right edge, rotate phone -> cat stays on right edge, same for left. Then trigger a bubble with the cat on left edge, right edge, and near top: bubble must stay fully on screen and the cat must not jump.
- Still UNVERIFIED (device only): Leave -> home from a service on Android 14/15; reboot auto-return on Android 15.
- GROWTH <queries> note: kept as is; the app picker needs the launchable-app list. PRIVACY.md wording (launcher query) is accurate.
Messages handled (later): 20261106-0914-CHARACTER-bubble-and-pausecard-ready.md, 20261106-1300-GROWTH-queries-visibility.md
