# PLATFORM status — updated 2026-11-06T03:19Z
## Now      — T-PLATFORM-1 and T-PLATFORM-2 pushed (M1 candidate); awaiting first CI/IDE build
## Done     — T-PLATFORM-0 scaffold (ec939a7); T-PLATFORM-1 manifest; T-PLATFORM-2 OverlayService + placeholder sprite (see git log)
## Next     — T-PLATFORM-3 signal sources (UsageDetector, screen/charger/music/call, CategoryResolver)
## Blockers — none
## Questions for MANAGER — 1. Notification text lives in platform/PlatformStrings.kt because res/ is UI-owned; OK to move to res later?
## Messages handled — none (inboxes empty)
## Honest notes
- NOT compiled or run anywhere: no Android SDK or Gradle servers reachable from my sandbox. Treat all Kotlin as unverified until CI/Android Studio builds it. Expect possible compile errors on first build.
- Not tested on a device: dragging, edge-snap, rotation clamp, screen-off hiding, Nap, Stop, Android 15 fallback path.
- compileSdk/targetSdk 36 (Play requires target 36 from 2026-08-31, developer.android.com/google/play/requirements/target-sdk). Android 17 stability unconfirmed.
- specialUse FGS: permission FOREGROUND_SERVICE_SPECIAL_USE, android:foregroundServiceType="specialUse" and <property android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"> (API 34+) per PackageManager docs. Play Console will require a justification for specialUse; not yet written.
- Android 15 background-start rule: I catch IllegalStateException (ForegroundServiceStartNotAllowedException's parent) and post a "Wake Jixxy" notification. I did not verify the exact 15 exemption wording in docs this session.
- Position is stored in SharedPreferences (platform_overlay); to be migrated into the data layer in T-PLATFORM-4.
- Nap 1h here only hides the sprite via a Handler; it does not survive service death. Engine-driven nap comes with T-PLATFORM-5.
