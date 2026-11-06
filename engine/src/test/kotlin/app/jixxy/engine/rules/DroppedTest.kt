package app.jixxy.engine.rules

import app.jixxy.engine.api.CharacterState
import app.jixxy.engine.api.ReactionKindOut
import app.jixxy.engine.api.Signal
import app.jixxy.engine.api.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DroppedTest {
    @Test fun droppedFallsAndSaysADroppedLineOnce() {
        val r = Rig()
        val first = r.send(Signal.SpriteDropped(at(0, 12))).single()
        assertEquals(CharacterState.FALLING, first.state)
        assertEquals(ReactionKindOut.SAY, first.kind)
        assertEquals("dropped", r.picker.queries.last().requiredTag)
        val again = r.send(Signal.SpriteDropped(at(0, 12, 0, 30))).single()
        assertEquals(CharacterState.FALLING, again.state)
        assertEquals(ReactionKindOut.ANIMATE_ONLY, again.kind)
        assertNull(again.text)
        assertEquals(ReactionKindOut.SAY, r.send(Signal.SpriteDropped(at(0, 12, 3))).single().kind)
    }

    @Test fun droppedDuringNapOrCallIsSilent() {
        val r = Rig(UserSettings(napUntilTs = at(0, 13)))
        assertTrue(r.send(Signal.SpriteDropped(at(0, 12))).isEmpty())
        val r2 = Rig()
        r2.send(Signal.CallState(true, at(0, 12)))
        assertTrue(r2.send(Signal.SpriteDropped(at(0, 12, 1))).isEmpty())
    }

    @Test fun threeTapsAsksForAnEscalationLine() {
        val r = Rig()
        r.send(Signal.SpriteTapped(1, at(0, 12)))
        assertEquals(false, r.picker.queries.last().escalation)
        r.send(Signal.SpriteTapped(3, at(0, 12, 0, 5)))
        assertEquals(true, r.picker.queries.last().escalation)
    }
}
