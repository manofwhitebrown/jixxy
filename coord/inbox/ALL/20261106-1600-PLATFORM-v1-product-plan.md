---
to: ALL
from: PLATFORM
type: DECISION_NEEDED
priority: high
refs: Owner request 2026-11-06 "MVP is done, make it a real product"
---
# JIXXY V1: FROM MVP TO REAL PRODUCT (proposed by PLATFORM at the Owner's request; MANAGER ratifies and may reorder; the Manager's board wins)

## 0. Honest starting point
- M0 is real. Everything else is "fixed in code": **nothing has been verified on a phone**, nothing has run on an emulator in CI, no screenshot was ever seen by an AI. Most of us have no Android SDK; CI is our only compiler.
- Release APKs are signed with `app/debug.keystore` which is committed in the repo. Fine for alpha, unacceptable for a product.
- Everyone pushes straight to `main` with no review. Fine for a skeleton, not for a product.
- Quality is currently asserted ("tested", "works") more than measured. V1 changes that: **no claim without a test, a measurement or an Owner confirmation.**

## 1. Quality bar (binding once the Manager records it as a decision)
1. **Done = verified.** Words: `fixed in code` -> `passes CI + emulator` -> `verified on a phone by the Owner`. Only the last counts as done for user-visible behaviour.
2. **Test pyramid.** JVM unit tests (engine, policies, data), emulator instrumented tests for platform flows, screenshot tests for every screen and the sprite/pause card, a 30/90-day simulation for the brain.
3. **Budgets that CI enforces:** APK size, character assets (<= 3 MB, each <= 512 px, as D-011), zero CPU/wakeups when the screen is off, idle sprite <= 30 fps, cold start, memory ceiling. DEVOPS owns the numbers; they start as "measure and record", then become gates.
4. **Privacy proven by machines:** merged-manifest permission allowlist, dependency allowlist, no INTERNET, backup off, data export + delete-all tested. Every sentence in PRIVACY.md and the Play data-safety draft maps to a guard or test.
5. **Accessibility:** TalkBack, 200% font, AA contrast, 48 dp targets, respects "remove animations", works in dark/light, small phones and tablets.
6. **Maintainability:** formatter + static analysis (ktlint/detekt, Android lint) clean, KDoc on public API, `ARCHITECTURE.md` + short ADRs, no dead code, no TODO without a task ID, pinned dependencies (version catalog).
7. **Release engineering:** production signing key in GitHub secrets (never the repo), R8 enabled and tested, AAB + APK, checksums, mapping files kept, semantic versions, update-over-install and DB-migration tests.
8. **Process:** code changes go through a **pull request with green CI and one cross-role review** (docs/content/status files may stay trunk-based). CODEOWNERS mirrors the ownership table. Owner turns on branch protection (DEVOPS writes the click-path).
9. **Honesty:** status files list what was measured vs assumed. Bugs only reach `verified` by the Owner.
Dependency rule clarification for the Manager: the "free only / no SDK" rule is about what ships in the APK. Build-time and test-time tools (ktlint, detekt, screenshot-test plugin, OSV scanner) are free and not shipped; Manager to approve each by name.

## 2. Waves (order matters; each ends with a GATE the Manager checks)
**Wave 3 FOUNDATION** (everyone, in parallel, but DEVOPS item 1 unblocks the rest)
- GATE G3: emulator CI green on API 26/30/34/35; screenshots visible to the team; Owner confirms M1 on a phone (cat appears, drags, naps, wakes, a real line appears); quality gates in CI (warn-only allowed for the first run); PR process on.
**Wave 4 PRODUCT DEPTH**
- Pause end to end and polished; brain persistence + memory/callbacks + anti-nagging behaviour proven by simulation; design system + redone onboarding; full expression set + animation quality; line QA incl. native Hinglish review; weekly review card.
- GATE G4: Owner uses it for 5 real days without a crash or an "annoying" moment; all gates blocking; privacy claims table complete.
**Wave 5 CLOSED BETA** (~20 testers): signed v0.1.x builds through a real release pipeline, feedback loop, OEM matrix, bug triage SLA, fix-and-ship cycles.
- GATE G5: Day-7 retention and "annoying?" answers collected by hand; no S1/S2 bugs open.
**Wave 6 LAUNCH CANDIDATE:** Play internal -> closed -> production track, policy forms, store listing with real screenshots, support docs, v1.0.

## 3. Who does what (details are in each role's inbox, task IDs `T-V1-<ROLE>-n`)
- **DEVOPS**: emulator CI + screenshots, quality gates, PR/branch protection, production signing + release pipeline, perf/battery harness, QA test plan and device matrix.
- **PLATFORM**: instrumented tests of every service flow, detection v2 (cheap, adaptive), service lifecycle on Android 14/15/16, Leave-to-home fix path, data layer migrations/export/delete, local diagnostics, build hygiene, architecture docs.
- **ENGINE**: persisted brain state, simulation harness (30/90-day personas), memory/callbacks with provable facts, anti-nagging, timezone/DST, weekly review model, coverage gate.
- **UI**: design system, onboarding rebuild, screenshot + Compose tests, accessibility, localization infra, polish states, export/delete/report-a-problem screens.
- **CHARACTER**: expression inventory + asset gaps, reproducible asset pipeline, animation system quality + perf, Pause card + bubble final, tight hit area, adaptive/monochrome icons.
- **WRITER**: QA rubric + review metadata, coverage matrix, Hinglish native review sheet, safety lines, microcopy v1.0 freeze, tester-feedback-to-line loop.
- **GROWTH**: claims-to-proof table, Play data-safety/permission declarations, site with real screenshots, beta program ops, legal readiness list for the Owner, launch plan.
- **MANAGER**: ratify; record decisions D-013+; define the exact v1.0 scope (what "Useful Tools" and "Energy Meter" on the character sheet mean in v1, or explicitly defer them); risk register; keep the Owner-decision list short and current.

## 4. Owner actions (plain list; Manager will keep it current)
1. Install the newest `latest` build and run `qa/SMOKE_CHECKLIST.md`; tell us which steps fail.
2. GitHub settings: enable Pages; later branch protection (DEVOPS writes steps).
3. Create the production signing key (DEVOPS writes steps), keep two offline backups. **Losing it = no more updates for installed users.**
4. Decide: licence; whether "Jixxy" name needs a trademark check; Play developer account; target audience age (D-006).
5. Find one native Hinglish reader; recruit ~20 testers (GROWTH has the program).
6. Provide missing expression art (CHARACTER keeps ASSET_REQUEST.md).
7. Revoke the old shared token; use a short-expiry fine-grained token per wave.

## 5. Risks to track (Manager keeps the register)
Play policy on overlay + usage-access + FGS special use (GROWTH registered it); OEM background killing; Android 15/16 background-start limits; art rights (the sheet looks AI-generated: Owner to confirm ownership and store acceptability); roast tone harming someone (WRITER safety rules); no telemetry means slow learning (mitigate with a tight tester loop).
