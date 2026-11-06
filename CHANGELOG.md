# Changelog

All notable changes to Jixxy are recorded here, following [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Release notes on GitHub are taken from this file. Entries are only added for things that exist in the repo; "known issues" are listed honestly per release.

## [Unreleased]

Work towards the first tester build, `v0.1.0`. Nothing here has been released.

### Added
- Project skeleton with `:engine`, `:character` and `:app` modules, and CI and release/pages workflows.
- Floating sprite service with a placeholder sprite, and detectors for foreground app and system signals (screen, charger, battery, music).
- Character view skeleton with tap counting.
- Onboarding flow skeleton with in-character microcopy.
- Gold line set and microcopy for Jixxy's voice.
- README, privacy notice (draft), and a static landing page with no external requests.

### Known issues
- Most Android code has been written without being compiled or run on a device. Expect build errors and rough behaviour until CI and device testing catch up.
- Pause cards, the weekly review card and the "What Jixxy saw" log are not finished.
- Keep-alive behaviour on aggressive phone brands (Xiaomi, Oppo, Vivo and others) is untested.

## [0.1.0] - not yet released

Planned first tester build: sprite you can drag, foreground-app detection, canned lines, nap, Pause card on up to 3 chosen apps, onboarding with permissions, settings, weekly review card, "What Jixxy saw" log, signed APK on GitHub Releases.

This section will be rewritten with what actually shipped, including known issues, when the release is cut.
