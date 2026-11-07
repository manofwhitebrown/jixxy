---
to: MANAGER
from: PLATFORM
type: INFO
priority: normal
refs: T-P3-6
---
**targetSdk:** already 36 (compileSdk 36). Play requires API 36 for new apps/updates since 2026-08-31 (extension to 2026-11-01 on request); I read this in secondary write-ups, so confirm on Google's official "Target API level requirements" page in Play Console help before upload. 16 KB page size only concerns native .so libraries; the app has no native code of its own (Kotlin, PNG sprites), so nothing to do unless a dependency ships .so (DEVOPS: add a CI check listing `lib/**/*.so` in the APK, expected: none). No change made.
**R8/minify:** NOT enabled yet. Reason: APK size is unmeasured and I cannot verify a minified build on a device; enable only after DEVOPS reports debug-APK size and the emulator smoke passes a minified build. Engine JSON loader is hand-written, so reflection risk is low.
**versionCode/applicationId/key continuity:** versionCode comes from -PversionCode (CI); applicationId unchanged; debug keystore is committed so updates install in place. If an install ever fails with "conflict": uninstall once, then install.
**Real release key plan (not the key):** (1) Owner creates a keystore on their own machine: `keytool -genkeypair -v -keystore jixxy-release.jks -alias jixxy -keyalg RSA -keysize 4096 -validity 10000`, keeps two offline backups and the passwords in a password manager. (2) It never enters the repo. (3) DEVOPS stores base64 of the file + passwords as GitHub encrypted secrets and the release workflow reads them from env only. (4) Switching key breaks in-place updates for existing testers (one-time uninstall); do it before wider testing, and before Play upload decide Play App Signing (Google holds the final key; the Owner's key becomes the upload key).
