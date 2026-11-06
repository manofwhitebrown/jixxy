# DEVOPS status — updated 2026-11-06 09:36 UTC
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
