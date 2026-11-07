# DEVOPS status — updated 2026-11-07 05:33 UTC (Wave 3)
## Takeover (Wave 3): 2026-11-07 05:10 UTC
Fresh AI-6 replacing Wave 2. Found: main green at 0550d84 (CI, Latest, Emulator all success); Wave-2 had also shipped emulator.yml (API 26/30/34/35) and ci-evidence upload. First: T-D3-0 verification, then T-D3-3 gates (validateLines + assets blocking, test summary), then T-D3-1/2.
## Wave 3 verification (T-D3-0) — what I actually ran
- main green: yes, CI run 42341346937 on 0550d84; every step success incl. :engine:validateLines.
- `latest`: notes name commit 0550d84, versionCode 163, built 2026-11-07 05:08 UTC = newest green commit. Downloaded jixxy-latest.apk (12,137,823 bytes); its SHA-256 equals the .sha256 asset.
- Published APK (aapt): permissions are SYSTEM_ALERT_WINDOW, FOREGROUND_SERVICE(+SPECIAL_USE), PACKAGE_USAGE_STATS, POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED; **no INTERNET**; allowBackup=false; minSdk 26, targetSdk 36.
- Cert check: scripts/check_apk_cert.sh on that APK vs app/debug.keystore (alias jixxydebug) => match, ff57fdf9...3f1e.
- Guards on a throwaway copy with seeded violations: fake github_pat token, a `*.local.md`, a `.jks` keystore, firebase-analytics dependency, okhttp dependency, INTERNET in merged manifest, over-long line with undeclared slot => ALL failed as intended; clean copy passed.
- **Gap found:** manifest guard did NOT reject READ_MEDIA_IMAGES (storage permissions were not on the forbidden list). Fixed below.
- GROWTH's two edits reviewed line by line: apk_cert.sh b801214 (regex now accepts optional `V2 `/`V3.1 ` prefix before `Signer`; still requires exactly one distinct digest) and check_apk_cert.sh ae3979f (`[[:space:]]*` before `SHA256:` for tab-indented keytool output). Both correct, minimal; keeping them.
- Notices seen in CI annotations: ubuntu-latest migrates to Ubuntu 26 (announced 2026-10-19) => pinned all workflows to ubuntu-24.04; Gradle 8.14.3 "out of date" (PLATFORM's file, not mine; no action needed now).
## Now
Awaiting UI/ENGINE to ship Nag level, uncapped picker and sounds (D-020/D-021); then check the ★ steps in the sheets. Next: CI cost/cache review (T-D3-7), emulator workflow review.
## Done (Wave 3)
- T-D3-1: B-006 "Cat looks half cut off" added (Owner via MANAGER, open, CHARACTER notified). Messages handled: 20261107-1050-MANAGER-bug-cat-cut-off.md, -1200-MANAGER-app-cap-8.md, -1230-MANAGER-naggy-d020.md, -1300-MANAGER-sound-d021.md.
- T-D3-6: scripts/check_resolved_deps.sh (resolved release dependency tree vs banned list incl. play-services, networking, analytics; rejects an empty/thin report) runs in CI, green; qa/THIRD_PARTY.md written (declared libraries only; org.json test-only licence flagged). GROWTH request (3) done.
- D-021: check_assets.py allows only `character/src/main/res/raw/*.ogg`, 400 KB combined, each file needs a line with URL + CC0/generated in design/character/audio/CREDITS.md; tested with 6 seeded cases (no credits, no URL/licence, >400 KB, mp3, ogg outside raw, valid).
- D-019/D-020/D-021 sheets: smoke checklist now has no app cap, a "too naggy? turn him down" step, dismissibility, sounds (off by default, silent mode, calls), blind mode, quiet hours, clear log (23 steps, ★ marks steps that need a feature the build must have); M2 sheet has 12 steps incl. Nag level Calm.
- T-D3-7: CI durations of the last 15 runs on main: 119-357 s (most 2-6 min, includes queueing behind other roles' pushes). Concurrency on main deliberately does NOT cancel (every commit needs a verdict). Gradle cache via setup-gradle is on. Pages deploys from `site/` directly, so the asset guard's site checks cover the built site.
- T-D3-4 (release path): reviewed release.yml and found two bugs that would have bitten at the first real tag: (1) "previous release" was `gh release list | head -1` minus the tag, which picks the rolling `ci-evidence` (no APK, so `gh release download` errors and the job dies) or `latest`; now only numbered tags `v[0-9]*` count, and a missing APK is a notice, not a crash; (2) the manifest guard ran on the debug manifest even after `assembleRelease`; it now builds and checks the manifest of the variant actually built. Added scripts/apk_version_code.sh (reads versionCode out of the APK with aapt2/aapt, tested on the published APK = 163, on a missing file and on junk). release.yml now fails if the APK's versionCode differs from what Gradle was given, or is not strictly higher than the previous numbered release; vs `latest` it must not be lower (equal is only a notice, e.g. tagging the commit `latest` was built from). CI checks on every run that the versionCode inside the debug APK equals the commit count (this also proves aapt exists on the runner). Removed the unused copy of the keystore file in release.yml (Gradle decodes JIXXY_KEYSTORE_B64 itself). latest.yml notes now print the versionCode read from the APK (falls back to the commit count).
- qa/RELEASE_SIGNING.md: stale "Needs PLATFORM" line replaced with what CI now checks. The signed path stays disabled until the Owner adds the 4 secrets.
- e0b8a88 CI run 42341901351 all green with the new steps: unit-test annotation (358 run, 0 failed: app 154, character 86, engine 118), release-manifest guard, blocking line bank + assets. Build job took ~1m46s with a warm cache.
- T-D3-5: Pages enabled, build_type=workflow, deployment success, URL https://manofwhitebrown.github.io/jixxy/ (my sandbox cannot open it: Owner to open once). check_assets.py now also enforces site/ <= 1 MB total, <= 300 KB per file, and rejects remote and protocol-relative src/srcset (tested with seeded violations).
- T-D3-1: qa/BUGS.md refreshed (B-001..B-005 vs code, with SHAs; none verified); added severity S1-S4, next free ID B-006, and a "not bugs yet" list of unproven risks.
- T-D3-2: qa/SMOKE_CHECKLIST.md rewritten in sections A-H (one phone screen each, incl. restricted settings, bubble, Pause/Leave/Stay, update over previous build); qa/MILESTONE_M2_PAUSE.md added (11 steps, plain reply format); REGRESSION_NAP_STOP_WAKE.md checked, still accurate.
- T-D3-0 verification (above).
- T-D3-3: validateLines (Gradle) now in the Gate (a failure turns CI red); asset budget guard runs STRICT=1 (first clean run: 174 KB of 3072 KB); new scripts/ci_test_summary.py puts test counts and failing tests into annotations; manifest guard now also forbids storage permissions (READ/WRITE/MANAGE_EXTERNAL_STORAGE, READ_MEDIA_*), requires allowBackup="false", and also runs on the RELEASE merged manifest (`:app:processReleaseManifest`). GROWTH requests (1),(2),(4) done; (3) resolved-dependency scan still to do.
- T-D3-7 (part): all workflows pinned to ubuntu-24.04.
## Honest notes (Wave 3)
- D-020 safety floors: I do NOT know which are unit-tested in ENGINE (calls, protected apps, quiet hours, blind mode, napped). The smoke sheet covers calls, protected apps, dismissibility, nap, blind mode, quiet hours by hand only. Nag level, uncapped picker and sounds are not in code yet (`maxTargetApps = 3` still), so the ★ steps are unverified text.
- release.yml has still NEVER run on GitHub with these changes (no tag pushed; a test tag would create a public pre-release, so I did not). Its bash was syntax-checked and the version comparison logic unit-tested locally. The release-key branch (`mode=release`) cannot be tested without the Owner's secrets.
- The new CI steps have not run on GitHub at the time of writing; I tested the scripts locally against fake inputs only. `:app:processReleaseManifest` has never been run in CI by us.
- Nothing here was tested on a phone. The test sheets describe behaviour read from code (stay delay 3 s, grace 5 min, Leave goes Home); the real behaviour is unproven.
- check_assets.py site rules and the new test sheets were not run in CI before this push (assets script ran locally, strict, exit 0).

## ---- Wave 2 notes (kept for history) ----
## Takeover: 2026-11-06 08:55 UTC
Wave-2 DEVOPS (AI-6), replacing the previous DEVOPS. Verified: repo cloned, ci_errors.sh says run 42158128299 on 58e7b01 is success (both jobs); debug key is committed in app/debug.keystore; release.yml, ci.yml, guards read. Found: versionCode is hard-coded to 1 in app/build.gradle.kts (PLATFORM file). First: T-D2-1, T-D2-2, T-D2-3.
## Now
T-D2-1..6 and T-D2-8 (partly) done. Next: watch CI after the action bumps, triage red runs (T-D2-7), first Owner bug reports
## Done
- (Wave 2) T-D2-5: CharacterDemoActivity (debug-only, character/src/debug, added by GROWTH at the Owner's request) is inside the published `latest` APK (checked in classes6.dex; manifest has no INTERNET). Second launcher icon "Jixxy demo"; step 0 added to qa/SMOKE_CHECKLIST.md
- (Wave 2) T-D2-6: qa/SMOKE_CHECKLIST.md rewritten for a phone (15 steps, plain reply format)
- (Wave 2) T-D2-8: pinned actions bumped to Node 24 releases (checkout v5.1.0, setup-java v6.0.1, setup-python v6.3.0, upload-artifact v7.0.1, setup-gradle v6.4.0); Pages API says build_type=workflow and Pages runs succeed; I cannot open the site from my sandbox, so deployment itself is unconfirmed
- (Wave 2) latest.yml: no longer moves the `latest` tag (GITHUB_TOKEN got 403 repointing a ref at commits that change .github/workflows); assets and notes are replaced, notes name the real commit. The tag stays on the first build.
- (Wave 2) T-D2-1 cert check + versionCode from commit count (CI green after fixes; real apksigner prints `V2 Signer: certificate SHA-256 digest:`)
- (Wave 2) T-D2-2 latest.yml: rolling pre-release, link https://github.com/manofwhitebrown/jixxy/releases/download/latest/jixxy-latest.apk (first published at versionCode 132; older builds are skipped, never replace a newer one)
- (Wave 2) T-D2-3 qa/BUGS.md (B-001..B-005) + qa/REGRESSION_NAP_STOP_WAKE.md
- (Wave 2) T-D2-4 scripts/check_assets.sh/.py, warn-only in CI
- T-DEVOPS-0: .github/workflows/ci.yml (guards job + build job, skip-safe without gradlew)
- T-DEVOPS-1: scripts/check_no_secrets.sh, check_manifest.sh, check_dependencies.sh, check_ownership.sh, validate_lines.py, install_hooks.sh
- T-DEVOPS-4: qa/TEST_PLAN.md, DEVICE_MATRIX.md, SMOKE_CHECKLIST.md, KILL_TEST.md, .github/ISSUE_TEMPLATE/*
- T-DEVOPS-2: debug-keystore request sent to PLATFORM (inbox/PLATFORM/20261106-0318-DEVOPS-debug-keystore.md)
- T-DEVOPS-3: .github/workflows/release.yml (tags v*, SHA-256, notes from CHANGELOG.md, debug-keystore fallback) and pages.yml (skips until site/index.html exists)
- CI fix: concurrency no longer cancels runs on main (rapid pushes by several roles were cancelling each other, so commits got no verdict)
- T-DEVOPS-5: ci.yml split into test/lint/assemble with --continue; scripts/ci_annotate_errors.py reports Gradle errors as annotations
- T-DEVOPS-7: scripts/ci_errors.sh (prints failure annotations of the newest completed CI run on main)
- T-DEVOPS-2: qa/RELEASE_SIGNING.md (Owner guide for creating the release keystore and the four GitHub secrets)
- check_manifest.sh: ignores XML comments (they cannot declare anything; real entries are still checked) and puts matched lines inside the annotation
- (Wave 2) T-D2-8: Pages actions bumped (configure-pages v6.0.0, upload-pages-artifact v5.0.0, deploy-pages v5.0.1, SHAs from the API); not yet run. ci.yml now also runs :engine:validateLines (continue-on-error, so it reports but cannot turn main red until I see it pass once).
## Next
1. Triage the first completed CI run on main and message owners of any failing path
2. Wire :engine:validateLines into CI when ENGINE lands it
3. Confirm debug keystore with PLATFORM, then test release.yml with a throwaway tag
## Blockers
none. Owner still has to enable Pages.
## Questions for MANAGER
none
## Messages handled
20261106-0319-PLATFORM-reply-keystore-wrapper.md, 20261106-0327-PLATFORM-need-build-errors.md, 20261106-0329-MANAGER-ack-annotations.md, 20261106-0338-MANAGER-manifest-guard-fix.md
## Honest notes
- Guard scripts were tested locally against a clean repo and against seeded violations (fake token, *.local.md, INTERNET permission, firebase dependency, bad line bank). All failed or passed as intended.
- ci.yml parses as YAML but has NOT run on GitHub yet; I cannot see Actions results from here, so the first run is unverified.
- check_manifest.sh assumes the default AGP merged-manifest path; unverified until a real build exists.
- release.yml and pages.yml have NOT run: they parse as YAML and the changelog extraction was tested on a sample only. release.yml expects PLATFORM to read JIXXY_KEYSTORE_PATH and the JIXXY_* env vars in signingConfigs.release; that is not confirmed.
- pages.yml needs the Owner to set Settings > Pages > Source to GitHub Actions.
- The token can read Actions runs and jobs; I have not tried to read logs.
- ci_annotate_errors.py is confirmed working on a real failing run (it surfaced the build.gradle.kts error). Annotations are repeated per step, so the same error appears three times.
- ci_errors.sh was tested on a red run, with no token and with a bad token; not on a green run.
- RELEASE_SIGNING.md has not been followed by anyone yet; the keytool and base64 commands are standard but unverified on the Owner's machine.
- No script or workflow has been run on a device. Nothing Android-specific was tested.
- (Wave 2) T-D2-1 scripts/apk_cert.sh, check_apk_cert.sh, version_code.sh tested only against a FAKE apksigner and keytool output format from memory; the real apksigner/keytool steps have not run on GitHub yet. versionCode stays 1 until PLATFORM reads the Gradle property. release.yml cert comparison uses `gh release download` of the previous release; untested.
- (Wave 2) Verified on GitHub: latest release exists with APK + sha256, a later run skipped an older build correctly. NOT verified: that the APK installs over v0.0.1-alpha on a phone; the release.yml previous-release comparison has never run; Pages deploy state unknown (Owner must enable Settings > Pages > Source: GitHub Actions).
## Manual test for the Owner (T-D2-1/2)
1. On the phone open the link above in the browser, download jixxy-latest.apk, tap it, choose Update/Install.
2. Expected: it installs over v0.0.1-alpha WITHOUT uninstalling, and your settings stay. If Android says "App not installed" or "conflicts with an existing package", tell us the exact words.
3. Then run qa/REGRESSION_NAP_STOP_WAKE.md.
- (Wave 2) Still unverified: configure-pages, deploy-pages and upload-pages-artifact are still on Node 20 versions (not bumped); I did not read the CI logs of the first run after the bumps beyond job status.
