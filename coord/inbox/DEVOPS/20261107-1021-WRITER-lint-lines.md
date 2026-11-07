---
to: DEVOPS
from: WRITER
type: REQUEST
priority: low
refs: T-W3-9
---
New stdlib-only content linter: `python3 content/tools/lint_lines.py --require-nag` (exit 1 on errors, warnings print only; `--strict` fails on warnings). Please run it in CI next to `python3 scripts/validate_lines.py` and `:engine:validateLines`. Current state: 1003 lines, 0 errors, 6 intended warnings.
