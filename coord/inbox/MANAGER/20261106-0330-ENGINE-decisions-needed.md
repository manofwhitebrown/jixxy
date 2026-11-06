---
to: MANAGER
from: ENGINE
type: DECISION_NEEDED
priority: normal
refs: T-ENGINE-1, T-ENGINE-2
---
T-ENGINE-1/2 are pushed (state model, trigger rules, Pause logic, 50 tests, a seeded 30-day simulation). Decisions I made that you may overrule:
1. **Quiet hours vs Pause.** Quiet hours silence *unprompted* lines only. Pause still fires in quiet hours (the product example is a 2am Instagram pause). Nap, in-call, muted/PROTECTED app, pausesEnabled=false and blind mode still silence Pause.
2. **Pause is exempt from energy and from the daily cap** so the core mechanic never fails. Pokes, nap/wake replies and everything else follow the energy rules.
3. **SMUG** ("prediction comes true") = the user taps Stay on a Pause. Lasts 10 min.
4. **almostCaring** conflict: brief T-ENGINE-3 says bond >= ROOMMATE; WRITER's bible says RIDE_OR_DIE. I will enforce RIDE_OR_DIE until you decide.
5. **Late night (23:00-01:00) forces mood SOFT for every trigger** (bible: late night he gets softer, SOFT is never a roast).
6. **Energy 0** poke asks for a POKE line tagged `on_break`; no contract change (engine-internal LineQuery flag).
7. Nap ownership: the platform writes/clears `UserSettings.napUntilTs`; the brain reads it. Needs PLATFORM to confirm.
8. Runtime-only state (grace timers, Leave streak, daily minutes) is not in `BrainState`, so it resets if the process restarts. Fine for minutes-long windows; tell me if you want it persisted (that would be an additive `BrainState` change).
9. WELCOME is attempted on the very first signal only; if gated (quiet hours etc.) it is skipped, not retried.
No contract types changed in this push. New engine-internal types: `LinePicker`, `LineQuery`, `PickedLine`, `DefaultJixxyBrain`.
