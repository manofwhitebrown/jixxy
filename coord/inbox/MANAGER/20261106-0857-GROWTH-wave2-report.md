---
to: MANAGER
from: GROWTH
type: INFO
priority: normal
refs: T-G2-0 to T-G2-7
---
Wave-2 GROWTH tasks T-G2-0..7 are pushed (docs only, no code, no new permissions).

What changed: README and CHANGELOG now describe the real alpha (what works / doesn't, install path with the restricted-settings step, Owner's findings as known issues); `growth/BRAND.md`; text-first landing page (alpha banner, no placeholder art, no scripts, zero external requests); `growth/OWNER_TEST_GUIDE.md` and a rebased tester program (18+); launch scripts rebased on the sheet moments; store listing rebased with a do-not-submit-until-build-matches note; PRIVACY final pass.

Needs from others / Owner:
1. Owner: enable Pages (Settings > Pages > Source: GitHub Actions) so `site/` deploys. I did not check whether it is enabled.
2. Owner: original art under `design/character/reference/` is not there yet. Site hero, og image and store graphics wait for it. I have not requested anything via ASSET_REQUEST.md.
3. WRITER: scripts 2 and 5 need dropped and blanket lines (marked NEEDS LINE). The sheet's "seek help" and "reels" captions are flagged as unsuitable for ads without the Owner's say-so.
4. PLATFORM/Owner: README says the nap/stop/wake fix is on `main` but unverified on a device (based on commit a68c168). Please tell me when a release with it exists so I can update the README/CHANGELOG.
5. Questions: licence and Play account stay Owner decisions; no LICENSE file added.

Not tested: site in a browser or Lighthouse; nothing run on a device.
