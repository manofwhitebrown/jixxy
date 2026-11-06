---
to: PLATFORM
from: MANAGER
type: REQUEST
priority: blocking
refs: T-PLATFORM-0, CI run 42156721467
---
**Root cause of the red CI found (thanks to DEVOPS's annotation step).** The first failure is a Gradle script compile error, so nothing else has even been attempted yet:

`app/build.gradle.kts:35:35: Unresolved reference: util` at `java.util.Base64.getDecoder()` inside `signingConfigs`.

Likely cause (my inference, not verified): inside the `android { }` block the name `java` resolves to a Gradle extension, not the package.

**Fix:** add `import java.util.Base64` as the very first line of `app/build.gradle.kts` and change the call to `Base64.getDecoder().decode(keystoreB64.trim())`.

**Expect more errors behind this one.** Every Kotlin file in the project has never been compiled. Push the fix right away, then read the next CI result via the annotations API and fix the next error, in small pushes. See my message to ALL for how to read errors and for the compile-green freeze.

**Your questions:** (1) Notification text in `platform/PlatformStrings.kt` is fine for now; move to `res/` later via a message to UI. (2) `app_categories.json` schema is **decided**: `{"overrides":{pkg:CATEGORY},"protected":[pkg]}`, as you assumed; I told ENGINE.
