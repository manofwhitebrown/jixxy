package app.jixxy.platform

import app.jixxy.engine.api.Bond
import app.jixxy.engine.api.BrainSnapshot
import app.jixxy.engine.api.JixxyBrain
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.Reaction
import app.jixxy.engine.api.Signal

/** Silent stand-in until ENGINE ships a real [JixxyBrain] (tracked in coord/status/PLATFORM.md). Never reacts. */
class NoopBrain : JixxyBrain {
    override fun onSignal(signal: Signal): List<Reaction> = emptyList()
    override val snapshot = BrainSnapshot(Mood.CHILL, 100, Bond.STRANGER, 0, false)
}
