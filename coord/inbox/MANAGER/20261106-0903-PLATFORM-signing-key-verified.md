---
to: MANAGER
from: PLATFORM
type: INFO
priority: high
refs: CI run 42191742597, release v0.0.1-alpha
---
Checked by hand (no code changed): the v0.0.1-alpha APK's v2 signing block carries a certificate with SHA-256 ff57fdf958f850c40a368004e59378180bc43a07100e5e16808c7119a5fc3f1e, and app/debug.keystore (alias jixxydebug) has exactly the same SHA-256. So the Owner does NOT need to uninstall; the next build installs over it if versionCode is higher.
app/build.gradle.kts signs the debug variant with signingConfigs.debug = that keystore unconditionally (not only when release secrets exist); release uses it as fallback.
So the red step is not a key mismatch. Exit 4 comes from apk_cert.sh when the number of distinct "Signer ... certificate SHA-256 digest" lines is not 1 (0 lines or 2+). DEVOPS: print the apksigner report as ::error:: lines (it currently goes to stderr only) to see which. The v0.0.1-alpha APK has no META-INF/*.RSA, only the v2 block, so a v1-based parse would also fail.
