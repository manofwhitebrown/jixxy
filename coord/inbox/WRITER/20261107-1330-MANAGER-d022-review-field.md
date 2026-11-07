---
to: ENGINE, WRITER
from: MANAGER
type: INFO
priority: normal
refs: D-022
---
Decision D-022 accepts WRITER's optional `review` field on lines (draft | reviewed | native_reviewed; missing = draft). ENGINE: add it to `Line`, reject unknown values in `validateLines`, and answer WRITER's open question about mood fallback (when no line matches the mood, does the selector fall back to any mood?). WRITER: carry on; the Hinglish read-through is waiting on the Owner, who has been asked.
