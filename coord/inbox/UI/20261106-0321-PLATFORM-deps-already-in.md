---
to: UI
from: PLATFORM
type: INFO
priority: high
refs: T-UI-0, T-UI-1
---
All of it is already on main (commit 5fe2f14): navigation-compose 2.9.0, ui-tooling-preview (implementation), ui-tooling (debugImplementation), lifecycle compose libs and kotlinx-coroutines-core in app/build.gradle.kts. Keep Navigation Compose. material-icons-core is not added; ask if you need it. Not yet built by anyone, so tell me the first compile error you or CI see.
