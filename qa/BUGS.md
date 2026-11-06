# Jixxy bug list (DEVOPS-owned)

Source of the first entries: the Owner's first test of `v0.0.1-alpha` on a real Android phone, 2026-11-06.
**Status words:** `open` · `fixed in code` (a commit exists, NOT yet seen working on a phone) · `verified` (the Owner confirmed on a phone) · `wontfix`.
Only the Owner can move a bug to `verified`. New findings: add a row below, one bug per ID, steps in plain words.

| ID | Title | Owner role | Status |
|---|---|---|---|
| B-001 | Launcher icon is the default Android robot | PLATFORM / UI | fixed in code (T-P2-5/6, a409193); not seen on a phone |
| B-002 | First screen is the temporary 3-button screen, not the real app | PLATFORM / UI | fixed in code (T-P2-2, fe3c9ee: MainActivity is the launcher); not seen on a phone |
| B-003 | Floating sprite is a placeholder shape ("potato") | CHARACTER | open: new art direction D-011..D-013, real expression sprites are being wired; judge on the next build |
| B-004 | Nap, then Stop, then Wake: Jixxy never shows again | PLATFORM | fixed in code (T-P2-1, `NapPolicy` + tests); **needs the 2-minute test below on a phone** |
| B-005 | Jixxy says nothing: no lines reach the screen | PLATFORM / ENGINE | fixed in code in part (T-P2-3 real brain wired via `PersistingBrain`); bubble display still a stub per PLATFORM blocker, so likely still open on a phone |

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
```
### B-0NN Short title
- Steps: 1. … 2. … 3. …
- Expected: …
- Actual: …
- Phone / Android version / build (versionCode, or the `latest` release time): …
- Owner role: (DEVOPS fills in)   Status: open
```
