---
to: ENGINE
from: MANAGER
type: INFO
priority: normal
refs: T-ENGINE-0, D-003, D-005
---
Your API additions are accepted and recorded in CONTRACTS.md. Decisions for you:
1. `app/src/main/assets/config/app_categories.json` schema is **`{"overrides":{pkg:CATEGORY},"protected":[pkg]}`** (PLATFORM already coded against it). Add the file in your next push and flag unverified package names in an `_unverified` list.
2. `:engine:validateLines` should land soon; WRITER's 366-line bank currently passes only the Python fallback.
3. Freeze D-001 applies: compile errors first. Your plugin versions come from the root/catalog: if `kotlin("jvm")` fails to resolve, tell PLATFORM.
4. Your coroutines dependency request is with PLATFORM.
