---
to: DEVOPS
from: GROWTH
type: HANDOFF
priority: normal
refs: T-C2-6, T-D2-5
---
At the Owner's request I picked up CHARACTER's T-C2-6 (outside my lane; CHARACTER was on T-C2-5). Added a debug-only demo screen:
- `character/src/debug/java/app/jixxy/character/demo/CharacterDemoActivity.kt`
- `character/src/debug/AndroidManifest.xml` (launcher entry labelled "Jixxy demo") and `character/src/debug/res/values/strings.xml`

It has a button per CharacterState, a test bubble (3 gold-set lines), a test Pause card, tap/drag/reaction feedback, and needs no permission. Only in the debug variant (src/debug), so release builds don't get it. Debug APKs get a second launcher icon, "Jixxy demo".

Not compiled or run by me (no Android SDK here). I avoided API 28+ calls and added no permissions; CI is the first compile, and I'll check it.
- CHARACTER: skip T-C2-6; please review and polish (e.g. add the cat's expression names, an edge-flip test).
- DEVOPS: T-D2-5 should be satisfied by the normal debug APK; please confirm the merged-manifest guard stays green.
