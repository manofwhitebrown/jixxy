# GROWTH status — updated 2026-11-06 09:05 UTC

## Takeover: 2026-11-06 08:54 UTC
I am AI-7 GROWTH (Wave 2), replacing the previous GROWTH AI. Read: predecessor's status, TASKS, DECISIONS, README, PRIVACY, CHANGELOG, site/index.html.

### What I found that is stale or overclaims (T-G2-0 audit)
- README: pitch is the generic "tiny roommate", no cat, no brand lines; install path says "Follow Jixxy's in-app setup" although the launcher is still PLATFORM's temporary 3-button screen; no Known limitations for the alpha findings; says first build is `v0.1.0` being prepared, but `v0.0.1-alpha` exists.
- README: describes the Pause card, bubbles and reactions as if working. Brain is NoopBrain, Pause is a stub, so none of that is visible yet.
- CHANGELOG: no `v0.0.1-alpha` entry; "CI is failing" known issue is outdated (main is green); Owner findings missing (potato sprite, temporary launcher screen, nap/stop/wake bug, default robot icon, no speech).
- site/index.html: hero is the old placeholder round blob (the "potato"), alt text calls it "a sleepy round character"; favicon is an emoji; no alpha banner; Pause example card is shown as if live; the line-rotation script presents lines as app behaviour.
- PRIVACY: content matches the manifest as last checked; needs a re-check and a final pass (T-G2-7).
- store/LISTING.md, growth/launch/*, growth/TESTER_PROGRAM.md: written for the old concept character and the pre-alpha state; to be rebased (T-G2-4/5/6). Not yet re-read line by line.

### What I will do first
T-G2-1 README + CHANGELOG, then T-G2-2 `growth/BRAND.md`, then T-G2-3 text-first landing page (no cat art until Owner's reference files land in `design/character/reference/`).

## Now
Wave-2 list T-G2-0..7 delivered. Waiting on Owner art, Pages enabling and a release containing the nap fix.

## Done (Wave 2)
- 2026-11-06: overlay/usage-access policy re-check (found no specific rule or form; see store/LISTING.md), CHANGELOG/README synced to qa/BUGS.md "fixed in code" states.
- Owner-approved help for CHARACTER: debug-only CharacterDemoActivity (T-C2-6, 7092441), CI green (run 42195396490). Not seen on a phone.
- Owner-approved cross-lane unblock: fixed two parsing bugs in DEVOPS scripts (apk_cert.sh b801214, check_apk_cert.sh ae3979f); CI on main green afterwards (run 42194034786). DEVOPS to review.
- Site: cat hero + 8-expression strip from D-013 crops, 57 KB (525c887)
- T-G2-0 takeover audit (2ee6c46)
- T-G2-1 README + CHANGELOG (e07ddad)
- T-G2-2 growth/BRAND.md (7280d66)
- T-G2-3 site text-first (6d7bf57)
- T-G2-4 OWNER_TEST_GUIDE + tester program (037c6d7)
- T-G2-5 launch scripts rebased (84a3670)
- T-G2-6 store listing (e518874)
- T-G2-7 PRIVACY pass (4e9b110)

## Done (Wave 1, by predecessor; see git log)
README, PRIVACY draft, site skeleton, tester program, launch pack, store listing draft, consistency pass.

## Next
Hero/expression strip and og image once the Owner's art lands; screenshots once the real app runs; re-read overlay/usage-access Play policy; update README/CHANGELOG when a release has the nap fix.

## Blockers
none blocking. Waiting on: release containing the nap fix (README/CHANGELOG update); hi-res art (already requested by CHARACTER in ASSET_REQUEST.md); Owner enabling Pages; red CI step "signing certificate" is with DEVOPS/PLATFORM.

## Questions for MANAGER
none yet

## Messages sent
- coord/inbox/MANAGER/*-GROWTH-wave2-report.md

## Messages handled
- 20261106-0322-WRITER-gold-lines-handoff.md
- 20261106-0325-PLATFORM-queries-ack.md
- 20261106-0326-WRITER-gold-lines-update.md
- 20261106-0329-MANAGER-decisions-play-and-licence.md

## Honest notes
- I cannot run Android or a browser here; nothing is device-tested. Everything I write about the app is taken from the brief, the code and status files, not from running it.
- Site not opened in a browser, Lighthouse not run (target >= 90 unverified). Pages deploy not confirmed; Owner must enable Pages.
- README "What works today" comes from the brief and commit messages. The nap fix on main is described as unverified.
- Policy re-check read Device and Network Abuse only; no overlay-specific rule found, which is not proof none exists. og:image not added: needs the final Pages URL (Owner must enable Pages).
- Manual test steps for the Owner are in growth/OWNER_TEST_GUIDE.md.
