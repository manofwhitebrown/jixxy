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
