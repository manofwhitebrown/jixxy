# GROWTH status (Wave 3, AI-7) — updated 2026-11-07

## Takeover: 2026-11-07
Found (T-G3-0 audit): README, site "What works today" and CHANGELOG still described the old alpha (placeholder sprite, 3-button screen, silent) while `latest` carries the real app in code; OWNER_TEST_GUIDE pointed at the Releases page; TESTER_PROGRAM used a Google Form and had no permissions table; store/LISTING.md header said "alpha, placeholder art"; no distribution or licence memo. Not stale: PRIVACY (manifest re-read: SYSTEM_ALERT_WINDOW, FOREGROUND_SERVICE(+SPECIAL_USE), PACKAGE_USAGE_STATS, POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED, `<queries>` launcher intent; no INTERNET).

## Now
Staged D-021 sounds and D-020 dial claims in growth/PENDING_CLAIMS.md (not in docs until a build ships them). D-019/D-020 applied to all public docs (number-free wording, naggy-by-design line, not claimed live: code still has MAX_TARGET_APPS=3 and maxUnpromptedPerDay=6). Play risk re-assessed in DISTRIBUTION_OPTIONS B8.
T-G3-0 to -4, -6, -7 pushed. Next: T-G3-6 PRIVACY final pass (blocked on PLATFORM's exact diagnostics-log contents), then T-G3-7 shot list, then docs after the Owner confirms a build.

## Done (Wave 3)
- T-G3-6 PRIVACY.md final pass: debug-report section written from DiagnosticsRepository.kt KDoc (chosen-app package names ARE in the report; others redacted); notification actions, weekly-card wording and Data-safety caveat fixed. Draft for Owner review.
- T-G3-1 growth/OWNER_TEST_GUIDE.md rewritten (latest APK link, restricted-settings step, 8 things, report format; "Copy debug info" marked not-yet-in-app).
- T-G3-2 store/DISTRIBUTION_OPTIONS.md (A GitHub / B Play / C IzzyOnDroid+F-Droid / D 2027 developer verification; sources, [unverified] flags, decisions for Owner).
- T-G3-3 growth/LICENCE_OPTIONS.md (no licence file added).
- T-G3-4 growth/TESTER_PROGRAM.md (WhatsApp invite, permissions table, 5-question plain-text form, no tracking).
- Docs synced: README, CHANGELOG [Unreleased], site "What works today", store/LISTING.md header.

## Next
1. Waiting on the Owner's phone report. Then:
2. After Owner confirms a build: README/CHANGELOG/site "in this release" + known issues from qa/BUGS.md.

## Blockers
none. Lighthouse and the site were not run (no browser here).

## Questions for MANAGER
Please record the Owner decisions needed: licence (growth/LICENCE_OPTIONS.md), distribution (store/DISTRIBUTION_OPTIONS.md), release signing key, Pages.

## Messages handled
20261107-0517-PLATFORM-diagnostics-privacy.md, 20261107-0525-MANAGER-unblocked-privacy-pass.md, 20261107-0518-DEVOPS-claims-guards-done.md (do not claim more than 'no networking/analytics library coordinates in Gradle files' until DEVOPS gap 3 lands). B-006 noted: do not use the default in-app sprite as final art on site/store.
20261106-1730-WRITER-safety-tone.md (noted; SAFETY_LINES.md rules apply to store tone). Earlier Wave-2 messages listed in the history below.

## Honest notes
- Site static audit 2026-11-07 (by reading files, NOT Lighthouse; no browser available here): 92 KB total, 8 local webp images (4 to 9 KB each), every img has alt, html lang/title/description/viewport present, no external scripts/fonts/images. Lighthouse >= 90 is still unverified; run it in Chrome DevTools on the Pages URL once Pages is on.
- Read-only scan of app resources, Kotlin and content for stale 'up to 3' / '6 a day' / 'silent by default' wording: none found.
- I tested nothing on a phone and did not open the site in a browser. All app behaviour in docs is from the manifest, code names, status files and qa/BUGS.md.
- Distribution facts: Google/F-Droid/IzzyOnDroid pages were read via search snippets on 2026-11-07, not full-page reads. Items marked [unverified] in the memo are third-party or memory. India payment methods, F-Droid signing behaviour and non-free-asset handling are unchecked.
- Owner test steps: growth/OWNER_TEST_GUIDE.md (install the latest APK, 8 things, how to reply).

---
# Previous (Wave 2) notes

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
T-V1-GROWTH-1 and -6 done; next -2 (Play declarations), then -4 (site Lighthouse) and -5 (beta ops).
## (older) Now
Wave-2 list T-G2-0..7 delivered. Waiting on Owner art, Pages enabling and a release containing the nap fix.

## Done (Wave 2)
- T-V1-GROWTH-6 growth/LEGAL_READINESS.md (licence options, trademark, art ownership, Play/privacy/age-rating questions; mostly [unverified], not legal advice). Owner needs to answer L1-L5.
- T-V1-GROWTH-1 growth/CLAIMS.md (24 claims, proof levels, gaps sent to DEVOPS/PLATFORM/ENGINE). Messages handled: 20261106-1600-PLATFORM-v1-growth-work.md, 20261106-1530-CHARACTER-store-icon.md, 20261106-1330-WRITER-marketing-lines.md
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
