package app.jixxy.engine.rules

import app.jixxy.engine.api.*
import org.junit.Assert.*
import org.junit.Test

class PauseLogicTest {
    private val t = "t.app"
    private fun rig(vararg targets: String = arrayOf(t)) = Rig(UserSettings(targetApps = targets.toSet()))
    private fun open(r: Rig, ts: Long, pkg: String = t) = r.send(Signal.ForegroundApp(pkg, AppCategory.SOCIAL, ts))
    private fun choose(r: Rig, c: PauseChoiceKind, ts: Long) = r.send(Signal.PauseChoice(t, c, ts))

    @Test fun firstOpenOfTargetAppShowsPauseCard() {
        val r = rig()
        val out = open(r, at(0, 12)).single()
        assertEquals(ReactionKindOut.PAUSE_PROMPT, out.kind)
        assertEquals(t, out.pause!!.pkg)
        assertEquals("App[$t]", out.pause!!.appLabel)
        assertEquals(3, out.pause!!.stayEnabledAfterSec)
        assertEquals(5, out.pause!!.grantMinutes)
        assertEquals(0, r.picker.queries.last().tier)
    }

    @Test fun pauseWorksWithNoLineAvailable() {
        val r = rig()
        r.picker.returnNull = true
        val out = open(r, at(0, 12)).single()
        assertEquals(ReactionKindOut.PAUSE_PROMPT, out.kind)
        assertNull(out.text)
        assertNotNull(out.pause)
    }

    @Test fun stayGrantsFiveQuietMinutesThenPromptsAgain() {
        val r = rig()
        open(r, at(0, 12))
        choose(r, PauseChoiceKind.STAY, at(0, 12, 0, 5))
        open(r, at(0, 12, 1), "other")
        assertTrue(open(r, at(0, 12, 3)).isEmpty())
        open(r, at(0, 12, 4), "other")
        assertEquals(ReactionKindOut.PAUSE_PROMPT, open(r, at(0, 12, 6)).single().kind)
    }

    @Test fun grantedStayExpiryGivesExactlyOneGentleSecondPrompt() {
        val r = rig()
        open(r, at(0, 12))
        choose(r, PauseChoiceKind.STAY, at(0, 12, 0, 5))
        assertTrue(r.send(Signal.SessionTick(t, 4, at(0, 12, 4))).isEmpty())
        val second = r.send(Signal.SessionTick(t, 6, at(0, 12, 6))).single()
        assertEquals(ReactionKindOut.PAUSE_PROMPT, second.kind)
        assertTrue(r.picker.queries.last().followUp)
        assertTrue(r.send(Signal.SessionTick(t, 7, at(0, 12, 7))).none { it.kind == ReactionKindOut.PAUSE_PROMPT })
        assertTrue(r.send(Signal.SessionTick(t, 8, at(0, 12, 8))).none { it.kind == ReactionKindOut.PAUSE_PROMPT })
    }

    @Test fun afterSecondPromptSessionLongRemarksResume() {
        val r = rig()
        open(r, at(0, 12))
        choose(r, PauseChoiceKind.STAY, at(0, 12, 0, 5))
        r.send(Signal.SessionTick(t, 6, at(0, 12, 6)))
        val out = r.send(Signal.SessionTick(t, 30, at(0, 12, 30)))
        assertEquals(Trigger.SESSION_LONG, out.single().trigger())
    }

    @Test fun sessionLongIsSilentDuringGrantedStay() {
        val r = rig()
        r.send(Signal.ForegroundApp("x", AppCategory.OTHER, at(0, 11)))
        open(r, at(0, 11, 30))
        choose(r, PauseChoiceKind.STAY, at(0, 11, 30, 5))
        assertTrue(r.send(Signal.SessionTick(t, 30, at(0, 11, 33))).isEmpty())
    }

    @Test fun leaveGivesThreeQuietMinutes() {
        val r = rig()
        open(r, at(0, 12))
        choose(r, PauseChoiceKind.LEAVE, at(0, 12, 0, 2))
        open(r, at(0, 12, 1), "other")
        assertTrue(open(r, at(0, 12, 2)).isEmpty())
        open(r, at(0, 12, 3), "other")
        assertEquals(ReactionKindOut.PAUSE_PROMPT, open(r, at(0, 12, 6)).single().kind)
    }

