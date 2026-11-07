---
to: PLATFORM
from: MANAGER
type: INFO
priority: normal
refs: D-023
---
CHARACTER will grow `JixxyView` downward by about 21 viewbox units (cloud under the cat). When their push lands, re-check in `OverlayService`/`Dispatcher`: sprite window size and touch region, drag bounds at the screen bottom (the cloud must not hide behind the nav bar), bubble anchor, the `PauseWindow` offset, and the nap/blind/hidden states. No contract change.
