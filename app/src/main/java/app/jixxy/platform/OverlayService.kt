package app.jixxy.platform

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import app.jixxy.R
import app.jixxy.character.JixxyView
import app.jixxy.character.JixxyViewListener
import app.jixxy.engine.JixxyBrains
import app.jixxy.engine.api.CharacterState
import app.jixxy.engine.api.PauseChoiceKind
import app.jixxy.engine.api.PauseSpec
import app.jixxy.engine.api.ReactionKind
import app.jixxy.engine.api.Signal
import app.jixxy.engine.api.UserSettings
import app.jixxy.engine.lines.LineBank
import app.jixxy.engine.lines.LineBankLoader
import app.jixxy.platform.signals.CategoryResolver
import app.jixxy.ui.MainActivity
import app.jixxy.platform.signals.SystemSignals
import app.jixxy.platform.signals.UsageDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.ZoneId
import kotlinx.coroutines.launch

/**
 * Foreground service owning the sprite window, the signal sources and the [Dispatcher] (T-PLATFORM-2/5).
 *
 * The sprite window is exactly the sprite's size, so touches elsewhere reach the app underneath.
 * Docs: TYPE_APPLICATION_OVERLAY needs SYSTEM_ALERT_WINDOW (developer.android.com/reference/android/view/WindowManager.LayoutParams#TYPE_APPLICATION_OVERLAY).
 */
class OverlayService : Service(), ReactionRenderer {

