# Onboarding flow (T-UI-1 wireframe, v0)

One permission per screen. Value first, then the ask. No jokes on permission screens' explanations; Jixxy's voice only in headings/buttons.
Every screen: "Skip / not now" is possible; user is never trapped; state survives leaving to system Settings.

1. **Hello** - one-line intro, [Meet him] / [Just let me try him] (lite mode: overlay only).
2. **Overlay - why** - "I sit on top of your apps so I can show up." Can't-do list: no internet, no messages, no screenshots. [Open setting].
   (Grant state is shown on the same screen: after returning from Settings the button becomes a tick.)
4. **Usage access - why** - "I only see WHICH app is open. Never what's inside it." [Open setting].
   - If `restrictedSettingsLikelyBlocked`: show Restricted-settings explainer (below) before/after the toggle.
6. **Notifications (Android 13+)** - why; [Allow] / [Not now].
7. **Keep-alive** - battery exemption + OEM steps (reuses T-UI-6 guide); honest "menus differ".
8. **Pick up to 3 apps** - launcher-intent list with icons, hard cap of 3, PROTECTED apps hidden.
9. **Roast dial** - Gentle / Normal / Savage with live sample line.
10. **Wake him up** - done; navigate to Home.

## Restricted-settings explainer (sideload, Android 13+)
Tap the toggle -> it may be greyed out or blocked -> open App info -> three-dot menu -> "Allow restricted settings" -> come back.
Wording differs by brand/Android version: hedge honestly, keep it short, one illustration.

## Lite mode
Grants overlay only. Engine uses time/charger/touch triggers. Explain what unlocks later (pausing apps needs usage access).

## States to preview
Each step in: not granted / granted / blocked (restricted) / font scale 200% / dark / light.
