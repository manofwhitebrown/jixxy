package app.jixxy.ui.components

import app.jixxy.ui.home.JixxyStatus
import app.jixxy.ui.onboarding.OnboardingStep
import org.junit.Assert.assertEquals
import org.junit.Test

class CatPosesTest {
    @Test fun everyStepHasAPose() {
        OnboardingStep.entries.forEach { CatPoses.forStep(it) }
    }

    @Test fun helloPeeksAndFinishSparkles() {
        assertEquals(CatPose.PEEK, CatPoses.forStep(OnboardingStep.HELLO))
        assertEquals(CatPose.OOH_NICE, CatPoses.forStep(OnboardingStep.FINISH))
    }

    @Test fun permissionAsksGetTheStare() {
        assertEquals(CatPose.REALLY, CatPoses.forStep(OnboardingStep.OVERLAY))
    }

    @Test fun napStatusShowsNapPose() {
        assertEquals(CatPose.NAP, CatPoses.forStatus(JixxyStatus.NAPPING))
    }
}
