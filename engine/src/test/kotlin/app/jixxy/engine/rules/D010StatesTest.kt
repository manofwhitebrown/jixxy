package app.jixxy.engine.rules

import app.jixxy.engine.api.CharacterState
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.Trigger
import org.junit.Assert.assertEquals
import org.junit.Test

/** D-010: PEEK, CHARGING and LOW_BATTERY mappings. */
class D010StatesTest {
    @Test fun chargerShowsCharging() =
        assertEquals(CharacterState.CHARGING, MoodMachine.characterFor(Trigger.CHARGER_ON, Mood.CHILL))

    @Test fun batteryLowShowsLowBattery() =
        assertEquals(CharacterState.LOW_BATTERY, MoodMachine.characterFor(Trigger.BATTERY_LOW, Mood.PANICKING))

    @Test fun pausePromptPeeksFirst() =
        assertEquals(CharacterState.PEEK, MoodMachine.characterFor(Trigger.PAUSE_PROMPT, Mood.GRUMPY))

    @Test fun pauseStayStillJudges() =
        assertEquals(CharacterState.JUDGING, MoodMachine.characterFor(Trigger.PAUSE_STAY, Mood.GRUMPY))
}
