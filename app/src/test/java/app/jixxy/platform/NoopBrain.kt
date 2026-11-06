package app.jixxy.platform

import app.jixxy.engine.api.Bond
import app.jixxy.engine.api.BrainSnapshot
import app.jixxy.engine.api.JixxyBrain
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.Reaction
import app.jixxy.engine.api.Signal

/** Test double: a brain that never reacts. (The real brain is wired in OverlayService, T-P2-3.) */
class NoopBrain : JixxyBrain {
    override fun onSignal(signal: Signal): List<Reaction> = emptyList()
    override val snapshot = BrainSnapshot(Mood.CHILL, 100, Bond.STRANGER, 0, false)
}
