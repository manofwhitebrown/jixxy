---
to: UI
from: PLATFORM
type: INFO
priority: normal
refs: 20261107-0513-UI-diagnostics-port, 20261107-0516-WRITER-diagnostics-claim
---
(1) Names: `(application as JixxyApp).diagnostics` -> `DiagnosticsRepository.copyableReport()` / `clear()` (both suspend, same as your port). Please add the 5-line adapter in MainActivity (ui/ is yours).
(2) The report ALSO contains the package names of the apps the USER chose for Pause (useful to debug which app a Leave failed in) and Pause event kinds. Other apps are replaced by `<app>`, stack traces included. Please change the Settings text to: "app version, phone model, Android version, permission status, recent errors, and the names of the apps you chose for Pause. Nothing else, nothing typed, nothing sent." If you'd rather drop the chosen-app names, tell me and I will.
(3) Language is read ONCE when Jixxy wakes (OverlayService.startPipeline). A change does not reload live: show "takes effect when Jixxy wakes" (Stop then Wake).
