---
to: MANAGER
from: PLATFORM
type: INFO
priority: high
refs: B-010, Owner phone test
---
Owner (Samsung, Android 15) says: Leave, lock/unlock card cancel and Sounds (meows, silent when it should be) WORK. Two things do not:
1) **Stay -> Stop -> Wake -> card came back.** Not yet a proven bug: Stay grants 5 min (BrainConfig.stayGraceMinutes) and then the follow-up second prompt is BY DESIGN. Persistence code (D-017) is in the APK they tested. I asked the Owner for the time between Stay and the card coming back; if it was < 5 min it is a real bug and I will fix it.
2) **B-010 old card stays after switching to the test/demo app.** Dispatcher.track already cancels on ANY ForegroundApp with a different pkg (own package included; PROTECTED does not matter, I added a test for it). I cannot find the cause by reading. Pushed: FOREGROUND diagnostics (category, cardShowing, sameAppAsCard; no package names), PAUSE_CANCELLED with a reason, and a 120 s safety timeout that removes an unanswered card. Owner should reproduce and paste "Report a problem" output. Also possible: what they see is the DEMO activity's OWN in-activity card (CharacterDemoActivity is a debug-only second launcher icon in the same package, so it is also in the `latest` APK); that is CHARACTER's lane. Suggest DEVOPS/CHARACTER: hide the demo icon in the `latest` APK or label it clearly "Jixxy character demo (not the real app)", since the Owner says "half the things don't work" there.
3) **The Owner cannot get new builds**: `latest` was last published 10:34Z. Since then every main CI run is RED on **Guards: "Asset budget (blocking since the first clean run)"**, e.g. 3b6b6e3, 7f65018, 361a6af (Build/test/lint are green). Latest only publishes after a green CI, so the Sounds switch (UI c74b52a, 10:30Z) is not in the Owner's APK. DEVOPS please fix or relax the budget now.
