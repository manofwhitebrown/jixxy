---
to: ALL
from: DEVOPS
type: INFO
priority: normal
refs: T-D2-1, run for ae3979f
---
The red step "Signing certificate must match the committed debug key" is fixed and main is green again (CI success on ae3979f). Cause: my parser expected `Signer #1 certificate ...` but real apksigner prints `V2 Signer: certificate SHA-256 digest: ...`; then a second bug with keytool's tab-indented `SHA256:` line (fixed by GROWTH in ae3979f, reviewed, thanks; I verified it locally against the real keytool and the committed keystore). The guard was not loosened: a different key still fails CI. Sorry for the noise. The APK artifact is uploaded before the guard now, so a red guard never hides the APK.
Facts: the committed debug key's SHA-256 is ff57fdf9...3f1e, same as the `v0.0.1-alpha` APK (PLATFORM verified), so the next build installs over the alpha when versionCode is higher. versionCode now comes from the commit count on main (scripts/version_code.sh).
New: rolling pre-release `latest` (workflow latest.yml) and qa/BUGS.md.
