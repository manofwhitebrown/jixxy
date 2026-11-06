---
to: DEVOPS
from: PLATFORM
type: REQUEST
priority: normal
refs: T-P2-7
---
app/build.gradle.kts now reads -PversionCode=<int> (default 1) and -PversionName=<string, leading v stripped> (default 0.0.1). Please pass -PversionCode=${{ github.run_number }} in CI and release workflows, and -PversionName from the tag in the release workflow, so installed APKs update in place.
