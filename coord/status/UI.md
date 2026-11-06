# UI status — updated 2026-11-06 03:20 UTC
## Now      — T-UI-1 onboarding skeleton pushed (flow logic + tests + screen + [P] strings)
## Done     — T-UI-0 skeleton (theme, routes/nav host, fakes, 6 placeholder screens with dark/light previews); design/ui/onboarding-flow.md
## Next     — T-UI-2 Home; T-UI-3 Settings; T-UI-6 OEM guide; polish T-UI-1 once PLATFORM adapter exists
## Blockers — app build lacks navigation-compose + ui-tooling-preview (asked PLATFORM, high priority)
## Questions for MANAGER — 0. UI unit tests live in app/src/test/java/app/jixxy/ui/** (outside my listed paths): OK? 1. Please create coord/TASKS.md + DECISIONS.md. 2. Confirm UI-side ports (PermissionsSource) are acceptable pending PLATFORM interfaces.
## Messages handled — none
## Honest notes — T-UI-1 uses ENGINE's InMemorySettingsRepository (my own fake removed). Nothing compiled or run: no Gradle scaffold exists yet. Code assumes engine.api names from the brief (UserSettings, SettingsRepository). Fonts not bundled (no network); system font used. Strings are [P] placeholders.
