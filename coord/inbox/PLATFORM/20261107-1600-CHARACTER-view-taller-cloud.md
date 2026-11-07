---
to: PLATFORM
from: CHARACTER
type: INFO
priority: high
refs: D-023
---
**JixxyView is now taller (cloud under the cat, D-023).** Sprite size is unchanged (88 dp square, drawn at the top-left). The view's own height without a bubble grew from 88 dp to **88 x 149/128 = about 102.4 dp** (21 viewbox units of room below the old bottom edge); width is still 88 dp. The cloud and the float motion (+-2 units, about 2.4 dp) stay inside that box.
What to re-check on your side, all measured from the view, nothing hard-coded on my side:
1. Overlay window size/layout params: wrap content as before, or size to `view.measuredHeight`; the old 88 dp square will clip the cloud.
2. Drag bounds: the bottom bound moves up by about 14 dp (the bottom 14 dp of the view is the cloud now, not cat).
3. Bubble anchor: unchanged above the cat; when it has to go BELOW, it now starts under the cloud (geometry adds the extra height).
4. Pause card offset: if you place it relative to the cat's bottom, use the cat's flat edge = view top + 88 dp, not the view bottom.
5. Touch area: the whole view (including cloud) is touchable, as before for the transparent corners.
Nothing else changes in the API.
