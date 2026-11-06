# Changelog

All notable changes to Jixxy are recorded here, following [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Release notes on GitHub are taken from this file. Entries are only added for things that exist in the repo; "known issues" are listed honestly per release.

## [Unreleased]

Work towards the first tester build, `v0.1.0`. Nothing here has been released.

### Changed
- Jixxy is now a black, fluffy, sarcastic cat (Owner's character sheet). The earlier placeholder concepts are retired.

### Fixed (on `main`, not yet in a release, not yet checked on a device)
- Nap, then Stop, then Wake could leave Jixxy hidden for the rest of the nap. An explicit Wake is meant to clear the nap now. Unit tests were added; device testing is pending.
- A Jixxy launcher icon replaces the default Android robot (code only; not seen on a phone).
- The real app screen is set as the launcher instead of the temporary 3-button screen (code only; not seen on a phone).
- The real brain is wired in, so lines can be chosen. The speech bubble display is still being finished, so Jixxy may still look silent.
- The sprite keeps its snapped screen edge after rotating the phone (code only).

### Planned for `v0.1.0`
The real app as the first screen, the cat art, the brain wired in so lines appear, the Pause card, onboarding, settings, weekly review card, signed APK. This list will be rewritten with what actually ships.

## [0.0.1-alpha] - 2026-11-06

First public build (milestone M0, "Skeleton"). Published on GitHub Releases. Installed by the Owner on a real Android phone.

### What it has
- Installs and opens on Android 8+.
- A temporary start screen with three buttons: Allow overlay, Wake Jixxy, Stop.
- A floating sprite service (overlay) with a placeholder shape; detectors for foreground app and system signals (screen, charger, battery, music); on-device settings and event storage (30-day retention).
- Project skeleton with `:engine`, `:character`, `:app`, CI, release and Pages workflows.
- Written but **not wired in or not visible yet:** onboarding, home, settings and "What Jixxy saw" screens; the English line bank (366 lines); the brain.

### Known issues
- **Placeholder sprite.** The Owner's verdict: "Jixxy looks like a potato." The cat art is not in this build.
- **Temporary launcher screen.** The real app screens are not the first screen.
- **Default launcher icon** (the Android robot).
- **Nap, then Stop, then Wake: Jixxy does not show up.** Reported by the Owner on a real phone. Likely cause from reading the code: the nap stays saved after Stop, so a later Wake hides the sprite. Not reproduced by us. Fixed on `main` after this release, not yet checked on a device.
- **Jixxy says nothing.** The line bank is not connected to the screen yet.
- **Pause card, weekly review card:** not working yet.
- Keep-alive behaviour on aggressive phone brands (Xiaomi, Oppo, Vivo and others) is untested.
- More bugs are expected from real-device testing; DEVOPS keeps the bug list in `qa/`.

## [0.1.0] - not yet released

Planned first tester build. This section is rewritten with what actually shipped when the release is cut.
