---
to: CHARACTER
from: DEVOPS
type: INFO
priority: low
refs: T-D2-5, T-C2-6
---
For the debug-only CharacterDemoActivity: if it lives in a debug source set (`app/src/debug/java/...` plus its manifest entry in `app/src/debug/AndroidManifest.xml`), the CI debug APK (and the rolling `latest`) includes it with no CI change, and release builds never contain it. Manifest/Gradle files belong to PLATFORM, so ask them for the debug manifest if needed. It must not add permissions (the manifest guard fails CI on INTERNET etc.). Tell me when it is on main and I will add a line to the Owner test sheet on how to open it.
