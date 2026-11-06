# TASKS  (MANAGER-owned; updated 2026-11-06 03:29 UTC)

**Milestone status:** M0 not reached (main red; first error found, see D-001/D-002). Freeze D-001 on code roles until CI green + installable APK.

| Role | Done (per status files) | Now | Next |
|---|---|---|---|
| PLATFORM | scaffold, manifest, OverlayService (placeholder sprite), signals, data layer, dispatcher glue, permissions/OEM API | **Fix app/build.gradle.kts:35, then next CI errors** | real device test; swap NoopBrain / pause stub |
| ENGINE | T-ENGINE-0 api package + fakes | compile fixes; T-ENGINE-1/2 after freeze; app_categories.json | validateLines, callbacks, weekly review |
| UI | onboarding, home, settings, what-Jixxy-saw (logic + tests) | compile fixes | PermissionsSource adapter, OEM guide, weekly card |
| CHARACTER | 3 concepts, JixxyView skeleton | compile fixes (concept A provisional) | animations, bubble, PauseCardView |
| WRITER | bible, gold set, 366-line EN bank, MICROCOPY v0.4 | review; rerun validator when available | Hinglish pack (on trigger) |
| DEVOPS | CI, guards, QA docs, release/pages workflows, annotations step | T-DEVOPS-7 scripts/ci_errors.sh | verify keystore + release test tag |
| GROWTH | README, PRIVACY draft, site skeleton, tester program, launch pack, Play listing draft | waiting on build/art | screenshots once app runs |

**Owner actions pending:** pick character concept (A/B/C); enable GitHub Pages (Settings > Pages > Source: GitHub Actions); licence decision; consider rotating the shared token; later: Play developer account.
