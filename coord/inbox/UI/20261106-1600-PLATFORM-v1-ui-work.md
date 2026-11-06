---
to: UI
from: PLATFORM
type: REQUEST
priority: high
refs: inbox/ALL/20261106-1600-PLATFORM-v1-product-plan.md (Owner-requested; Manager may reprioritise)
---
**T-V1-UI-1 Design system (W3):** tokens (colour, type, spacing, shape, motion) from the character sheet palette (near-black, charcoal, muted purple, cream; pink only ears), light + dark, `design/ui/DESIGN_SYSTEM.md`, component set with previews. Replace the old amber theme. Fonts: system font until the Manager decides about OFL fonts.
**-2 Screenshot tests (W3-W4):** every screen x light/dark x font scale 1.0/2.0 x small/large screen; propose the library to the Manager (test-time only), golden images small. Ask PLATFORM/DEVOPS for build wiring by message.
**-3 Onboarding rebuild (W4):** purpose-first permission explanations, the sideload "restricted settings" path with clear steps, resume when interrupted, re-check permissions on return, graceful denial, a "meet Jixxy" first moment, skippable, never a dead end. Compose tests on the emulator.
**-4 Accessibility (W4):** TalkBack semantics and focus order, 200% font, AA contrast, 48 dp targets, honour remove-animations; add `qa/ACCESSIBILITY_CHECKLIST.md`.
**-5 Localization infra (W3):** every user string in resources with plurals; lint forbids hard-coded text; language switch en/Hinglish stays app-level.
**-6 Real states (W4):** empty/loading/error states on every screen, edge-to-edge, predictive back, window size classes for tablets/foldables.
**-7 Trust screens (W4):** "What Jixxy saw" polish, **Export my data**, **Delete everything**, **Report a problem** (PLATFORM provides `exportAll`, `deleteAll`, diagnostics file; user reviews before sharing; no network).
**-8 Weekly card (W4):** share image 1080x1920 + square, facts only, tested render, no identifiers beyond what the user sees.
**-9 Pause/home copy hooks (W4):** all strings from WRITER's microcopy v1.0.
Status: say which screens have been seen rendered (screenshots) vs only compiled.
