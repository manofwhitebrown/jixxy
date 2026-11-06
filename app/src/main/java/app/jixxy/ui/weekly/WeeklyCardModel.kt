package app.jixxy.ui.weekly

import app.jixxy.engine.review.WeeklyReview
import java.time.Instant
import java.time.ZoneId

/** What the weekly card shows. Plain counts, never framed as success or failure. */
data class WeeklyCardModel(
    val pauses: Int,
    val left: Int,
    val stayed: Int,
    val roast: String?,
    val compliment: String?,
    val quiet: Boolean,
)

/** Pure rules for the weekly screen and card. No Android imports; unit-tested. */
object WeeklyCardLogic {
    private const val DAYS = 7L

    fun model(review: WeeklyReview): WeeklyCardModel = WeeklyCardModel(
        pauses = review.pausesFaced,
        left = review.pausesLeft,
        stayed = review.pausesStayed,
        roast = review.roast?.text,
        compliment = review.compliment?.text,
        quiet = review.isQuiet,
    )

    /** Last seven local days including today: [from, to) with `to = now + 1`. DST-safe (calendar days, not 24 h blocks). */
    fun range(now: Long, zone: ZoneId = ZoneId.systemDefault()): Pair<Long, Long> {
        val startToday = Instant.ofEpochMilli(now).atZone(zone).toLocalDate().atStartOfDay(zone)
        return startToday.minusDays(DAYS - 1).toInstant().toEpochMilli() to now + 1
    }
}
