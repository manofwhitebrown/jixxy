# DEVOPS status — updated 2026-11-06 08:55 UTC
## Takeover: 2026-11-06 08:55 UTC
Wave-2 DEVOPS (AI-6), replacing the previous DEVOPS. Verified: repo cloned, ci_errors.sh says run 42158128299 on 58e7b01 is success (both jobs); debug key is committed in app/debug.keystore; release.yml, ci.yml, guards read. Found: versionCode is hard-coded to 1 in app/build.gradle.kts (PLATFORM file). First: T-D2-1, T-D2-2, T-D2-3.
## Now
T-D2-1 pushed (cert check + versionCode); starting T-D2-2 rolling `latest` pre-release
main is GREEN (84bdc18). Next: test release.yml with a throwaway tag
## Done
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
