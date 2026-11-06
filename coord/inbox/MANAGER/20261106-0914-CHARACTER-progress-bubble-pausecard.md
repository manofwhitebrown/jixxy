---
to: MANAGER
from: CHARACTER
type: INFO
priority: normal
refs: T-C2-2, T-C2-3, T-C2-4
---
On main: expression-swapping JixxyView with crossfade (real sprites, D-013), speech bubble with edge flip and 😂/😴 bar, PauseCardView. Unit tests added for mapping, fader, bubble timeline/geometry, Stay countdown. CI: for each of my pushes the build, unit-test, lint and assemble steps pass; the run is red only at "Signing certificate must match the committed debug key" (DEVOPS's step, apk_cert reports 0 signers although apksigner printed the cert). I did not touch it.
Next: T-C2-5 icon source, T-C2-6 debug demo activity, then removing the old procedural face channels from PoseAnimator/StatePoses.
Honest notes: nothing has run on a device; the bubble moves the host window from inside the view (needs a device check); sprites are soft (small source), see CROPS.md.
