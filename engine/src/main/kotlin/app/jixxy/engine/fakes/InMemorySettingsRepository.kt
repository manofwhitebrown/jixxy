package app.jixxy.engine.fakes

import app.jixxy.engine.api.SettingsRepository
import app.jixxy.engine.api.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory [SettingsRepository] for tests and previews. */
class InMemorySettingsRepository(initial: UserSettings = UserSettings()) : SettingsRepository {
    private val flow = MutableStateFlow(initial)
    override val settings: Flow<UserSettings> = flow
    override suspend fun update(transform: (UserSettings) -> UserSettings) { flow.value = transform(flow.value) }
}
