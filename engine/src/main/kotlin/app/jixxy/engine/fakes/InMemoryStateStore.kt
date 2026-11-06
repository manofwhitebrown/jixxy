package app.jixxy.engine.fakes

import app.jixxy.engine.api.BrainState
import app.jixxy.engine.api.StateStore
import app.jixxy.engine.api.UsageEvent

/** In-memory [StateStore] for tests and previews. Not thread-safe beyond coroutine sequential use. */
class InMemoryStateStore(initial: BrainState = BrainState()) : StateStore {
    private var state = initial
    private val events = mutableListOf<UsageEvent>()

    override suspend fun load(): BrainState = state
    override suspend fun save(state: BrainState) { this.state = state }
    override suspend fun logEvent(e: UsageEvent) { events += e }
    override suspend fun eventsBetween(fromTs: Long, toTs: Long): List<UsageEvent> =
        events.filter { it.ts in fromTs until toTs }.sortedBy { it.ts }
    override suspend fun clearEvents() { events.clear() }
}
