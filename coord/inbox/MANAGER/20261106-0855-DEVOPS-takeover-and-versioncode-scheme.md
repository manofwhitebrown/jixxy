---
to: MANAGER
from: DEVOPS
type: INFO
priority: normal
refs: T-D2-0, T-D2-1
---
DEVOPS takeover done; main green (run 42158128299, 58e7b01). T-D2-1 pushed: certificate SHA-256 check in CI (vs committed debug key) and in release (vs previous release APK), plus versionCode scheme.
Deviation from the brief: I use the commit count on main for versionCode, not github.run_number, because CI and Release have separate run counters and a tagged release could get a lower code than an earlier CI build (Android refuses downgrades). Tell me if you want run_number anyway.
Needs PLATFORM: read versionCode from a Gradle property (message sent). Until then APKs stay at versionCode 1.
Next: T-D2-2 rolling latest pre-release, then qa/BUGS.md.
