package app.jixxy.engine.rules

import app.jixxy.engine.api.*
import org.junit.Assert.*
import org.junit.Test

class SilenceAndCapsTest {
    private val target = "t.app"

    @Test fun quietHoursSilenceUnpromptedButNotLaterThatDay() {
        val r = Rig()
        assertTrue(r.send(Signal.Charger(true, 50, at(0, 3))).isEmpty())
        val out = r.send(Signal.Charger(true, 50, at(0, 12)))
        assertEquals(1, out.size)
        assertEquals(Trigger.CHARGER_ON, out[0].trigger())
    }

    @Test fun pauseStillFiresInQuietHours() {
        val r = Rig(UserSettings(targetApps = setOf(target)))
        val out = r.send(Signal.ForegroundApp(target, AppCategory.SOCIAL, at(0, 2)))
        assertEquals(ReactionKindOut.PAUSE_PROMPT, out.single().kind)
    }

    @Test fun napSilencesEverythingUntilItEnds() {
        val r = Rig(UserSettings(targetApps = setOf(target), napUntilTs = at(0, 13)))
        assertTrue(r.send(Signal.ForegroundApp(target, AppCategory.SOCIAL, at(0, 12))).isEmpty())
        assertTrue(r.send(Signal.Charger(true, 50, at(0, 12, 5))).isEmpty())
        assertTrue(r.send(Signal.SpriteTapped(1, at(0, 12, 6))).isEmpty())
        assertEquals(1, r.send(Signal.Charger(true, 50, at(0, 13, 30))).size)
    }

    @Test fun blindModeIgnoresEverythingAndLogsNothing() {
        val r = Rig(UserSettings(blindMode = true, targetApps = setOf(target)))
        assertTrue(r.send(Signal.ForegroundApp(target, AppCategory.SOCIAL, at(0, 12))).isEmpty())
        assertTrue(r.send(Signal.Charger(true, 50, at(0, 12, 1))).isEmpty())
        assertTrue(r.send(Signal.NapRequested(10, at(0, 12, 2))).isEmpty())
        assertTrue(r.brain.drainEvents().isEmpty())
    }

    @Test fun inCallIsSilent() {
        val r = Rig(UserSettings(targetApps = setOf(target)))
        r.send(Signal.CallState(true, at(0, 12)))
        assertTrue(r.send(Signal.Charger(true, 50, at(0, 12, 1))).isEmpty())
        assertTrue(r.send(Signal.ForegroundApp(target, AppCategory.SOCIAL, at(0, 12, 2))).isEmpty())
        r.send(Signal.CallState(false, at(0, 12, 30)))
        assertEquals(1, r.send(Signal.Charger(true, 50, at(0, 13))).size)
    }

    @Test fun protectedAppsNeverTriggerAndAreNeverLogged() {
        val r = Rig(UserSettings(targetApps = setOf("bank.app")))
        assertTrue(r.send(Signal.ForegroundApp("bank.app", AppCategory.PROTECTED, at(0, 12))).isEmpty())
        assertTrue(r.send(Signal.SessionTick("bank.app", 40, at(0, 12, 40))).isEmpty())
        assertTrue(r.send(Signal.Charger(true, 50, at(0, 13))).isEmpty())
        assertTrue(r.brain.drainEvents().none { it.pkg == "bank.app" })
    }

    @Test fun mutedForegroundAppSilencesUnpromptedAndPause() {
        val r = Rig(UserSettings(targetApps = setOf(target), mutedApps = setOf(target)))
        assertTrue(r.send(Signal.ForegroundApp(target, AppCategory.SOCIAL, at(0, 12))).isEmpty())
        assertTrue(r.send(Signal.Charger(true, 50, at(0, 13))).isEmpty())
    }

    @Test fun screenOffIsSilent() {
        val r = Rig()
        r.send(Signal.ScreenState(false, at(0, 12)))
        assertTrue(r.send(Signal.Charger(true, 50, at(0, 12, 1))).isEmpty())
        r.send(Signal.ScreenState(true, at(0, 13)))
        assertEquals(1, r.send(Signal.Charger(true, 50, at(0, 14))).size)
    }

