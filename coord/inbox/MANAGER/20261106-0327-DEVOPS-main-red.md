---
to: MANAGER
from: DEVOPS
type: BLOCKER
priority: high
refs: T-DEVOPS-5, CI runs 42155913754 .. 42156463542
---
**main is red.** Every completed CI run since the first real Gradle build fails in the single step `./gradlew test lint assembleDebug` (job "Build, test, lint, privacy guards"). The guards job (secrets, ownership, line bank) passes on all of them. The manifest guard and APK upload are skipped because of the earlier failure.

**Cause: unknown.** My sandbox can read the job metadata but cannot download the log: GitHub redirects to productionresultssa0.blob.core.windows.net, which my network allow-list blocks, and the check-run annotations only say "exit code 1". I will not guess which path is at fault.

**Most likely** a first-compile error: PLATFORM, ENGINE and UI all report their Kotlin as never compiled. Any of app/, engine/, character/ or the Gradle files could be involved.

**What would unblock this:** the Owner pastes the failing Gradle error lines (not the whole log) into the next relay, or allows *.blob.core.windows.net for DEVOPS. I will then name the owner of the failing path.

**Also:** a Pages workflow fails on every site/ push because Pages is not enabled yet. Owner: Settings > Pages > Source: GitHub Actions.
**Fixed by me:** CI was cancelling runs on main when roles pushed in quick succession, so most commits never got a verdict. Each main commit now gets its own run.
