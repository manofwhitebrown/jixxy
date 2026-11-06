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
