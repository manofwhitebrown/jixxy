---
to: PLATFORM
from: ENGINE
type: HANDOFF
priority: high
refs: T-E2-1, T-E2-3
---
The real brain is pushed. Replace NoopBrain in OverlayService/Dispatcher wiring with:

    val bank = LineBankLoader.fromTexts(assetTextsUnder("lines/en"))   // read every *.json under assets/lines/<lang>; pass the raw texts
    val brain = JixxyBrains.create(
        lineBank = bank,
        initialState = stateStore.load(),            // BrainState
        settings = { currentUserSettings },           // read on every signal
        appLabel = { pkg -> labelFor(pkg) },          // PackageManager label, fall back to pkg
        zone = ZoneId.systemDefault())                // returns DefaultJixxyBrain (is a JixxyBrain)

Packages: app.jixxy.engine.JixxyBrains, app.jixxy.engine.lines.LineBankLoader, app.jixxy.engine.rules.DefaultJixxyBrain.
After EVERY onSignal: persist brain.currentState() via StateStore.save and brain.drainEvents() via logEvent.
Time: the brain uses Signal.ts only, so stamp every signal with your clock. Send Heartbeat once a minute.

Nap/wake (T-E2-3): WakeRequested is now authoritative. It clears the brain's own nap and makes it ignore the persisted napUntilTs it sees at that moment, resets mood to CHILL, and replies with a WAKE line. You should still clear napUntilTs in settings on Wake (that is the bug you own), and send NapRequested when the user naps so the brain's own nap tracking matches.
Reactions: SAY and PAUSE_PROMPT may have text == null if no eligible line exists; show nothing for SAY, a neutral card for PAUSE_PROMPT.
Not yet verified on a device; compiled with kotlinc 2.1.21 and 72 engine tests pass locally.
