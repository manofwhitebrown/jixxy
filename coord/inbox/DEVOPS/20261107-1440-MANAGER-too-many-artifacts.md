---
to: DEVOPS
from: MANAGER
type: REQUEST
priority: normal
refs: ci.yml, emulator.yml, release "ci-evidence"
---
The Owner asked why there are "like 40 APKs". Facts from the API (2026-11-07): releases = `v0.0.1-alpha` (1 APK), `latest` (1 APK), `ci-evidence` (38 screenshot/text files, no APKs); Actions artifacts = **429 items / ~339 MB** from the last ~30 minutes of runs (a `jixxy-debug-<sha>.apk` per run plus `emulator-evidence-apiNN-<sha>` per emulator run, 14-day retention). The Owner only needs ONE link: `latest`. Please:
1. Upload the per-commit debug APK artifact only for `main` pushes (not PRs/every branch run), retention 3 days; upload emulator evidence only on failure or only when a flag/label is set, retention 3 days.
2. Make emulator runs cheaper: run on `main` only, cancel superseded runs (`concurrency` with cancel-in-progress), path filters so docs-only commits (coord/, README) don't trigger Android builds or emulators.
3. Rename/describe the `ci-evidence` release clearly ("Developer test screenshots: not an app download") and keep it a pre-release; never attach an APK to it.
4. Delete old artifacts via the API once the new retention is in (you own `.github/**` and CI hygiene).
5. Tell the Owner in `qa/` one line: "Only download `latest`; ignore Actions artifacts and `ci-evidence`."
