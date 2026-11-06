package app.jixxy.engine.rules

import app.jixxy.engine.api.BrainState
import app.jixxy.engine.api.Reaction
import app.jixxy.engine.api.ReactionKind
import app.jixxy.engine.api.Signal
import app.jixxy.engine.api.Trigger
import app.jixxy.engine.api.UserSettings
import app.jixxy.engine.selector.LinePicker
import app.jixxy.engine.selector.LineQuery
import app.jixxy.engine.selector.PickedLine
import java.time.LocalDateTime
import java.time.ZoneOffset

/** Picker that always answers; the line id encodes the trigger so tests can tell what was said. */
class FakePicker : LinePicker {
    val queries = mutableListOf<LineQuery>()
    val reactions = mutableListOf<Pair<String, ReactionKind>>()
    var returnNull = false
    private var n = 0

    override fun pick(query: LineQuery): PickedLine? {
        queries += query
        if (returnNull) return null
        n++
        return PickedLine("fx:${query.trigger}:$n", "line $n")
    }

    override fun onReaction(lineId: String, kind: ReactionKind) { reactions += lineId to kind }
}

/** Epoch millis for 2026-01-01 + [day] days at [h]:[m]:[sec] UTC. */
fun at(day: Int, h: Int, m: Int = 0, sec: Int = 0): Long =
    LocalDateTime.of(2026, 1, 1, h, m, sec).plusDays(day.toLong()).toInstant(ZoneOffset.UTC).toEpochMilli()

/** The trigger a reaction's fake line id encodes, or null. */
fun Reaction.trigger(): Trigger? = lineId?.split(":")?.getOrNull(1)?.let { Trigger.valueOf(it) }

/** A brain wired to a mutable settings holder and a [FakePicker]. firstSeenTs preset so WELCOME stays out of the way. */
class Rig(
    var settings: UserSettings = UserSettings(),
    state: BrainState = BrainState(firstSeenTs = 1L),
    val picker: FakePicker = FakePicker()
) {
    val brain = DefaultJixxyBrain(state, { settings }, picker, { "App[$it]" }, ZoneOffset.UTC)
    fun send(s: Signal) = brain.onSignal(s)
}
