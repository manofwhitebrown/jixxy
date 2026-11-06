package app.jixxy.engine.rules

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.PauseChoiceKind
import app.jixxy.engine.api.Signal
import app.jixxy.engine.api.Trigger
import app.jixxy.engine.api.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Test

class PauseSlotsTest {
    @Test fun leaveAndStayAnswersCarryMinutesTodayForThatApp() {
        val r = Rig(UserSettings(targetApps = setOf("t")))
        r.send(Signal.ForegroundApp("t", AppCategory.SOCIAL, at(0, 12)))
        r.send(Signal.PauseChoice("t", PauseChoiceKind.STAY, at(0, 12, 0, 5)))
        repeat(4) { r.send(Signal.SessionTick("t", it + 1, at(0, 12, it + 1))) }
        r.send(Signal.PauseChoice("t", PauseChoiceKind.LEAVE, at(0, 12, 5)))
        val q = r.picker.queries.last { it.trigger == Trigger.PAUSE_LEAVE }
        assertEquals("4", q.slots["minutes"])
        assertEquals("App[t]", q.slots["app"])
    }
}
