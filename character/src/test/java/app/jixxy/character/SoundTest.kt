package app.jixxy.character

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SoundTest {
    @Test fun statesMakeTheCueFromTheBrief() {
        assertEquals(SoundCue.CURIOUS_MEW, SoundCues.forState("PEEK"))
        assertEquals(SoundCue.ANNOYED_MEW, SoundCues.forState("POKED", 2))
        assertEquals(SoundCue.GRUMPY_MEW, SoundCues.forState("WAKE_GRUMPY"))
        assertEquals(SoundCue.PURR, SoundCues.forState("SLEEP"))
        assertEquals(SoundCue.HAPPY_MEW, SoundCues.forState("CHARGING"))
        assertEquals(SoundCue.WORRIED_MEW, SoundCues.forState("LOW_BATTERY"))
        assertEquals(SoundCue.YELP, SoundCues.forState("FALLING"))
    }

    @Test fun quietStatesAreSilent() {
        for (n in listOf("IMPRESSED", "SNORE", "IDLE", "BLINK", "DRAGGED", "HAPPY", "SMUG", "MUSIC", "SOMETHING_NEW")) {
            assertNull(n, SoundCues.forState(n))
        }
    }

    @Test fun pokesGetHigherAndOtherCuesStayNormal() {
        assertEquals(1f, SoundCues.rateFor(SoundCue.ANNOYED_MEW, 1), 0f)
        assertEquals(1.15f, SoundCues.rateFor(SoundCue.ANNOYED_MEW, 2), 0.0001f)
        assertEquals(1.3f, SoundCues.rateFor(SoundCue.ANNOYED_MEW, 3), 0.0001f)
        assertEquals(1.3f, SoundCues.rateFor(SoundCue.ANNOYED_MEW, 9), 0.0001f)
        assertEquals(1f, SoundCues.rateFor(SoundCue.PURR, 3), 0f)
    }

    @Test fun blipIsAValidShortWav() {
        val wav = BlipSynth.wav(600)
        val samples = BlipSynth.SAMPLE_RATE * BlipSynth.BLIP_MS / 1000
        assertEquals(44 + samples * 2, wav.size)
        assertEquals("RIFF", String(wav, 0, 4))
        assertEquals("WAVE", String(wav, 8, 4))
        assertEquals("data", String(wav, 36, 4))
    }

    @Test fun blipFadesInAndOutSoItDoesNotClick() {
        val wav = BlipSynth.wav(600)
        fun sample(i: Int) = ((wav[44 + 2 * i + 1].toInt() shl 8) or (wav[44 + 2 * i].toInt() and 0xFF)).toShort().toInt()
        val last = (wav.size - 44) / 2 - 1
        assertEquals(0, sample(0))
        assertEquals(0, sample(last))
        assertTrue((0..last).any { kotlin.math.abs(sample(it)) > 5_000 })
    }

    private val rawDir = java.io.File("src/main/res/raw")

    @Test fun everyCueHasABundledFile() {
        for (cue in SoundCue.values()) {
            val f = java.io.File(rawDir, "jixxy_snd_${cue.resName}.ogg")
            assertTrue("${cue.name}: missing ${f.path}", f.isFile && f.length() > 1_000)
        }
    }

    @Test fun bundledAudioStaysUnder400Kb() {
        val total = (rawDir.listFiles() ?: emptyArray()).sumOf { it.length() }
        assertTrue("audio is ${total / 1024} KB", total <= 400 * 1024)
    }
}
