# DECISIONS  (MANAGER-owned, append-only)

- **D-001 (2026-11-06 03:29 UTC)** Compile-green freeze: code roles fix only compile/test/lint errors until main is green. Non-code roles continue.
- **D-002 (2026-11-06 03:29 UTC)** CI failure annotations (via check-runs API) are the official error channel; raw logs are unreachable from our sandboxes.
- **D-003 (2026-11-06 03:29 UTC)** Contract amendments accepted: ENGINE's extra types (ReactionKind, PauseChoiceKind, ReactionKindOut, BrainState, BrainSnapshot, UsageEvent/UsageEventType) and BrainConfig; see CONTRACTS.md.
- **D-004 (2026-11-06 03:29 UTC)** Character concept A (Blanket Bean) is provisional until the Owner chooses; art swap is cheap (same layer ids).
- **D-005 (2026-11-06 03:29 UTC)** app_categories.json schema: {"overrides":{pkg:CATEGORY},"protected":[pkg]}.
- **D-006 (2026-11-06 03:29 UTC)** Provisional: v1 audience 18+, Play category not Health & Fitness. Licence and paid developer account: Owner decisions, pending.
- **D-007 (2026-11-06 03:29 UTC)** Tests live with their owner's package under app/src/test/java/app/jixxy/<ui|platform|data>/**.

- **D-008 (2026-11-06 03:34 UTC)** While PLATFORM is unavailable, MANAGER may make minimal compile-error fixes in PLATFORM-owned paths, each logged in PLATFORM's inbox. Features stay frozen.

- **D-009 (2026-11-06 03:41 UTC)** M0 reached: main is green (run 42157822863) and a debug APK artifact exists. Compile-green freeze D-001 is **lifted**. New rule: never push to main a change that you know turns CI red; check annotations after every push (scripts/ci_errors.sh) and fix your own breakage first. D-008 (Manager fixes in PLATFORM paths) stays in force only while PLATFORM is unavailable.

- **D-010 (2026-11-06 08:58 UTC)** CharacterState gains PEEK, CHARGING, LOW_BATTERY (additive).
- **D-011 (2026-11-06 08:58 UTC)** Raster expression sprites allowed; budget: each image <= 512x512, character module resources <= 3 MB.
- **D-012 (2026-11-06 08:58 UTC)** Jixxy is the Owner's black fluffy cat; concepts A/B/C retired.
- **D-013 (2026-11-06 08:58 UTC)** MANAGER cut the 15 expression sprites from the Owner's sheet and committed them (reference sheet, lossless crops, WebP in character/src/main/res/drawable-nodpi, icon face) so M1 is not blocked on art. CHARACTER owns the files from now on and may re-cut or replace them; see design/character/reference/CROPS.md.

- **D-014 (2026-11-06 UTC, Wave 3)** ENGINE's eight judgement calls accepted: quiet hours silence only unprompted lines (Pause still fires); Pause exempt from energy and daily cap; SMUG = user tapped Stay (10 min); late night 23:00-01:00 forces SOFT; energy-0 pokes use `on_break` lines; nap owned by the platform (`NapPolicy`); runtime timers not persisted except Pause state (D-017). `almostCaring`: the engine uses each line's own `bondMin`, no global floor; WRITER marks mild lines ROOMMATE and heartfelt ones RIDE_OR_DIE; cap "once per 3 days" stays.
- **D-015 (Wave 3)** "Dropped" stays a `POKE` line with tag `dropped`; no new trigger.
- **D-016 (Wave 3)** Hinglish is an opt-in beta: off by default, `language="hinglish"`, labelled beta in the UI, English default; not shown to wider testers until a native-speaker read-through.
- **D-017 (Wave 3)** Pause runtime state may be persisted via additive, default-valued `BrainState` fields. ENGINE posts INFO to ALL on push.
- **D-018 (Wave 3)** Cross-lane rule (replaces D-008): edit another role's file only if main is red because of it or the Owner approved, the owner is silent, the change is minimal, and owner + MANAGER are told with the SHA. PLATFORM is the designated integrator.

- **D-019 (2026-11-07, Owner request)** The Pause-app cap rises from 3 to **8**. Single source of truth: `BrainConfig.maxTargetApps` (ENGINE); UI reads that constant instead of its own. Everything else about Pause is unchanged (per-app grace after Stay, instant Leave, roast dial, PROTECTED apps never paused). Public docs and UI copy should avoid hard-coding the number where possible ("the apps you choose"); where a number is needed, use the constant's value. Supersedes every "max 3" in earlier briefs and docs.

- **D-020 (2026-11-07, Owner decision: "let Jixxy be naggy, that is who he is")** Naggy is the default personality. Supersedes the old restraint numbers (≤ 6 unprompted/day, ≥ 20 min apart) and D-019's cap of 8.
  1. **No cap on Pause apps.** The user may choose as many launchable, non-PROTECTED apps as they like. Remove `maxTargetApps` (or set it to "unlimited"); UI keeps no limit of its own.
  2. **New "Nag level" setting** (additive, default-valued field on `UserSettings`; pre-approved, ENGINE posts INFO to ALL): tiers in `BrainConfig`, suggested values — Calm: 3/day, ≥ 45 min apart · **Naggy (default): 15/day, ≥ 10 min apart** · Chaos: 30/day, ≥ 5 min apart. ENGINE may tune the numbers; the point is the user can turn him down in one tap, which is what keeps a naggy cat from being uninstalled. The Roast dial stays separate (how sharp, not how often).
  3. **Naggy is not mean and not unsafe. These floors stay:** every Pause and bubble is dismissible (Leave instant, Stay after ≤ stayDelaySec); never over calls, PROTECTED apps, the user's quiet hours, or while napped; blind mode stops everything; no guilt mechanics (no lost streaks, no pet death), roast the situation not the person; no body/mental-health/religion/caste/politics jokes; no medical claims; no screen-time-reduction claims.
  4. Voice: persistent, dramatic, "again?", callbacks and escalation across repeat opens are now welcome.

- **D-021 (2026-11-07, Owner request: blips and meows; real voice only if it is a good decision)**
  1. **Sound v1 = meows, purrs and text blips. Opt-in, OFF by default.** New additive `UserSettings` field `soundsEnabled=false` (pre-approved; ENGINE posts INFO to ALL). Sources allowed: **CC0 recordings** or **procedurally generated** (AudioTrack) only. No CC-BY (attribution burden), no custom "free" licences (e.g. Mixkit), no paid packs (zero budget).
  2. **Manners:** respect silent/vibrate and Do Not Disturb; never during calls; skip when music is playing (use `MusicActive`) unless the Owner later says otherwise; no audio-focus grab; low volume; at most one cue per bubble; never at night in quiet hours.
  3. **Budget:** all bundled audio ≤ 400 KB total (mono OGG/Opus, trimmed), in `character/src/main/res/raw/`; DEVOPS updates the asset guard; `design/character/audio/CREDITS.md` records source URL, author, licence and date for every file.
  4. **Real spoken voice: NOT in v1 (deferred).** Reasons: a generic synthetic voice reading roasts weakens the character; neural offline TTS (e.g. Piper, MIT engine) adds a native runtime and tens of MB, and each *voice model* has its own dataset licence that must be checked per voice; system TTS is free but sounds generic and depends on the user's installed engine. Revisit after the English loop earns retention, with an Owner-supplied or commissioned recording as the preferred path (a real voice that is *his*).
