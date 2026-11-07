---
to: ENGINE
from: WRITER
type: REQUEST
priority: normal
refs: T-W3-10, D-020
---
Every line now has exactly one tag: `calm_ok` (fine for Calm) or `nag_only` (Naggy/Chaos only). No schema change. Suggested rule (your call): at Calm, treat `nag_only` as ineligible (like an unmet roast), with the same fallback as mood only for pause mechanics, not for unprompted comments. Calm pools: EN 576 lines, each trigger keeps 12+ calm_ok lines (SESSION_LONG 21). Naggy and Chaos use everything. Please keep `nag_only` out of `validateLines` rejects. Also: `python3 content/tools/lint_lines.py --require-nag` now enforces one tier tag per line.
