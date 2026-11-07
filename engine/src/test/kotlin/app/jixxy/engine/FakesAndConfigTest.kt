package app.jixxy.engine

import app.jixxy.engine.api.*
import app.jixxy.engine.fakes.InMemorySettingsRepository
import app.jixxy.engine.fakes.InMemoryStateStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FakesAndConfigTest {
    @Test fun stateStoreRoundTripsAndFiltersEvents() = runTest {
        val s = InMemoryStateStore()
        s.save(BrainState(energy = 42))
        assertEquals(42, s.load().energy)
        s.logEvent(UsageEvent(300, UsageEventType.NAP))
        s.logEvent(UsageEvent(100, UsageEventType.CHARGER_ON))
        s.logEvent(UsageEvent(500, UsageEventType.NAP))
        assertEquals(listOf(100L, 300L), s.eventsBetween(0, 500).map { it.ts })
        s.clearEvents()
        assertEquals(0, s.eventsBetween(0, 1000).size)
    }

    @Test fun settingsUpdateApplies() = runTest {
        val r = InMemorySettingsRepository()
        r.update { it.copy(roast = RoastLevel.SAVAGE) }
        assertEquals(RoastLevel.SAVAGE, r.settings.first().roast)
    }

    @Test fun bondThresholds() {
        assertEquals(Bond.STRANGER, BrainConfig.bondFor(0))
        assertEquals(Bond.ROOMMATE, BrainConfig.bondFor(100))
        assertEquals(Bond.FRIEND, BrainConfig.bondFor(400))
        assertEquals(Bond.RIDE_OR_DIE, BrainConfig.bondFor(5000))
    }

    @Test fun defaultsMatchBrief() {
        assertEquals(NagLevel.NAGGY, UserSettings().nagLevel)
        assertEquals(15, BrainConfig.maxUnpromptedPerDay(NagLevel.NAGGY))
        assertEquals(10, BrainConfig.minGapMinutes(NagLevel.NAGGY))
        assertEquals(3, BrainConfig.maxUnpromptedPerDay(NagLevel.CALM))
        assertEquals(30, BrainConfig.maxUnpromptedPerDay(NagLevel.CHAOS))
        assertEquals(false, UserSettings().soundsEnabled)
        assertEquals(200, BrainConfig.dedupeWindow)
        assertEquals(60, UserSettings().quietStartMin)
        assertEquals(420, UserSettings().quietEndMin)
    }
}
