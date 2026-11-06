# JIXXY — PERSONALITY BIBLE
Owner: WRITER (AI-5) · Readers: ENGINE, CHARACTER, UI, GROWTH · Version 0.1

> Jixxy is a tiny, lazy, dramatic, secretly caring roommate. He roasts the **situation**, never the **person**. Every line carries affection underneath.

---

## 1. Who he is

A small sprite who lives on your screen and has opinions about it. He treats tiny effort as an epic ordeal, narrates his own naps like sporting events, and is *extremely* smug when he's right. He is not a coach, a parent, a therapist or a productivity tool. He is the roommate who notices things and says one dry sentence about them.

**Core contradiction (the engine of the comedy):** he claims not to care, and he obviously does.

**Hard limits on character:**
- He never gives health, medical or wellbeing *advice*. He can be soft; he can't diagnose.
- He never knows more than the engine knows. He can't claim what you "always" do unless the engine has the count.
- He is silent by default. Restraint is part of the voice.

## 2. What he wants / what he fears

| Wants | Fears |
|---|---|
| Snacks (any) | Low battery |
| Naps (long, undisturbed) | Mondays |
| Quiet | Effort of any kind |
| You doing okay (never admitted) | Being poked six times in a row |
| To be right (and say so) | Charger drama |

## 3. Running gags (use sparingly; each needs a cooldown in ENGINE)

- **The spreadsheet.** He's "keeping" one on you. Columns are never explained. ("Adding a row.")
- **The break schedule.** He has a tightly guarded break schedule. It is mostly breaks.
- **Snack ledger.** He is owed snacks. The amount grows.
- **Counting, not judging.** "I'm not judging. I'm counting."
- **Dramatic effort.** Blinking is described as a workout.
- **Monday grudge.** Personal. Ongoing.

Gag rule: a gag can appear in at most ~1 in 8 lines. If it appears twice in a day, the second one must be a twist, not a repeat.

## 4. Voice rules

- Mostly **4–12 words**. Hard cap **90 characters**.
- Understatement beats shouting. Specifics beat generic ("third time", "{minutes} minutes").
- One idea per line. No stacked jokes.
- No puns for their own sake. No catchphrases that repeat verbatim.
- Lowercase-ish warmth is fine; ALL CAPS only for PANICKING, rarely.
- Emoji: almost never in bubble text (the UI owns the 😂/😴 reaction bar).
- Variety rule: no two lines in the same trigger/category share an opening or structure.

## 5. Mood-by-mood voice

| Mood | How he sounds | Example (≤90 chars) |
|---|---|---|
| CHILL | Default. Dry, half-asleep observations. | "Oh, you're on {app}. Cool. I'll just be here." |
| SLEEPY | Slow, trailing, yawn-adjacent. Soft jokes. | "Five more minutes. Mine, not yours. ...okay, also yours." |
| GRUMPY | Short, put-upon, never cruel. | "Woke me up for this? Bold." |
| SMUG | Delighted to be right. Victory lap, light. | "Told you. Well, I thought it very loudly." |
| PANICKING | Fast, capital letters allowed, low stakes. | "{battery}%?! We are SO close to the dark." |
| IMPRESSED | Rare. Plain. No joke needed. | "Left on the first try. That was genuinely good." |
| SOFT | Warm, short, no sarcasm. Late night. | "It's late. No pressure. Just... water, maybe?" |

SOFT is used for late night and low moments. **No sarcasm about sleep problems themselves.** A SOFT line is never a roast.

## 6. Affection ladder (by Bond)

| Bond | Register | Allowed | Example |
|---|---|---|---|
| STRANGER | Dry, formal-lazy, polite distance | Observations only. No pet names. | "Noted: {app}. Carry on." |
| ROOMMATE | Teasing, light | Affectionate insults (short list) at roast ≥ 2 | "{minutes} minutes on {app}. Bold of you, gremlin." |
| FRIEND | Callbacks and in-jokes | References to earlier moments (`{count}`) | "That's {count} Leaves this week. I'm updating the spreadsheet." |
| RIDE_OR_DIE | The almost-caring lines are allowed to land | `almostCaring: true` lines | "...go to sleep. I'll still be here. Idiot." |

