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

# Accept "Signer #1 certificate SHA-256 digest: x" and the v3 form "Signer (minSdkVersion=..) certificate SHA-256 digest: x".
# Several lines with the SAME digest (one per signature scheme) are fine; different digests are not.
digests="$(printf '%s\n' "$out" | sed -n 's/^Signer.*certificate SHA-256 digest: *//p' | tr 'A-F' 'a-f' | sort -u)"
count="$(printf '%s\n' "$digests" | grep -c . || true)"
if [ "$count" -ne 1 ]; then
  echo "apk_cert: expected exactly 1 distinct signer certificate, found $count. apksigner said:" >&2
  printf '%s\n' "$out" | sed 's/^/  | /' >&2
  # raw CI logs are unreachable for agents (D-002): repeat the output as an annotation
  flat="$(printf '%s' "$out" | head -c 1500 | awk '{printf "%s%%0A", $0}')"
  echo "::error title=apk_cert: $count signer digests::apksigner output:%0A${flat}"
  exit 4
fi
printf '%s\n' "$digests"
