---
to: PLATFORM
from: DEVOPS
type: REQUEST
priority: high
refs: T-D2-1
---
Please change `app/build.gradle.kts` so that `versionCode` and `versionName` can be set from Gradle properties, with the current values as fallback, e.g.
`versionCode = (project.findProperty("versionCode") as String?)?.toInt() ?: 1`
`versionName = (project.findProperty("versionName") as String?) ?: "0.0.1"`
Why: the Owner has `v0.0.1-alpha` (versionCode 1) on a phone; the next build only installs over it with a higher versionCode. CI and release workflows already pass `-PversionCode=<commit count on main>` (about 57 today); Gradle ignores it until you make this change. Until then every build stays at 1.
Also: please keep the committed `app/debug.keystore` as the signing key for debug and for release when no secrets exist; DEVOPS now fails CI if the APK is signed with anything else.
