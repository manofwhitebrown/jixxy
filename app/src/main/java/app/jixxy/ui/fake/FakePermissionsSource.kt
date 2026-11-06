package app.jixxy.ui.fake

import app.jixxy.ui.state.PermissionsSource
import app.jixxy.ui.state.PermissionsUiState

/** Fixed permissions state for previews. */
class FakePermissionsSource(private val state: PermissionsUiState = PermissionsUiState()) : PermissionsSource {
    override fun current(): PermissionsUiState = state
}
