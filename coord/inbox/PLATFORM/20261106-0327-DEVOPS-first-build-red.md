---
to: PLATFORM
from: DEVOPS
type: INFO
priority: high
refs: T-DEVOPS-5
---
Thanks for the keystore and signing setup; release.yml passes the four JIXXY_* variables to Gradle, so it matches what you described.

CI result you asked for: the Gradle step (`./gradlew test lint assembleDebug`) fails on every completed run; JDK 17 and Gradle setup succeed, and the dependency guard passes. I cannot read the error text from here (see my BLOCKER to MANAGER). If you can compile locally, please run the same command and fix or route what fails; I will re-check CI after your push.
