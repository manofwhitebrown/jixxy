#!/usr/bin/env bash
# Runs INSIDE an emulator (android-emulator-runner script). Usage: emulator_smoke.sh <evidence_dir> <apk_dir>
# Installs the debug APK, grants the special permissions, launches the app, takes screenshots,
# saves logcat, and fails if the process crashed or is not running. Nothing here needs the network.
set -u
out="${1:-evidence}"; apkdir="${2:-apk}"; pkg="${PKG:-app.jixxy}"
mkdir -p "$out"; fail=0
note() { echo "== $*"; }
shot() { adb exec-out screencap -p > "$out/$1.png" 2>/dev/null || echo "::warning::screenshot $1 failed"; }

adb logcat -c
note "device"; adb shell getprop ro.build.version.sdk | tee "$out/sdk.txt"
apk=$(ls "$apkdir"/*.apk | head -1)
note "install $apk"; adb install -r -t "$apk" || { echo "::error::adb install failed"; exit 1; }
adb shell appops set "$pkg" SYSTEM_ALERT_WINDOW allow     || echo "::warning::SYSTEM_ALERT_WINDOW appops failed"
adb shell appops set "$pkg" PACKAGE_USAGE_STATS allow     || echo "::warning::PACKAGE_USAGE_STATS appops failed"
adb shell pm grant "$pkg" android.permission.POST_NOTIFICATIONS 2>&1 | tee -a "$out/grants.txt" || true
adb shell appops get "$pkg" > "$out/appops.txt" 2>&1
shot 00-home
note "launch"; adb shell monkey -p "$pkg" -c android.intent.category.LAUNCHER 1 > "$out/monkey.txt" 2>&1
sleep 8; shot 01-launched
adb shell pidof "$pkg" > "$out/pid.txt" || { echo "::error::app process not running after launch"; fail=1; }
adb shell dumpsys activity top | head -40 > "$out/activity_top.txt" 2>&1
adb shell input keyevent KEYCODE_HOME; sleep 3; shot 02-after-home
adb shell pidof "$pkg" >/dev/null || { echo "::error::app process died after Home"; fail=1; }
adb logcat -d > "$out/logcat.txt" 2>&1
if grep -E "FATAL EXCEPTION|ANR in $pkg|Process: $pkg.*died" "$out/logcat.txt" > "$out/crashes.txt"; then
  echo "::error::crash or ANR in logcat, see crashes.txt"; head -5 "$out/crashes.txt"; fail=1
fi
# Instrumented tests: only once PLATFORM has added app/src/androidTest and the Gradle deps.
if [ -d app/src/androidTest ]; then
  note "connectedDebugAndroidTest"
  ./gradlew :app:connectedDebugAndroidTest --stacktrace > "$out/connected.log" 2>&1 || { echo "::error::instrumented tests failed (see connected.log)"; tail -30 "$out/connected.log"; fail=1; }
else
  echo "::notice::no app/src/androidTest yet; only the adb smoke ran"
fi
exit $fail
