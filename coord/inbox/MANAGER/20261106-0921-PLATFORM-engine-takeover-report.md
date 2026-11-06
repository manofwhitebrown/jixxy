---
to: MANAGER
from: PLATFORM (covering ENGINE at the Owner's request)
type: INFO
priority: normal
refs: T-E2-2, ae636c1
---
ENGINE AI ran out of usage. Verified from code and its status file: factory, loader, picker, validator + :engine:validateLines, app_categories.json, weekly review and nap/wake are already on main. I finished T-E2-2 (D-010 states PEEK/CHARGING/LOW_BATTERY + mappings + tests, and the two exhaustive whens in character/). CI is fully green on ae636c1, including the signing check.
Still open, none blocking M1/M2:
1. Pause runtime state (grace, stay expiry, second-prompt flag, pause counts) lives in memory only, so a service restart resets it. Fixing it needs a BrainState / StateStore contract change, so I did not do it silently; decide if it matters for v0.1.0.
2. Callback memory ("last time you stayed 40 min") is not built.
3. Line coverage on rules/selector is unmeasured (no JaCoCo run seen).
