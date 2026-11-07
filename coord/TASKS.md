# TASKS  (MANAGER-owned; rewritten at Wave-3 handover, 2026-11-06 ~10:00 UTC)

**Milestones:** M0 reached. **M1 "Alive": code complete on main, NOT yet verified on a phone.** M2 "Pause works" and M3 tester build `v0.1.0` next. Main is green; rolling `latest` pre-release is live: https://github.com/manofwhitebrown/jixxy/releases/download/latest/jixxy-latest.apk

**Gate:** the Owner's device test of the Wave-2 build. Fix what they find before adding features.

| Role | Wave-3 focus |
|---|---|
| PLATFORM (AI-1, integrator) | device-test loop; Pause end to end (Leave->Home on Android 14/15); service survival (boot/update, Android 15 FGS rules); local diagnostics log; battery audit; release/targetSdk plan; weekly trigger |
| ENGINE (AI-2) | persist Pause runtime state (D-017); almostCaring per-line bondMin; callback memory; JaCoCo + real-bank simulation; Hinglish plumbing; weekly polish |
| UI (AI-3) | onboarding robustness + restricted-settings path; "Copy debug info"; Language row; accessibility; Home/What saw; weekly card; screenshots |
| CHARACTER (AI-4) | demo review; feel/size tuning from Owner; hi-res art + blink/yawn/stretch; motion budget; bubble + Pause card polish; mapping audit |
| WRITER (AI-5) | apply D-014..D-016; 2 am audit of EN bank; coverage gaps; callbacks; weekly voice; microcopy; Hinglish read-through sheet |
| DEVOPS (AI-6) | BUGS.md as truth; test sheets; real gates (validateLines, coverage, assets); signing plan; Pages check; licences list |
| GROWTH (AI-7) | Owner test guide; distribution memo; licence memo; tester kit; docs after each release; privacy final pass |

**Owner actions pending (one at a time):** test the `latest` APK; rotate the token; enable GitHub Pages; licence; Play account vs other distribution; hi-res art + 3 missing frames.

**Handled Manager inbox messages:** (to be listed at next sync; Wave-2 messages not yet individually answered)

## Manager notes
- Wave-3 briefs: AI-1..AI-7 `*_v3.md` (no secrets inside; the Owner pastes the token).
- Biggest open risks: Leave->Home on Android 14/15; foreground-service start after reboot/update; Play policy (specialUse FGS, overlay, package visibility).
