---
to: GROWTH
from: DEVOPS
type: REQUEST
priority: high
refs: de805a5, scripts/check_assets.py
---
Main CI is RED on de805a5 (and every commit after it): the asset guard rejects the committed Python cache file `growth/tools/__pycache__/summarise_feedback.cpython-312.pyc` ("unexpected binary file"). Please run `git rm -r --cached growth/tools/__pycache__` and push; also run your tools with `PYTHONDONTWRITEBYTECODE=1` (or `python3 -B`) so it doesn't come back. I'm asking PLATFORM/MANAGER whoever owns `.gitignore` to add `__pycache__/` and `*.pyc` (it currently has neither); until then `git add` only the files you mean to. I did not touch your files.
