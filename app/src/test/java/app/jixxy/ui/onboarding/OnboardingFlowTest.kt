package app.jixxy.ui.onboarding

import org.junit.Assert.assertEquals
import org.junit.Test

class OnboardingFlowTest {
    @Test fun fullFlowOnAndroid13HasNotifications() {
        val steps = OnboardingFlow.order(lite = false, sdkInt = 33)
        assertEquals(
            listOf(OnboardingStep.HELLO, OnboardingStep.OVERLAY, OnboardingStep.USAGE, OnboardingStep.NOTIFICATIONS,
                OnboardingStep.KEEP_ALIVE, OnboardingStep.PICK_APPS, OnboardingStep.ROAST, OnboardingStep.FINISH), steps)
    }

    @Test fun notificationsSkippedBelowAndroid13() {
        assertEquals(false, OnboardingStep.NOTIFICATIONS in OnboardingFlow.order(lite = false, sdkInt = 30))
    }

    @Test fun liteModeOnlyNeedsOverlay() {
        assertEquals(
            listOf(OnboardingStep.HELLO, OnboardingStep.OVERLAY, OnboardingStep.ROAST, OnboardingStep.FINISH),
            OnboardingFlow.order(lite = true, sdkInt = 34))
    }

    @Test fun nextAndBackStayInBounds() {
        assertEquals(OnboardingStep.HELLO, OnboardingFlow.back(OnboardingStep.HELLO, false, 34))
        assertEquals(OnboardingStep.FINISH, OnboardingFlow.next(OnboardingStep.FINISH, false, 34))
        assertEquals(OnboardingStep.USAGE, OnboardingFlow.next(OnboardingStep.OVERLAY, false, 34))
        assertEquals(OnboardingStep.OVERLAY, OnboardingFlow.back(OnboardingStep.USAGE, false, 34))
    }

    @Test fun appPickerCapsAtThree() {
        var s = emptySet<String>()
        listOf("a", "b", "c", "d").forEach { s = OnboardingFlow.toggleApp(s, it) }
        assertEquals(setOf("a", "b", "c"), s)
        assertEquals(setOf("b", "c", "d"), OnboardingFlow.toggleApp(OnboardingFlow.toggleApp(s, "a"), "d"))
    }
}
