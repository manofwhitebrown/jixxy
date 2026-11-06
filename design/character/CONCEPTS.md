# Jixxy: silhouette concepts (T-CHAR-0)

Three original concepts, all vector-only, 128x128 viewBox, five colours or fewer, a dark outline so they read on light and dark apps. Every file uses the same layer ids (`shadow`, `arms`, `body`, `eyes`, `brows`, `mouth`, `accessory`, plus `lid_l/r`, `brow_l/r`, `arm_l/r`), so any of them can be animated procedurally with the same rig. None of them borrows from Clippy, Tamagotchi, Finch, Kirby, Pou or Tom.

**A. Blanket Bean** (`A_blanket_bean.svg`). A slumped bean wearing a periwinkle blanket like a hood, with one corner trailing down the left side. The silhouette is "lump with a flag". The blanket tail is a free animation lever: it swings on drag, flaps on fall, wraps closer for SOFT and SLEEP. All facial features are fully visible, so mood reads well even at 56 dp. Simplest to rig.

**B. Hoodie Blob** (`B_hoodie_blob.svg`). A coral hoodie with the hood up and a crooked peak flopping to the right, the face sitting in a dark opening, and a kangaroo pocket. Most distinctive silhouette of the three and the strongest "roommate" read. The trade-off is that the eyes are small inside the opening, so expressions are harder to read at 56 dp and the heavy-lid look needs larger lids.

**C. Slump Pillow** (`C_slump_pillow.svg`). A squished cream pillow with a stitched seam and tassels on two corners; the right tassel droops. Clear, squarish silhouette that differs from every round mascot. Big flat face means the best expression readability, but squash-and-stretch looks less natural on a rectangle, and it is the least "alive" at rest.

**Recommendation: A.** It is the simplest rig, the best balance of face readability and silhouette, and the blanket gives SOFT (late night) and SLEEP states a built-in visual. B is the pick if the Owner values silhouette above all. I am building the T-CHAR-1 skeleton against A unless told otherwise.

All three keep the heavy-lidded, slightly slumped default expression and flat-to-raised-inner brows, so the base look is lazy rather than angry. Palettes are in the SVG comments, and colours will move into the theme object in T-CHAR-6.
