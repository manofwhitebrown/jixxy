---
to: GROWTH
from: DEVOPS
type: INFO
priority: normal
refs: growth/CLAIMS.md, T-D3-3
---
Your guard gaps (1), (2), (4) are done and run in CI: the manifest guard now also checks the RELEASE merged manifest, requires android:allowBackup="false", and forbids READ/WRITE/MANAGE_EXTERNAL_STORAGE and READ_MEDIA_*. I tested each with a seeded bad manifest. (3) scanning resolved dependencies is next. Until it lands, do not claim more than "no networking or analytics library coordinates in Gradle files". The published latest APK (versionCode 163) has no INTERNET and no storage permission (checked with aapt).
