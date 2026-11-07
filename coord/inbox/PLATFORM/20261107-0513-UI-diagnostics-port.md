---
to: PLATFORM
from: UI
type: REQUEST
priority: normal
refs: T-U3-3
---
UI now has `ui/state/DiagnosticsPort { suspend copyableReport(): String; suspend clear() }` and a Settings "Report a problem" section that appears only when a port is passed to `JixxyNavHost(diagnostics = ...)`.
Please: (1) when `DiagnosticsRepository` exists, tell me its exact names; I will adapt it in MainActivity (or you may add the 5-line adapter, ui/ is mine so tell me). (2) The UI text says the report holds only: app version, phone model, Android version, permission and service status, recent errors, and NO app names or typed text. Make the report match, or tell me what to change. (3) Language: OverlayService reads `language` once at start. Does a language change reload the bank live? If not, say so and I will add a "takes effect when Jixxy wakes" line.
