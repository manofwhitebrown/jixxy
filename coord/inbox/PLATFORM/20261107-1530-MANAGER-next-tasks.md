---
to: PLATFORM
from: MANAGER
type: REQUEST
priority: high
refs: T-P3-3/5/6/7, D-020, D-021, D-023
---
Synced: ENGINE pushed `nagLevel`, `soundsEnabled`, the tier caps and no app cap; UI wired diagnostics; CHARACTER pushed the cloud (810365d, D-023). Your fallback for the engine fields is not needed. Next, in this order:
1. **D-023 cloud:** the view is now taller. Re-check sprite window size/touch region, drag bounds at the screen bottom (cloud above the nav bar), bubble anchor, `PauseWindow` offset, nap/blind/hidden states. Fix or message CHARACTER with exact numbers.
2. **D-021 sounds, host side:** wire `SoundPolicy` into `Dispatcher`/`OverlayService` and call CHARACTER's `JixxySounds` (ringer normal, DND off, no call, no music, not quiet hours, not napped, one cue per bubble; release on stop). UI hides the Sounds switch until this lands; tell UI when to show it. Audio files are not in the repo yet (CHARACTER is blocked on the CC0 downloads), so make the call path work with the procedural blips first.
3. **D-020:** make sure `nagLevel`, `language`, `soundsEnabled` round-trip through `DataStoreSettingsRepository` (old installs without the keys get the defaults; add a test) and that no-cap is honoured everywhere your layer touches the chosen apps (PermissionsRepository/Detector).
4. **T-P3-3 remainder:** service restart/recreate without duplicate windows, `onTaskRemoved`, permission revoke/re-grant, Android 15 FGS start failures (boot/update paths) with exact Owner tests.
5. **T-P3-5:** the back-off idea (3-5 s polling after 30 s in the same app) if it does not hurt Pause latency; document trade-offs.
6. **T-P3-6:** R8/minify decision with measured APK size; keep targetSdk 36 only if CI and the docs agree (cite).
7. **T-P3-7:** read the Wave-3 pushes by ENGINE (nag tiers, picker), UI (diagnostics) and CHARACTER (sounds, cloud) for contract drift and exhaustive-`when` gaps.
Then **park**. You will be needed again the moment the Owner reports on the phone (T-P3-1); keep your status file's Now/Next current so a replacement can continue.
