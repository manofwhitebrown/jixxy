#!/usr/bin/env bash
# Inspects the MERGED debug manifest (includes library manifests) and fails on forbidden
# permissions/components. Turns "Jixxy cannot send your data anywhere" into a checked promise.
set -u
# Usage: check_manifest.sh [debug|release]   (default debug; release needs :app:processReleaseManifest first)
cd "$(git rev-parse --show-toplevel)"
V="${1:-debug}"
M=$(find app/build/intermediates -path "*merged_manifests/$V*" -name AndroidManifest.xml 2>/dev/null | head -1)
[ -z "$M" ] && M=$(find app/build/intermediates -path '*merged_manifest*' -name AndroidManifest.xml 2>/dev/null | grep -i "$V" | head -1)
if [ -z "$M" ]; then
  echo "::error::Merged $V manifest not found under app/build/intermediates. Build it first (assembleDebug / processReleaseManifest)."
  exit 1
fi
echo "Checking $M"
FORBIDDEN='android.permission.INTERNET|ACCESS_NETWORK_STATE|QUERY_ALL_PACKAGES|READ_PHONE_STATE|ACCESS_FINE_LOCATION|ACCESS_COARSE_LOCATION|ACCESS_BACKGROUND_LOCATION|READ_CONTACTS|WRITE_CONTACTS|GET_ACCOUNTS|android.permission.CAMERA|RECORD_AUDIO|BIND_ACCESSIBILITY_SERVICE|android.accessibilityservice|BIND_NOTIFICATION_LISTENER_SERVICE|android.service.notification.NotificationListenerService|READ_EXTERNAL_STORAGE|WRITE_EXTERNAL_STORAGE|MANAGE_EXTERNAL_STORAGE|READ_MEDIA_'
# XML comments cannot declare anything, so they are blanked out before matching (line numbers kept).
# Every real <uses-permission>, <service>, <receiver> etc. is still checked.
STRIPPED=$(mktemp)
python3 - "$M" "$STRIPPED" <<'PY'
import re, sys
src = open(sys.argv[1], encoding="utf-8", errors="replace").read()
open(sys.argv[2], "w", encoding="utf-8").write(
    re.sub(r"<!--.*?-->", lambda m: "\n" * m.group(0).count("\n"), src, flags=re.S))
PY
hits=$(grep -nE "$FORBIDDEN" "$STRIPPED" | cut -c1-200 || true)
rm -f "$STRIPPED"
if [ -n "$hits" ]; then
  # One multi-line annotation (%0A = newline): the Checks API keeps only the ::error:: line itself.
  body=$(printf '%s' "$hits" | head -20 | sed 's/%/%25/g' | awk '{printf "%s%%0A", $0}')
  echo "::error title=Manifest guard::Forbidden permission/component in merged manifest (file: $M)%0A${body}Do not loosen this guard. Report to MANAGER: a library may have pulled it in."
  echo "$hits"
  exit 1
fi
# Privacy promise: nothing leaves the phone, so cloud/adb backup of app data must stay off.
if ! grep -q 'android:allowBackup="false"' "$M"; then
  echo "::error title=Manifest guard::android:allowBackup=\"false\" is missing in the merged $V manifest (file: $M). Do not loosen this guard; ask PLATFORM to set it."
  exit 1
fi
echo "check_manifest ($V): OK"
