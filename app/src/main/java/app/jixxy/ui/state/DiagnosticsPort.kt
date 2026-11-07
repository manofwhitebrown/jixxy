package app.jixxy.ui.state

/**
 * What the UI needs from PLATFORM's diagnostics (T-U3-3). PLATFORM's `DiagnosticsRepository`
 * is adapted to this in MainActivity. [copyableReport] must be plain text that stays on the phone
 * until the user pastes it somewhere by hand; [clear] deletes the stored diagnostics.
 */
interface DiagnosticsPort {
    suspend fun copyableReport(): String
    suspend fun clear()
}
