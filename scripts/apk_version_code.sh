#!/usr/bin/env bash
# Prints the versionCode stored INSIDE an APK (digits only on stdout). Used by release.yml/latest.yml so the
# number we compare and publish is what Android will see, not what we think we passed to Gradle.
# Usage: scripts/apk_version_code.sh path/to/app.apk      Needs aapt2 or aapt (Android SDK build-tools).
set -euo pipefail
apk="${1:?usage: apk_version_code.sh <apk>}"
[ -f "$apk" ] || { echo "apk_version_code: file not found: $apk" >&2; exit 2; }

find_tool() {
  local t root
  for t in aapt2 aapt; do
    if command -v "$t" >/dev/null 2>&1; then command -v "$t"; return; fi
  done
  for root in "${ANDROID_HOME:-}" "${ANDROID_SDK_ROOT:-}" /usr/local/lib/android/sdk; do
    [ -n "$root" ] && [ -d "$root/build-tools" ] || continue
    for t in aapt2 aapt; do
      ls -1d "$root"/build-tools/*/"$t" 2>/dev/null | sort -V | tail -1 | grep . && return
    done
  done
}

tool="$(find_tool || true)"
[ -n "$tool" ] || { echo "apk_version_code: neither aapt2 nor aapt found" >&2; exit 3; }
code="$("$tool" dump badging "$apk" 2>/dev/null | sed -n "s/^package:.* versionCode='\([0-9][0-9]*\)'.*/\1/p" | head -1 || true)"
[ -n "$code" ] || { echo "apk_version_code: could not read versionCode from $apk" >&2; exit 4; }
echo "$code"
