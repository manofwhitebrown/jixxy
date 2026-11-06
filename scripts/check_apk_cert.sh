#!/usr/bin/env bash
# Compares the signing certificate of an APK with an expected one.
# Usage:
#   check_apk_cert.sh <apk> --apk <other.apk>     expected = cert of another APK
#   check_apk_cert.sh <apk> --keystore <file> <alias> <storepass>   expected = cert in a keystore
#   check_apk_cert.sh <apk> --sha <hex>           expected = given SHA-256 digest
# Prints the digest as a GitHub notice and exits 1 on mismatch.
set -euo pipefail
here="$(cd "$(dirname "$0")" && pwd)"

apk="${1:?usage: check_apk_cert.sh <apk> --apk|--keystore|--sha ...}"; shift
actual="$("$here/apk_cert.sh" "$apk" 2>/dev/null)"
echo "::notice title=APK signing certificate::$(basename "$apk") SHA-256 $actual"

mode="${1:-}"; shift || true
case "$mode" in
  --apk)      expected="$("$here/apk_cert.sh" "${1:?other apk}" 2>/dev/null)";;
  --sha)      expected="$(printf '%s' "${1:?hex}" | tr 'A-F' 'a-f' | tr -d ': ')";;
  --keystore)
    expected="$(keytool -list -v -keystore "${1:?keystore}" -alias "${2:?alias}" -storepass "${3:?pass}" 2>/dev/null \
      | sed -n 's/^ *SHA256: *//p' | tr -d ':' | tr 'A-F' 'a-f' | head -1)";;
  *) echo "check_apk_cert: unknown mode '$mode'" >&2; exit 2;;
esac
[ -n "$expected" ] || { echo "check_apk_cert: could not determine expected certificate" >&2; exit 2; }

if [ "$actual" != "$expected" ]; then
  echo "::error title=Signing certificate mismatch::$(basename "$apk") is signed with $actual but $expected was expected. Phones will refuse to install this build over the previous one."
  exit 1
fi
echo "Signing certificate matches: $actual"
