package app.jixxy.ui.state

/** What the UI needs from PLATFORM's weekly-review trigger. [markShown] is called once the person has seen the card. */
interface WeeklyPort {
    fun isEnabled(): Boolean
    fun setEnabled(on: Boolean)
    fun markShown()
}
