package app.jixxy.platform

/** Interim home for service/notification text until UI moves it into res/strings (T-UI). */
internal object PlatformStrings {
    const val CHANNEL_NAME = "Jixxy is around"
    const val NOTIFICATION_TITLE = "Jixxy is around"
    const val ACTION_NAP = "Nap 1h"
    const val ACTION_STOP = "Stop"
    const val WAKE_TITLE = "Wake Jixxy"
    const val PAUSE_LEAVE = "Leave"
    fun pauseStay(minutes: Int) = "Stay $minutes more minutes"
}
