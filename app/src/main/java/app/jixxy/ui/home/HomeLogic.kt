package app.jixxy.ui.home

import app.jixxy.engine.api.NagLevel
import app.jixxy.engine.api.UsageEvent
import app.jixxy.engine.api.UsageEventType
import app.jixxy.engine.api.UserSettings
import java.time.Instant
import java.time.ZoneId

/** What Jixxy is doing right now, as shown on Home. Blind mode wins, then a stopped service, then napping. */
enum class JixxyStatus { AWAKE, NAPPING, BLIND, STOPPED }

/** A gentle heads-up on Home. Overlay missing wins: without it he cannot show up at all. */
enum class HomeNotice { NONE, OVERLAY_MISSING, NO_APPS }

/** Today's own numbers. Plain counts, never framed as success or failure. */
data class TodayStats(val paused: Int = 0, val left: Int = 0, val stayed: Int = 0)

/** Everything the Home screen needs. [napUntilTs] is set only while [status] is NAPPING. */
data class HomeUiState(
    val status: JixxyStatus = JixxyStatus.AWAKE,
    val napUntilTs: Long? = null,
    val today: TodayStats = TodayStats(),
    val notice: HomeNotice = HomeNotice.NONE,
    val nagLevel: NagLevel = NagLevel.NAGGY,
)

/** Pure rules for Home. No Android imports; unit-tested. */
object HomeLogic {
    const val NAP_MINUTES = 60
    private const val MS_PER_MINUTE = 60_000L

    /** [serviceRunning] is the real state of the sprite service, not a local toggle. */
    fun status(settings: UserSettings, now: Long, serviceRunning: Boolean = true): JixxyStatus = when {
        settings.blindMode -> JixxyStatus.BLIND
        !serviceRunning -> JixxyStatus.STOPPED
        (settings.napUntilTs ?: 0L) > now -> JixxyStatus.NAPPING
        else -> JixxyStatus.AWAKE
    }

    fun notice(settings: UserSettings, overlayGranted: Boolean): HomeNotice = when {
        !overlayGranted -> HomeNotice.OVERLAY_MISSING
        settings.targetApps.isEmpty() && !settings.blindMode -> HomeNotice.NO_APPS
        else -> HomeNotice.NONE
    }

    fun napUntil(now: Long, minutes: Int = NAP_MINUTES): Long = now + minutes * MS_PER_MINUTE

    /** Counts today's pause events. Events outside the list's day must already be filtered out by the caller's query. */
    fun stats(events: List<UsageEvent>): TodayStats = TodayStats(
        paused = events.count { it.type == UsageEventType.PAUSE_SHOWN },
        left = events.count { it.type == UsageEventType.PAUSE_LEAVE },
        stayed = events.count { it.type == UsageEventType.PAUSE_STAY },
    )

    fun uiState(settings: UserSettings, events: List<UsageEvent>, now: Long, serviceRunning: Boolean = true, overlayGranted: Boolean = true): HomeUiState {
        val status = status(settings, now, serviceRunning)
        return HomeUiState(
            status = status,
            napUntilTs = if (status == JixxyStatus.NAPPING) settings.napUntilTs else null,
            today = stats(events),
            notice = notice(settings, overlayGranted),
            nagLevel = settings.nagLevel,
        )
    }

    /** Epoch millis of local midnight for the day containing [now]. */
    fun startOfLocalDay(now: Long, zone: ZoneId = ZoneId.systemDefault()): Long =
        Instant.ofEpochMilli(now).atZone(zone).toLocalDate().atStartOfDay(zone).toInstant().toEpochMilli()
}
