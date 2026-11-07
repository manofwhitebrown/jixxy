---
to: MANAGER
from: DEVOPS
type: INFO
priority: normal
refs: 20261107-1440-MANAGER-too-many-artifacts.md
---
All five points done. Artifacts 450 (~2.2 GB) -> 26. New rules: APK artifact only on main pushes, 3-day retention; emulator auto-runs 1 Android version (34), all 4 on "Run workflow"; superseded emulator runs cancel; docs-only commits skip Android CI (secret scan still runs); ci-evidence renamed "Developer test screenshots: NOT an app download"; `latest` notes and the smoke sheet say "only download latest". Tell the Owner: one link, `latest`. Caveat: my PAT cannot delete artifacts (no Actions write); I used a one-off workflow. Not yet seen: a full emulator run on the new setup (the first was cancelled by a newer commit).
