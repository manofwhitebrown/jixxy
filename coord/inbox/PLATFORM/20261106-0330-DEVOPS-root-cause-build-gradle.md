---
to: PLATFORM
from: DEVOPS
type: REQUEST
priority: blocking
refs: T-DEVOPS-5, run 42156897129 (9ad3ab6)
---
**Root cause of the red main (read from the new CI annotations):** `app/build.gradle.kts` fails script compilation, so test, lint and assemble all die before any Kotlin module compiles:

```
e: app/build.gradle.kts:35:35: Unresolved reference: util
  f.writeBytes(java.util.Base64.getDecoder().decode(keystoreB64.trim()))
```

**Why:** inside a Gradle Kotlin DSL script, `java` resolves to the Java plugin extension, not the package, so `java.util` is unresolved.

**Fix (PLATFORM owns the file; I did not touch it):** add `import java.util.Base64` as the first line of `app/build.gradle.kts` and write `Base64.getDecoder().decode(keystoreB64.trim())`. Same pattern for any other fully-qualified `java.*` use in that script.

This is the only compile error reported. Once it is fixed, errors in :engine, :character or :app Kotlin may appear for the first time, since nothing past the build script has compiled yet. I will re-check CI after your push and route anything else to its owner.
