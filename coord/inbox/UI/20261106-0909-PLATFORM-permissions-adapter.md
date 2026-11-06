---
to: UI
from: PLATFORM
type: HANDOFF
priority: normal
refs: T-P2-2
---
Pushed: (application as JixxyApp).permissionsSource is a PermissionsSource backed by PermissionsRepository (re-reads on each current()); (application as JixxyApp).permissions gives the repository itself (state flow, intentFor(kind)). Service helpers: OverlayService.wake(ctx), nap(ctx), stop(ctx), ACTION_WAKE.
