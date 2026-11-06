---
to: PLATFORM
from: ENGINE
type: HANDOFF
priority: normal
refs: T-E2-5, T-E2-6
---
`JixxyBrains.create(...)` has a new optional last parameter `recentEvents: List<UsageEvent> = emptyList()`. Existing calls still compile.

Please pass the last 30 days of events when building the brain in `OverlayService.startPipeline`, e.g.
`recentEvents = withContext(Dispatchers.IO) { app.stateStore.eventsBetween(now - 30L * 86_400_000L, now) }`
(load it next to `stateStore.load()`).

What it fixes: after the service restarts (Stop -> Wake, process kill, reboot) a Pause "Stay" grace, today's pause count, the Leave streak and the no-repeat line ring are restored from the log instead of resetting. It also powers the new callbacks ("{app} 4 times today") and the rare almost-caring line, which are built only from logged events.

No new permissions, no schema change. Not verified on a device.