    @Test fun humourEscalatesWithCountAndResetsNextDay() {
        val r = rig()
        for (i in 0 until 3) {
            open(r, at(0, 10 + i))
            choose(r, PauseChoiceKind.LEAVE, at(0, 10 + i, 0, 2))
            open(r, at(0, 10 + i, 30), "other")
        }
        assertEquals(listOf(0, 1, 2), r.picker.queries.filter { it.trigger == Trigger.PAUSE_PROMPT }.map { it.tier })
        open(r, at(1, 10))
        assertEquals(0, r.picker.queries.last { it.trigger == Trigger.PAUSE_PROMPT }.tier)
    }

    @Test fun pauseMinutesTodayComeFromSessionTicks() {
        val r = rig()
        open(r, at(0, 12))
        for (i in 1..5) r.send(Signal.SessionTick(t, i, at(0, 12, i)))
        choose(r, PauseChoiceKind.LEAVE, at(0, 12, 6))
        open(r, at(0, 13, 0), "other")
        assertEquals(5, open(r, at(0, 14)).single().pause!!.minutesToday)
    }

    @Test fun pauseIsNotCountedInTheDailyCapAndIgnoresEnergy() {
        val s = BrainState(firstSeenTs = 1L, energy = 0, unpromptedDayEpoch = 20454L, unpromptedToday = 6)
        val r = Rig(UserSettings(targetApps = setOf(t)), s)
        assertEquals(ReactionKindOut.PAUSE_PROMPT, open(r, at(0, 12)).single().kind)
        assertEquals(6, r.brain.currentState().unpromptedToday)
    }

    @Test fun choiceRepliesUsePauseStayAndPauseLeaveLines() {
        val r = rig()
        open(r, at(0, 12))
        assertEquals(Trigger.PAUSE_STAY, choose(r, PauseChoiceKind.STAY, at(0, 12, 1)).single().trigger())
        assertEquals(Trigger.PAUSE_LEAVE, choose(r, PauseChoiceKind.LEAVE, at(0, 12, 2)).single().trigger())
    }

    @Test fun threeLeavesInARowEarnImpressedAndAStayResetsTheStreak() {
        val r = rig()
        choose(r, PauseChoiceKind.LEAVE, at(0, 10))
        choose(r, PauseChoiceKind.LEAVE, at(0, 11))
        choose(r, PauseChoiceKind.STAY, at(0, 12))
        choose(r, PauseChoiceKind.LEAVE, at(0, 13))
        choose(r, PauseChoiceKind.LEAVE, at(0, 14))
        val third = choose(r, PauseChoiceKind.LEAVE, at(0, 15)).single()
        assertEquals(Trigger.IMPRESSED, third.trigger())
        assertEquals(Mood.IMPRESSED, third.mood)
        assertEquals(CharacterState.IMPRESSED, third.state)
        assertEquals(Trigger.PAUSE_LEAVE, choose(r, PauseChoiceKind.LEAVE, at(0, 16)).single().trigger())
    }

    @Test fun pausesDisabledMeansNoCard() {
        val r = Rig(UserSettings(targetApps = setOf(t), pausesEnabled = false))
        assertTrue(open(r, at(0, 12)).none { it.kind == ReactionKindOut.PAUSE_PROMPT })
    }

    @Test fun everyChosenTargetAppIsPausedWithNoCap() {
        val r = rig("a", "b", "c", "d")
        assertEquals(ReactionKindOut.PAUSE_PROMPT, open(r, at(0, 12), "d").single().kind)
        assertEquals(ReactionKindOut.PAUSE_PROMPT, open(r, at(0, 13), "c").single().kind)
    }

    @Test fun nonTargetAppGetsNoPause() {
        val r = rig()
        assertTrue(open(r, at(0, 12), "other").none { it.kind == ReactionKindOut.PAUSE_PROMPT })
    }

    @Test fun duplicateForegroundSignalDoesNotReprompt() {
        val r = rig()
        open(r, at(0, 12))
        assertTrue(open(r, at(0, 12, 1)).isEmpty())
    }
}
