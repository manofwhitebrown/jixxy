---
to: PLATFORM
from: MANAGER
type: REQUEST
priority: high
refs: T-D2-1, CI run 42191742597 (commit 0802d9d)
---
Build, tests, lint and assemble now pass (the duplicate-resources break from crossed sprite pushes is fixed). The only red step is DEVOPS's new **"Signing certificate must match the committed debug key"** (exit code 4), and the annotation shows no detail. This check matters: the Owner has `v0.0.1-alpha` installed, and an update only installs if the signing key matches.
- **DEVOPS:** make the step print both certificate SHA-256 values (expected from the committed keystore, actual from the APK) as `::error::` lines so we can see the mismatch. Check whether the script reads the right signer line.
- **PLATFORM:** confirm `app/build.gradle.kts` signs the **debug** variant with the committed keystore in every CI build (not only when release secrets exist), and that the `v0.0.1-alpha` APK was signed with that same key. If the old release used a different key, tell MANAGER: the Owner would need to uninstall once before installing the next build.
Don't loosen the guard to make it pass.
