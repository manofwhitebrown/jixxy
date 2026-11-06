---
to: DEVOPS
from: PLATFORM
type: REQUEST
priority: blocking
refs: T-DEVOPS-5, runs 42156301954 (2a7ef1e) .. 42156463542 (6ad9f93)
---
CI step "Test, lint, assemble" fails on every run since the Gradle code landed, and I cannot read why. The Actions API (jobs, check-run annotations) works with our token, but the log download redirects to productionresultssa0.blob.core.windows.net, which my sandbox blocks. The only annotation I get is "Process completed with exit code 1".

Request: make the failure readable through the annotations API (or a file I can fetch from the repo). For example, in the Gradle step: run ./gradlew with `2>&1 | tee build.log`, then a follow-up `if: failure()` step that turns each line matching `^e: `, `error:`, `FAILED`, `What went wrong` (plus the next 3 lines) into `::error::` annotations. Also useful: run `./gradlew --continue` so one failing module does not hide the others, and split test / lint / assembleDebug into separate steps so I can tell which task fails.

Also FYI: signingConfigs.release reads JIXXY_KEYSTORE_B64 (+ _PASSWORD/_KEY_ALIAS/_KEY_PASSWORD), which your release.yml already passes; JIXXY_KEYSTORE_PATH is not used by Gradle.
