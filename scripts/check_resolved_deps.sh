#!/usr/bin/env bash
# Scans the RESOLVED dependency tree (what actually ships, transitive libraries included) for
# analytics, ads, crash, networking and Google Play services libraries. check_dependencies.sh only sees
# names written in Gradle files; this sees what those libraries pull in.
# Usage: scripts/check_resolved_deps.sh <file with `gradlew :app:dependencies --configuration releaseRuntimeClasspath` output>
set -u
f="${1:?usage: check_resolved_deps.sh <dependencies-output.txt>}"
[ -s "$f" ] || { echo "::error title=Resolved deps::dependency report $f is missing or empty"; exit 1; }
# Must look like a real report, otherwise an empty/odd file would "pass".
n=$(grep -cE '[A-Za-z0-9_.-]+:[A-Za-z0-9_.-]+:[0-9]' "$f" || true)
[ "$n" -ge 10 ] || { echo "::error title=Resolved deps::report has only $n coordinates; Gradle output not as expected"; exit 1; }
BANNED='com\.google\.firebase|firebase-|com\.google\.android\.gms|com\.google\.android\.play|com\.appsflyer|com\.facebook\.android|com\.adjust|io\.sentry|com\.bugsnag|com\.crashlytics|io\.fabric|com\.mixpanel|com\.amplitude|com\.segment|com\.squareup\.okhttp|com\.squareup\.retrofit|com\.android\.volley|io\.ktor:ktor-client|io\.grpc|org\.chromium\.net|org\.apache\.httpcomponents|com\.applovin|com\.unity3d\.ads|com\.google\.android\.ump|org\.matomo|com\.flurry|io\.branch|com\.onesignal|com\.clevertap|com\.moengage|com\.datadoghq|com\.newrelic|io\.embrace|com\.instabug'
hits=$(grep -nE "$BANNED" "$f" | cut -c1-200 | head -20 || true)
if [ -n "$hits" ]; then
  body=$(printf '%s' "$hits" | sed 's/%/%25/g' | awk '{printf "%s%%0A", $0}')
  echo "::error title=Resolved dependency guard::Banned library in the resolved dependency tree%0A${body}Do not loosen this guard. Find which dependency pulls it in (gradlew :app:dependencyInsight) and tell PLATFORM."
  echo "$hits"; exit 1
fi
echo "check_resolved_deps: OK ($n coordinates scanned)"
