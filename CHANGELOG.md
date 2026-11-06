# Changelog

All notable changes to Jixxy are recorded here, following [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Release notes on GitHub are taken from this file. Entries are only added for things that exist in the repo; "known issues" are listed honestly per release.

## [Unreleased]

Work towards the first tester build, `v0.1.0`. Nothing here has been released.

### Added
- Project skeleton with `:engine`, `:character` and `:app` modules, plus CI, release and Pages workflows.
- Floating sprite service with a placeholder sprite; detectors for foreground app and system signals (screen, charger, battery, music); dispatcher and pause window stub.
- Character view skeleton with tap counting.
- On-device settings and state storage; event log kept for 30 days and clearable.
- Onboarding with in-character microcopy, a permissions flow, and brand-specific keep-alive guides (Xiaomi/Redmi/POCO, Oppo/Realme, Vivo, Samsung, stock Android).
- Home screen (nap/wake, today's numbers), Settings screen, and a "What Jixxy saw" screen showing the last 7 days with a button to erase the log.
- English line bank and microcopy for Jixxy's voice.
- README, privacy notice (draft), tester program plan, Play Store listing draft, launch content drafts, and a static landing page with no external requests.

### Known issues
- Most Android code has been written without being compiled or run on a device. Expect build errors and rough behaviour until CI and device testing catch up.
- As of this entry, CI on `main` is failing at the Gradle step (reported by DEVOPS and UI); a tester build can't be cut until it's green.
- The real pause card, the weekly review card and the full dispatch pipeline are not finished or verified.
- Keep-alive behaviour on aggressive phone brands (Xiaomi, Oppo, Vivo and others) is untested.

## [0.1.0] - not yet released

Planned first tester build: sprite you can drag, foreground-app detection, canned lines, nap, Pause card on up to 3 chosen apps, onboarding with permissions, settings, weekly review card, "What Jixxy saw" log, signed APK on GitHub Releases.

This section will be rewritten with what actually shipped, including known issues, when the release is cut.
