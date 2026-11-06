# GROWTH status — updated 2026-11-06 (UTC)

## Now
First-wave tasks all delivered. Doing a consistency pass against what the team has built (docs aligned to real code and in-app copy). Waiting on a green CI build, art and screens before anything can be filmed or posted.

## Done
- Consistency pass: PRIVACY/README now say 30-day retention, 7-day viewer, erase-all (from reading `Retention.kt`, `WhatSawLogic.kt`, `SqliteStateStore.kt`); restricted-settings and keep-alive steps aligned to MICROCOPY v0.4; CHANGELOG brought up to date
- T-GROWTH-0: `README.md` (e0c06ef)
- T-GROWTH-1: `PRIVACY.md` draft, checked against manifest (e0c06ef)
- T-GROWTH-2 (part): `site/` skeleton with placeholder art (e0c06ef)
- T-GROWTH-3: `growth/TESTER_PROGRAM.md`
- T-GROWTH-6: `CHANGELOG.md` (Unreleased + planned 0.1.0, honest known issues) (92bbd9b)
- T-GROWTH-5: `growth/launch/` (README rules, 5 scripts, captions, WhatsApp/Product Hunt/Reddit drafts, 10 one-liners); all Jixxy lines verified verbatim against `content/gold_lines.md`
- T-GROWTH-4: `store/LISTING.md` with policy research and risks; messages to MANAGER and PLATFORM
- T-GROWTH-2: site now quotes WRITER's marketing-safe lines (3 Pause lines, 1 late-night, 1 praise), unedited, with the off-limits list followed

## Next
- T-GROWTH-2: add `og.png`; add real screenshots once UI screens are final
- Film/post nothing until a real build exists; then record the 5 scripts
- T-GROWTH-2: real screenshots; T-GROWTH-3: recruit testers once a build exists (Owner)

## Blockers
none

## Questions for MANAGER
1. Tester age: brief says 17+, D-006 says audience 18+. Align testers to 18+? (message sent)
3. DEVOPS: please add the tester feedback issue form draft from `growth/TESTER_PROGRAM.md` section 5 (I don't edit `.github/`).

## Messages handled
20261106-0322-WRITER-gold-lines-handoff.md, 20261106-0325-PLATFORM-queries-ack.md, 20261106-0329-MANAGER-decisions-play-and-licence.md (applied D-006), 20261106-0326-WRITER-gold-lines-update.md (switched to "Left without a fight" on site and in launch content)

## Honest notes
- Retention/erase claims come from reading code, not from running the app. CI on main is red (not my files); I did not investigate it.
- Launch content is unfilmed and unposted. Product Hunt/Reddit rules and limits are unverified; check before posting. Scripts assume UI behaviours (Leave instant, Stay delay, bubbles) that I haven't seen run.
- Play policy: foreground-service, sensitive-permission and data-safety rules were read from Google's pages on 2026-11-06. Usage-access declaration requirement, closed-test account rules, category and audience options, and listing length limits are NOT verified (marked in the file).
- Nothing run in a browser or Lighthouse; site accessibility/performance targets untested.
- Keep-alive steps per brand are from general knowledge, unverified on devices.
- Tester program is a plan; no testers recruited and no distribution channel confirmed beyond GitHub Releases APK.
- Changelog lists items based on the git log and status files, not on running the app.
- Pages needs enabling by Owner (Settings > Pages > Source: GitHub Actions).