    @Test fun minimumGapBlocksSecondUnpromptedLine() {
        val r = Rig()
        assertEquals(1, r.send(Signal.Charger(true, 50, at(0, 12))).size)
        // Naggy default (D-020): at least 10 minutes between unprompted lines.
        assertTrue(r.send(Signal.MusicActive(true, at(0, 12, 5))).isEmpty())
        assertEquals(1, r.send(Signal.MusicActive(true, at(0, 12, 15))).size)
    }

    @Test fun dailyCapBlocksThenResetsNextDay() {
        val capped = BrainState(firstSeenTs = 1L, unpromptedDayEpoch = 20454L, unpromptedToday = BrainConfig.maxUnpromptedPerDay(NagLevel.NAGGY)) // 2026-01-01
        val r = Rig(state = capped)
        assertTrue(r.send(Signal.Charger(true, 50, at(0, 12))).isEmpty())
        assertEquals(1, r.send(Signal.Charger(true, 50, at(1, 12))).size)
    }

    @Test fun perTriggerCooldownApplies() {
        val r = Rig()
        assertEquals(1, r.send(Signal.Charger(true, 50, at(0, 9))).size)
        assertTrue(r.send(Signal.Charger(true, 50, at(0, 10))).isEmpty())
        assertEquals(1, r.send(Signal.Charger(true, 50, at(0, 12, 30))).size)
    }

    @Test fun welcomeFiresOnceOnVeryFirstSignal() {
        val r = Rig(state = BrainState())
        assertEquals(Trigger.WELCOME, r.send(Signal.Heartbeat(at(0, 12))).single().trigger())
        assertTrue(r.send(Signal.Heartbeat(at(0, 13))).isEmpty())
    }

    @Test fun appOpenAfterLongSessionAndFirstOpenOfDay() {
        val r = Rig()
        assertEquals(Trigger.APP_OPEN, r.send(Signal.ForegroundApp("a", AppCategory.OTHER, at(0, 12))).single().trigger())
        r.send(Signal.SessionTick("a", 25, at(0, 12, 25)))
        assertEquals(Trigger.APP_OPEN, r.send(Signal.ForegroundApp("b", AppCategory.OTHER, at(0, 14))).single().trigger())
        assertTrue(r.send(Signal.ForegroundApp("c", AppCategory.OTHER, at(0, 15))).isEmpty())
    }

    @Test fun sessionLongRemarksAreSpacedOut() {
        val r = Rig()
        r.send(Signal.ForegroundApp("a", AppCategory.OTHER, at(0, 12)))
        assertTrue(r.send(Signal.SessionTick("a", 29, at(0, 12, 29))).isEmpty())
        assertEquals(Trigger.SESSION_LONG, r.send(Signal.SessionTick("a", 30, at(0, 12, 30))).single().trigger())
        assertTrue(r.send(Signal.SessionTick("a", 31, at(0, 12, 31))).isEmpty())
        assertTrue(r.send(Signal.SessionTick("a", 45, at(0, 12, 45))).isEmpty())
    }

    @Test fun lateNightOpensOncePerNightAndIsSoft() {
        val r = Rig()
        val out = r.send(Signal.ForegroundApp("a", AppCategory.OTHER, at(0, 23, 30)))
        assertEquals(Trigger.LATE_NIGHT, out.single().trigger())
        assertEquals(Mood.SOFT, out.single().mood)
        r.send(Signal.ForegroundApp("b", AppCategory.OTHER, at(1, 0, 20)))
        assertTrue(r.send(Signal.ForegroundApp("a", AppCategory.OTHER, at(1, 0, 50))).isEmpty())
    }

    @Test fun noLineAvailableMeansSilenceNotAnError() {
        val r = Rig()
        r.picker.returnNull = true
        assertTrue(r.send(Signal.Charger(true, 50, at(0, 12))).isEmpty())
    }
}
