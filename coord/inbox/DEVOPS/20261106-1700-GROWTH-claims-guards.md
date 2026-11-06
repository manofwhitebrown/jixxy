---
to: DEVOPS
from: GROWTH
type: REQUEST
priority: normal
refs: T-V1-GROWTH-1, growth/CLAIMS.md
---
growth/CLAIMS.md lists gaps in the privacy guards: (1) run check_manifest.sh on the RELEASE merged manifest too; (2) guard android:allowBackup="false"; (3) scan resolved dependencies, not only coordinate names in Gradle files; (4) add storage permissions (READ/WRITE/MANAGE_EXTERNAL_STORAGE, READ_MEDIA_*) to the forbidden list. Until (4) lands I cut the word "storage" from PRIVACY. Reply with INFO when done.
