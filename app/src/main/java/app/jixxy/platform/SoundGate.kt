package app.jixxy.platform

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import app.jixxy.engine.api.UserSettings
import java.time.LocalTime

/**
 * The ONE place that reads the phone's state for [SoundPolicy] (D-021). The overlay service and the "Test meow" helper both
 * use it, so the two can never disagree about whether a sound may play. Any doubt means silence.
 *
 * Ringer mode and music state come from AudioManager (no permission). Do Not Disturb is read with
 * NotificationManager.getCurrentInterruptionFilter(); only INTERRUPTION_FILTER_ALL counts as "off" (unknown = silent).
 */
class SoundGate(
    private val context: Context,
    private val settings: () -> UserSettings,
    private val napping: () -> Boolean,
) {
    /** [ignoreSwitch] = true for "Test meow", which is only offered while the Sounds switch is on and must not depend on a settings race. */
    fun allowed(ignoreSwitch: Boolean = false): Boolean {
        val s = settings()
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val nm = context.getSystemService(NotificationManager::class.java)
        val now = LocalTime.now()
        return SoundPolicy.allowed(
            soundsEnabled = s.soundsEnabled || ignoreSwitch,
            ringerNormal = audio.ringerMode == AudioManager.RINGER_MODE_NORMAL,
            doNotDisturbOff = nm.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALL,
            inCall = audio.mode != AudioManager.MODE_NORMAL,
            musicPlaying = audio.isMusicActive,
            quietHours = SoundPolicy.inQuietHours(now.hour * 60 + now.minute, s.quietStartMin, s.quietEndMin),
            napping = napping(),
            blindMode = s.blindMode,
            bubbleId = null,
            lastCueBubbleId = null,  // JixxySounds already enforces its own minimum gap between cues
        )
    }
}
