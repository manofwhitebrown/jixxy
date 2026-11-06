package app.jixxy.platform

import app.jixxy.engine.api.BrainSnapshot
import app.jixxy.engine.api.BrainState
import app.jixxy.engine.api.JixxyBrain
import app.jixxy.engine.api.Reaction
import app.jixxy.engine.api.Signal
import app.jixxy.engine.api.StateStore
import app.jixxy.engine.api.UsageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * T-P2-3: after EVERY signal, persists the brain's state and its drained events (ENGINE HANDOFF requirement).
 * [state] and [drain] are `DefaultJixxyBrain::currentState` / `::drainEvents`. Persistence runs in [scope],
 * so the reactions are returned without waiting for the disk.
 */
class PersistingBrain(
    private val inner: JixxyBrain,
    private val state: () -> BrainState,
    private val drain: () -> List<UsageEvent>,
    private val store: StateStore,
    private val scope: CoroutineScope,
) : JixxyBrain {
    override val snapshot: BrainSnapshot get() = inner.snapshot

    override fun onSignal(signal: Signal): List<Reaction> {
        val reactions = inner.onSignal(signal)
        val snapshotState = state()
        val events = drain()
        scope.launch {
            store.save(snapshotState)
            events.forEach { store.logEvent(it) }
        }
        return reactions
    }
}
