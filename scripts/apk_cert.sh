#!/usr/bin/env bash
# Prints the SHA-256 digest of the APK's signing certificate (lowercase hex, nothing else on stdout).
# Usage: scripts/apk_cert.sh path/to/app.apk
# Needs apksigner from the Android SDK build-tools (present on GitHub's ubuntu runners).
set -euo pipefail

apk="${1:?usage: apk_cert.sh <apk>}"
[ -f "$apk" ] || { echo "apk_cert: file not found: $apk" >&2; exit 2; }

find_apksigner() {
  if command -v apksigner >/dev/null 2>&1; then command -v apksigner; return; fi
  local root
  for root in "${ANDROID_HOME:-}" "${ANDROID_SDK_ROOT:-}" /usr/local/lib/android/sdk; do
    [ -n "$root" ] && [ -d "$root/build-tools" ] || continue
    ls -1d "$root"/build-tools/*/apksigner 2>/dev/null | sort -V | tail -1
    return
  done
}

signer="$(find_apksigner || true)"
[ -n "$signer" ] || { echo "apk_cert: apksigner not found (need SDK build-tools)" >&2; exit 2; }

out="$("$signer" verify --print-certs "$apk" 2>&1)" || { echo "apk_cert: apksigner failed on $apk" >&2; echo "$out" >&2; exit 3; }
echo "$out" >&2   # full report goes to stderr (certificate DNs and digests are public data)

digests="$(printf '%s\n' "$out" | sed -n 's/^Signer #[0-9]* certificate SHA-256 digest: *//p' | tr 'A-F' 'a-f')"
count="$(printf '%s\n' "$digests" | grep -c . || true)"
[ "$count" -eq 1 ] || { echo "apk_cert: expected exactly 1 signer, found $count" >&2; exit 4; }
printf '%s\n' "$digests"
