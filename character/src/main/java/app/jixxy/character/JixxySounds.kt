package app.jixxy.character

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.SystemClock
import java.io.File
import java.util.EnumMap

/**
 * Jixxy's meows, purrs and text blips (D-021). Sounds are opt-in and the HOST decides whether anything may play:
 * pass [allowed], which is asked before every sound (settings, ringer mode, Do Not Disturb, calls, music, quiet hours
 * live there, so they are checked at the moment of playing). This class only decides WHAT plays.
 *
 * Plays through [SoundPool] as `USAGE_ASSISTANCE_SONIFICATION`, quietly, and never requests audio focus. At most one
 * meow per [MIN_CUE_GAP_MS]. Missing cue files are skipped without error. Call [release] when the overlay goes away.
 * Set [JixxyView.sounds] to start playing cues on state changes and blips while a bubble types out.
 */
class JixxySounds(private val context: Context, private val allowed: () -> Boolean) {
    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private val cueIds = EnumMap<SoundCue, Int>(SoundCue::class.java)
    private val blipIds = IntArray(BlipSynth.PITCHES.size)
    private val ready = HashSet<Int>()
    private var lastCueMs = 0L
    private var released = false

    /** When false only meows and purrs play; the typewriter blips stay silent. */
    var blips: Boolean = true

    init {
        pool.setOnLoadCompleteListener { _, sampleId, status -> if (status == 0) ready.add(sampleId) }
        for (cue in SoundCue.values()) {
            val res = context.resources.getIdentifier(RAW_PREFIX + cue.resName, "raw", context.packageName)
            if (res != 0) cueIds[cue] = pool.load(context, res, 1)
        }
        BlipSynth.PITCHES.forEachIndexed { i, hz ->
            val file = File(context.cacheDir, "jixxy_blip_$hz.wav")
            if (!file.exists() || file.length() == 0L) runCatching { file.writeBytes(BlipSynth.wav(hz)) }
            if (file.exists()) blipIds[i] = pool.load(file.path, 1)
        }
    }

    /** True when the audio file for [cue] is bundled and loaded (the demo shows this). */
    fun hasCue(cue: SoundCue): Boolean = cueIds[cue]?.let { it in ready } == true

    /** Plays the cue for entering [stateName], if there is one. [pokeLevel] is the tap count 1..3 of a POKED state. */
    fun playForState(stateName: String, pokeLevel: Int) {
        SoundCues.forState(stateName, pokeLevel)?.let { play(it, pokeLevel) }
    }

    /** Plays [cue] once at low volume; skipped when not [allowed], when too soon after the last cue, or when its file is missing. */
    fun play(cue: SoundCue, pokeLevel: Int = 1) {
        if (released || !allowed()) return
        val now = SystemClock.uptimeMillis()
        if (now - lastCueMs < MIN_CUE_GAP_MS) return
        val id = cueIds[cue]?.takeIf { it in ready } ?: return
        lastCueMs = now
        pool.play(id, CUE_VOLUME, CUE_VOLUME, 1, 0, SoundCues.rateFor(cue, pokeLevel))
    }

    /** One typewriter blip; [index] picks the pitch so a line sounds like talking. Quieter than the meows. */
    fun blip(index: Int) {
        if (released || !blips || !allowed()) return
        val id = blipIds[Math.floorMod(index, blipIds.size)].takeIf { it in ready } ?: return
        pool.play(id, BLIP_VOLUME, BLIP_VOLUME, 1, 0, 1f)
    }

    /** Frees the sound pool. The object plays nothing afterwards. */
    fun release() {
        released = true
        pool.release()
    }

    private companion object {
        const val RAW_PREFIX = "jixxy_snd_"
        const val MIN_CUE_GAP_MS = 1_200L
        const val CUE_VOLUME = 0.5f
        const val BLIP_VOLUME = 0.2f
    }
}
