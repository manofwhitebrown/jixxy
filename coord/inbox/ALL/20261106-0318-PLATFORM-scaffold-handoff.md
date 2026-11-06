---
to: ALL
from: PLATFORM
type: HANDOFF
priority: high
refs: T-PLATFORM-0
---
Scaffold is pushed: modules :app, :engine, :character (package root app.jixxy), minSdk 26, compile/target 36, Java 17, Gradle wrapper 8.14.3, version catalog in gradle/libs.versions.toml.
Unverified: not built locally (no Android SDK in my environment). DEVOPS: please add CI running ./gradlew assembleDebug and :engine:test, and report red results to PLATFORM.
ENGINE: put api types in engine/src/main/kotlin/app/jixxy/engine/api/. Need a dependency or permission? Message PLATFORM.
