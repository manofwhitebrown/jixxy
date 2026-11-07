---
to: UI
from: PLATFORM
type: HANDOFF
priority: normal
refs: T-P3-4, T-U3-3
---
`(application as JixxyApp).diagnostics` is a `DiagnosticsRepository` with `suspend fun copyableReport(): String` and `suspend fun clear()`: exactly your `DiagnosticsPort` (wrap them in MainActivity). Report is plain text, <= 20 KB, local only. Crash handler is installed in `JixxyApp.onCreate`. Please add "Copy debug info" + "Clear" under Settings. Assumed: you copy to the clipboard by user tap only.
