# Jixxy bug list (DEVOPS-owned)

Source of the first entries: the Owner's first test of `v0.0.1-alpha` on a real Android phone, 2026-11-06.
**Status words:** `open` · `fixed in code` (a commit exists, NOT yet seen working on a phone) · `verified` (the Owner confirmed on a phone) · `wontfix`.
Only the Owner can move a bug to `verified`. New findings: add a row below, one bug per ID, steps in plain words.

| ID | Title | Owner role | Status |
|---|---|---|---|
| B-001 | Launcher icon is the default Android robot | UI | fixed in code (adaptive cat icon, a0f50f5; manifest uses `@mipmap/ic_launcher`); not seen on a phone |
| B-002 | First screen is the temporary 3-button screen, not the real app | UI | fixed in code (`ui.MainActivity` is the launcher, c2972cf); not seen on a phone |
| B-003 | Floating sprite is a placeholder shape ("potato") | CHARACTER | fixed in code (15 expression sprites from the Owner's sheet in `character/src/main/res`, speech bubble 8789abc); the Owner judges the look on the next build, art quality (soft, 3 frames missing) is a separate pending decision |
| B-004 | Nap, then Stop, then Wake: Jixxy never shows again | PLATFORM | fixed in code (`NapPolicy` + `NapPolicyTest`, a68c168); **needs the 2-minute test `qa/REGRESSION_NAP_STOP_WAKE.md` on a phone** |
| B-005 | Jixxy says nothing: no lines reach the screen | PLATFORM / ENGINE | fixed in code (real brain `JixxyBrains.create` inside `PersistingBrain`, `Dispatcher.say` calls the real bubble `sprite?.showBubble`, both in `OverlayService`); not seen on a phone |

All five come from the Owner's first test of `v0.0.1-alpha` (versionCode 1; phone model and Android version were not recorded). Nothing here is `verified` yet: only the Owner moves a row to `verified`.

## Not bugs yet: unproven things (from the team brief, section A)
These are risks nobody has seen fail or work. They become rows here the moment the Owner reports a failure. Tests: `qa/SMOKE_CHECKLIST.md`, `qa/MILESTONE_M2_PAUSE.md`.
- Pause end to end; **Leave goes Home from a service on Android 14/15** (step 4 of the M2 sheet).
- Overlay hosting of the bigger cat and the bubble.
- Foreground service starts after reboot or app update (Android 15 rules); OEM keep-alive (Samsung, Xiaomi, Oppo, Vivo); battery cost; TalkBack; Hinglish voice quality.

## Details

### B-001 Launcher icon is the default Android robot
- Steps: install the APK, look at the app drawer / home screen.
- Expected: the Jixxy cat face. Actual (alpha): green Android robot.

### B-002 First screen is the temporary 3-button screen
- Steps: open the app after install.
- Expected: onboarding / home screen (Compose UI). Actual (alpha): `LauncherActivity` with "1. Allow overlay", "2. Wake Jixxy", "Stop".
- Owner's words: "not impressed… still developing".

### B-003 Placeholder sprite
- Steps: grant overlay permission, tap Wake.
- Expected: the black fluffy cat from the character sheet. Actual (alpha): a placeholder shape, Owner: "Jixxy looks like a potato."

### B-004 Nap, Stop, Wake: Jixxy never shows again
- Steps: see `qa/REGRESSION_NAP_STOP_WAKE.md` (2 minutes).
- Expected: after an explicit Wake the cat is visible, always. Actual (alpha): not visible for the rest of the nap hour.
- Suspected cause (read from code, never reproduced): `napUntilTs` stayed in settings after Stop; Wake restarted the service and `updateNap()` hid the sprite.
- Rule from the brief: an explicit user "Wake" must clear the nap.

### B-005 No lines appear
- Steps: Wake Jixxy, open and leave a few apps for several minutes.
- Expected: now and then a short speech bubble with a real line (max 6 unprompted a day, 20 minutes apart, silent in quiet hours 01:00 to 07:00 by default).
- Actual (alpha): never; `Dispatcher` used `NoopBrain`.

## Template for new bugs
Add the row to the table above AND a block below, in the same session as the report. Next free ID: B-006.
```
### B-0NN Short title
- Steps: 1. … 2. … 3. …
- Expected: …
- Actual: …
- Phone / Android version / build (versionCode, or the `latest` release time): …
- Reported by / when: …
- Owner role: (DEVOPS fills in)   Status: open   (words: open · fixed in code · verified · wontfix)
- Severity: S1 crash, data loss or privacy · S2 core loop broken · S3 annoying · S4 cosmetic
```
