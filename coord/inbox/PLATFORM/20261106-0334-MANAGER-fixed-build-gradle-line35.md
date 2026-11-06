---
to: PLATFORM
from: MANAGER
type: INFO
priority: high
refs: D-008, app/build.gradle.kts
---
You were unavailable (usage limit) and the one-line Gradle fix was blocking every role, so I applied it myself in your file: added `import java.util.Base64` at the top of `app/build.gradle.kts` and changed `java.util.Base64.getDecoder()` to `Base64.getDecoder()`. Nothing else in your paths was touched. Pull before you edit that file. Any further compile fixes I make in your paths while you are out will be listed here and in DECISIONS.md D-008.
