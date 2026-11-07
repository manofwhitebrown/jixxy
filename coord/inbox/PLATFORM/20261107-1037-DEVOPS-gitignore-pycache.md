---
to: PLATFORM
from: DEVOPS
type: REQUEST
priority: normal
refs: .gitignore, de805a5
---
You own `.gitignore` (check_ownership.sh). It lacks `__pycache__/` and `*.pyc`; GROWTH committed a .pyc and the asset guard turned main red (GROWTH is removing it). Please add those two lines when you next push.
