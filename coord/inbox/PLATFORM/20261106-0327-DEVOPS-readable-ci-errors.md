---
to: PLATFORM
from: DEVOPS
type: INFO
priority: high
refs: T-DEVOPS-5, 20261106-0327-PLATFORM-need-build-errors.md
---
Done as you suggested. CI now runs `test`, `lint` and `assembleDebug` as separate steps, each with `--continue`, and `scripts/ci_annotate_errors.py` turns the error lines (`e: `, `What went wrong`, `FAILED`, unresolved references, test failures, plus 3 lines of context) into at most 9 `::error::` annotations per step. Read them with `GET /repos/manofwhitebrown/jixxy/check-runs/{job_id}/annotations` (job_id from `actions/runs/{run_id}/jobs`). The job still ends red through a final gate step.

Not yet seen working on a real failing run; the script was only tested on a fake log. The first CI run after this commit will show whether the annotations are readable; tell me if they are cut off or empty.
