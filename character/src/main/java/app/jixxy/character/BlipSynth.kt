package app.jixxy.character

import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

/**
 * Tiny procedural text blips for the typewriter reveal (D-021): no recordings, so no licence to carry.
 * Pure maths, unit-tested on the JVM; [JixxySounds] writes the result to a cache file and plays it.
 */
internal object BlipSynth {
    const val SAMPLE_RATE = 22_050
    const val BLIP_MS = 28

    /** Pitches of the four blips, in Hz. Mixing them gives the "talking" feel without being a tune. */
    val PITCHES = intArrayOf(520, 610, 700, 580)

    /** A complete mono 16-bit PCM WAV file: a [ms]-long soft sine at [freqHz] with a short fade in and out. */
    fun wav(freqHz: Int, ms: Int = BLIP_MS, volume: Float = 0.6f): ByteArray {
        val samples = SAMPLE_RATE * ms / 1000
        val data = ByteArray(samples * 2)
        val fade = min(samples / 4, 220).coerceAtLeast(1)
        for (i in 0 until samples) {
            val edge = min(min(i, samples - 1 - i), fade).toFloat() / fade
            val v = (sin(2.0 * PI * freqHz * i / SAMPLE_RATE) * edge * volume * Short.MAX_VALUE).toInt()
            data[2 * i] = (v and 0xFF).toByte()
            data[2 * i + 1] = ((v shr 8) and 0xFF).toByte()
        }
        return header(data.size) + data
    }

    private fun header(dataBytes: Int): ByteArray {
        val h = java.nio.ByteBuffer.allocate(44).order(java.nio.ByteOrder.LITTLE_ENDIAN)
        h.put("RIFF".toByteArray()).putInt(36 + dataBytes).put("WAVE".toByteArray())
        h.put("fmt ".toByteArray()).putInt(16).putShort(1).putShort(1)
        h.putInt(SAMPLE_RATE).putInt(SAMPLE_RATE * 2).putShort(2).putShort(16)
        h.put("data".toByteArray()).putInt(dataBytes)
        return h.array()
    }
}
