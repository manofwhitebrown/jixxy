# PLATFORM status (Wave 3, AI-1) — updated 2026-11-07
## Takeover: 2026-11-07 (UTC)
Found: Wave-2 code compiles on CI but is unrun on a phone. Read OverlayService, Dispatcher, PauseWindow, UsageDetector, SystemSignals, BootReceiver, PersistingBrain. Three real bugs found by reading (fixed below): (1) a Pause card dismissed by screen-off/nap/service stop left `Dispatcher.pauseShowing=true` forever, so NO further Pause would ever show; (2) an exception in `goHome()` would skip emitting `PauseChoice`; (3) no restart after an app update (only BOOT_COMPLETED was handled).
## Now — T-P3-0, T-P3-2 (core), T-P3-4 pushed; CI GREEN at b24c124 (compiles + unit tests pass; nothing seen on a phone)
## Done (this session)
- T-P3-4 (b24c124): `data/DiagnosticsLog` (file ring buffer ~200 KB, line-trimmed), `DiagnosticsRedactor` (other apps' packages -> `<app>`), `DiagnosticsReport` (<= 20 KB, newest lines), `platform/DiagnosticsRepository` (`copyableReport()`, `clear()`, crash handler installed in `JixxyApp.onCreate`). Events logged: APP_START, SERVICE_CREATE/COMMAND/DESTROY, FGS_REFUSED, NO_OVERLAY, PAUSE_SHOWN/CHOICE/CANCELLED, GO_HOME_FAILED, CRASH. Tests: DiagnosticsLogTest. HANDOFF sent to UI (needs a Settings 'Copy debug info' button) and GROWTH (PRIVACY text).
- Pause card lifecycle: cancel callback + `dismissPause()`; card is dropped on app switch, call start, screen off, nap, add-view failure; dispatcher is never stuck. `goHome()` failure no longer swallows the choice. Tests added in DispatcherTest.
- `MY_PACKAGE_REPLACED` restart (RestartPolicy + test + manifest filter). `addSprite()` now returns false and stops the service cleanly if overlay is missing (no half-started pipeline).
## Next — T-P3-1 as soon as the Owner reports; T-P3-5 battery audit (detector polls every 1.5 s, audio every 3 s, heartbeat 60 s, all screen-on only; idea: back-off to 3-5 s after 30 s in the same app); T-P3-6 R8/targetSdk doc check (targetSdk is already 36); T-P3-8 weekly trigger
## Blockers — none. No device available to me; CI is the only compile.
## Questions for MANAGER — none

## T-P3-0 truth table (from code; U = unit-tested, R = reasoning only)
| Event | What the code does | Cover |
|---|---|---|
| Cold start (user taps Wake) | `OverlayService.wake()` -> startForegroundService(ACTION_WAKE) -> onCreate: channel, startForeground, add sprite, register screen receiver, load state+bank, build brain, start Dispatcher + detectors; ACTION_WAKE clears nap, emits WakeRequested | NapPolicy U; flow R |
| Wake while running | `enterForeground()` again (required answer to startForegroundService), clears nap, sprite shown | R |
| Nap | settings.napUntilTs = now+1h, NapRequested emitted, sprite hidden, card dismissed, timer re-shows him | NapPolicy U; service R |
| Stop | wantedRunning=false, nap cleared, stopSelf; onDestroy removes sprite+card+receiver, stops detectors | NapPolicy U; R |
| Reboot | BootReceiver restarts only if wantedRunning && overlay granted; Android 15 may refuse -> swallowed (user reopens app) | RestartPolicy U; OS behaviour UNVERIFIED |
| App update | NEW: MY_PACKAGE_REPLACED handled like boot | RestartPolicy U; UNVERIFIED on phone |
| Process death | `START_NOT_STICKY`: service does NOT come back by itself (kept on purpose: restart-from-background is not documented as exempt; revisit after Owner test). State is saved after every signal (PersistingBrain); Pause grace timers are NOT persisted yet (D-017 pending from ENGINE) | PersistingBrain U; R |
| Screen off | screenReceiver: card dismissed, sprite hidden; SystemSignals stops ALL polling (detector, heartbeat, audio) | R |
| Screen on | sprite shown if awake; polling restarts (new session) | R |
| Call start/end | audio mode poll every 3 s (screen on only) -> CallState; Dispatcher drops renders in call; NEW: card cancelled when a call starts | DispatcherTest U |
| Charger plug/unplug | broadcast -> Signal.Charger -> brain; logged on connect | R |
## Pause edge-case verdicts (T-P3-2)
- Switch app with card up: card cancelled (NEW). Call arrives: cancelled (NEW). Screen off / nap / service stop: cancelled (NEW). Two chosen apps in a row: second ignored while one shows, and first is cancelled on switch so the next can show (NEW, tested). Overlay revoked: addView failure caught -> cancelled; service stops cleanly if sprite cannot be added.
- Tap outside card: window is wrap-content + NOT_TOUCH_MODAL, so touches pass to the app below; card is not dismissed silently. Leave is instant, Stay unlocks after delay (CHARACTER card).
- Back button/gesture: window is NOT_FOCUSABLE so it never receives back; the app underneath handles it. Verdict: accept.
- Rotation with card up: width fixed 300 dp, gravity centre, window re-laid by system. Not verified. Split-screen / PiP / quiet hours / protected / blind: quiet hours and protected handled in engine+Dispatcher; split-screen/PiP not specially handled (card just overlays). Consciously not handled in v0.1.
- Detection latency: poll every 1.5 s while screen on, zero while screen off, so card appears within ~1.5-2 s. Adaptive back-off not done yet (T-P3-5).
## Leave -> Home (Android 14/15) — doc-backed decision
Source: developer.android.com/guide/components/activities/secure-bal (exceptions list): an app may start an activity from the background if it has a visible window, OR the user granted SYSTEM_ALERT_WINDOW. The Pause card is a visible overlay AND the app holds SYSTEM_ALERT_WINDOW, so `startActivity(HOME, NEW_TASK)` from the service is a documented-allowed path. A blocked start is silent (only a Logcat line), so the card is always closed first and the choice always emitted. If the Owner's test shows Leave does nothing, next step is a PendingIntent/transparent-activity route (not built, not needed on paper).
FGS start sources: boot/update/notification-button/app-open are all on the docs' exemption list (developer.android.com/develop/background-work/services/fgs/restrictions-bg-start). Android 15 narrows only the SYSTEM_ALERT_WINDOW exemption (behavior-changes-15). Not found in what I retrieved: whether specialUse is blocked from BOOT_COMPLETED on 15; fail-soft path exists.
## MANUAL TEST for the Owner (install the `latest` APK after CI is green)
1. Open Jixxy, finish setup, pick Instagram (or any app you chose) as a Pause app.
2. Open that app. Within ~2 seconds a card should pop up. Tap **Leave**: you should land on the home screen. Reply: "Leave worked" or "Leave did nothing / app stayed open".
3. Open the app again, wait for the card, then lock the phone and unlock it. Card should be gone. Open the app again: a new card should appear (this was the stuck bug).
4. Open the app, wait for the card, switch to another app with the gesture: the card should disappear.
5. Update test: install a newer APK over the old one with Jixxy running; after install, the cat should come back by itself within a few seconds (if not, open Jixxy and tap Wake, then tell me your phone model).
6. Reboot test: restart the phone; cat should return within about a minute (many phones block this; tell me the phone model either way).
## Owner test for diagnostics (after UI adds the button)
Settings -> Copy debug info -> paste it into chat. Expect: short text with app version, Android version, phone model, permission states, then dated event lines. If it shows names of apps you did NOT choose, tell me at once.
## Honest notes
- Process death / battery / R8 / weekly trigger (T-P3-3 rest, T-P3-5/6/8) not started; table above is the T-P3-3 audit result.
- Nothing here was run on a phone or compiled by me; CI is the first compile. `fixed in code` only.
- Messages handled: all Wave-2 inbox items were read through the predecessor's status; none new for Wave 3 yet.
## Earlier history (condensed from Wave 2)
Nap/Stop/Wake fix (NapPolicy, a68c168); launcher + real brain (fe3c9ee); versionCode via -P props (0f59980); sprite edge memory after rotation; PauseWindow hosts PauseCardView; Android 15 doc check (SAW-based FGS exemption narrowed).
