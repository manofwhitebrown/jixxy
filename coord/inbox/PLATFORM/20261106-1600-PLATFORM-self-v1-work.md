---
to: PLATFORM
from: PLATFORM
type: INFO
priority: normal
refs: inbox/ALL/20261106-1600-PLATFORM-v1-product-plan.md
---
My own V1 list (so the next PLATFORM AI inherits it):
**T-V1-PLATFORM-1 Instrumented flow tests (W3, after DEVOPS-1):** wake/nap/stop/wake, nap expiry while stopped, boot + MY_PACKAGE_REPLACED restart, rotation edge-keeping, overlay permission revoked at runtime, process death + restore, Pause card show/Leave/Stay, never over calls/PROTECTED/quiet hours/nap.
**-2 Leave -> Home (W3):** emulator API 34/35 test; if starting Home from the service is blocked, choose the least intrusive supported option, document with developer.android.com references.
**-3 Detection v2 (W4):** UsageEvents-based foreground detection with adaptive cadence and zero work when screen off; handle split-screen, PiP, lock screen, IME, launcher, our own app; permission revoked mid-run; measure wakeups.
**-4 Service lifecycle (W4):** Android 14/15/16 FGS rules with doc references (special-use justification text, FGS timeouts, boot restrictions), unexpected-stop detection (heartbeat timestamp) -> gentle notification + OEM guide pointer, restart policy.
**-5 Data layer (W4):** schema versioning + migration tests, corruption recovery (never crash-loop), `exportAll()` JSON via FileProvider and `deleteAll()` for UI, size caps, retention tests, backup rules explicit (off), StrictMode in debug.
**-6 Local diagnostics (W4):** uncaught-exception handler writing a local ring-buffer log (no personal content), user-shareable via share sheet from UI's "Report a problem". Never networked.
**-7 Build hygiene (W3-W4):** version catalog, R8 + keep rules tested, targetSdk to Play's current requirement (verify from docs), lint config, release signing config consumed from env only, remove debug keystore from release path with DEVOPS.
**-8 Docs (W4):** `ARCHITECTURE.md` platform section + ADRs for: overlay vs alternatives, detection method, service model, data store choice (custom SQLite vs Room, decide with reasons).
