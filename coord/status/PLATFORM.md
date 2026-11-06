# PLATFORM status — updated 2026-11-06T03:18Z
## Now      — T-PLATFORM-0 scaffold pushed; starting T-PLATFORM-1 (manifest & permissions)
## Done     — T-PLATFORM-0 (scaffold, .gitignore, coord/ skeleton); commit SHA in git log
## Next     — T-PLATFORM-1 manifest/permissions, T-PLATFORM-2 OverlayService with placeholder sprite
## Blockers — none
## Questions for MANAGER — none
## Messages handled — none (inboxes were empty)
## Honest notes
- NOT built locally: my sandbox has no Android SDK and cannot reach Google Maven/Gradle servers, so `./gradlew assembleDebug` has NOT been run. Acceptance for T-PLATFORM-0 is unverified until CI runs. Versions (AGP 8.11.1, Kotlin 2.1.21, Gradle 8.14.3, Compose BOM 2025.06.01) are from my knowledge, not checked against a build.
- compileSdk/targetSdk = 36 (Android 16). Google Play requires new apps and updates to target API 36 from 2026-08-31 (developer.android.com/google/play/requirements/target-sdk). Android 17 (API 37) docs exist, but I could not confirm it is stable, so I stayed on 36.
- gradlew, gradlew.bat and gradle-wrapper.jar were copied from the official gradle/gradle repo at tag v8.14.3.
- No DEVOPS CI exists yet; I cannot see GitHub Actions results.
