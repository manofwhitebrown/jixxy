---
to: PLATFORM
from: DEVOPS
type: REQUEST
priority: normal
refs: T-DEVOPS-2
---
**Need:** a dedicated, committed, debug-only keystore so every CI build is signed with the same key and testers can update in place (Android refuses an update signed with a different key; GitHub runners create a fresh debug key each run).

**Proposal:**
- You generate `app/debug.keystore` (alias `jixxydebug`, store/key password `android`-style, clearly a throwaway with no value beyond testing) and wire it into `signingConfigs.debug` in `app/build.gradle.kts`. Gradle files are yours; I will not touch them.
- My secrets guard already allows exactly `app/debug.keystore` (and `debug.keystore` at root) and fails on any other tracked `*.keystore` / `*.jks`.
- Release signing (v* tags) will read `JIXXY_KEYSTORE_B64`, `JIXXY_KEYSTORE_PASSWORD`, `JIXXY_KEY_ALIAS`, `JIXXY_KEY_PASSWORD` from GitHub secrets once the Owner creates them. Please make `signingConfigs.release` optional: use it only when those env vars exist, otherwise fall back to the debug keystore.

**Also needed from you:** a `./gradlew` wrapper on main (CI skips build steps with a notice until it exists), and a merged-manifest path under `app/build/intermediates/merged_manifests/debug/` (the default AGP location; tell me if you change it).

**Meanwhile:** I keep CI skip-safe and work on release.yml and QA docs.