    private lateinit var windowManager: WindowManager
    private lateinit var app: JixxyApp
    private lateinit var pauseWindow: PauseWindow
    private var systemSignals: SystemSignals? = null
    private var sprite: JixxyView? = null
    private lateinit var params: WindowManager.LayoutParams
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var settings = UserSettings()
    private var napping = false
    private var screenOn = true
    private var snappedRight = false

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            screenOn = intent.action == Intent.ACTION_SCREEN_ON
            if (!screenOn) pauseWindow.dismiss()
            applyVisibility()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        app = application as JixxyApp
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        pauseWindow = PauseWindow(this, windowManager)
        createChannel()
        if (!enterForeground()) {
            stopSelf()
            return
        }
        setWantedRunning(this, true)
        addSprite()
        registerReceiver(screenReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        })
        startPipeline()
    }

    /** Loads state + line bank, builds the real brain, then wires detectors -> bus -> dispatcher and follows settings. */
    private fun startPipeline() {
        val categories = CategoryResolver(this)
        val usage = UsageDetector(this, app.bus, categories)
        systemSignals = SystemSignals(this, app.bus, usage)
        scope.launch {
            val (initial, bank) = withContext(Dispatchers.IO) {
                app.stateStore.load() to loadLineBank(app.settings.settings.first().language)
            }
            val real = JixxyBrains.create(
                lineBank = bank,
                initialState = initial,
                settings = { settings },
                appLabel = { pkg -> categories.label(pkg) },
                zone = ZoneId.systemDefault(),
            )
            val brain = PersistingBrain(real, real::currentState, real::drainEvents, app.stateStore, scope)
            // The dispatcher subscribes before any detector starts, so no early signal is lost.
            Dispatcher(scope, app.bus, brain, app.stateStore, this@OverlayService, { settings }).start()
            app.settings.settings.collect { s ->
                settings = s
                app.blindMode = s.blindMode
                systemSignals?.setBlindMode(s.blindMode)  // starts detectors when blind mode is off
                updateNap()
            }
        }
        scope.launch { app.stateStore.purgeExpired() }
    }

    /** Reads every `*.json` under assets/lines/<lang>; falls back to English if that language has none. */
    private fun loadLineBank(language: String): LineBank {
        fun texts(lang: String): List<String> =
            (assets.list("lines/$lang") ?: emptyArray()).filter { it.endsWith(".json") }.sorted().map { name ->
                assets.open("lines/$lang/$name").bufferedReader().use { it.readText() }
            }
        val chosen = texts(language).ifEmpty { texts("en") }
        return LineBankLoader.fromTexts(chosen)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                // Stop = clean slate: clear the nap, remember the user wants him off, then end the service.
                // The settings write must finish before stopSelf(), because onDestroy cancels the scope.
                setWantedRunning(this, false)
                scope.launch {
                    runCatching { app.settings.update { it.copy(napUntilTs = NapPolicy.afterStop()) } }
                    stopSelf()
                }
            }
            ACTION_NAP -> scope.launch {
                val t = System.currentTimeMillis()
                app.settings.update { it.copy(napUntilTs = NapPolicy.napUntil(t)) }
                app.bus.emit(Signal.NapRequested(60, t))
            }
            ACTION_WAKE -> {
                // Explicit user wake always clears the nap and shows the sprite.
                // startForegroundService() must be answered by startForeground() every time, even if already running.
                enterForeground()
                setWantedRunning(this, true)
                scope.launch {
                    app.settings.update { it.copy(napUntilTs = NapPolicy.afterWake()) }
                    app.bus.emit(Signal.WakeRequested(System.currentTimeMillis()))
                }
            }
            else -> enterForeground()  // plain start (boot, restart): keep any still-running nap
        }
        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Rotation: clamp the saved position into the new screen bounds.
        val view = sprite ?: return
        // Keep the snapped side: a sprite on the right edge must stay on the right edge after rotating.
        params.x = if (snappedRight) maxX(view) else params.x.coerceIn(0, maxX(view))
        params.y = params.y.coerceIn(0, maxY(view))
        windowManager.updateViewLayout(view, params)
    }

    override fun onDestroy() {
        scope.cancel()
        handler.removeCallbacksAndMessages(null)
        systemSignals?.stop()
        pauseWindow.dismiss()
        runCatching { unregisterReceiver(screenReceiver) }
        sprite?.let { runCatching { windowManager.removeView(it) } }  // never leak the overlay view
        sprite = null
        super.onDestroy()
    }

    // ---- ReactionRenderer -------------------------------------------------------------------------------

    override fun say(text: String, bubbleMs: Long, lineId: String?) {
        sprite?.showBubble(text, bubbleMs, lineId)
    }

    override fun setCharacterState(state: CharacterState) {
        sprite?.setCharacterState(state)
    }

    override fun showPause(spec: PauseSpec, text: String, onChoice: (PauseChoiceKind) -> Unit) {
        pauseWindow.show(spec, text, onChoice)
    }

    /** Sends the user to the home screen. Background-start allowance via SYSTEM_ALERT_WINDOW is unverified for Android 14+. */
    override fun goHome() {
        startActivity(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    // ---- window + notification ---------------------------------------------------------------------------

    /** Returns false if Android refused to start the foreground service. */
    private fun enterForeground(): Boolean = try {
        val type = if (Build.VERSION.SDK_INT >= 34) ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
        ServiceCompat.startForeground(this, NOTIFICATION_ID, buildNotification(), type)
        true
    } catch (e: IllegalStateException) {
        // ForegroundServiceStartNotAllowedException (API 31+) extends IllegalStateException.
        postWakeNotification()
        false
    }

    private fun addSprite() {
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return }
        val view = JixxyView(this)
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = prefs.getInt(KEY_X, 0)
            y = prefs.getInt(KEY_Y, resources.displayMetrics.heightPixels / 3)
        }
        snappedRight = prefs.getBoolean(KEY_RIGHT, false)
        view.listener = spriteListener(view, prefs)
        windowManager.addView(view, params)
        sprite = view
        applyVisibility()
    }

    /** The view reports raw finger positions; the window follows them, snaps to an edge on release, and signals the brain. */
    private fun spriteListener(view: JixxyView, prefs: android.content.SharedPreferences) = object : JixxyViewListener {
        override fun onTap(count: Int) = app.bus.emit(Signal.SpriteTapped(count, System.currentTimeMillis()))

        override fun onDragMove(rawX: Float, rawY: Float) {
            params.x = (rawX - view.width / 2f).toInt().coerceIn(0, maxX(view))
            params.y = (rawY - view.height / 2f).toInt().coerceIn(0, maxY(view))
            windowManager.updateViewLayout(view, params)
        }

        override fun onDragEnd(rawX: Float, rawY: Float) {
            onDragMove(rawX, rawY)
            snappedRight = params.x >= maxX(view) / 2
            params.x = if (snappedRight) maxX(view) else 0
            windowManager.updateViewLayout(view, params)
            prefs.edit().putInt(KEY_X, params.x).putInt(KEY_Y, params.y).putBoolean(KEY_RIGHT, snappedRight).apply()
            app.bus.emit(Signal.SpriteDropped(System.currentTimeMillis()))
        }

        override fun onReaction(lineId: String?, kind: ReactionKind) {
            if (lineId != null) app.bus.emit(Signal.UserReaction(lineId, kind, System.currentTimeMillis()))
        }
    }

    private fun maxX(v: View) = (resources.displayMetrics.widthPixels - v.measuredWidth).coerceAtLeast(0)
    private fun maxY(v: View) = (resources.displayMetrics.heightPixels - v.measuredHeight).coerceAtLeast(0)

    /** Recomputes the nap flag from settings and schedules the wake-up at napUntilTs. */
    private fun updateNap() {
        handler.removeCallbacks(napCheck)
        val now = System.currentTimeMillis()
        val until = settings.napUntilTs
        val remaining = NapPolicy.remainingMs(until, now)
        napping = remaining > 0
        if (napping) {
            pauseWindow.dismiss()
            handler.postDelayed(napCheck, remaining)
        } else if (until != null) {
            // Expired nap left over in settings: clear it so it can never hide him later.
            scope.launch { runCatching { app.settings.update { it.copy(napUntilTs = null) } } }
        }
        applyVisibility()
    }

    private val napCheck = Runnable { updateNap() }

    private fun applyVisibility() {
        val awake = NapPolicy.shouldShowSprite(screenOn, settings.napUntilTs, System.currentTimeMillis(), settings.blindMode)
        sprite?.visibility = if (awake) View.VISIBLE else View.GONE
        sprite?.setScreenActive(awake)
    }

    private fun createChannel() {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, PlatformStrings.CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
        )
    }

    private fun actionIntent(action: String, code: Int): PendingIntent = PendingIntent.getBroadcast(
        this, code,
        Intent(this, NotificationActionReceiver::class.java).setAction(action),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    private fun buildNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_jixxy)
            .setContentTitle(PlatformStrings.NOTIFICATION_TITLE)
            .setOngoing(true)
            .addAction(0, PlatformStrings.ACTION_NAP, actionIntent(ACTION_NAP, 1))
            .addAction(0, PlatformStrings.ACTION_WAKE, actionIntent(ACTION_WAKE, 4))
            .addAction(0, PlatformStrings.ACTION_STOP, actionIntent(ACTION_STOP, 2))
            .build()

    /** Fallback when the OS refuses the foreground start: ask the user to wake Jixxy by hand. Skipped if notifications are off. */
    @SuppressLint("MissingPermission")  // guarded by areNotificationsEnabled(), which covers POST_NOTIFICATIONS
    private fun postWakeNotification() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) return
        val open = PendingIntent.getActivity(
            this, 3, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val n = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_jixxy)
            .setContentTitle(PlatformStrings.WAKE_TITLE)
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this).notify(WAKE_ID, n)
    }

    companion object {
        const val ACTION_NAP = "app.jixxy.action.NAP"
        const val ACTION_STOP = "app.jixxy.action.STOP"
        const val ACTION_WAKE = "app.jixxy.action.WAKE"
        private const val CHANNEL_ID = "jixxy_around"
        private const val NOTIFICATION_ID = 1
        private const val WAKE_ID = 2
        private const val PREFS = "platform_overlay"
        private const val KEY_X = "x"
        private const val KEY_Y = "y"
        private const val KEY_RIGHT = "snapped_right"
        private const val KEY_WANTED = "wanted_running"

        /** Explicit user wake: starts the service (if needed) and clears any nap. Use this everywhere the user wakes him. */
        fun wake(context: Context) {
            context.startForegroundService(Intent(context, OverlayService::class.java).setAction(ACTION_WAKE))
        }

        /** Nap for one hour. */
        fun nap(context: Context) {
            context.startService(Intent(context, OverlayService::class.java).setAction(ACTION_NAP))
        }

        /** Stop: hides him, clears the nap, ends the service. */
        fun stop(context: Context) {
            context.startService(Intent(context, OverlayService::class.java).setAction(ACTION_STOP))
        }

        /** True from the moment the service starts until the user explicitly stops it; read by [BootReceiver]. */
        fun wantedRunning(context: Context): Boolean =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_WANTED, false)

        private fun setWantedRunning(context: Context, wanted: Boolean) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_WANTED, wanted).apply()
        }
    }
}
