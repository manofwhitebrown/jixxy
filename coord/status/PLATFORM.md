# PLATFORM status — updated 2026-11-06T03:25Z
## Now      — T-PLATFORM-6 pushed; first wave complete, nothing built or run yet
## Done     — T-PLATFORM-0 scaffold (ec939a7); T-PLATFORM-1 manifest; T-PLATFORM-2 OverlayService + placeholder sprite (see git log)
## Next     — fix whatever CI/first build reports; swap NoopBrain and PauseWindow stub when ENGINE/CHARACTER publish real ones; real device test of Leave->Home and OEM kill behaviour
## T-3 notes
- Files: platform/signals/{SignalBus,CategoryRules,CategoryResolver,UsageDetector,SystemSignals}.kt; unit tests CategoryRulesTest (never run).
- Assumed app_categories.json schema {"overrides":{pkg:CATEGORY},"protected":[pkg]}: ENGINE has not pushed the file; please confirm or send the real schema.
- Own package is treated as PROTECTED. ApplicationInfo.CATEGORY_MAPS maps to PROTECTED; messaging/browser/shopping depend on the JSON overrides (Android has no such category).
- Detectors are not yet started by OverlayService; that wiring is T-PLATFORM-5.
- Usage polling stops while screen is off; heartbeat and audio poll also stop then (no wake-ups). Heartbeat uses Handler, not alarms.
- Unverified: queryEvents behaviour on real OEM devices, receiver registration rules on targetSdk 36 (screen/power broadcasts are protected system broadcasts, so I believe no export flag is needed, not checked in docs).
## T-4 notes
- settings: app/data/DataStoreSettingsRepository (Preferences DataStore 1.1.7). state/events: app/data/SqliteStateStore on plain SQLite (chose it over Room to avoid KSP I cannot test). logEvent is a no-op while isBlind() is true. Purge: every 200 writes and via purgeExpired(); clearEvents deletes rows then VACUUM.
- Only retentionCutoff() has a unit test. SQLite and DataStore code is untested (no Robolectric/instrumented tests yet).
- Overlay position still in SharedPreferences "platform_overlay"; not migrated, low priority.
- DataStore 1.1.7 and the rest of the catalog versions are from memory, unverified.
 — none
## Questions for MANAGER — 1. Notification text lives in platform/PlatformStrings.kt because res/ is UI-owned; OK to move to res later?
## Messages handled — 20261106-0318-DEVOPS-debug-keystore.md, 20261106-0318-UI-interfaces-needed.md (replied; FileProvider and PermissionsRepository pending), 20261106-0400-ENGINE-coroutines-dependency.md, 20261106-0320-UI-deps-exact.md, 20261106-0321-CHARACTER-jixxyview-handoff.md, 20261106-0321-UI-fileprovider-paths.md, 20261106-1300-GROWTH-queries-visibility.md
## Honest notes
- NOT compiled or run anywhere: no Android SDK or Gradle servers reachable from my sandbox. Treat all Kotlin as unverified until CI/Android Studio builds it. Expect possible compile errors on first build.
- Not tested on a device: dragging, edge-snap, rotation clamp, screen-off hiding, Nap, Stop, Android 15 fallback path.
- compileSdk/targetSdk 36 (Play requires target 36 from 2026-08-31, developer.android.com/google/play/requirements/target-sdk). Android 17 stability unconfirmed.
- specialUse FGS: permission FOREGROUND_SERVICE_SPECIAL_USE, android:foregroundServiceType="specialUse" and <property android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"> (API 34+) per PackageManager docs. Play Console will require a justification for specialUse; not yet written.
- Android 15 background-start rule: I catch IllegalStateException (ForegroundServiceStartNotAllowedException's parent) and post a "Wake Jixxy" notification. I did not verify the exact 15 exemption wording in docs this session.
- Position is stored in SharedPreferences (platform_overlay); to be migrated into the data layer in T-PLATFORM-4.
- Nap 1h here only hides the sprite via a Handler; it does not survive service death. Engine-driven nap comes with T-PLATFORM-5.
