package app.jixxy.character

import app.jixxy.engine.api.CharacterState
import app.jixxy.engine.api.CharacterState.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Assert.assertTrue
import kotlin.math.abs
import kotlin.random.Random

class PoseAnimatorTest {
    private fun animator(): PoseAnimator = PoseAnimator(Random(7)).also { it.resume(0L) }

    private fun PoseAnimator.runTo(endMs: Long, fromMs: Long = 0L, stepMs: Long = 33L) {
        var t = fromMs
        while (t <= endMs) { step(t); t += stepMs }
    }

    @Test fun idleBlinksEveryFewSeconds() {
        val a = animator()
        var blinks = 0
        var wasBlinking = false
        var t = 0L
        while (t <= 30_000L) {
            a.step(t)
            val blinking = a.blinkEnvelope > 0f
            if (blinking && !wasBlinking) blinks++
            wasBlinking = blinking
            t += 33L
        }
        assertTrue("blinks=$blinks", blinks in 5..14)
    }

    @Test fun noBlinkWhileAsleep() {
        val a = animator()
        a.request(SLEEP, 0L)
        var t = 0L
        while (t <= 30_000L) {
            a.step(t)
            assertEquals(0f, a.blinkEnvelope, 0f)
            t += 33L
        }
    }

    @Test fun oneShotReturnsToIdle() {
        val a = animator()
        a.request(YAWN, 0L)
        a.step(1_700L)
        assertEquals(YAWN, a.state)
        a.step(1_900L)
        assertEquals(IDLE, a.state)
    }

    @Test fun holdStatesPersist() {
        for (s in listOf(SLEEP, SNORE, MUSIC, SOFT)) {
            val a = animator()
            a.request(s, 0L)
            a.runTo(60_000L, stepMs = 1_000L)
            assertEquals(s, a.state)
        }
    }

    @Test fun draggedBeatsPokedBeatsScripted() {
        val a = animator()
        a.setDragging(true, 0L)
        a.request(SMUG, 10L)
        assertEquals(DRAGGED, a.state)
        a.poke(1, 20L)
        assertEquals(DRAGGED, a.state)
        a.setDragging(false, 30L)
        assertEquals(FALLING, a.state)
    }

    @Test fun pokedBeatsScriptedThenScriptedPlaysAfterwards() {
        val a = animator()
        a.poke(1, 0L)
        a.request(SMUG, 100L)
        assertEquals(POKED, a.state)
        a.step(1_000L)
        assertEquals(SMUG, a.state)
    }

    @Test fun staleScriptedRequestIsDropped() {
        val a = animator()
        a.setDragging(true, 0L)
        a.request(SMUG, 100L)
        a.setDragging(false, 5_000L)
        a.step(6_000L)
        assertEquals(IDLE, a.state)
    }

    @Test fun externalDraggedIsIgnored() {
        val a = animator()
        a.request(DRAGGED, 0L)
        assertEquals(IDLE, a.state)
    }

    @Test fun externalPokedDoesNotEscalateRunningPoke() {
        val a = animator()
        a.poke(2, 0L)
        a.request(POKED, 50L)
        assertEquals(2, a.pokeLevel)
    }

    @Test fun pokeLevelEscalatesAndLastsLonger() {
        val level1 = animator().also { it.poke(1, 0L); it.step(1_000L) }
        assertEquals(IDLE, level1.state)
        val level3 = animator().also { it.poke(5, 0L); it.step(1_000L) }
        assertEquals(3, level3.pokeLevel)
        assertEquals(POKED, level3.state)
    }

    @Test fun sleepingEyesCloseGradually() {
        val a = animator()
        a.runTo(330L)
        a.request(SLEEP, 363L)
        a.step(396L)
        val lid = a.pose[P.LID]
        assertTrue("lid=$lid", lid > 0.5f && lid < 0.9f)
    }

    @Test fun landingNeverSnaps() {
        val a = animator()
        a.runTo(500L)
        a.setDragging(true, 533L)
        var prev = a.pose[P.SY]
        var maxStep = 0f
        var t = 533L
        while (t <= 1_500L) { a.step(t); maxStep = maxOf(maxStep, abs(a.pose[P.SY] - prev)); prev = a.pose[P.SY]; t += 33L }
        a.setDragging(false, t)
        while (t <= 3_000L) { a.step(t); maxStep = maxOf(maxStep, abs(a.pose[P.SY] - prev)); prev = a.pose[P.SY]; t += 33L }
        assertTrue("maxStep=$maxStep", maxStep < 0.1f)
    }

    @Test fun everyStateStaysFiniteAndInRange() {
        val o = Pose()
        for (s in CharacterState.values()) {
            for (level in 1..3) {
                var t = 0f
                while (t <= 10f) {
                    StatePoses.fill(s, t, t * 3.7f, level, o)
                    for (i in 0 until P.COUNT) assertTrue("$s ch$i t=$t", o[i].isFinite())
                    assertTrue("$s SX", o[P.SX] in 0.7f..1.3f)
                    assertTrue("$s SY", o[P.SY] in 0.7f..1.3f)
                    assertTrue("$s LID", o[P.LID] in 0f..1f)
                    assertTrue("$s OPEN", o[P.OPEN] in 0f..1f)
                    t += 0.05f
                }
            }
        }
    }

    @Test fun calmStatesUseSlowerFrames() {
        val a = animator()
        a.step(0L)
        assertEquals(PoseAnimator.CALM_FRAME_MS, a.frameIntervalMs)
        a.request(PANIC, 10L)
        a.step(40L)
        assertEquals(PoseAnimator.ACTIVE_FRAME_MS, a.frameIntervalMs)
    }

    @Test fun repeatedHoldRequestDoesNotRestart() {
        val a = animator()
        a.request(SLEEP, 0L)
        a.step(100L)
        a.request(SLEEP, 5_000L)
        a.step(5_100L)
        assertEquals(SLEEP, a.state)
    }

}
