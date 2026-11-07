package app.jixxy.ui.onboarding

/** Steps of the first-run flow. One permission per screen; grant state is shown on the same screen after returning from system Settings. */
enum class OnboardingStep { HELLO, OVERLAY, USAGE, NOTIFICATIONS, KEEP_ALIVE, PICK_APPS, ROAST, FINISH }

/** An app the user may pick for Pause (launcher-intent list, supplied by PLATFORM; PROTECTED apps are filtered out before this). */
data class AppChoice(val pkg: String, val label: String)

/** Pure, unit-testable navigation rules for onboarding. No Android imports. */
object OnboardingFlow {
    private const val NOTIFICATION_PERMISSION_SDK = 33

    /**
     * Ordered steps for this run. [lite] ("Just let me try him") grants overlay only,
     * so usage access, keep-alive and app picking are skipped; notification permission
     * is only asked on Android 13+.
     */
    fun order(lite: Boolean, sdkInt: Int): List<OnboardingStep> = buildList {
        add(OnboardingStep.HELLO)
        add(OnboardingStep.OVERLAY)
        if (!lite) {
            add(OnboardingStep.USAGE)
            if (sdkInt >= NOTIFICATION_PERMISSION_SDK) add(OnboardingStep.NOTIFICATIONS)
            add(OnboardingStep.KEEP_ALIVE)
            add(OnboardingStep.PICK_APPS)
        }
        add(OnboardingStep.ROAST)
        add(OnboardingStep.FINISH)
    }

    /** Next step, or [current] if already last (or not part of this run). */
    fun next(current: OnboardingStep, lite: Boolean, sdkInt: Int): OnboardingStep {
        val steps = order(lite, sdkInt)
        val i = steps.indexOf(current)
        return if (i in 0 until steps.lastIndex) steps[i + 1] else current
    }

    /** Previous step, or [current] if first. The user is never trapped. */
    fun back(current: OnboardingStep, lite: Boolean, sdkInt: Int): OnboardingStep {
        val steps = order(lite, sdkInt)
        val i = steps.indexOf(current)
        return if (i > 0) steps[i - 1] else current
    }

    /**
     * Step to resume at after the app was swiped away. [saved] is the stored step name; anything
     * unknown, or not part of this run, restarts at HELLO so a bad value can never trap the user.
     */
    fun resume(saved: String?, lite: Boolean, sdkInt: Int): OnboardingStep {
        val step = OnboardingStep.entries.firstOrNull { it.name == saved } ?: return OnboardingStep.HELLO
        return if (step in order(lite, sdkInt)) step else OnboardingStep.HELLO
    }

    /** Toggle [pkg] in [selected]. There is no cap on Pause apps (D-020); PROTECTED apps are filtered out before the list is shown. */
    fun toggleApp(selected: Set<String>, pkg: String): Set<String> =
        if (pkg in selected) selected - pkg else selected + pkg
}
