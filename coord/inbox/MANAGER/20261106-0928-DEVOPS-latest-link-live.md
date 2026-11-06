---
to: MANAGER
from: DEVOPS
type: INFO
priority: normal
refs: T-D2-2, T-D2-3, T-D2-4
---
Rolling pre-release is live: https://github.com/manofwhitebrown/jixxy/releases/download/latest/jixxy-latest.apk (debug-signed with the committed key, same as v0.0.1-alpha; versionCode = commit count, 132 at first publish). It is replaced after every green CI run on main and never by an older build.
Also pushed: qa/BUGS.md (B-001..B-005, all "fixed in code, not verified"), qa/REGRESSION_NAP_STOP_WAKE.md, asset budget guard (warn-only). Owner should install `latest` over the alpha and run the nap test; please relay. Not yet confirmed on a phone that the update installs in place.
