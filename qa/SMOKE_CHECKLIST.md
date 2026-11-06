# Smoke checklist (run per device, per build)
Record: device, OEM skin, Android version, build (commit SHA), tester, date. Mark each step PASS / FAIL / N/A.

1. Install the APK (sideload). Note any "restricted settings" block.
2. Restricted-settings path: if blocked, follow the in-app guide to allow it.
3. Grant overlay, usage access and notifications when asked. Each prompt explains why, in character.
4. Onboarding completes; pick up to 3 apps (a 4th is refused); pick roast level.
5. Sprite appears; drag it; drop it; it stays on-screen after rotation.
6. Touches pass through everywhere except the sprite and the card.
7. Open a chosen app: Pause card appears.
8. Leave works instantly. Stay becomes enabled after the delay and grants the configured minutes.
9. Nap in one tap; Jixxy goes quiet; wake restores him.
10. Open a PROTECTED app (banking, dialer, maps, password manager) and take a call: Jixxy stays silent.
11. Reboot: service state is as the user left it.
12. Install the next build over the previous one: no uninstall needed, settings preserved.
13. Blind mode: detection and logging stop.
14. Clear log: "What Jixxy saw" is empty.
15. Uninstall leaves nothing behind (no overlay, no notification, no leftover permission prompts).
