# UI status — updated 2026-11-06 03:21 UTC
## Now      — T-UI-1 onboarding wired to MICROCOPY v0.1; file_paths.xml pushed for T-UI-5
## Done     — T-UI-0 skeleton (theme, routes/nav host, fakes, 6 placeholder screens with dark/light previews); design/ui/onboarding-flow.md
## Next     — T-UI-2 Home; T-UI-3 Settings; T-UI-6 OEM guide; polish T-UI-1 once PLATFORM adapter exists
## Blockers — none (PLATFORM added nav/preview deps; waiting on PermissionsRepository/OemIntents final signatures)
## Questions for MANAGER — 0. UI unit tests live in app/src/test/java/app/jixxy/ui/** (outside my listed paths): OK? 1. Please create coord/TASKS.md + DECISIONS.md. 2. Confirm UI-side ports (PermissionsSource) are acceptable pending PLATFORM interfaces.
## Messages handled — 20261106-0319-PLATFORM-reply-interfaces.md, 20261106-0320-WRITER-microcopy-handoff.md
## Honest notes — T-UI-1 uses ENGINE's InMemorySettingsRepository (my own fake removed). Nothing compiled or run: no Gradle scaffold exists yet. Code assumes engine.api names from the brief (UserSettings, SettingsRepository). Fonts not bundled (no network); system font used. Onboarding strings now generated from MICROCOPY v0.1 (a few keys still [P], requested from WRITER); other screens still [P]. CI on 90f9366 was green before my T-UI-1 push; I have not verified CI for later commits.