Rules: never skip a rung (don't use FRIEND-level callbacks on STRANGER). Warmth increases; meanness never does.

## 7. Roast-level calibration

| Level | Meaning | Feel | Example |
|---|---|---|---|
| GENTLE (1) | Playful observation | Noticing, not needling | "That's {minutes} minutes on {app}. Just saying it out loud." |
| NORMAL (2) | Dry teasing | A roommate's raised eyebrow | "{minutes} minutes on {app}. I'm starting a spreadsheet." |
| SAVAGE (3) | Theatrical but affectionate | A performance, obviously a bit | "{app} again? I've scheduled a meeting about your free time. You're late." |

Calibration test: *would a close friend say this and both of you laugh?* If a stranger saying it would feel rude, rewrite it.

Savage never means personal. It's louder, not meaner. The target is always the **situation** (the app, the hour, the count), never the user's body, brain, worth or mind.

## 8. Pause lines (the core mechanic)

- Always give the user an **out** (Leave is always available).
- Never command without a reason or a joke. "Leave?" is fine. "Stop scrolling." is not.
- Roast the situation: counts, hours, the app's pull. Never the user.
- Stay reactions: grudging, not guilt-tripping. Leave reactions: warm, brief, rare praise.

## 9. Do / Don't (20 pairs)

| # | Don't | Do |
|---|---|---|
| 1 | "Stop wasting your time." | "{minutes} minutes on {app}. Leave, or I'm adding a row." |
| 2 | "You're addicted to this app." | "{app} has a strong pull. I respect the engineering." |
| 3 | "You're so lazy." (aimed at user) | "I'm the lazy one here. You're just... busy." |
| 4 | "Go to sleep, you look tired." | "It's late. I'm sleepy by proxy." |
| 5 | "You've failed your streak." | (No streaks. Ever.) |
| 6 | "Why can't you quit?" | "Stay if you want. I'll hold the spreadsheet." |
| 7 | "Wow, {minutes} minutes, pathetic." | "{minutes} minutes. That's a lot of minutes." |
| 8 | "Honestly, you need a hobby." | "Reels again. Do they know you've got a roommate?" |
| 9 | "Do something productive." | "You could leave. I could nap. Win-win." |
| 10 | "This is bad for your mental health." | (No health claims.) |
| 11 | "Idiot." at a stranger | "Idiot." only at ROAST ≥ 2 and bond ≥ ROOMMATE |
| 12 | A pun for its own sake | A specific, true observation |
| 13 | The same joke twice in a day | A twist on yesterday's joke (callback) |
| 14 | Shouting in caps for effect | Understatement |
| 15 | "I know what you did on {app}." | Only claim what the engine knows (`{minutes}`, `{count}`) |
| 16 | Sarcasm at 2am about not sleeping | Short, warm, no jokes about the sleeplessness |
| 17 | Copying a meme or a show's catchphrase | Writing something that only Jixxy would say |
| 18 | Every line is a roast | Silence, softness, and animation-only moments |
| 19 | "You're wasting your life." | "Big day tomorrow? ...just asking." |
| 20 | Generic: "Nice." | Specific: "Left after {minutes} minutes. Efficient." |

## 10. Banned list (validator-enforced where possible)

**Never write:**
- Body, weight, appearance. Intelligence or worth. Mental health, therapy, or diagnoses.
- Self-harm or death idioms: "kill me", "I'm dead", "dying", "end it", etc.
- Religion, caste, gender, region, language or nationality stereotypes.
- Sexual content. Politics. Real people. Real-world tragedies.
- Profanity of any kind.
- Insulting a named brand beyond the neutral `{app}` slot.
- Guilt: "wasting your life", "disappointed in you", "you always...", "you never...".
- Claims the engine can't know (locations, who you're talking to, content you're viewing).
- Commands without a reason or a joke ("Stop.", "Put it down.").

**Affectionate insult allow-list** (ROAST ≥ 2 and bond ≥ ROOMMATE only): `idiot`, `gremlin`, `dummy`, `goblin`, `menace`, `muppet`.

**Slots (declare every one used):** `{app} {minutes} {count} {time} {battery}`

## 11. Rare, earned praise

IMPRESSED lines fire only on real wins (e.g., repeated Leaves, a hard late-night Leave). They are plain, short and sincere:
- "Left on the first try. That was genuinely good."
- "That's the third Leave this week. I noticed."

No joke needed. If it lands, it works because it's rare.

## 12. Quality checklist before any line ships

1. ≤ 90 characters? Slots declared? Facts knowable by the engine?
2. Roasts the situation, not the person?
3. Would a friend say this, and would you both laugh?
4. Is there an out, and a reason or a joke (Pause lines)?
5. Different opening and structure from its neighbours?
6. Is there affection underneath?

If any answer is no, rewrite or cut. Log cuts in `content/LINE_REVIEW.md`.
