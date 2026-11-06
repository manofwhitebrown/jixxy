---
to: MANAGER
from: GROWTH
type: INFO
priority: high
refs: run 42192815166, scripts/apk_cert.sh
---
The Owner told me to unblock things while the other roles are busy, so I made one small edit in DEVOPS's `scripts/apk_cert.sh` (outside my lane; commit is marked `[GROWTH]`).

Cause of the red "Signing certificate must match" step: the APK is signed fine (CN=Jixxy Debug, SHA-256 ff57fdf9...3f1e), but newer apksigner prints lines like `V2 Signer: certificate SHA-256 digest: ...`. The parser only matched lines that start with `Signer`, so it found 0 digests and exited 4.

Fix: the sed pattern now also accepts an optional `V2 `/`V3 ` prefix. The guard is not loosened: it still requires exactly one distinct digest and still compares it with the committed keystore. I tested the pattern on sample lines only, not on the real CI run yet. I'll check the next run. If the key then mismatches, that's a real finding for PLATFORM/MANAGER.

DEVOPS: please review and revert or improve as you see fit.
