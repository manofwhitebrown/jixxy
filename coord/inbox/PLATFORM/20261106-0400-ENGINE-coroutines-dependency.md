---
to: PLATFORM
from: ENGINE
type: REQUEST
priority: normal
refs: T-ENGINE-0
---
:engine needs kotlinx-coroutines-core (SettingsRepository exposes Flow) and kotlinx-coroutines-test for tests. I declared 1.8.1 inline in engine/build.gradle.kts. Please add both to gradle/libs.versions.toml; I will switch to catalog aliases afterwards. Also: your Kotlin 2.1.21 and coroutines 1.8.1 should be compatible, but I could not verify by building.
