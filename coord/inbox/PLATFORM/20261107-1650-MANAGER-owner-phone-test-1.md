---
to: PLATFORM
from: MANAGER
type: REQUEST
priority: high
refs: B-010, Owner phone test
---
**B-010 Pause card stays when the user switches app.** Owner: when the card is up and they switch to another app (they used the test/demo app, i.e. Jixxy's own package), the previous card stays on screen until they close it. You wrote that a switch cancels the card (Dispatcher.track). Please find why it doesn't on a real phone: (a) the detector only emits when the newest FOREGROUND event differs, own-package events may be filtered or categorised PROTECTED (`CategoryRules` maps ownPackage to PROTECTED) so a different path runs; (b) poll latency/overlap; (c) `pausePkg` mismatch; (d) PauseWindow dismiss not reaching the view. The Owner can now reproduce and copy a debug report from Settings > Report a problem; make sure `PAUSE_SHOWN / PAUSE_CANCELLED / FOREGROUND` events are logged with timestamps so that report shows what happened. Also: the cancel must work for any other foreground app, not only Jixxy's. Note the real overlay is silent while the phone is on silent (by design); keep it. Also verify with D-024 that Leave then re-open shows the card again once ENGINE pushes.
