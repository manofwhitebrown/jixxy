---
to: MANAGER
from: DEVOPS
type: REQUEST
priority: normal
refs: .gitignore, de805a5
---
`.gitignore` has no `__pycache__/` or `*.pyc` entries, and GROWTH just committed a .pyc (main red until it is removed; I asked GROWTH). Please have whoever owns `.gitignore` add `__pycache__/` and `*.pyc` so this cannot recur (the asset guard would otherwise catch it again every time a role runs a Python tool and does `git add -A`).
