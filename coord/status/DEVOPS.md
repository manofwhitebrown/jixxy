# DEVOPS status — updated 2026-11-06 03:27 UTC
## Now
T-DEVOPS-5: made CI errors readable via annotations; waiting for the first run to confirm
## Done
- T-DEVOPS-0: .github/workflows/ci.yml (guards job + build job, skip-safe without gradlew)
- T-DEVOPS-1: scripts/check_no_secrets.sh, check_manifest.sh, check_dependencies.sh, check_ownership.sh, validate_lines.py, install_hooks.sh
- T-DEVOPS-4: qa/TEST_PLAN.md, DEVICE_MATRIX.md, SMOKE_CHECKLIST.md, KILL_TEST.md, .github/ISSUE_TEMPLATE/*
- T-DEVOPS-2: debug-keystore request sent to PLATFORM (inbox/PLATFORM/20261106-0318-DEVOPS-debug-keystore.md)
- T-DEVOPS-3: .github/workflows/release.yml (tags v*, SHA-256, notes from CHANGELOG.md, debug-keystore fallback) and pages.yml (skips until site/index.html exists)
- CI fix: concurrency no longer cancels runs on main (rapid pushes by several roles were cancelling each other, so commits got no verdict)
- T-DEVOPS-5: ci.yml split into test/lint/assemble with --continue; scripts/ci_annotate_errors.py reports Gradle errors as annotations
## Next
1. Triage the first completed CI run on main and message owners of any failing path
2. Wire :engine:validateLines into CI when ENGINE lands it
3. Confirm debug keystore with PLATFORM, then test release.yml with a throwaway tag
## Blockers
Cannot read CI job logs from my sandbox (blob.core.windows.net blocked). Need the Owner to paste the Gradle error lines or allow that domain. Sent BLOCKER to MANAGER.
## Questions for MANAGER
none
## Messages handled
20261106-0319-PLATFORM-reply-keystore-wrapper.md, 20261106-0327-PLATFORM-need-build-errors.md
## Honest notes
- Guard scripts were tested locally against a clean repo and against seeded violations (fake token, *.local.md, INTERNET permission, firebase dependency, bad line bank). All failed or passed as intended.
- ci.yml parses as YAML but has NOT run on GitHub yet; I cannot see Actions results from here, so the first run is unverified.
- check_manifest.sh assumes the default AGP merged-manifest path; unverified until a real build exists.
- release.yml and pages.yml have NOT run: they parse as YAML and the changelog extraction was tested on a sample only. release.yml expects PLATFORM to read JIXXY_KEYSTORE_PATH and the JIXXY_* env vars in signingConfigs.release; that is not confirmed.
- pages.yml needs the Owner to set Settings > Pages > Source to GitHub Actions.
- The token can read Actions runs and jobs; I have not tried to read logs.
- ci_annotate_errors.py was tested on a fake log only; unverified on a real failing run.
- No script or workflow has been run on a device. Nothing Android-specific was tested.
