---
to: ENGINE
from: PLATFORM
type: REQUEST
priority: high
refs: inbox/ALL/20261106-1600-PLATFORM-v1-product-plan.md (Owner-requested; Manager may reprioritise)
---
(PLATFORM covered engine work in Wave 2; the next ENGINE AI should read `coord/status/ENGINE.md` first. You own it again.)
**T-V1-ENGINE-1 Persist all runtime state (W3):** caps, last-comment times, de-dupe ring, bond, energy, pause history. Versioned `BrainState`; tests: save -> kill -> restore yields identical behaviour.
**-2 Simulation harness (W3):** seeded personas (night owl, heavy reels, light user, student with exams, traveller across time zones/DST) over 30 and 90 simulated days. Assert: <= 6 unprompted/day, >= 20 min apart, silent in quiet hours/calls/PROTECTED, no line repeats within N days, no Pause spam. Write a readable report (lines per day, repeats, Leave/Stay) CI uploads.
**-3 Anti-nagging (W4):** if the user chose Stay several times in a row, back off and offer to change settings; reactance guard; escalation tied to Roast Dial; nap respected. Prove with -2 personas.
**-4 Facts you can honestly claim (W4):** typed facts (stays_week, late_opens_week, longest_session_day, minutes_today...) and a test that every line's claim maps to a fact the engine actually has; coordinate list with WRITER.
**-5 Time (W4):** injectable clock everywhere; timezone change, DST, manual clock change, midnight rollover tests.
**-6 Weekly review model (W4):** pure data (facts only, no medical claims) with tests; UI renders it.
**-7 Tunable config (W4):** all thresholds in `assets/config/*.json` with schema validation and doc of the research rationale; no magic numbers in code.
**-8 API discipline (W3):** Kotlin explicit API mode in `:engine`, KDoc, `engine/API.md`, jacoco >= 85% (DEVOPS gates it). `:engine:validateLines` runs in CI and fails the build.
Status file: measured results only (kotlinc/gradle/JUnit actually run vs not).
