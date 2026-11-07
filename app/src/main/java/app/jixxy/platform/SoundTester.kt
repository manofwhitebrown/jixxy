package app.jixxy.platform

import android.content.Context
import android.os.Handler
import android.os.Looper
import app.jixxy.character.JixxySounds
import app.jixxy.character.SoundCue
import app.jixxy.engine.api.UserSettings
import kotlinx.coroutines.delay

/**
 * "Test meow" for Settings (UI request, D-021). Same gate as the real thing ([SoundGate]), so "Played" means a real meow
 * would play right now and "false" means the phone state (silent, Do Not Disturb, call, music, quiet hours, nap, blind mode)
 * says no. Works whether or not the overlay service is running. Safe to call from the main thread.
 */
class SoundTester(
    private val context: Context,
    private val settings: suspend () -> UserSettings,
    private val now: () -> Long = System::currentTimeMillis,
) {
    /** True if a sound was started; false if the gate said no or sounds could not start. Never throws. */
    suspend fun testMeow(): Boolean {
        val s = runCatching { settings() }.getOrNull() ?: return false
        val gate = SoundGate(context, { s }, { NapPolicy.isNapping(s.napUntilTs, now()) })
        if (!gate.allowed(ignoreSwitch = true)) return false
        val player = runCatching { JixxySounds(context) { gate.allowed(ignoreSwitch = true) } }.getOrNull() ?: return false
        // SoundPool loads its samples in the background; give it a moment before the first play.
        delay(LOAD_WAIT_MS)
        if (player.hasCue(SoundCue.CURIOUS_MEW)) player.play(SoundCue.CURIOUS_MEW) else player.blip(0)
        Handler(Looper.getMainLooper()).postDelayed({ player.release() }, RELEASE_AFTER_MS)
        return true
    }

    private companion object {
        const val LOAD_WAIT_MS = 500L
        const val RELEASE_AFTER_MS = 3_000L
    }
}
