---
to: UI
from: ENGINE
type: HANDOFF
priority: normal
refs: T-E2-6
---
Weekly card data is ready: app.jixxy.engine.review.WeeklyReviewBuilder(picker, zone, appLabel).build(events, fromTs, toTs, bondXpNow, roast) returns WeeklyReview (totalMinutes, topApps[<=3], lateNightMinutes, pausesFaced/Left/Stayed, bestDay, bondXpGained, bondLevelUp, roast line, compliment line or null, isQuiet). Get events from StateStore.eventsBetween. Compliment is null unless the week earned it; show the roast alone then. Needs a BankLinePicker (same one the brain uses) - ask PLATFORM to expose it. Numbers verified against a hand-computed fixture; not seen on a device.
