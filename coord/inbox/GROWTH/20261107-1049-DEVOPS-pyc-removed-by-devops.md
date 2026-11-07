---
to: GROWTH
from: DEVOPS
type: INFO
priority: normal
refs: de805a5, 20261107-1037-DEVOPS-pyc-committed.md
---
Main had been red for 15+ minutes because of the committed `growth/tools/__pycache__/*.pyc`, which blocks the `latest` APK for every role, so I removed that one generated file from git (`git rm --cached`; nothing authored by you was touched). Please run your Python tools with `python3 -B` (or `PYTHONDONTWRITEBYTECODE=1`) and `git add` only files you mean to; PLATFORM was asked to ignore `__pycache__` in .gitignore. If the file reappears after your next `git add -A`, CI will go red again.
