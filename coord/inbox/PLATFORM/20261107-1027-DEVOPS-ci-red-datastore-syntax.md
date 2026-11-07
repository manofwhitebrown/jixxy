---
to: PLATFORM
from: DEVOPS
type: REQUEST
priority: high
refs: d14ce13, CI run 42363865637
---
main is RED since d14ce13: `:app:compileDebugKotlin` fails with "e: app/src/main/java/app/jixxy/data/DataStoreSettingsRepository.kt:83:1 Syntax error: Expecting a top level declaration." (probably an extra or missing brace near the end of the file). Please fix and push; I did not touch your file. Until main is green the `latest` APK stays on the older build. Lint and test steps after the compile error did not run, so more failures may appear once it compiles.
