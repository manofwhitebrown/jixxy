# Licence options for Jixxy (memo for the Owner)

Written 2026-11-07 by GROWTH. **I am not a lawyer; this is not legal advice.** Nothing is added to the repo until you choose. Marks: **[verified]** = read on a named page; **[unverified]** = general knowledge, check first.

## What a licence does, in one minute
Right now the repo has **no licence file**, so by default **all rights are reserved**: people can read the code on GitHub but may not legally copy, change or share it. A licence is your written permission, with conditions. You choose one for the **code** and, separately, terms for the **art, name and lines**, because the cat is the part with real brand value.

## 1. Code: your options

| Option | What others may do | What it means for you | Fits F-Droid / IzzyOnDroid? |
|---|---|---|---|
| **All rights reserved** (no licence) | Read only | Maximum control. No outside contributors, no reuse, no open store. | **No**, both need an open licence [verified: F-Droid and IzzyOnDroid policies] |
| **MIT** | Do almost anything, including closed-source commercial forks, if they keep your copyright notice | Easiest, most popular. A copycat can ship a closed, ad-filled clone. | Yes |
| **Apache-2.0** | Same as MIT, plus an explicit patent grant | Like MIT with slightly more legal protection. The Android libraries we use (AndroidX, Compose, Kotlin) are under Apache-2.0, so it matches [unverified: have DEVOPS list our dependency licences] | Yes |
| **GPL-3.0** | Use and change freely, but any shared version must stay open under GPL-3.0 | Discourages closed clones. You, as sole author, can still publish your own app on any store. Third-party contributors' code can make store terms awkward (the 2011 VLC / Apple App Store dispute is the known example) [unverified] | Yes |
| **AGPL-3.0** | Like GPL, plus network use counts as sharing | Built for server software. Jixxy has no server and no network, so it adds nothing over GPL. | Yes, but pointless here |

**Commercial use:** MIT and Apache-2.0 let anyone sell your code. GPL lets people sell it only if they also share their source. All-rights-reserved lets nobody use it without asking you.
**Forks:** with MIT/Apache a fork can go closed. With GPL a fork must stay open. In all open cases people may fork, but **they may not use your cat or name** if section 2 is done properly.
**Past versions:** a licence cannot be taken back for copies people already have, so pick calmly. You can add a more open licence later; going stricter only affects future versions.

## 2. Art, name and lines: a separate choice
The cat sheet, the 15 sprites, the icon, the line bank and the name "Jixxy" are what people recognise, and the art is yours. Common practice is **open code, closed brand**: say in writing that the licence for code does **not** cover them.

| Option | Meaning | Consequence |
|---|---|---|
| **A. All rights reserved** (recommended) | Nobody may use the cat, sprites, icon, name or lines without your permission | Forks must draw their own character. Strongest control. |
| **B. Source-visible, no commercial reuse** (e.g. Creative Commons BY-NC-ND) | People may share the art unchanged, with credit, not for money, no edits | Simple, but "non-commercial" licences are **not** free-software licences, so F-Droid-style stores may flag or refuse them [unverified: ask at submission; F-Droid's policy says it flags non-free assets as an anti-feature rather than always rejecting, but I did not confirm the exact wording]. |
| **C. Fully open** (e.g. CC BY 4.0 or CC0) | Anyone may reuse and sell it | Easiest for stores and fans. You lose control of the character, including for merchandise. |

**Name and trademark:** a copyright licence does not protect the name. Keep a short note: "the name Jixxy and the cat are not licensed", and check the name before launch (`growth/LEGAL_READINESS.md`, section 2).
**Skins later:** the brief leaves room for skins. If you ever sell them, option A keeps that possible; B and C make it harder.
**Ownership check first:** before licensing the art to anyone, confirm who made it and how (`growth/LEGAL_READINESS.md`, section 3). If the images are purely AI-generated, copyright protection may be weak in some countries and a "rights reserved" label may not stop copying [verified for the US: Copyright Office report, Jan 2025, as summarised in LEGAL_READINESS]; India's position is **unchecked**.

## 3. Other people's text and code
Today everything in the repo was produced inside this project (by the Owner and the team's AIs). Do not accept outside code or art until you decide how: either "contributions come under the same licence" in a short CONTRIBUTING note, or no outside contributions yet. The line bank was written by the writer role; if you want line texts open or closed, include them in section 2 (the recommendation below keeps them closed).

## Recommendation
- **Code: GPL-3.0** if you mainly want to stop closed copycat clones and still be allowed on F-Droid-style stores; **Apache-2.0** if you want the widest adoption and don't mind clones. For a cute app that could attract ad-filled copies, I lean **GPL-3.0**.
- **Art, name, lines: option A (all rights reserved)**, written in a separate short file so it cannot be read as part of the code licence.
- Stay with no licence file until you have answered the art-ownership questions. Waiting costs nothing; testers still get the app.

## The exact choice you must make
Reply with three short answers:
1. **Code licence:** MIT, Apache-2.0, GPL-3.0, or keep all rights reserved for now.
2. **Art, name and lines:** A (all rights reserved), B, or C.
3. **Outside contributions:** none for now, or "same licence as the code".

When you reply, GROWTH will not add the files itself, because `LICENSE` sits outside the files this role owns and the root of the repo is shared. I will send PLATFORM/DEVOPS a message with the exact file text (one `LICENSE` and one `ART_AND_BRAND.md`), and update the README licence line.

Sources: F-Droid inclusion policy https://f-droid.org/docs/Inclusion_Policy · IzzyOnDroid policy https://izzyondroid.org/docs/general/AppInclusionPolicy/ · Distribution memo `store/DISTRIBUTION_OPTIONS.md` · Legal list `growth/LEGAL_READINESS.md`.
