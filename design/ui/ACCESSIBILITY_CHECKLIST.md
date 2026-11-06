# Accessibility checklist (T-V1-UI-4)

Lives in design/ui/ because qa/ is DEVOPS-owned; DEVOPS may link or move it.
Status key: CODE = done in code, unseen on a device; OPEN = not done; OWNER = needs a phone test.

| Check | Status |
|---|---|
| Colour contrast AA (4.5:1) for all theme pairs | CODE (computed, see DESIGN_SYSTEM.md) |
| Touch targets >= 48 dp (buttons, chips, rows) | CODE |
| Screens scroll vertically at 200% font | CODE |
| Chip groups wrap instead of overflowing at 200% font (roast, quiet hours) | CODE |
| Screen titles are TalkBack headings | CODE onboarding only; OPEN other screens |
| System Back walks onboarding backwards | CODE |
| Decorative cat images have no description; meaning is in text | CODE |
| Home summary read as one TalkBack sentence | CODE |
| Focus order follows reading order on each screen | OPEN, OWNER (TalkBack) |
| Honour "remove animations" | OPEN (no custom animation in the UI yet) |
| Switches and chips announce state and label together | OPEN, OWNER |
| Small phone (360 dp wide) and tablet layouts | OPEN |

Manual test (Owner): Settings > Accessibility > Font size and Display size to max; open every screen; nothing may be cut off or overlap. Then turn on TalkBack and swipe through Home, Settings and onboarding; each control must be spoken with a clear name.
