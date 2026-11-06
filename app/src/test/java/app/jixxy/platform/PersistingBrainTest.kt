package app.jixxy.platform

import app.jixxy.engine.api.BrainSnapshot
import app.jixxy.engine.api.BrainState
import app.jixxy.engine.api.JixxyBrain
import app.jixxy.engine.api.Reaction
import app.jixxy.engine.api.Signal
import app.jixxy.engine.api.StateStore
import app.jixxy.engine.api.UsageEvent
import app.jixxy.engine.api.UsageEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PersistingBrainTest {
    private class RecordingStore : StateStore {
        val saved = mutableListOf<BrainState>()
        val events = mutableListOf<UsageEvent>()
        override suspend fun load(): BrainState = throw UnsupportedOperationException()
        override suspend fun save(state: BrainState) { saved += state }
        override suspend fun logEvent(e: UsageEvent) { events += e }
        override suspend fun eventsBetween(fromTs: Long, toTs: Long) = emptyList<UsageEvent>()
        override suspend fun clearEvents() = Unit
    }

    @Test fun savesStateAndEventsAfterEachSignal() {
        val store = RecordingStore()
        val job = Job()
        val scope = CoroutineScope(Dispatchers.Unconfined + job)
        val state = BrainState()
        val event = UsageEvent(5L, UsageEventType.NAP)
        val inner = NoopBrain()
        val brain = PersistingBrain(inner as JixxyBrain, { state }, { listOf(event) }, store, scope)
        val out: List<Reaction> = brain.onSignal(Signal.Heartbeat(1L))
        runBlocking { job.children.forEach { it.join() } }
        assertEquals(emptyList<Reaction>(), out)
        assertEquals(listOf(state), store.saved)
        assertEquals(listOf(event), store.events)
        brain.onSignal(Signal.Heartbeat(2L))
        runBlocking { job.children.forEach { it.join() } }
        assertEquals(2, store.saved.size)
    }
}
