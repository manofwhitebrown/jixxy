package app.jixxy.character

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CloudShapeTest {
    private val puffs = FloatArray(CloudShape.PUFFS * 3)
    private val base = FloatArray(4)
    private val viewH = 128f + CloudShape.EXTRA_UNITS

    @Test fun everyCloudStaysInsideTheViewAtEveryTime() {
        for (e in Expression.values().filter { it.hasCloud }) {
            for (t in listOf(0f, 0.7f, 1.4f, 2.1f, 2.8f, 3.5f, 4.2f)) {
                CloudShape.layout(e.ledgeLeft, e.ledgeRight, t, breathing = true, out = puffs)
                CloudShape.baseRect(e.ledgeLeft, e.ledgeRight, base)
                val lift = CloudShape.MAX_BOB
                var i = 0
                while (i < puffs.size) {
                    val reach = puffs[i + 2] + CloudShape.OUTLINE
                    assertTrue("${e.name} left t=$t", puffs[i] - reach >= -0.001f)
                    assertTrue("${e.name} right t=$t", puffs[i] + reach <= 128.001f)
                    // shade drops 2 units and the whole thing floats up to MAX_BOB down
                    assertTrue("${e.name} bottom t=$t", puffs[i + 1] + puffs[i + 2] + CloudShape.SHADE_DROP + lift <= viewH)
                    i += 3
                }
                assertTrue("${e.name} base", base[0] >= 0f && base[2] <= 128f && base[0] < base[2] && base[3] + lift <= viewH)
            }
        }
    }

    @Test fun faceStaysClearOfTheTopPuffs() {
        // really's lower eyelid is about 117.5 units down; the puffs in front of the face (outline included) must stay below it
        CloudShape.layout(Expression.REALLY.ledgeLeft, Expression.REALLY.ledgeRight, 0f, breathing = false, out = puffs)
        // the two centre bottom puffs and the whole top row sit in front of the chest; the outer bottom puffs are shoulders
        var top = Float.MAX_VALUE
        for (n in listOf(2, 3, 6, 7, 8, 9)) {
            top = minOf(top, puffs[n * 3 + 1] - puffs[n * 3 + 2] - CloudShape.OUTLINE)
        }
        assertTrue("highest edge $top", top >= 119f)
    }

    @Test fun puffsDoNotMoveWhenMotionIsReduced() {
        val a = FloatArray(puffs.size)
        CloudShape.layout(30f, 100f, 0.3f, breathing = false, out = puffs)
        CloudShape.layout(30f, 100f, 2.9f, breathing = false, out = a)
        assertTrue(puffs.contentEquals(a))
    }

    @Test fun breathingStaysWithinAboutThreePercent() {
        val still = FloatArray(puffs.size)
        CloudShape.layout(30f, 100f, 0f, breathing = false, out = still)
        for (t in listOf(0f, 1f, 2f, 3f, 4f)) {
            CloudShape.layout(30f, 100f, t, breathing = true, out = puffs)
            var i = 2
            while (i < puffs.size) {
                assertEquals(1f, puffs[i] / still[i], 0.031f)
                i += 3
            }
        }
    }

    @Test fun bobIsSmallAndOffWhenReduced() {
        assertEquals(0f, CloudShape.bob(1.2f, dragging = true, reduceMotion = true), 0f)
        var t = 0f
        while (t < 8f) {
            assertTrue(kotlin.math.abs(CloudShape.bob(t, dragging = false, reduceMotion = false)) <= 1.5f + 0.0001f)
            assertTrue(kotlin.math.abs(CloudShape.bob(t, dragging = true, reduceMotion = false)) <= CloudShape.MAX_BOB + 0.0001f)
            t += 0.05f
        }
    }
}
