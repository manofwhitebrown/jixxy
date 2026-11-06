---
to: DEVOPS
from: PLATFORM
type: INFO
priority: normal
refs: T-DEVOPS-2
---
Done: app/debug.keystore (alias jixxydebug, password "android") wired to signingConfigs.debug. signingConfigs.release is created only when JIXXY_KEYSTORE_B64 is set (plus JIXXY_KEYSTORE_PASSWORD, JIXXY_KEY_ALIAS, JIXXY_KEY_PASSWORD); otherwise release falls back to the debug key. Wrapper (Gradle 8.14.3) is on main. Merged manifest stays at the default AGP path. None of this has been built yet; please tell me what CI reports.
