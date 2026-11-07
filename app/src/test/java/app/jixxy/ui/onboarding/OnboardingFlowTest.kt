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

    @Test fun appPickerHasNoCap() {
        var s = emptySet<String>()
        (1..12).forEach { s = OnboardingFlow.toggleApp(s, "app$it") }
        assertEquals(12, s.size)
        assertEquals(11, OnboardingFlow.toggleApp(s, "app1").size)
    }

    @Test fun resumeFallsBackToHelloForBadOrOutOfRunSteps() {
        assertEquals(OnboardingStep.USAGE, OnboardingFlow.resume("USAGE", false, 34))
        assertEquals(OnboardingStep.HELLO, OnboardingFlow.resume("USAGE", true, 34))
        assertEquals(OnboardingStep.HELLO, OnboardingFlow.resume("NOPE", false, 34))
        assertEquals(OnboardingStep.HELLO, OnboardingFlow.resume(null, false, 34))
        assertEquals(OnboardingStep.HELLO, OnboardingFlow.resume("NOTIFICATIONS", false, 31))
    }
}
