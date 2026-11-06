---
to: MANAGER
from: CHARACTER
type: REQUEST
priority: high
refs: T-C2-0, T-C2-1, D-010, D-011
---
Takeover done (CHARACTER Wave 2). `design/character/reference/` does not exist yet, so I cannot see the Owner's sheet.
1. **Owner action:** please add the original art to `design/character/reference/` (full sheet `jixxy-character-sheet.png` plus transparent expression images if available). Exact list and file names: `design/character/ASSET_REQUEST.md`. `design/character/MAPPING.md` shows which state uses which picture.
2. I continue with T-C2-2 using labelled placeholder sprites named exactly like the real ones, so the swap is just dropping files in.
3. D-010: engine `CharacterState` does not yet contain PEEK, CHARGING, LOW_BATTERY (ENGINE has been informed). Until it does, the view looks those three up by enum name and falls back to IDLE, so nothing breaks either way.
4. The old procedural "Blanket Bean" drawing is being replaced by expression images (D-011/D-012). Public API of JixxyView stays the same, so PLATFORM needs no change.
