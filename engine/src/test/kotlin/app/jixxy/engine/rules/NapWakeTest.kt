package app.jixxy.engine.rules

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.Signal
import app.jixxy.engine.api.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Nap/wake semantics, including the Owner-reported bug: Nap, Stop, Wake, then no Jixxy for the hour. */
class NapWakeTest {
    private val tick = Signal.Charger(true, 50, 0L)

    @Test fun ownNapSilencesUntilItEnds() {
        val r = Rig()
        r.send(Signal.NapRequested(60, at(0, 12)))
        assertTrue(r.brain.snapshot.napping)
        assertTrue(r.send(tick.copy(ts = at(0, 12, 30))).isEmpty())
        assertEquals(1, r.send(tick.copy(ts = at(0, 13, 5))).size)
        assertFalse(r.brain.snapshot.napping)
    }

    @Test fun wakeClearsOwnNapImmediately() {
        val r = Rig()
        r.send(Signal.NapRequested(60, at(0, 12)))
        r.send(Signal.WakeRequested(at(0, 12, 10)))
        assertFalse(r.brain.snapshot.napping)
        assertEquals(1, r.send(tick.copy(ts = at(0, 12, 15))).size)
    }

    @Test fun wakeBeatsAStalePersistedNap() {
        // Platform restarted the service; settings still say napUntilTs is in the future.
        val r = Rig(UserSettings(napUntilTs = at(0, 13)))
        assertTrue(r.send(tick.copy(ts = at(0, 12, 1))).isEmpty())
        val wake = r.send(Signal.WakeRequested(at(0, 12, 2)))
        assertEquals(1, wake.size)
        assertFalse(r.brain.snapshot.napping)
        assertEquals(1, r.send(tick.copy(ts = at(0, 12, 30))).size)
    }

    @Test fun aNewPersistedNapAfterWakeIsHonoured() {
        val r = Rig(UserSettings(napUntilTs = at(0, 13)))
        r.send(Signal.WakeRequested(at(0, 12, 2)))
        r.settings = r.settings.copy(napUntilTs = at(0, 15))
        assertTrue(r.send(tick.copy(ts = at(0, 12, 30))).isEmpty())
    }

    @Test fun napSilencesPauseAndAppReactions() {
        val r = Rig(UserSettings(targetApps = setOf("t")))
        r.send(Signal.NapRequested(30, at(0, 12)))
        assertTrue(r.send(Signal.ForegroundApp("t", AppCategory.SOCIAL, at(0, 12, 5))).isEmpty())
    }
}
