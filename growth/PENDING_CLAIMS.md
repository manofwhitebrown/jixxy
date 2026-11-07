# Claims waiting for a shipped build (GROWTH)

Rule: nothing below goes into README, site, PRIVACY or the store text until a build contains it **and** the check in the right-hand column passes. Then GROWTH pastes the text, marks the CLAIMS.md row, and says "in the latest build, not yet verified on a phone" until the Owner confirms. Updated 2026-11-07.

| Decision | Ready-to-paste text | Switch on when |
|---|---|---|
| D-020 no cap on Pause apps | "You choose which apps get a Pause. There is no limit." | The code no longer has `maxTargetApps` / `MAX_TARGET_APPS = 3` (UI side was reworked in `c8751d9`; ENGINE side still to check) |
| D-020 Nag level | "He is naggy on purpose. Settings has a Calm / Naggy / Chaos dial, so you can turn him down in one tap." | `UserSettings` has the nag field and Settings shows the dial |
| D-021 Sounds | "Optional meows and blips, **off by default**. Sounds are bundled in the app, nothing is downloaded, and Jixxy never uses the microphone. They stay quiet on silent/vibrate, Do Not Disturb, during calls, while music plays and in quiet hours." | `soundsEnabled` exists, defaults to false, and the audio files are in `character/src/main/res/raw/` |
| D-021 Sound credits | A short "Sound credits" section in README, built from `design/character/audio/CREDITS.md` (source, author, licence, date per file) | `CREDITS.md` exists and is complete |
| D-021 Voice | **Do not promise a spoken voice.** If asked: "Not planned for v1." | n/a |

PRIVACY.md additions at the same time: a line under "What Jixxy processes" that the music yes/no also decides when sounds stay quiet, and a note that playing sound needs no new permission (check the manifest first). Do **not** change "no microphone": playing sound is not recording.

Store note: sounds off by default and no new permission means no new Play declaration is expected [unverified].
