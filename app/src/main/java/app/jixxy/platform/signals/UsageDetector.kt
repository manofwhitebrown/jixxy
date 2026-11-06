package app.jixxy.platform.signals

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import app.jixxy.engine.api.Signal
import app.jixxy.platform.UsageAccess

/**
 * Foreground-app detection via UsageStatsManager.queryEvents (needs PACKAGE_USAGE_STATS access).
 * Polls only between [start] and [stop]; the owner stops it whenever the screen is not interactive.
 * Event types: ACTIVITY_RESUMED on API 29+, MOVE_TO_FOREGROUND below
 * (developer.android.com/reference/android/app/usage/UsageEvents.Event).
 */
class UsageDetector(
    private val context: Context,
    private val bus: SignalBus,
    private val categories: CategoryResolver,
    private val now: () -> Long = System::currentTimeMillis,
) {
    private val handler = Handler(Looper.getMainLooper())
    private val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private var running = false
    private var lastQueryTs = 0L
    private var currentPkg: String? = null
    private var sessionStartTs = 0L
    private var lastTickMinute = 0

    private val poll = object : Runnable {
        override fun run() {
            if (!running) return
            pollOnce()
            handler.postDelayed(this, POLL_MS)
        }
    }

    fun start() {
        if (running) return
        running = true
        lastQueryTs = now() - LOOKBACK_MS
        handler.post(poll)
    }

    fun stop() {
        running = false
        handler.removeCallbacks(poll)
        currentPkg = null  // a new session starts when the screen comes back
    }

    fun hasUsageAccess(): Boolean = UsageAccess.isGranted(context)

    private fun pollOnce() {
        if (!hasUsageAccess()) return
        val t = now()
        val events = usm.queryEvents(lastQueryTs, t)
        lastQueryTs = t
        val event = UsageEvents.Event()
        var newest: String? = null
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == FOREGROUND_EVENT) newest = event.packageName
        }
        if (newest != null && newest != currentPkg) {
            currentPkg = newest
            sessionStartTs = t
            lastTickMinute = 0
            bus.emit(Signal.ForegroundApp(newest, categories.resolve(newest), t))
        }
        currentPkg?.let { pkg ->
            val minutes = ((t - sessionStartTs) / 60_000L).toInt()
            if (minutes > lastTickMinute) {
                lastTickMinute = minutes
                bus.emit(Signal.SessionTick(pkg, minutes, t))
            }
        }
    }

    private companion object {
        const val POLL_MS = 1_500L
        const val LOOKBACK_MS = 10_000L
        val FOREGROUND_EVENT =
            if (Build.VERSION.SDK_INT >= 29) UsageEvents.Event.ACTIVITY_RESUMED
            else @Suppress("DEPRECATION") UsageEvents.Event.MOVE_TO_FOREGROUND
    }
}
