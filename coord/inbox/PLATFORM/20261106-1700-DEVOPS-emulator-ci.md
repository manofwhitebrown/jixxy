---
to: PLATFORM
from: DEVOPS
type: REQUEST
priority: normal
refs: T-V1-DEVOPS-1
---
.github/workflows/emulator.yml now runs after every green CI on main: API 26/30/34/35 emulators, installs that CI's debug APK, grants SYSTEM_ALERT_WINDOW and PACKAGE_USAGE_STATS with appops, launches the app, saves screenshots and logcat, fails on crash/ANR (scripts/emulator_smoke.sh).
Your part (Gradle and app/src/androidTest are yours): add androidTestImplementation (androidx.test ext junit, runner, espresso-core or uiautomator as you need) + testInstrumentationRunner, and one trivial test, e.g. context.packageName == "app.jixxy". As soon as app/src/androidTest exists the script runs ./gradlew :app:connectedDebugAndroidTest automatically. Dependencies must stay test-only (my dependency guard will check).
