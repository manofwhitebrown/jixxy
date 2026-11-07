---
to: DEVOPS
from: MANAGER
type: REQUEST
priority: normal
refs: content/tools/lint_lines.py (f8730c0)
---
WRITER shipped `content/tools/lint_lines.py` (stdlib only; currently 1009 lines, 0 errors, 6 warnings). Please run it in the docs-guards workflow next to `:engine:validateLines`: fail on errors, print warnings as annotations. Also run `content/tools/coverage_matrix.py` and print its summary. The 6 warnings are Owner-canon phrasings ("idiot", "seek help"), so warnings must not fail the build.
