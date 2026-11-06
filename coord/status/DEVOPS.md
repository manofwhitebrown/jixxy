# DEVOPS status — updated 2026-11-06 03:18 UTC
## Now
T-DEVOPS-2 / T-DEVOPS-3 planning (release workflow not started)
## Done
- T-DEVOPS-0: .github/workflows/ci.yml (guards job + build job, skip-safe without gradlew)
- T-DEVOPS-1: scripts/check_no_secrets.sh, check_manifest.sh, check_dependencies.sh, check_ownership.sh, validate_lines.py, install_hooks.sh
- T-DEVOPS-4: qa/TEST_PLAN.md, DEVICE_MATRIX.md, SMOKE_CHECKLIST.md, KILL_TEST.md, .github/ISSUE_TEMPLATE/*
- T-DEVOPS-2: debug-keystore request sent to PLATFORM (commit SHA added in next update)
## Next
1. T-DEVOPS-3 release.yml (tags v*, SHA-256, notes from CHANGELOG.md) and pages.yml
2. Re-check CI result once PLATFORM pushes the scaffold
3. Wire :engine:validateLines into CI when ENGINE lands it
## Blockers
none (build verification waits on PLATFORM's gradlew)
## Questions for MANAGER
none
## Messages handled
none yet
## Honest notes
- Guard scripts were tested locally against a clean repo and against seeded violations (fake token, *.local.md, INTERNET permission, firebase dependency, bad line bank). All failed or passed as intended.
- ci.yml parses as YAML but has NOT run on GitHub yet; I cannot see Actions results from here, so the first run is unverified.
- check_manifest.sh assumes the default AGP merged-manifest path; unverified until a real build exists.
- No script or workflow has been run on a device. Nothing Android-specific was tested.
