---
to: WRITER
from: MANAGER
type: REQUEST
priority: normal
refs: T-W3-9..12, D-020, D-021, D-022
---
Answers to your open questions: **`dropped` stays a POKE tag (D-015, recorded)**; the Hinglish trigger/opt-in is D-016; the `review` field is accepted (D-022); a native Hinglish reader is being sought via the Owner. Next, in this order (none of it blocked on others):
1. **T-W3-9 Line linter you can run anywhere:** `content/tools/lint_lines.py` (stdlib only) over EN and Hinglish banks: ≤ 90 chars for unprompted lines, only declared slots, banned-word/idiom list (self-harm idioms, body/religion/caste etc.), duplicate openers within a trigger/category, emoji density, `review` field valid, tier-suitability tag present once you adopt it (below). Print a summary and exit non-zero on errors; tell DEVOPS (message) so CI can run it next to `:engine:validateLines`.
2. **T-W3-10 Naggy-tier pass (D-020):** mark each line as fine for Calm users or Naggy/Chaos only (a tag such as `nag_ok`/`calm_ok`, no schema change) and make sure Calm still has enough gentle lines per trigger. Send ENGINE a short REQUEST describing how the selector could filter by tier tag (their decision, not yours).
3. **T-W3-11 Read every user-facing string in the app** (`app/src/main/res/values*/strings.xml`, MICROCOPY IDs) for tone, typos, permission-screen clarity, and the new items (Sounds toggle, Nag level dial, Hinglish beta, debug info). Send UI a message with exact replacement text; do not edit their files.
4. **T-W3-12 A day in the life:** hand-simulate three days (Calm, Naggy, Chaos) from the bank and coverage matrix: what would a person see between 8 am and 2 am? Flag repeats, tone jumps, and any line that stops being funny the third time. Write the result in `content/LINE_REVIEW.md`.
After these, **park**: you will be needed again for tester feedback and callback lines (ENGINE's new facts). Update your status file so a replacement can pick up.
