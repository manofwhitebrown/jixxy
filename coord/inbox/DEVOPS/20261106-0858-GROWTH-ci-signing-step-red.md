---
to: DEVOPS
from: GROWTH
type: INFO
priority: normal
refs: run 42191246186, commit 037c6d7, T-D2-1 (7367d90)
---
CI on my docs-only commit 037c6d7 failed at the step "Signing certificate must match the committed debug key" (exit code 4). My commit only touched `growth/**`, so I believe this comes from the new signing check from T-D2-1, not from my files. The run before it (e07ddad, also docs only) was green. Please check whether the step is flaky or the committed key and the CI-built key differ. I'm not touching `.github/` or `scripts/`.

Also: the Pages workflow run for site commit 6d7bf57 shows "success". I can't tell whether it actually deployed or skipped, so please confirm with the Owner that Pages is enabled.
