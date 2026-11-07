# LINE REVIEW LOG (WRITER)

## v0.1 English bank (366 lines)
Checks run: scripts/validate_lines.py (JSON, ids, <=90 chars, declared and allowed slots) plus a local script for banned words, affectionate-insult gating (roast >= 2 and bondMin >= 1) and repeated openings within a trigger/category.

Rewritten (not cut):
- About 25 lines that shared their first two words with a neighbour in the same trigger/category (for example several "{minutes} minutes on {app}." openers). The slot or the joke now comes later in the sentence.
- A bible example that used a death-adjacent word was replaced before it reached the bank.

Cut: none yet. No tester feedback exists. Retire or rewrite lines here once the Manager relays "annoying" or 😴 patterns.

Not yet checked: ENGINE's :engine:validateLines (does not exist yet) and the real trigger x category coverage table.

## v0.2 read-through (same 366 lines, 14 rewritten)
Rule applied: a line may only claim what the engine knows. Rewrote lines that implied extra facts or sounded sarcastic at night.
- "Left on the first try" → "Left without a fight" (engine may not know a "first try").
- "Left again?" → "Left, just like that" (no repeat is implied).
- "Headphones on?" → "Music on?" (engine only knows music is playing).
- "Messages to answer" → "Time for chats" (engine can't know there are messages).
- "No judgement, at all" and "tomorrow exists" late-night lines softened to remove any sarcasm or nagging tone.
- "Thank you, {battery}% me" removed (awkward); Out-in-minutes duplicate reworded.
- Gold set and bible updated to match; marketing-safe row 9 changed accordingly.

## Hinglish pack v0.1 (150 lines, PROVISIONAL)
Started on the Owner's go-ahead without a Manager trigger; Manager to confirm or retire it. Written natively in Roman script, mild and affectionate. No slurs, no regional, caste or religious jokes, no religious greetings or words, no body or mental-health words ("pagal" avoided). Same checks as English (length, slots, banned words, repeated openings). Ids use an `hg_` prefix and `lang` is `"hinglish"` so ids stay unique across languages.
Not done: review by a native Hinglish speaker, and testing whether the sarcasm lands harder in Hinglish (the brief's hypothesis). Treat tone and idiom as unverified until testers read it.

## v0.3 (WRITER takeover, cat update)
Added 92 EN lines: the Owner's canonical captions (tagged `owner_canon`), CHARGER_ON x8, BATTERY_LOW x8, dropped (POKE + tag `dropped`) x6, POKE escalation x6, CHILL/SMUG POKE x10, on_break x8, NAP x6, WAKE x8 (acknowledge the nap), MUSIC x7, LATE_NIGHT blanket/soft x8, ALMOST_CARING x4, IMPRESSED x5, SESSION_LONG x3, APP_OPEN stare x3, WELCOME x2.
Rewritten: weekly "sleepy sprite" -> "sleepy cat" (retired sprite wording).
Honest notes: "seek help" (Owner caption) is kept only in question form because the engine cannot know the song repeats. The skull emoji appears once, in the Owner's caption line. Canonical "...go to sleep, idiot." is roast 2, bondMin 3, almostCaring.

## v0.4 Pause lines (T-W2-3) and ENGINE gap list
Added 83 EN lines: PAUSE_PROMPT SOFT x10, BROWSER x10, generic (any category) x10, followup x8 (tag `followup`), escalation tiers (`tier0`/`tier1`/`tier2`) for SOCIAL+VIDEO x21; PAUSE_STAY SMUG x8 and SOFT x6; PAUSE_LEAVE celebrate x4 and SOFT x6.
Test applied to each: would a tired 19-year-old at 2 am smirk or feel judged? Rewrote the BROWSER lines that said "again" (the first pause of the day has no history).
Honest notes: tier1/tier2 lines use {count} (nth pause of that app today) which the engine confirms for PAUSE_PROMPT. Humour is my judgement; testers confirm.

## v0.5 CALLBACK / WEEKLY tags (ENGINE request 0330 items 6)
Every CALLBACK line now has exactly one `fact:` tag (pauses_day, pauses_week, leaves_week, app_visits_day, app_visits_week). 8 lines were reworded so the period is explicit ("today" / "this week") and the tag is truthful. WEEKLY lines tagged `weekly_roast` / `weekly_praise`; +14 lines incl. 6 `weekly_quiet` (no {count}) for a zero-pause week. Not used: fact:late_opens_week, fact:stays_week, fact:longest_session_day (no lines yet).

## v0.6 Validator loop (T-W2-5)
ENGINE landed LineBankValidator + :engine:validateLines (0c7fb49). I cannot run Gradle here, so I re-implemented its rules in Python (banned list incl. "ass", "god", "hell", "trash", "mental"; slot availability per trigger; insult gating; UTF-16 length) and ran them on all 705 EN and 150 Hinglish lines: 0 errors. CI's validateLines output remains the authority; if it flags anything I will fix it first.

## v0.7 Opening-variety pass
Reworded 22 of my own new lines that shared their first two words with a neighbour in the same trigger/group (battery_low, charger_on, late_night, nap, poke, pause_stay, browser prompts, weekly, welcome, wake, session_long, almost_caring). The Owner's canonical captions keep their exact wording. Slots re-derived; 0 validator errors.

## v0.8 Roast and bond coverage (+40 EN lines)
Gap found: PAUSE_STAY, PAUSE_LEAVE, POKE, WAKE, MUSIC, IMPRESSED had almost nothing at roast 2-3 or bondMin 1, so Normal/Savage users at ROOMMATE bond kept hearing gentle strangers' lines. Added 40 lines (stay x8, leave x6, poke x6, wake x3, music x3, impressed x2, app_open x12 with cat flavour for SOCIAL/VIDEO/GAME/MESSAGING/PRODUCTIVITY). Affectionate insults only at roast >= 2 and bondMin >= 1. Reworded four lines that implied a repeat ("again", "another") the engine cannot know. Local validator replica: 0 problems.

## Hinglish pack v0.2 (T-W2-6, Manager go relayed by the Owner)
The Manager's yes reached me through the Owner, not as a repo message; I read it as "trigger the Hinglish pack". Added 90 cat-voice Hinglish lines (150 -> 240; ids `hg_*`, lang `hinglish`, Roman script) mirroring the English canon: charger, low battery, dropped, poke escalation/on_break, nap/wake, music, late-night blanket, almost-caring, impressed, Pause SOFT/followup/tiers/BROWSER, Stay/Leave, weekly_quiet. "bro" becomes "bhai"; the skull emoji is not used. No Hindi insults (the engine's insult gate only knows the English list); only "gremlin" at roast 2 / bond 3.
Not done: native-speaker review. Idiom, gender agreement (Jixxy speaks as "raha hoon") and whether sarcasm lands harder in Hinglish are unverified. Local validator replica: 0 problems on all 821+ lines.

## Wave 3, T-W3-1 (2026-11-07T05:13Z)
- D-014: ALMOST_CARING lines now use their own bondMin. Mild/joking = 1 (ROOMMATE): 002, 004, 006, 007, 009, 012, 016, 017, 018, 019, 020. Heartfelt = 3 (RIDE_OR_DIE): 001, 003, 005, 008, 010, 011, 013, 014, 015. The once-per-3-days cap stays in the engine.
- D-015: all 6 "dropped" lines (poke_any_021..026) are POKE + tag dropped. weekly_any_006 only says "fell asleep" and is not a dropped line.
- D-016: English files hold only lang "en", Hinglish files only "hinglish"; picker falls back to English only, never to Hinglish. validate_lines: 847 lines, 0 problems.

## Wave 3, T-W3-2: 2 am audit of the EN bank (2026-11-07T05:15Z)
Read every Pause line (prompt, stay, leave) and every LATE_NIGHT, SESSION_LONG, MUSIC, CALLBACK, IMPRESSED and POKE line as a tired 19-year-old. Kept: all canonical captions, all SOFT/late-night lines, the Leave 'Respect.' family. Nothing needed a hard cut; fixes were rewrites. Reasons used:
- **False history**: 'again' / 'back on' in untagged Pause prompts (engine's tier tags are only preferences, so these could fire on the first pause of the day). 'again' removed or reworded.
- **Slot the engine can't know**: tab counts, 'colour-coding', 'one real task', 'open since the last ice age', a literal '2 a.m.' in a line that fires any time in the late window.
- **Judges the person**: 'you're not researching, you're wandering', 'starring you', 'The spreadsheet is stunned' (implied they wouldn't leave), 'Look at you, leaving on purpose', 'Quietly impressive' for a Pause count.
- **Pleading / religion-adjacent**: 'Leave, please?' and 'Streaming gods demand tribute'.
- **Repeated openings** in the same trigger: music 'Music on', session 'N minutes', stay 'Stay, then', leave 'Left {app}', duplicate phrase 'I respect the commitment'.
- **Poke lines that imply repetition were untagged** (poke_any_003, 005, 006, 008, 015, 019): tagged 'escalation' so they only fire after 3 quick taps (ENGINE reserves that tag). poke_any_055 'Again?' reworded.

| id | before | after |
|---|---|---|
| pause_prompt_social_002 | {app} again. Quick pause, then it's all yours. | {app}? Quick pause, then it's all yours. |
| pause_prompt_social_004 | Back on {app}? Leave, or I'll narrate your scrolling. | Opening {app}? Leave, or I'll narrate your scrolling. |
| pause_prompt_video_005 | {app} again. The algorithm sends its regards, and an invoice. | {app}. The algorithm sends its regards, and an invoice. |
| pause_prompt_game_005 | {app} again. The leaderboard doesn't know you, but I do, menace. | {app}. The leaderboard may not know you, but I do, menace. |
| pause_prompt_shopping_001 | {app} again. Quick pause before the cart gets ideas. | {app}. Quick pause before the cart gets ideas. |
| pause_prompt_shopping_005 | Your cart has more friends than I do, dummy. {app} again? | Your cart has more friends than I do, dummy. Shopping on {app}? |
| pause_prompt_other_005 | {app} AGAIN? Theatre of the absurd, starring you and {minutes} minutes. | {app}?! Theatre of the absurd, one cat in the audience. {minutes} minutes today. |
| pause_prompt_video_010 | Streaming gods demand tribute. {minutes} minutes on {app}, goblin. | Infinite queue, finite goblin. {minutes} minutes on {app}. |
| pause_prompt_other_010 | Breaking news: {app} again. Experts say leave, menace. | Breaking news: {app} opens. Experts say leave, menace. |
| pause_prompt_social_024 | {app}, {count} pauses deep. I'm out of jokes. Leave, please? | {app}, {count} pauses deep. I've run out of jokes. Your move. |
| pause_prompt_browser_011 | Fifteen tabs, zero answers. {app} again? | If it's fifteen tabs and zero answers, leave {app}. I'll hold the browser. |
| pause_prompt_browser_012 | {minutes} minutes in {app}. The research is now just vibes. | If this is research, it's mostly vibes now. {minutes} minutes in {app}. |
| pause_prompt_browser_013 | {app} again. You're not researching, you're wandering, gremlin. | Is this research, or wandering with extra steps, gremlin? {app} is open. |
| pause_prompt_browser_014 | {minutes} minutes of 'one quick search'. Bold of you, dummy. | {minutes} minutes on {app} today. 'One quick search' is famously quick, dummy. |
| pause_prompt_browser_015 | That tab has been open since the last ice age. Leave {app}? | Some tabs feel older than the last ice age. Leave {app}? |
| pause_prompt_productivity_003 | {app} again? Productive. I'm suspicious, but proud. | {app}? Productive. I'm suspicious, but proud. |
| pause_prompt_productivity_005 | Colour-coding {app} again? The deadline can see you, goblin. | Pause first, {app} after, goblin. I'll guard your to-do list. |
| pause_prompt_productivity_006 | {minutes} minutes in {app} and one real task. I counted. | {minutes} minutes in {app}. I counted every one, quietly, goblin. |
| pause_stay_any_004 | Your call. I respect the commitment. | Your call. I'll stay out of it. Loudly. |
| pause_stay_any_036 | Stay, then. I'll watch the clock, gently. | Okay. I'll watch the clock. Gently. |
| pause_stay_any_040 | Stay, then, gremlin. I'm timing you. Lovingly. | Fine, gremlin. I'm timing you. Lovingly. |
| pause_leave_any_005 | Look at you, leaving on purpose. | Left on purpose. I noticed. |
| pause_leave_any_035 | Left {app}. Quietly proud. Don't tell anyone. | Quietly proud you left {app}. Don't tell anyone. |
| pause_leave_any_040 | Left {app}. The spreadsheet is stunned. So am I. | Left {app}. The spreadsheet is thrilled. So am I. |
| pause_prompt_social_002 | {app} again. Quick pause, then it's all yours. | {app}? Quick pause, then it's all yours. |
| pause_prompt_social_004 | Back on {app}? Leave, or I'll narrate your scrolling. | Opening {app}? Leave, or I'll narrate your scrolling. |
| pause_prompt_video_005 | {app} again. The algorithm sends its regards, and an invoice. | {app}. The algorithm sends its regards, and an invoice. |
| pause_prompt_game_005 | {app} again. The leaderboard doesn't know you, but I do, menace. | {app}. The leaderboard may not know you, but I do, menace. |
| pause_prompt_shopping_001 | {app} again. Quick pause before the cart gets ideas. | {app}. Quick pause before the cart gets ideas. |
| pause_prompt_shopping_005 | Your cart has more friends than I do, dummy. {app} again? | Your cart has more friends than I do, dummy. Shopping on {app}? |
| pause_prompt_other_005 | {app} AGAIN? Theatre of the absurd, starring you and {minutes} minutes. | {app}?! Theatre of the absurd, one cat in the audience. {minutes} minutes today. |
| pause_prompt_video_010 | Streaming gods demand tribute. {minutes} minutes on {app}, goblin. | Infinite queue, finite goblin. {minutes} minutes on {app}. |
| pause_prompt_other_010 | Breaking news: {app} again. Experts say leave, menace. | Breaking news: {app} opens. Experts say leave, menace. |
| pause_prompt_social_024 | {app}, {count} pauses deep. I'm out of jokes. Leave, please? | {app}, {count} pauses deep. I've run out of jokes. Your move. |
| pause_prompt_browser_011 | Fifteen tabs, zero answers. {app} again? | If it's fifteen tabs and zero answers, leave {app}. I'll hold the browser. |
| pause_prompt_browser_012 | {minutes} minutes in {app}. The research is now just vibes. | If this is research, it's mostly vibes now. {minutes} minutes in {app}. |
| pause_prompt_browser_013 | {app} again. You're not researching, you're wandering, gremlin. | Is this research, or wandering with extra steps, gremlin? {app} is open. |
| pause_prompt_browser_014 | {minutes} minutes of 'one quick search'. Bold of you, dummy. | {minutes} minutes on {app} today. 'One quick search' is famously quick, dummy. |
| pause_prompt_browser_015 | That tab has been open since the last ice age. Leave {app}? | Some tabs feel older than the last ice age. Leave {app}? |
| pause_prompt_productivity_003 | {app} again? Productive. I'm suspicious, but proud. | {app}? Productive. I'm suspicious, but proud. |
| pause_prompt_productivity_005 | Colour-coding {app} again? The deadline can see you, goblin. | Pause first, {app} after, goblin. I'll guard your to-do list. |
| pause_prompt_productivity_006 | {minutes} minutes in {app} and one real task. I counted. | {minutes} minutes in {app}. I counted every one, quietly, goblin. |
| pause_stay_any_004 | Your call. I respect the commitment. | Your call. I'll stay out of it. Loudly. |
| pause_stay_any_036 | Stay, then. I'll watch the clock, gently. | Okay. I'll watch the clock. Gently. |
| pause_stay_any_040 | Stay, then, gremlin. I'm timing you. Lovingly. | Fine, gremlin. I'm timing you. Lovingly. |
| pause_leave_any_005 | Look at you, leaving on purpose. | Left on purpose. I noticed. |
| pause_leave_any_035 | Left {app}. Quietly proud. Don't tell anyone. | Quietly proud you left {app}. Don't tell anyone. |
| pause_leave_any_040 | Left {app}. The spreadsheet is stunned. So am I. | Left {app}. The spreadsheet is thrilled. So am I. |
| late_night_any_022 | Quiet company, as ordered. | Quiet company. Complimentary. |
| late_night_any_044 | You don't have to be okay at 2 a.m. Just be here. That's enough. | You don't have to be okay at this hour. Just be here. That's enough. |
| session_long_any_001 | {minutes} minutes on {app}. Stretch break? | Stretch break? {minutes} minutes on {app}. |
| session_long_any_035 | {minutes} minutes straight. Just checking you're comfy. Carry on if you are. | Straight for {minutes} minutes. Just checking you're comfy. Carry on if you are. |
| music_any_012 | Music on. I'll tap my tail. Tail only. | Tail-tapping, tail only. Music's on. |
| music_any_018 | Music on. Soft songs, I'm in. Loud ones, I'm leaving. | Soft songs, I'm in. Loud ones, I'm leaving. |
| callback_any_007 | Quietly impressive: {count} pauses today. | {count} pauses today. I'm keeping the notes tidy. |
| poke_any_055 | Hehe. Again? Fine, I love attention. Don't repeat that. | Hehe. Poking me? Fine, I love attention. Don't repeat that. |

Open for the Owner: music_any_011 ('Same song on repeat? Seek help.') and session_long_any_031 (💀) are the Owner's own captions and stay; 'seek help' is a casual idiom but borders on mental-health wording, so if any tester reads it badly, retire it. pause_stay 'Five' lines assume PauseSpec.grantMinutes = 5 (current default); if Stay length ever becomes a setting, those lines need a slot.
Not yet audited line by line: app_open, battery_low, charger_on, nap, wake, weekly, welcome (scanned by pattern only, no hits). Validator: 847 lines, 0 problems.

## Wave 3, T-W3-3 (partial, 2026-11-07T05:18Z)
ENGINE's coverage table (T-E3-4) has not arrived yet, so I used my own content/COVERAGE.md. Thinnest cells were PAUSE_PROMPT roast 3 (2 to 3 lines per category) and PRODUCTIVITY roast 2. Added 18 lines (EN now 639): 2 roast-3 lines (bondMin 1, affectionate insult only here) for each of the 8 chosen-app categories, plus 2 PRODUCTIVITY roast-2 lines (bondMin 0, no insult). No 'again', no slot beyond {app}/{minutes}, no tab/cart/tasks claims stated as fact (all conditional or joke). Validator: 865 lines (EN + Hinglish), 0 problems.
Still open: IMPRESSED roast 2 (2 lines), NAP/WAKE variety, weekly voice (T-W3-5), callbacks for stays_week / late_opens_week / longest_session_day (T-W3-4, waiting for ENGINE's slot names).

## Wave 3, T-W3-5 weekly voice (2026-11-07T05:20Z)
The builder shows one roast (or one weekly_quiet line when no pauses) plus one weekly_praise line only if the week had a Leave. So praise lines may claim only 'at least one Leave'. Reworded four that claimed more: weekly_any_029 ('Some Stays' claimed Stays), 030 ('More Leaves than I expected' claimed a comparison), 028 ('when it counted'), 007 ('small wins' for every pause). Added headline.4 and headline.quiet to MICROCOPY (new IDs, none changed; UI to wire headline.quiet only if it wants a quiet-week title). Quiet lines already avoid inventing numbers. Validator 0 problems.

## Wave 3, T-W3-2 finish: remaining banks (2026-11-07T05:23Z)
Read app_open, battery_low, charger_on, nap, wake, welcome line by line. Four rewrites: app_open messaging 'The thumbs are busy again' (false history, 'again' dropped), app_open productivity 'A work app. Rare.' (implied they never work; now 'A work app. I'm delighted.'), charger_on 'I forgive you for waiting' (mild guilt; now 'Peace treaty signed.'), wake 'Back already?' (assumed a short nap; now 'Awake. I was just warming up the nap.'). Everything else kept. The whole EN bank has now had a full human-style read; the audit is complete.

## Wave 3, T-W3-4 callbacks and D-019 (2026-11-07T05:28Z)
ENGINE message 20261107-0524: new facts stays_week, late_opens_week, top_app_minutes_day (slots app/count, app/count, app/minutes). Added 12 CALLBACK lines (4 per fact, roast 1, bondMin 2, one fact: tag each, only the slots ENGINE lists). Rules kept: stays lines quote only the Stay count; late-night lines are gentle (no sarcasm about sleep, they never fire while it is late night); top-app lines claim only 'top app of the day' and minutes. Not built by ENGINE, so no lines: longest unbroken session, bond change, first_pause_ever, leave_streak_today, days_together. EN bank 651 lines; validator 877 total, 0 problems.
D-019: no bank line mentions a number of apps (searched). MICROCOPY v0.9 section 18 rewords the five cap strings without the number; cap_reached uses {n}.

## Wave 3, D-020 naggy voice (2026-11-07T05:33Z)
Manager request 20261107-1230. Added 41 EN lines (bank 692), all within the D-020 floors (roast the situation, no guilt about worth, no body/mental-health/etc., claims only known facts):
- PAUSE_PROMPT escalation, any category: 6 tier1 ('again' is true from the 2nd pause) and 6 tier2 (use {count}); per category GAME/MESSAGING/SHOPPING/BROWSER/PRODUCTIVITY/OTHER: 1 tier1 + 1 tier2 each (SOCIAL/VIDEO already had tiers). Roast mix keeps roast 1-2 as the majority so Calm users stay mostly gentle; only 1 roast-3 line per tier (needs bond 1).
- PAUSE_PROMPT follow-ups ('you said five minutes'): 8, tag followup (reserved by the picker, so they appear only after a Stay).
- CALLBACK: 9 more across app_visits_day, pauses_day, leaves_week, app_visits_week, stays_week.
Bible v0.3: 'restraint' replaced by 'persistent but lovable', with the floors listed (section 12). MICROCOPY v1.0 section 19: Nag level setting copy (Calm/Naggy/Chaos, no numbers), onboarding.apps.body no longer says 'fewer is calmer', cap_reached retired. Check: no bank line mentions a number of apps. Validator 918 total, 0 problems.
Not done / needs ENGINE: unprompted APP_OPEN repeat-open lines ('third time tonight') need a count slot or a fact tag from ENGINE; callbacks only cover thresholds ENGINE lists. Tell me the new thresholds under Naggy and I will tune lines.

## Wave 3, repeat-rate pass from ENGINE's findings (2026-11-07T10:11Z)
ENGINE (20261107-0540/0546): repeats within 7 days are 26-55% of unprompted lines because pools are small. Added 49 EN lines (bank 741) where pools were smallest: APP_OPEN +16 (2 per category), CHARGER_ON +8, MUSIC +8, SESSION_LONG +10 (adds the first roast-2 GRUMPY line and more SMUG), LATE_NIGHT +8 (all SOFT, no jokes). All slots limited to the ones each trigger already provides; no claims beyond the trigger. Validator 968 total, 0 problems. Not yet added: PAUSE_PROMPT GRUMPY mood in SOCIAL/VIDEO/MESSAGING/SHOPPING/OTHER (ENGINE note 3) and more NAP/WAKE/IMPRESSED; will re-run ENGINE's simulation numbers once they publish a new run.
UI request 20261107-0547 answered in MICROCOPY v1.1 section 20.

## Wave 3, gap fill 2 (2026-11-07T10:11Z)
Added 30 EN lines (bank 771): PAUSE_PROMPT GRUMPY roast-2 lines for SOCIAL, VIDEO, MESSAGING, SHOPPING, OTHER (2 each, no 'again', no tier claims), NAP +6, WAKE +6, IMPRESSED +6 (slot-free). Validator 996 total, 0 problems.

## Wave 3, T-W3-9 linter and T-W3-10 nag tiers (2026-11-07T10:21Z)
**Linter:** content/tools/lint_lines.py (stdlib, run from anywhere). Errors: >90 chars with realistic slot values, undeclared/unknown slots, invalid review, banned words (self-harm idioms, body, mental-health labels, religion/caste/politics, medical and screen-time claims, guilt hooks, profanity EN+Hinglish), both tier tags. Warnings: same 2-word opener >3x per trigger/category, emoji density, words a human should glance at, native_reviewed without reviewed_by. `--require-nag` makes a missing tier tag an error; `--strict` fails on warnings. First run found 0 errors and 9 warnings; fixed the 7 opener repeats (4 productivity/followup, 4 session lines) and left 6 intended ones: Owner-canon 'seek help', affectionate 'idiot' (bond-gated), Jixxy calling himself 'lazy'.
**Nag tiers:** every line (EN and Hinglish) now carries exactly one of `calm_ok` / `nag_only`. Rule: nag_only = roast 3, or tier2 / escalation, or tier1 with roast 2+, or roast 2+ outside the Pause triggers (Pause roast 2 stays calm_ok because the Roast dial already gates it). Result: EN calm_ok 576 / nag_only 195; every EN trigger keeps at least 12 calm_ok lines (SESSION_LONG calm 21, WELCOME 12). Hinglish SESSION_LONG and MUSIC were thin for Calm, so I added 7 gentle Hinglish lines (bank 232). Validator 1003 total, lint with --require-nag: 0 errors.

## Wave 3, T-W3-11 string read-through (2026-11-07T10:22Z)
Read all 214 UI strings. 7 replacement texts and 1 deletion sent to UI (MICROCOPY v1.2 mirrors them). Themes: two strings contradicted D-020 ('nag you', 'comment now and then'), voice mixed first/third person on Home, one label mismatch (Roast help vs Gentle/Normal/Savage), one retired placeholder still in the file. Privacy and permission claims checked against the code's declared permissions in the brief; nothing overclaims. Dependency to remember: 'Stay 5 more minutes' hard-codes grantMinutes = 5.

## Wave 3, T-W3-12 a day in the life (2026-11-07T10:23Z)
Method: content/tools/day_sim.py replays one scripted day (38 events: 7 Instagram opens, YouTube, a game, charger, pokes, music, long sessions, late night) through the real EN bank with ENGINE's caps (Calm 3/day + 45 min gap, Naggy 15 + 10, Chaos 30 + 5), 200-line dedupe, roast 2, bond 1, calm_ok filter at Calm, tier preference for Pause. It is a rough hand-simulation, not the engine: no mood, no energy, no bond growth.
**What it showed**
1. **Wrong-hour claims (fixed):** pause_prompt_any_004 'Late one.' and pause_prompt_any_009 'It's late' could fire at 9:30 am; video_015 'your evening'. Now time-neutral ({time} slot in 009). Earlier audit missed them because they were not in a late-night trigger.
2. **Spreadsheet gag overused:** 11 of the 19 spreadsheet lines are Pause lines, so a heavy Instagram day shows it 4 times. Rewrote pause_stay_any_003 and 029 (door open, sofa warm).
3. **Follow-up sameness:** four of the follow-ups began 'Five...' so a third Stay in one evening read as a loop. Added 6 follow-ups that do not start with 'Five' (pause_prompt_any_049..054), 3 roast 1 and 3 roast 2.
4. **Tier ladder works:** at Naggy a 7-Instagram day goes: '...really? Instagram?' -> 'Second time today' -> 'pause 3, counting out loud' -> 'again?! calling a meeting' -> 'must really miss you' -> 'started a new column' -> 'I've run out of jokes. Your move.' Best beats: 'must really miss you' and 'run out of jokes'; weakest: the three spreadsheet-row variants (they read the same).
5. **Calm still hears a lot:** Nag level only limits unprompted lines, so a Calm user with 7 Pauses still got 30 Pause lines. That is by design (Pauses are the product) but worth an Owner look: a 'no escalation beyond tier 1' option for Calm would soften it. Not built, ENGINE's call.
6. **Possible double voice (ENGINE to check):** my sim let a LATE_NIGHT comment land on the same open as a Pause prompt. If the engine can do that, two bubbles stack; unprompted lines should wait while a Pause card is up.
7. **Limits of this test:** Chaos and Naggy came out identical because only about 10 unprompted chances exist in this script, so the 30/day Chaos cap never bound; the nag_only pools are barely exercised. Real repeats (ENGINE measured 26-55%) are unchanged by anything here. Needs a phone day.
Lint now 1009 lines, 0 errors. Bank EN 777 / Hinglish 232.

## Wave 3, third-open escalation, Calm tiers, after-Leave staging (2026-11-07T10:55Z)
- **APP_OPEN escalation (ENGINE 1039):** 60 EN lines tagged escalation + nag_only, 15 per group (social+video, game, messaging, browser/shopping/productivity/other), slots app/time only. 'third time' is true because the engine fires them exactly on the 3rd open of the same app in a local day, never at Calm. Roast mix: about half roast 1, a third roast 2, 7 roast-3 (bond 1). 8 Hinglish escalation lines added too (bank EN 859 / Hinglish 240).
- **Calm Pause escalation (ENGINE 1027):** only 27% of Calm Pause prompts matched the count tier. Added 24 calm_ok lines, roast 1: per category 2 tier1 + 1 tier2 (gentle 'again' and counts, no roasting). Expect Calm tier match to rise; ENGINE can re-measure.
- **D-024 after-Leave lines (Manager 1650):** drafted 14 EN (7 calm_ok, 7 nag_only, roast 1-3, only slot app, no time claims because no seconds slot exists) and 4 Hinglish, but they are STAGED in content/staging/after_leave_en.json and after_leave_hinglish.json, not in the live bank: without a reserved tag the picker would show 'Back already?' on ordinary first prompts. Waiting for ENGINE to reserve tag after_leave and tell me slots.
Validator 1101 total, lint 0 errors.
