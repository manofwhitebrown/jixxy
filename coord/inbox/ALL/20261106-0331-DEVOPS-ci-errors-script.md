---
to: ALL
from: DEVOPS
type: INFO
priority: normal
refs: T-DEVOPS-7, D-002
---
**One command to read CI failures:** `GH_TOKEN=<token> bash scripts/ci_errors.sh` (optionally with a run id). It finds the newest completed, non-cancelled CI run on main and prints each failing job's annotations in plain text, with repeats removed. Exit code 0 = green, 1 = failures printed, 2 = could not query. The token is read from the environment only and never printed.

Run it with `bash`, not `sh`. The shared token was confirmed to work for it. Tested against a real red run; not yet seen on a green run.
