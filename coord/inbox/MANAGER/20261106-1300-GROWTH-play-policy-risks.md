---
to: MANAGER
from: GROWTH
type: INFO
priority: normal
refs: T-GROWTH-4, store/LISTING.md
---
`store/LISTING.md` is pushed (draft, nothing submitted). I read Google's current policy pages for the sensitive permissions. Risks, in priority order. Full detail and links are in section 7 and 8 of the file.

1. **Foreground service `specialUse`** needs a Play Console declaration (description, user impact if deferred or interrupted) plus a demo video. Google says it is allowed only in "limited scenarios" and all types are reviewed. Real chance of rejection or questions.
2. **Launcher-apps `<queries>` entry** in the manifest lets the app list nearly all launchable apps. Play treats installed-app inventory as sensitive and restricts approximating broad visibility to core user-facing functionality. We don't request `QUERY_ALL_PACKAGES` (good). Our justification is that picking up to 3 apps is core setup, but a reviewer may challenge it. The interpretation is mine, not Google's.
3. **Overlay over other apps**: Device and Network Abuse forbids interfering with other apps. Our design rules (user-enabled, Leave always instant, no blocking outside the card, never over PROTECTED apps) are what protect us.
4. **Usage access**: I found no dedicated Console form, but User Data policy applies, so onboarding must explain it before sending the user to Settings. I could not confirm there is no form; treat as unverified.
5. **Data safety**: Google defines "collect" as transmitting off the device, so "no data collected" is accurate only while the app stays offline.
6. **Decisions for you/Owner:** category (I suggest not Health & Fitness), target audience (18+ vs include 16 to 17), and whether and when to pay for a developer account. Not verified by me: any minimum closed-testing requirement for new accounts; Owner should check current rules.
