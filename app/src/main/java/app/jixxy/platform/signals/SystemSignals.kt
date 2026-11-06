package app.jixxy.platform.signals

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import app.jixxy.engine.api.Signal

/**
 * Screen, charger/battery, heartbeat, music and call signals, plus lifecycle of [UsageDetector].
 * Everything that polls runs only while the screen is interactive, so nothing wakes the device when it is off.
 * Calls are inferred from AudioManager.mode (MODE_IN_CALL / MODE_IN_COMMUNICATION); no phone-state permission.
 */
class SystemSignals(
    private val context: Context,
    private val bus: SignalBus,
    private val usage: UsageDetector,
    private val now: () -> Long = System::currentTimeMillis,
) {
    private val handler = Handler(Looper.getMainLooper())
    private val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val power = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val battery = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    private var started = false
    private var blind = false
    private var lastMusic: Boolean? = null
    private var lastCall: Boolean? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, intent: Intent) {
            if (blind) return
            val t = now()
            when (intent.action) {
                Intent.ACTION_SCREEN_ON -> { bus.emit(Signal.ScreenState(true, t)); startPolling() }
                Intent.ACTION_SCREEN_OFF -> { bus.emit(Signal.ScreenState(false, t)); stopPolling() }
                Intent.ACTION_POWER_CONNECTED -> bus.emit(Signal.Charger(true, batteryPct(), t))
                Intent.ACTION_POWER_DISCONNECTED -> bus.emit(Signal.Charger(false, batteryPct(), t))
                Intent.ACTION_BATTERY_LOW -> bus.emit(Signal.BatteryLow(batteryPct(), t))
            }
        }
    }

    private val heartbeat = object : Runnable {
        override fun run() {
            bus.emit(Signal.Heartbeat(now()))
            handler.postDelayed(this, HEARTBEAT_MS)
        }
    }

    private val audioPoll = object : Runnable {
        override fun run() {
            val music = audio.isMusicActive
            if (music != lastMusic) { lastMusic = music; bus.emit(Signal.MusicActive(music, now())) }
            val call = audio.mode == AudioManager.MODE_IN_CALL || audio.mode == AudioManager.MODE_IN_COMMUNICATION
            if (call != lastCall) { lastCall = call; bus.emit(Signal.CallState(call, now())) }
            handler.postDelayed(this, AUDIO_POLL_MS)
        }
    }

    /** Begins listening. No-op while blind mode is on. */
    fun start() {
        if (started || blind) return
        started = true
        context.registerReceiver(receiver, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
        })
        if (power.isInteractive) startPolling()
    }

    /** Stops every detector and timer. Safe to call repeatedly. */
    fun stop() {
        if (!started) return
        started = false
        runCatching { context.unregisterReceiver(receiver) }
        stopPolling()
    }

    /** Blind mode: stop all detection immediately; leaving it restarts detectors. */
    fun setBlindMode(on: Boolean) {
        blind = on
        if (on) stop() else start()
    }

    private fun startPolling() {
        stopPolling()
        usage.start()
        handler.postDelayed(heartbeat, HEARTBEAT_MS)
        handler.post(audioPoll)
    }

    private fun stopPolling() {
        usage.stop()
        handler.removeCallbacks(heartbeat)
        handler.removeCallbacks(audioPoll)
        lastMusic = null
        lastCall = null
    }

    private fun batteryPct(): Int = battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).coerceIn(0, 100)

    private companion object {
        const val HEARTBEAT_MS = 60_000L
        const val AUDIO_POLL_MS = 3_000L
    }
}
