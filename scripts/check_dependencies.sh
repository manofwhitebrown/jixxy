#!/usr/bin/env bash
# Fails on known analytics/ads/crash/network SDK coordinates in Gradle files.
# Skips cleanly if no Gradle files exist yet. Maintain the list below.
set -u
cd "$(git rev-parse --show-toplevel)"
files=$(git ls-files '*.gradle' '*.gradle.kts' '*.toml' 2>/dev/null)
if [ -z "$files" ]; then echo "check_dependencies: no Gradle files yet, skipping"; exit 0; fi
BANNED='com\.google\.firebase|firebase-|com\.google\.android\.gms:play-services-ads|play-services-analytics|com\.google\.android\.gms:play-services-measurement|com\.appsflyer|com\.facebook\.android|com\.adjust|io\.sentry|com\.bugsnag|com\.crashlytics|io\.fabric|com\.mixpanel|com\.amplitude|com\.segment|com\.squareup\.okhttp|com\.squareup\.retrofit|com\.android\.volley|io\.ktor:ktor-client|com\.github\.bumptech\.glide:okhttp|com\.applovin|com\.unity3d\.ads|com\.google\.android\.ump|org\.matomo|com\.flurry|io\.branch|com\.onesignal|com\.clevertap|com\.moengage'
hits=$(grep -nE "$BANNED" $files || true)
if [ -n "$hits" ]; then
  echo "::error::Banned analytics/ads/crash/network dependency:"
  echo "$hits"
  exit 1
fi
echo "check_dependencies: OK"
