package app.jixxy.ui.components

import app.jixxy.ui.home.JixxyStatus
import app.jixxy.ui.onboarding.OnboardingStep

/** Which of the Owner's expression images a screen shows. Pure mapping, no Android imports. */
enum class CatPose { PEEK, REALLY, SMUG, NAP, OOH_NICE, SHOCKED }

object CatPoses {
    /** Hello: he peeks in; permission asks get the half-lidded stare; the end is sparkly. */
    fun forStep(step: OnboardingStep): CatPose = when (step) {
        OnboardingStep.HELLO -> CatPose.PEEK
        OnboardingStep.OVERLAY, OnboardingStep.USAGE, OnboardingStep.NOTIFICATIONS, OnboardingStep.KEEP_ALIVE -> CatPose.REALLY
        OnboardingStep.PICK_APPS, OnboardingStep.ROAST -> CatPose.SMUG
        OnboardingStep.FINISH -> CatPose.OOH_NICE
    }

    /** Home reflects the real service state: napping shows the nap pose; blind mode is the stare. */
    fun forStatus(status: JixxyStatus): CatPose = when (status) {
        JixxyStatus.AWAKE -> CatPose.SMUG
        JixxyStatus.NAPPING -> CatPose.NAP
        JixxyStatus.BLIND -> CatPose.REALLY
    }
}
