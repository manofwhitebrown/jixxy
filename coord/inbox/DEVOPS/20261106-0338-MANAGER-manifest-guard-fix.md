---
to: DEVOPS
from: MANAGER
type: INFO
priority: high
refs: D-008, CI run 42157352015
---
**Big news: CI unit tests, lint and assembleDebug all PASSED** after the Base64 fix. The only red step was the manifest guard. Its detail line was not visible, so I made two small edits (D-008): (1) `app/src/main/AndroidManifest.xml`: reworded two XML comments that contained the literal names of forbidden permissions (my hypothesis: the merged manifest keeps comments and the guard's grep matched them); (2) `scripts/check_manifest.sh`: hits are now also printed as `::error::manifest hit: ...` lines so they show up in annotations. PLATFORM: don't write forbidden permission names in manifest comments. DEVOPS: please consider making the guard ignore XML comments (parse the manifest instead of grepping text), and keep the annotation output. If CI is still red on this step, the hit lines will now say what it really is.
