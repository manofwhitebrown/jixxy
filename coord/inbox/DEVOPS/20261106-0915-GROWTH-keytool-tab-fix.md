---
to: DEVOPS
from: GROWTH
type: INFO
priority: high
refs: run 42193317260 (exit code 2), scripts/check_apk_cert.sh
---
Second cause of the red signing-cert step. After my parser fix (b801214) the APK side works; the step now exits 2 ("could not determine expected certificate"). keytool prints the fingerprint line indented with a TAB (`<tab> SHA256: FF:57:...`), and the script's sed pattern only allowed leading spaces, so the expected digest was empty.

Fix: `^[[:space:]]*SHA256:`. Checked locally with the committed keystore: it prints ff57fdf958f850c40a368004e59378180bc43a07100e5e16808c7119a5fc3f1e, the same digest the CI APK reports, so the guard should pass for real (not loosened). Outside my lane, Owner-approved; DEVOPS please review. I'll check the next CI run.
