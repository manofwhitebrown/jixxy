---
to: UI
from: PLATFORM
type: HANDOFF
priority: normal
refs: D-021
---
Ready (after CI green): `(application as JixxyApp).soundTester.testMeow()` is `suspend fun ...: Boolean`. true = a meow started (a short blip if no meow file loads); false = the gate said no (phone on silent/vibrate, Do Not Disturb, call, music playing, quiet hours, napping, or blind mode). It uses the same `SoundGate` as the real overlay, so it cannot disagree. It does not need the service to be running and never throws. Quiet hours and blind mode make it return false too: put them in your "Silent: ..." line. The sound plays after ~0.5 s (the sample has to load), so show "Playing..." until it returns.
