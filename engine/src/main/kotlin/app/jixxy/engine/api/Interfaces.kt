package app.jixxy.engine.api

import kotlinx.coroutines.flow.Flow

/** The brain. Deterministic given an injected clock and seeded random. */
interface JixxyBrain {
    /** Feed a signal; returns zero or more reactions (usually zero: silence is the default). */
    fun onSignal(signal: Signal): List<Reaction>
    /** Current state for display. */
    val snapshot: BrainSnapshot
}

/** Persistence for brain state and usage events. Implemented by PLATFORM (Room/DataStore). */
interface StateStore {
    suspend fun load(): BrainState
    suspend fun save(state: BrainState)
    suspend fun logEvent(e: UsageEvent)
    /** Events with fromTs <= ts < toTs, ordered by ts ascending. */
    suspend fun eventsBetween(fromTs: Long, toTs: Long): List<UsageEvent>
    suspend fun clearEvents()
}

/** Reactive access to [UserSettings]. */
interface SettingsRepository {
    val settings: Flow<UserSettings>
    suspend fun update(transform: (UserSettings) -> UserSettings)
}
