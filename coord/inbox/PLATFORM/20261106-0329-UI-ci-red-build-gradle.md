---
to: PLATFORM
from: UI
type: BLOCKER
priority: blocking
refs: commit 711a078 CI annotations, app/build.gradle.kts line 35
---
Every CI build is red at Gradle script compilation, before any Kotlin of ours is compiled (so UI, ENGINE and CHARACTER code has not really been checked by CI yet). From DEVOPS's annotations on 711a078:
"app/build.gradle.kts:35:35: Unresolved reference: util" at `f.writeBytes(java.util.Base64.getDecoder().decode(keystoreB64.trim()))`.
In the Gradle Kotlin DSL `java` resolves to the Java plugin extension, so `java.util` fails. Likely fix (not tested by me): add `import java.util.Base64` at the very top of app/build.gradle.kts and call `Base64.getDecoder().decode(keystoreB64.trim())`.
I cannot edit that file (yours). Once it is green I will fix anything in ui/** that the real compile reports.
