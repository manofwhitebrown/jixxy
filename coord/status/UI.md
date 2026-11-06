# UI status — updated 2026-11-06 03:25 UTC
## Now      — T-UI-3 Settings pushed; next T-UI-4 What Jixxy saw
## Done     — T-UI-0 skeleton (theme, routes/nav host, fakes, 6 placeholder screens with dark/light previews); design/ui/onboarding-flow.md
## Next     — T-UI-4 What Jixxy saw; T-UI-6 OEM guide; T-UI-5 weekly card
## Blockers — none (PLATFORM added nav/preview deps; waiting on PermissionsRepository/OemIntents final signatures)
## Questions for MANAGER — 0. UI unit tests live in app/src/test/java/app/jixxy/ui/** (outside my listed paths): OK? 1. Please create coord/TASKS.md + DECISIONS.md. 2. Confirm UI-side ports (PermissionsSource) are acceptable pending PLATFORM interfaces.
## Messages handled — 20261106-0322-WRITER-microcopy-v02.md, 20261106-0322-PLATFORM-fileprovider-added.md, 20261106-0319-PLATFORM-reply-interfaces.md, 20261106-0320-WRITER-microcopy-handoff.md
## Honest notes — T-UI-1 uses ENGINE's InMemorySettingsRepository (my own fake removed). Nothing compiled or run: no Gradle scaffold exists yet. Code assumes engine.api names from the brief (UserSettings, SettingsRepository). Fonts not bundled (no network); system font used. Onboarding strings now generated from MICROCOPY v0.1 (a few keys still [P], requested from WRITER); other screens still [P]. CI on 90f9366 was green before my T-UI-1 push; I have not verified CI for later commits.

- Verified for real: the pure-logic files (OnboardingFlow, HomeLogic) compile with kotlinc 2.1.21 together with the engine api (minus Interfaces.kt, which needs coroutines) and an assertion harness mirroring my JUnit tests passed 11/11. The JUnit files themselves and ALL Compose/Android files (screens, nav, theme) have NOT been compiled or run.
- CI: could not read job logs with my token; builds on 99d9eee and e0c06ef were red, cause unknown to me (may or may not be my files).
- Home uses a placeholder [P] string for the weekly shortcut (home_open_weekly); will request a WRITER key.
- Settings (T-UI-3): SettingsLogic compiled with kotlinc and an assertion harness mirroring SettingsLogicTest passed 9/9. The Settings Compose screen, TimePickerDialog use and nav wiring are NOT compiled or run.
- strings.xml is now generated: python3 design/ui/gen_strings.py (reads content/MICROCOPY.md v0.2; [P] keys listed in the script).
- Language is shown read-only (English only in v1). Settings uses the repositories passed in; PLATFORM's real DataStoreSettingsRepository/SqliteStateStore get wired when their holder (T-PLATFORM-5) exists.
