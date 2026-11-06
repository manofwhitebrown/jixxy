# DECISIONS  (MANAGER-owned, append-only)

- **D-001 (2026-11-06 03:29 UTC)** Compile-green freeze: code roles fix only compile/test/lint errors until main is green. Non-code roles continue.
- **D-002 (2026-11-06 03:29 UTC)** CI failure annotations (via check-runs API) are the official error channel; raw logs are unreachable from our sandboxes.
- **D-003 (2026-11-06 03:29 UTC)** Contract amendments accepted: ENGINE's extra types (ReactionKind, PauseChoiceKind, ReactionKindOut, BrainState, BrainSnapshot, UsageEvent/UsageEventType) and BrainConfig; see CONTRACTS.md.
- **D-004 (2026-11-06 03:29 UTC)** Character concept A (Blanket Bean) is provisional until the Owner chooses; art swap is cheap (same layer ids).
- **D-005 (2026-11-06 03:29 UTC)** app_categories.json schema: {"overrides":{pkg:CATEGORY},"protected":[pkg]}.
- **D-006 (2026-11-06 03:29 UTC)** Provisional: v1 audience 18+, Play category not Health & Fitness. Licence and paid developer account: Owner decisions, pending.
- **D-007 (2026-11-06 03:29 UTC)** Tests live with their owner's package under app/src/test/java/app/jixxy/<ui|platform|data>/**.

- **D-008 (2026-11-06 03:34 UTC)** While PLATFORM is unavailable, MANAGER may make minimal compile-error fixes in PLATFORM-owned paths, each logged in PLATFORM's inbox. Features stay frozen.

- **D-009 (2026-11-06 03:41 UTC)** M0 reached: main is green (run 42157822863) and a debug APK artifact exists. Compile-green freeze D-001 is **lifted**. New rule: never push to main a change that you know turns CI red; check annotations after every push (scripts/ci_errors.sh) and fix your own breakage first. D-008 (Manager fixes in PLATFORM paths) stays in force only while PLATFORM is unavailable.
