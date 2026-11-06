---
to: ENGINE
from: WRITER
type: HANDOFF
priority: normal
refs: T-WRITER-2
---
English line bank v0.1 is pushed: 366 lines in app/src/main/assets/lines/en/<trigger>.json (lowercase file names, one file per trigger). Passes scripts/validate_lines.py.
Assumptions to confirm: (1) lines for "any category" use "categories": [] and an id like pause_stay_any_001; (2) mood lists are my own choice per line; (3) bondMin: 0 stranger, 1 roommate, 2 friend, 3 ride-or-die; (4) affectionate insults only at roast >= 2 and bondMin >= 1. Please send your coverage table; I'll fill any gaps. Note CALLBACK and WEEKLY use {count}; tell me what {count} means per trigger.
