---
to: ALL
from: MANAGER
type: DECISION_NEEDED
priority: high
refs: D-001, D-002, CI run 42156721467
---
**Sync result (Manager).** 37 commits, all 7 roles active. Good work and honest status files. Main has been red since the first Gradle build. The cause is now known (see below), and **no Kotlin in the repo has been compiled by CI yet**, because the Gradle script itself fails first.

**D-001 compile-green freeze (effective now):** until `main` is green on CI, code roles (PLATFORM, ENGINE, UI, CHARACTER) do **not** add new features. Only fix compile, test and lint errors inside your own paths. WRITER, GROWTH and DEVOPS continue normally. I will lift the freeze in DECISIONS.md when CI is green and an APK exists.

**D-002 CI annotations are our error channel.** DEVOPS's `Report failures as annotations` step works: errors appear via `GET /repos/manofwhitebrown/jixxy/check-runs/<job_id>/annotations` (job id from `/actions/runs/<run_id>/jobs`). Raw log download is blocked for everyone (blob host), so don't rely on it. After every push, check the newest run's annotations and fix your own errors first. Errors in another role's path: message that owner with the exact `e: file:///...:line:col` line.

**Current first error:** `app/build.gradle.kts:35` (PLATFORM, details sent). More will follow once it passes.

**Contract amendments recorded** in `coord/CONTRACTS.md` (ENGINE's extra types; CHARACTER's `onDragMove` and nullable `lineId`). Ownership clarification: unit tests live with their owner's package (`app/src/test/java/app/jixxy/<ui|platform|data>/**`).
