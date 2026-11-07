# Claims versus evidence (T-G3-9)

Re-checked 2026-11-07 by GROWTH against commit `d14ce13`. I read the guard scripts, the CI workflow, the manifest, the test names and the code; **I ran none of it**. Re-run this table after each `latest` build and at every release.

**Status words**
- **proven** = a guard or test in CI fails the build if the claim breaks (static facts), or the Owner has seen it on a phone with evidence.
- **written** = the code and tests exist, but nobody has seen it work on a phone. Public text must say "in the newest build" or "meant to".
- **planned** = decided, not in code. Public text must say "planned" or leave it out.
A claim with no proof stays out of public text. Only the Owner can move an item to "seen on a phone".

| # | Public claim | Status | Evidence (file / test / line) | Gap or wording rule |
|---|---|---|---|---|
| 1 | No internet permission | **proven** | `scripts/check_manifest.sh` forbids `android.permission.INTERNET` and `ACCESS_NETWORK_STATE`, run on the debug **and release** merged manifests (`ci.yml`, two "Manifest guard" steps) | none |
| 2 | Nothing leaves the phone | **proven** (as "no network access") | #1, #3, plus `android:allowBackup="false"` checked by `check_manifest.sh` | Keep "no network access". Do not write "can never leave under any condition". The debug report leaves only if the user pastes it (PRIVACY says so). |
| 3 | No analytics, ads, crash or network libraries | **proven** (for known names) | `scripts/check_dependencies.sh` (Gradle/toml lists) and `scripts/check_resolved_deps.sh` on the resolved release classpath (`ci.yml`) | Say "none used". Unknown names could pass. |
| 4 | No accessibility service, no notification listener | **proven** | Forbidden list in `check_manifest.sh` | none |
| 5 | No microphone, camera, location, contacts, phone state | **proven** | Same list (`RECORD_AUDIO`, `CAMERA`, location x3, contacts x2, `GET_ACCOUNTS`, `READ_PHONE_STATE`) | Playing sound is not the microphone (see #26). |
| 6 | No storage permission, no "see all installed apps" permission | **proven** | Same list (`READ/WRITE/MANAGE_EXTERNAL_STORAGE`, `READ_MEDIA_`, `QUERY_ALL_PACKAGES`) | The app does use a launcher-apps `<queries>` entry; PRIVACY says so. |
| 7 | He doesn't read your messages or what's on screen | **written** | Backed by #4 and by the permissions that exist; usage access gives app name and time, the overlay draws only. No test proves no code reads the screen. | Keep the wording tied to "no accessibility service, no notification listener". PLATFORM to confirm no screen-capture API (asked before, no answer yet). |
| 8 | Calls detected without a phone-state permission | **written** | `SystemSignals.kt` uses `AudioManager.mode`; `READ_PHONE_STATE` is guarded (#5) | Behaviour on real phones unverified. |
| 9 | You choose the apps (no cap) | **written** | `maxTargetApps` and `MAX_TARGET_APPS` no longer appear in the main code (grep, 2026-11-07) | Code only. Say "no limit" only as "in the newest build". |
| 10 | Naggy by design; you set Calm / Naggy / Chaos, default Naggy | **written** | `NagLevel` in `State.kt` (default `NAGGY`); `SilenceAndCapsTest`, `SimulationTest`, `SettingsLogicTest.nagDefaultsToNaggyAndTurnDownLandsOnCalm`; onboarding step in `b53f71d` | Not seen on a phone. Do not publish prompt counts per level until ENGINE confirms the numbers. |
| 11 | Silent in quiet hours (01:00 to 07:00 by default) | **written** | `quietStartMin = 60`, `quietEndMin = 420` in `State.kt`; `SilenceAndCapsTest.quietHoursSilenceUnpromptedButNotLaterThatDay` | none |
| 12 | A nap silences him; Wake always brings him back | **written** | `NapPolicyTest`, `NapWakeTest.napSilencesPauseAndAppReactions`; `qa/REGRESSION_NAP_STOP_WAKE.md` is the phone test | Bug B-004 is "fixed in code". Do not say "fixed" until the Owner confirms. |
| 13 | Never over calls | **written** | `SilenceAndCapsTest.inCallIsSilent` | Depends on #8. |
| 14 | Protected apps (banking, payments, passwords, dialer, maps) are never reacted to, paused or logged | **written** | `SilenceAndCapsTest.protectedAppsNeverTriggerAndAreNeverLogged`, `AppCategoriesFileTest` | The list is built in and partly from memory. Say "a built-in list of common ones". |
| 15 | Blind mode stops detection and logging | **written** | `SilenceAndCapsTest.blindModeIgnoresEverythingAndLogsNothing` | Stopping the detectors in `OverlayService` needs a phone. |
| 16 | Keeps only the last 30 days | **written** | `BrainConfig.retentionDays`, `Retention.kt`, `RetentionTest`; `OverlayService.kt:196` calls `purgeExpired()`; `SqliteStateStore.purgeOld` | Check it runs in practice on a phone. |
| 17 | You can see and erase what he saved | **written** | `WhatSawScreen`, `SettingsScreen` call `clearEvents` | Not seen on a phone. |
| 18 | Pause card: Leave is instant, Stay unlocks after a few seconds | **written** | `PauseWindow.kt`; engine `stayDelaySec` | **Never claimed as working.** Riskiest step on Android 14 and 15; the Owner has not confirmed it. |
| 19 | Taps outside the cat and card pass through | **written** | Overlay uses wrap-content windows (`OverlayService.kt`) | Design rule only, no test. Needs the phone check. |
| 20 | Weekly review card you can share | **written** | `WeeklyScreen.kt`, route in `JixxyNavHost`, engine `review` tests | Not seen on a phone. |
| 21 | Debug report: local only, you paste it yourself | **written** | `DiagnosticsRepository.kt` KDoc, `MainActivity.diagnosticsPort`, UI wiring (`816ca3b`) | Not seen on a phone. PRIVACY describes the contents. |
| 22 | Free, no ads, no accounts, no payments | **proven** (no ads/accounts/payments code or permissions) | #3 plus no related permissions | "Free" is the Owner's choice. |
| 23 | Android 8.0 or newer | **proven** | `minSdk = 26` in `app/build.gradle.kts` | none |
| 24 | Optional sounds, off by default | **planned** | `soundsEnabled` defaults to `false` in `State.kt`; a Settings row exists; **no audio files** in the repo | Do not mention sounds publicly until files and credits ship (`growth/PENDING_CLAIMS.md`). |
| 25 | Hinglish is an opt-in beta | **written** | Language row in Settings (`7ab9791`); `content/HINGLISH_REVIEW.md` | No native-speaker review yet; keep the "beta" label. |
| 26 | No health or medical claims | **written** (editorial) | Wording rule only. `docs-guards.yml` runs the secrets and ownership checks, **not** a claims or wording check | GROWTH greps public files for health and addiction words before each release. A guard for this is a possible DEVOPS task. |
| 27 | Installs and opens on a real phone | **proven** (seen) | Owner installed `v0.0.1-alpha`, 2026-11-06 | Only the alpha. |
| 28 | The real cat shows on the home screen | **proven** (seen) | Owner screenshot, Samsung, 2026-11-07 10:49 | He looked cut off at the bottom (B-006); CHARACTER fix in code, not seen. |

## Retired
- Old claim "max 3 apps" and "at most 6 unprompted comments a day, 20 minutes apart": removed by D-020. Do not reuse.

## Open requests
- PLATFORM: confirm no `MediaProjection` or screenshot API anywhere (#7).
- ENGINE: publish the per-level prompt numbers before any doc quotes them (#10).
- Owner: the phone checks for every `written` row, starting with #12, #18, #19 (`qa/SMOKE_CHECKLIST.md`).

## How GROWTH uses this table
Public wording may be at or below a row's status, never above. A `planned` item is described as planned or left out. After each `latest` build, change a row to **proven** only with a CI guard that fails or an Owner confirmation, and record the commit above.
