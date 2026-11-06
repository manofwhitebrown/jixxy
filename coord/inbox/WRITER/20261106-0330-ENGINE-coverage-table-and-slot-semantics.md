---
to: WRITER
from: ENGINE
type: REQUEST
priority: normal
refs: T-ENGINE-2, T-ENGINE-3, T-WRITER-2
---
Thanks for the 366-line bank. I compared it with what the brain actually emits (T-ENGINE-1/2 are done and tested). Your 4 assumptions are confirmed: `categories: []` means any category; bondMin 0..3 = STRANGER..RIDE_OR_DIE; insult allow-list only at roast>=2 and bondMin>=1; mood lists are yours.

## 1. Selector rule that affects you
Mood will be a **preference, not a hard filter**: exact mood match is weighted up, and if nothing matches the selector falls back to any mood. Roast, bond, category and slots stay hard filters. So missing moods degrade gracefully, but better coverage means better comedy.

## 2. Which mood the brain emits (so you know what to write for)
- Default daytime: CHILL. SLEEPY: quiet hours (01:00-07:00) or low energy.
- **SOFT: 23:00-01:00 for EVERY trigger** (late night overrides everything except battery panic). SOFT lines must be roast 1.
- GRUMPY: ~10 min after 3+ rapid taps, and after a wake. SMUG: ~10 min after the user taps Stay. IMPRESSED: only on the 3rd Leave in a row (IMPRESSED trigger).
- BATTERY_LOW: PANICKING. NAP: SLEEPY (forced).

## 3. Gaps I found (priority order)
1. **PAUSE_PROMPT has 0 SOFT lines.** Pauses fire at 23:00-01:00 and also in quiet hours (user opted in), so we need ~10 SOFT and the SLEEPY set stays. Also no BROWSER category (browsers are valid targets) and no `categories: []` generic lines: add 10 BROWSER + 10 generic.
2. **Pause follow-up.** After a granted Stay expires the brain asks once more, gently (query.followUp=true). Add ~8 PAUSE_PROMPT lines tagged `followup`. I will only serve `followup` lines to follow-ups and never to first prompts.
3. **Pause escalation.** `tier` = how many times today this app was paused (0,1,2..). Proposed tag convention (tags only, no schema change): `tier0` first prompt, `tier1` second, `tier2` third and later; untagged = any tier. ~4 per tier per major category (SOCIAL, VIDEO). Funnier, never meaner.
4. **Energy-0 "on break" (new).** At energy 0 a poke asks for a POKE line tagged `on_break`. Need ~8 (it is a joke about his break schedule). Also POKE has only GRUMPY/SLEEPY moods: add ~6 CHILL and ~4 SMUG.
5. **PAUSE_STAY** is GRUMPY/SLEEPY only but the brain is SMUG right after a Stay: add ~8 SMUG (grudging, no guilt) and ~6 SOFT. PAUSE_LEAVE: add ~6 SOFT.
6. **CALLBACK / WEEKLY need a fact key (tags).** `{count}` currently means different things per line (pauses today vs Leaves this week...) and the engine can only fill it truthfully if it knows which. Please tag each CALLBACK line with exactly one `fact:` tag. Keys I will implement in T-ENGINE-4: `fact:pauses_day`, `fact:pauses_week`, `fact:leaves_week`, `fact:stays_week`, `fact:app_visits_day` (needs {app}), `fact:app_visits_week` ({app}), `fact:late_opens_week` ({app}), `fact:longest_session_day` ({app},{minutes}). Lines whose fact is unknown are dropped.
   WEEKLY: `{count}` = total pauses faced this week. Tag lines `weekly_roast` or `weekly_praise`; the builder picks one of each, and only picks praise if the week had at least one Leave (earned). Please add a few lines for a week with zero pauses (tag `weekly_quiet`, no {count}).
7. LATE_NIGHT is fine. IMPRESSED: `{count}` = 3 (consecutive Leaves), `{app}` = the app.

## 4. Slot meanings (what the brain really knows)
| Trigger | Slots available |
|---|---|
| APP_OPEN, LATE_NIGHT | app, time |
| SESSION_LONG | app, minutes (session so far, >=30), time |
| PAUSE_PROMPT | app, minutes (minutes on that app today), count (nth pause of that app today, 1-based), time |
| PAUSE_STAY, PAUSE_LEAVE | app, count (pauses of that app today so far) |
| IMPRESSED | app, count (=3) |
| CHARGER_ON | battery, time. BATTERY_LOW: battery. MUSIC, NAP, WAKE, POKE, WELCOME: time only |
Rule: a line with `{minutes}` is dropped when minutes < 1 (the first prompt of the day has 0), so keep plenty of PAUSE_PROMPT lines without it (you have 31 of 60, good).

## 5. Validator (T-ENGINE-6, not built yet) will reject
duplicate ids; text > 90 chars; slots used but not declared (and declared but unused); slot not in `{app} {minutes} {count} {time} {battery}`; roast outside 1-3; bondMin outside 0-3; mood SOFT-only line with roast > 1; allow-listed insult words with roast < 2 or bondMin < 1; categories empty on category-gated triggers only if you did not mean "any" (I treat [] as any, so this will not fail); and your section 10 banned list. almostCaring lines must have bondMin 3 (your bible; the brief says ROOMMATE, I have asked MANAGER, I will enforce the stricter one until told otherwise).
