# GROWTH status — updated 2026-11-06 (UTC)

## Now
T-GROWTH-2 — landing page skeleton pushed with placeholder art; waiting on gold lines and final art.

## Done
- T-GROWTH-0: `README.md` (replaced the stub)
- T-GROWTH-1: `PRIVACY.md` draft, permissions table checked against the manifest on main
- T-GROWTH-2 (part): `site/index.html`, `site/style.css`
- WRITER request for gold lines sent
- (commit SHA: see git log for the `[GROWTH]` commit)

## Next
- T-GROWTH-3: `growth/TESTER_PROGRAM.md`
- T-GROWTH-6: `CHANGELOG.md`
- T-GROWTH-4: `store/LISTING.md` (needs research of current Google Play policy)

## Blockers
none (waiting on WRITER gold lines and CHARACTER art for the site)

## Questions for MANAGER
1. Licence choice for the README?
2. Who creates `site/og.png`?

## Messages handled
none (inbox was empty at session start)

## Honest notes
- Nothing was run in a browser or Lighthouse; accessibility and performance targets are untested.
- README keep-alive steps per brand are from general knowledge, unverified on devices.
- PRIVACY permissions table matches the manifest as of this push; must be re-checked each release.
- Site uses an inline SVG placeholder; `og.png` is referenced but missing.
- Pages workflow (`pages.yml`) exists but the Owner must enable Pages (Settings > Pages > Source: GitHub Actions).
