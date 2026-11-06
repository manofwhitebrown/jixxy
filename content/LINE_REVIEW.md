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
