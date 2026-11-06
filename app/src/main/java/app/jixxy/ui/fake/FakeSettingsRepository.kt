package app.jixxy.ui.fake

import app.jixxy.engine.api.SettingsRepository
import app.jixxy.engine.api.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory settings for previews and pre-PLATFORM development. */
class FakeSettingsRepository(initial: UserSettings = UserSettings()) : SettingsRepository {
    private val state = MutableStateFlow(initial)
    override val settings: Flow<UserSettings> = state
    override suspend fun update(transform: (UserSettings) -> UserSettings) { state.value = transform(state.value) }
}
