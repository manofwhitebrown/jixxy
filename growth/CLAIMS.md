# Claims to proof (T-V1-GROWTH-1)

Draft, 2026-11-06, GROWTH. Every public claim in README, PRIVACY, the site and the store draft, with what proves it.
**Proof levels:** `CI` = a guard or test fails the build if it breaks · `CODE` = visible in code, no automated check · `DEVICE` = only a phone can show it, **not yet seen** · `NONE` = no proof, claim is cut or flagged.
Nothing here was run by me; I read the guard scripts, the manifest and the test names. Proof paths are for PLATFORM / DEVOPS to confirm.

| # | Public claim | Where said | Proof | Level | Gap / action |
|---|---|---|---|---|---|
| 1 | No internet permission | README, PRIVACY, site, store | `scripts/check_manifest.sh` greps the **merged debug** manifest for `android.permission.INTERNET` (ci.yml, "manifest guard" step) | CI | Release build's manifest is not checked. **DEVOPS:** run the guard on the release merge too. |
| 2 | Nothing leaves the phone | README, PRIVACY, site | Follows from #1, #3 and `allowBackup="false"` in `AndroidManifest.xml` | CI (#1) + CODE | `allowBackup` is not guarded. **DEVOPS:** add it to `check_manifest.sh`. Device-to-device transfer is not covered; keep wording "no network access", not "never leaves under any condition". |
| 3 | No analytics, ads, crash or network SDKs | README, PRIVACY, site | `scripts/check_dependencies.sh` greps Gradle/toml files for a list of known coordinates | CI (partial) | List-based: unknown or transitive libraries pass. **DEVOPS:** also scan the resolved dependency list from `./gradlew :app:dependencies`. Until then say "none used", not "none possible". |
| 4 | No accessibility service, notification listener | README, PRIVACY, site | Forbidden list in `check_manifest.sh` (`BIND_ACCESSIBILITY_SERVICE`, `android.accessibilityservice`, `BIND_NOTIFICATION_LISTENER_SERVICE`, `NotificationListenerService`) | CI | none |
| 5 | No microphone, camera, location, contacts | README, PRIVACY, site | Forbidden list: `RECORD_AUDIO`, `CAMERA`, location x3, contacts x2, `GET_ACCOUNTS`, `READ_PHONE_STATE` | CI | none |
| 6 | No storage permission (PRIVACY table says "Not requested: ... storage") | PRIVACY | Manifest has no storage permission today; **not in the forbidden list** | CODE | **DEVOPS:** add `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `MANAGE_EXTERNAL_STORAGE`, `READ_MEDIA_*`. Or **GROWTH cuts the word** if not added. |
| 7 | He doesn't read your messages / what's on screen | README, PRIVACY, site, store | Only via #4 and the permissions that exist (usage access gives app names and time; overlay draws, does not read). Nothing tests that no code reads screen content. | CODE | Wording stays "doesn't read", backed by "no accessibility / no notification listener" in the same sentence. **PLATFORM:** confirm no `MediaProjection`, no screenshot API anywhere. |
| 8 | Call detection without phone-state permission | PRIVACY (implied) | `SystemSignals.kt` infers calls from `AudioManager.mode`; `READ_PHONE_STATE` is forbidden by the guard | CI + CODE | Behaviour on real phones is unverified (**DEVICE**). |
| 9 | You choose up to 3 apps | README, site, store | `OnboardingFlow.MAX_TARGET_APPS = 3`, `SettingsLogic.targetCapReached`; unit tests under `ui/onboarding` and `ui/settings` | CI (UI logic) | The cap is enforced in UI only; `UserSettings` itself accepts more. **ENGINE/PLATFORM:** clamp in the repository, or accept that the UI is the only entry. |
| 10 | Max 6 unprompted comments a day, 20 minutes apart | README, site | `BrainConfig.maxUnpromptedPerDay = 6`, `minGapMinutes = 20`; `SilenceAndCapsTest`, `SimulationTest` | CI | Check the exact test names assert the numbers, not just "a cap exists". **ENGINE:** confirm. |
| 11 | Silent in quiet hours (01:00-07:00 default) | README, site | `SilenceAndCapsTest.quietHoursSilenceUnpromptedButNotLaterThatDay` | CI | Default hours are in `UserSettings`; confirm they still equal 60/420. |
| 12 | A nap silences him; Wake always brings him back | README, site | `NapPolicyTest`; `NapPolicy.shouldShowSprite` used in `OverlayService` | CI | The Owner's bug B-004 is `fixed in code`, **DEVICE** test `qa/REGRESSION_NAP_STOP_WAKE.md` pending. Do not say "fixed" publicly until the Owner verifies. |
| 13 | Never over calls | README | `SilenceAndCapsTest.inCallIsSilent` | CI | Call detection itself is #8. |
| 14 | Protected apps (banking, payments, passwords, dialer, maps) are never reacted to, paused or logged | README, PRIVACY, store | `SilenceAndCapsTest.protectedAppsNeverTriggerAndAreNeverLogged`; `AppCategoriesFileTest.protectedListIsGenerousAndNeverOverlapsOverrides` | CI | Package names in `app_categories.json` marked `_unverified` were written from memory. Say "a built-in list of common ones", never "all banking apps". |
| 15 | Blind mode stops detection and logging | README, PRIVACY | `SilenceAndCapsTest.blindModeIgnoresEverythingAndLogsNothing`; `OverlayService` stops detectors when set | CI (brain) | The detector stop in `OverlayService` is **DEVICE** only. |
| 16 | Stores only the last 30 days | README, PRIVACY | `BrainConfig.retentionDays`, `Retention.kt`, `RetentionTest` | CI | Check the purge actually runs on a schedule (who calls it?). **PLATFORM:** point to the call site. |
| 17 | You can see and erase what he saved, in the app | README, PRIVACY, site | `WhatSawScreen`, `SettingsScreen` call `clearEvents`; `StateStore.clearEvents` in the API | CODE | UI is not wired as launcher in the alpha release; **DEVICE** unverified. Say "in the app (coming in v0.1.0)" until seen. |
| 18 | Pause card: Leave is instant, Stay waits a few seconds | README, site, store | Contract `stayDelaySec`; the Pause card is still a stub per PLATFORM | NONE | **Cut from anything saying it works today.** Only "meant to" wording. |
| 19 | The overlay never blocks touches outside the sprite or card | README, PRIVACY, store | Overlay uses `WRAP_CONTENT` windows (`OverlayService.kt`), which is consistent but not a test | CODE | **DEVICE** test needed. Keep as a design rule, not a promise, until the smoke sheet covers it. |
| 20 | Weekly "review of your week" card | README, site | Review logic exists in `engine/.../review` with tests; no card UI shipped | NONE (as a feature) | Described as "planned" only. |
| 21 | Free, no ads, no accounts, no payments | README, site, store | #3 (SDK list) plus no related permissions | CI (partial) | "Free" is the Owner's decision, not a technical fact. |
| 22 | Only needs Android 8.0+ | README, store | `minSdk` in `app/build.gradle.kts` | CODE | Confirm value is 26. **PLATFORM.** |
| 23 | No health or medical claims | README, PRIVACY, store | Wording rule, no code | NONE (editorial) | **GROWTH** greps public files for health/addiction words before each release. |
| 24 | "Alpha, installs and opens" | README, site | Owner installed `v0.0.1-alpha` on a real phone, 2026-11-06 | DEVICE (seen) | Only claim with a human witness today. |

## Cut or reworded because of this table
- #6: remove "storage" from the PRIVACY "not requested" row **unless** DEVOPS adds it to the guard.
- #17, #18, #19, #20: stay in "meant to / planned" wording. Nothing may say they work.
- #2: wording stays "no network access"; no "can never leave your phone".

## Requests sent
- DEVOPS: #1 (release manifest), #2 (`allowBackup`), #3 (resolved deps), #6 (storage permissions).
- PLATFORM: #7, #16, #22 confirmations.
- ENGINE: #9, #10, #11.

## Re-run
Redo this table at every release and whenever a guard changes. A claim with no proof stays out.
