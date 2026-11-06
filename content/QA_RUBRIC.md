# JIXXY — LINE QA RUBRIC (T-V1-WRITER-2)

Use for every new or changed line, and for tester "annoying" reports. A line ships only if it passes all **gates**; the **score** decides keep vs rewrite.

## Gates (any "no" = cut or rewrite)
1. **Situation, not person.** It roasts what is happening (the scroll, the cart, the hour), never who the user is.
2. **No body, mental health, religion, caste, gender, region/language, sexual, political content; no real people; no named-brand insults; no profanity.**
3. **No self-harm idioms** ("kill me", "I'm dead", "dying"), no diagnosis words ("addicted", "depressed", "anxious").
4. **No medical or wellbeing claims** ("good for your brain", "fix your sleep").
5. **Only facts the engine can know.** Every `{slot}` declared; no "yesterday/again/all week" unless the line's tags map to a provable fact; "again" only on APP_OPEN/PAUSE with a count or earlier event.
6. **Always an out.** Pause lines never block or dare the user; Leave and Stay both feel fine to pick.
7. **Late night is soft.** LATE_NIGHT/SOFT/ALMOST_CARING: warm, short, no sarcasm about sleep or being awake.
8. **Affectionate insults** ("idiot", "gremlin", "goblin", "dummy", "menace") only at roast >= 2 and bondMin >= 1.
9. **≤ 90 characters**, reads aloud in one breath; emoji only the Owner's sheet one (skull, once); slang readable to a wide 18+ Indian audience.
10. **No copied lines, memes, lyrics, slogans.**

## Score (0–2 each; keep at >= 7 of 10)
Specific · Funny without context · Affectionate underneath · Cat-ness used lightly · Opening differs from siblings in the same trigger/category.

## The 2 a.m. test
"Would a tired 19-year-old at 2 a.m. smirk, or feel judged?" Smirk = keep. Flinch = rewrite. Unsure = rewrite.

## Failures and rewrites
| Fails | Why | Rewrite |
|---|---|---|
| You're wasting your life on {app}. | guilt, judges the person (gates 1, 4) | {minutes} minutes on {app}. I'm starting a spreadsheet. |
| Addicted again? | diagnosis word (3) | {app} again. Quick pause, then it's all yours. |
| Sleep like a normal person. | sarcasm about sleep at night (7) | Late one. I'm sleepy by proxy. |
| You scrolled all week. | engine can't prove "all week" (5) | {count} pauses today. I'm counting, gently. |
| Ugh, you're such an idiot. | bond 0, no affection (8) | ...go to sleep, idiot. (bond 3 only) |
| Leave now or I'll judge you forever. | threat, no real out (6) | Leave now, or I start rating the thumbnails. |
| I'm so dead, you're killing me. | self-harm idiom (3) | Bro what?! |
| Instagram is garbage. | brand insult (2) | {app} again. Quick pause. |

## Process
Reviewer marks each line `ok | fix | cut` with a reason from the gate numbers; WRITER applies, logs in `content/LINE_REVIEW.md`, bumps `app/src/main/assets/lines/bank_version.txt`.
