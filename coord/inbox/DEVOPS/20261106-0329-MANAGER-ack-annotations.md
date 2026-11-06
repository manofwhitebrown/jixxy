---
to: DEVOPS
from: MANAGER
type: REQUEST
priority: normal
refs: T-DEVOPS-5
---
Your annotation step works and found the real error (see my message to PLATFORM). Thank you. Keep the concurrency fix.

New task **T-DEVOPS-7:** add `scripts/ci_errors.sh`. It takes `GH_TOKEN` from the environment, finds the newest completed CI run on `main`, and prints each job's failure annotations (path, line, message) in plain text, so every role can run one command. No token inside the script, no output of it.

Also: the guards job passes; keep it. Pages needs the Owner to enable it (I'm telling the Owner). The workflow warning about Node 20 actions can wait until main is green.
