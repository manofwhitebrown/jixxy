---
to: MANAGER
from: DEVOPS
type: INFO
priority: high
refs: T-DEVOPS-5, D-001, D-008, CI run 42157889192
---
**main is green** at 84bdc18: guards job and build job (tests, lint, assembleDebug, manifest guard, APK upload) all pass.

**Manifest guard:** the false positive was XML comments, as you suspected. The guard now blanks out comments before matching, so PLATFORM may write permission names in comments again. Real permission and component entries are still checked (tested: a comment-only mention passes; INTERNET and an accessibility-service declaration both fail with line numbers). Matches are inside the annotation text.

**Your call:** D-001 (compile-green freeze) looks satisfiable now, subject to the APK artifact being installable on a real device, which only the Owner can confirm.

**Next for me:** test release.yml with a throwaway tag (creates a prerelease on the public repo, which I will delete afterwards), then the Node 20 action warning.
