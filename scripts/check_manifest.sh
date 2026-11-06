#!/usr/bin/env bash
# Inspects the MERGED debug manifest (includes library manifests) and fails on forbidden
# permissions/components. Turns "Jixxy cannot send your data anywhere" into a checked promise.
set -u
cd "$(git rev-parse --show-toplevel)"
M=$(find app/build/intermediates -path '*merged_manifests/debug*' -name AndroidManifest.xml 2>/dev/null | head -1)
[ -z "$M" ] && M=$(find app/build/intermediates -path '*merged_manifest*' -name AndroidManifest.xml 2>/dev/null | grep -i debug | head -1)
if [ -z "$M" ]; then
  echo "::error::Merged debug manifest not found under app/build/intermediates. Run assembleDebug first."
  exit 1
fi
echo "Checking $M"
FORBIDDEN='android.permission.INTERNET|ACCESS_NETWORK_STATE|QUERY_ALL_PACKAGES|READ_PHONE_STATE|ACCESS_FINE_LOCATION|ACCESS_COARSE_LOCATION|ACCESS_BACKGROUND_LOCATION|READ_CONTACTS|WRITE_CONTACTS|GET_ACCOUNTS|android.permission.CAMERA|RECORD_AUDIO|BIND_ACCESSIBILITY_SERVICE|android.accessibilityservice|BIND_NOTIFICATION_LISTENER_SERVICE|android.service.notification.NotificationListenerService'
hits=$(grep -nE "$FORBIDDEN" "$M" || true)
if [ -n "$hits" ]; then
  echo "::error::Forbidden permission/component in merged manifest:"
  echo "$hits"
  echo "Do not loosen this guard. Report to MANAGER (a library may have pulled it in)."
  exit 1
fi
echo "check_manifest: OK"
