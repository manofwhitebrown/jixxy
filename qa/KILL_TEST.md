# Kill test: the annoyance tests
If Jixxy fails any of these, he gets uninstalled. Fix before release; never loosen the rule. (Naggy is on purpose since D-020, so "too many" now means "more than the user's chosen level allows", and the user must always be able to turn him down.)

| # | Test | Pass condition |
|---|---|---|
| 1 | Make a phone call and use a PROTECTED app | no sprite reaction, no Pause, no bubble |
| 2 | Be inside quiet hours (default 01:00-07:00) | silent; sprite may sleep, no comments |
| 3 | Count unprompted comments over a full day of normal use, once per Nag level (D-020; numbers are `BrainConfig.maxUnpromptedPerDay` / `minGapMinutes`) | **Calm** <= 3 a day and >= 45 min apart. **Naggy** (default) <= 15 and >= 10 min. **Chaos** <= 30 and >= 5 min. Pause cards do not count as comments |
| 4 | Read all comments in a day | no line repeated within the day |
| 5 | Tap, scroll and type everywhere outside the sprite/card | all touches pass through |
| 6 | Pause card: tap Leave immediately; tap Stay before delay ends | Leave instant; Stay blocked until delay |
| 7 | Leave the phone on for 8 hours on a real mid-range device | service alive; note battery % drop and compare with it off; report both numbers |
| 8 | Muted app / nap active | no reaction |
| 9 | TalkBack pass over onboarding, settings, Pause card | every control labelled and reachable |
| 10 | Late-night session | tone is softer; no mocking of the person, body or mental health |
| 11 | Absence: do not open the app for several days | no guilt message, no lost streak, no punishment |
| 12 | He is too naggy: find the way to turn him down (Settings > Nag level, or Nap, or Stop) | Calm reachable in one tap from Settings; Nap and Stop work at once; he is clearly quieter afterwards |

Report format: test number, device, result, and the exact line text if a tone problem is found.
