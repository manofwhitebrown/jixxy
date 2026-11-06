package app.jixxy.ui.home

import app.jixxy.engine.api.UsageEvent
import app.jixxy.engine.api.UsageEventType
import app.jixxy.engine.api.UserSettings
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeLogicTest {
    private val now = 1_000_000L

    @Test fun awakeByDefault() = assertEquals(JixxyStatus.AWAKE, HomeLogic.status(UserSettings(), now))

    @Test fun nappingWhileNapUntilIsInTheFuture() {
        assertEquals(JixxyStatus.NAPPING, HomeLogic.status(UserSettings(napUntilTs = now + 1), now))
        assertEquals(JixxyStatus.AWAKE, HomeLogic.status(UserSettings(napUntilTs = now), now))
        assertEquals(JixxyStatus.AWAKE, HomeLogic.status(UserSettings(napUntilTs = now - 5), now))
    }

    @Test fun blindModeWinsOverNap() {
        assertEquals(JixxyStatus.BLIND, HomeLogic.status(UserSettings(blindMode = true, napUntilTs = now + 10), now))
    }

    @Test fun napIsOneHourByDefault() = assertEquals(now + 3_600_000L, HomeLogic.napUntil(now))

    @Test fun statsCountOnlyPauseEvents() {
        val events = listOf(
            UsageEvent(1, UsageEventType.PAUSE_SHOWN), UsageEvent(2, UsageEventType.PAUSE_SHOWN),
            UsageEvent(3, UsageEventType.PAUSE_SHOWN), UsageEvent(4, UsageEventType.PAUSE_LEAVE),
            UsageEvent(5, UsageEventType.PAUSE_LEAVE), UsageEvent(6, UsageEventType.PAUSE_STAY),
            UsageEvent(7, UsageEventType.FOREGROUND, pkg = "x"),
        )
        assertEquals(TodayStats(paused = 3, left = 2, stayed = 1), HomeLogic.stats(events))
    }

    @Test fun napUntilOnlyExposedWhileNapping() {
        assertNull(HomeLogic.uiState(UserSettings(napUntilTs = now - 1), emptyList(), now).napUntilTs)
        assertEquals(now + 9, HomeLogic.uiState(UserSettings(napUntilTs = now + 9), emptyList(), now).napUntilTs)
    }

    @Test fun startOfLocalDayUsesZone() {
        val utc = ZoneId.of("UTC")
        assertEquals(86_400_000L, HomeLogic.startOfLocalDay(86_400_000L + 5_000L, utc))
    }
}
