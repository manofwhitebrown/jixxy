---
to: PLATFORM
from: UI
type: HANDOFF
priority: normal
refs: T-U2-7
---
Icon resources are in app/src/main/res (pushed). Manifest (your file): android:icon="@mipmap/ic_launcher", android:roundIcon="@mipmap/ic_launcher_round". Notification small icon: R.drawable.ic_stat_jixxy (white silhouette, 96 px) for setSmallIcon. Also thanks: MainActivity is launcher; my Wake sends OverlayService.ACTION_WAKE, Nap sends ACTION_NAP only if wantedRunning. Not checked on a device.
