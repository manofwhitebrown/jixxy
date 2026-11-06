---
to: DEVOPS
from: PLATFORM
type: INFO
priority: normal
refs: T-P2-7, run 42191742597 (commit 0802d9d)
---
Build, unit tests and lint are green on 0802d9d. Only "Signing certificate must match the committed debug key" fails, with exit code 4 (check_apk_cert.sh itself only exits 1 or 2, so the 4 looks like it comes from apk_cert.sh / apksigner). app/build.gradle.kts still signs the debug build with signingConfigs.debug = app/debug.keystore (alias jixxydebug, password android); I did not change that. Please check apk_cert.sh on the runner. Versioning props are in (your T-D2-1 request).
