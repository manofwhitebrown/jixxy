package app.jixxy.engine.rules

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.BrainConfig
import app.jixxy.engine.api.BrainSnapshot
import app.jixxy.engine.api.BrainState
import app.jixxy.engine.api.CharacterState
import app.jixxy.engine.api.JixxyBrain
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.NagLevel
import app.jixxy.engine.api.PauseChoiceKind
import app.jixxy.engine.api.PauseRuntime
import app.jixxy.engine.api.PauseSpec
import app.jixxy.engine.api.Reaction
import app.jixxy.engine.api.ReactionKind
import app.jixxy.engine.api.ReactionKindOut
import app.jixxy.engine.api.Signal
import app.jixxy.engine.api.Trigger
import app.jixxy.engine.api.UsageEvent
import app.jixxy.engine.api.UsageEventType
import app.jixxy.engine.api.UserSettings
import app.jixxy.engine.memory.EventHistory
import app.jixxy.engine.selector.LinePicker
import app.jixxy.engine.selector.LineQuery
import app.jixxy.engine.selector.PickedLine
import java.time.ZoneId

/**
 * Jixxy's brain: state model (energy, mood, bond), trigger rules, caps and Pause logic.
 *
 * Deterministic: time comes only from [Signal.ts], settings from [settings], lines from [picker].
 * Memory: pass recent events (`StateStore.eventsBetween`) as [restoredEvents] so Pause grace, counts, streaks and the
 * no-repeat ring survive a process restart; callbacks and the almost-caring rule read the same history.
 * Not thread-safe; call [onSignal] from one thread.
 *
 * Platform contract:
 *  - After each [onSignal], persist [currentState] and any [drainEvents] via `StateStore`.
 *  - The platform owns `UserSettings.napUntilTs`: set it when handling a nap, clear it on wake. The brain also
 *    tracks its own nap from [Signal.NapRequested] and treats [Signal.WakeRequested] as authoritative: after a
 *    wake it ignores the persisted nap value it saw at that moment, so a stale `napUntilTs` can never keep him silent.
 *  - A [Reaction] of kind PAUSE_PROMPT may have `text == null` if no line was available; show the card with a neutral resource string.
 *
 * Speech rules:
 *  - Unprompted lines (WELCOME, APP_OPEN, SESSION_LONG, LATE_NIGHT, CHARGER_ON, BATTERY_LOW, MUSIC) need: screen on,
 *    no call, no nap, outside quiet hours, no PROTECTED or muted foreground app, energy > 0, under the daily cap,
 *    past the minimum gap and past the per-trigger cooldown.
 *  - Pause prompts and their follow-up lines are user-opted-in: exempt from quiet hours, energy and the cap,
 *    but still silent for nap, call, muted/PROTECTED apps and when pauses are disabled.
 *  - Blind mode makes the brain ignore every signal: no reactions, no logging.
 */
class DefaultJixxyBrain(
    initialState: BrainState,
    private val settings: () -> UserSettings,
    private val picker: LinePicker,
    private val appLabel: (String) -> String,
    private val zone: ZoneId,
    restoredEvents: List<UsageEvent> = emptyList()
) : JixxyBrain {

    private var state = initialState
    private val history = EventHistory(zone, restoredEvents)
    private val pending = mutableListOf<UsageEvent>()
    private var lastTs = 0L
    private var lastRegenTs: Long? = null
    private var dayEpoch: Long? = null
    private var firstOpenPending = true

    private val dailyMinutes = mutableMapOf<String, Int>()
    private val pauseCount = mutableMapOf<String, Int>()
    private val graceUntil = mutableMapOf<String, Long>()
    private val lastLeaveAt = mutableMapOf<String, Long>()
    private val stayExpiry = mutableMapOf<String, Long>()
    private val secondPromptDone = mutableSetOf<String>()
    private val lastTriggerTs = mutableMapOf<Trigger, Long>()
    private var lastRepeatOpenTs: Long? = null
    private var leaveStreak = 0
    private var lastDroppedLineTs: Long? = null

    private var fgPkg: String? = null
    private var fgCategory = AppCategory.OTHER
    private var lastSessionMinutes = 0
    private var sessionLongFired = 0

    private var inCall = false
    private var screenOn = true
    private var charging = false
    private var batteryLow = false
    private var lateNightKey: Long? = null

    private var tempMood: Mood? = null
    private var tempMoodUntil = 0L
    private var napStartedAt: Long? = null
    private var napPlannedUntil: Long? = null
    /** A persisted nap the user explicitly woke Jixxy from; ignored until a different nap value appears. */
    private var ignoredNapUntil: Long? = null

    init {
        restoreRuntime()
    }

    override val snapshot: BrainSnapshot
        get() {
            val s = settings()
            return BrainSnapshot(
                mood = currentMood(lastTs, s),
                energy = state.energy,
                bond = BrainConfig.bondFor(state.bondXp),
                bondXp = state.bondXp,
                napping = isNapping(lastTs, s)
            )
        }

    /** The state to persist after a signal, including the Pause runtime state ([BrainState.pause]). */
    fun currentState(): BrainState = state.copy(pause = pauseRuntime())

    /** Captures Pause runtime state, dropping entries that can no longer matter so the saved text stays small. */
    private fun pauseRuntime(): PauseRuntime {
        val staleBefore = lastTs - STAY_KEEP_MS
        val stay = stayExpiry.filterValues { it > staleBefore }
        return PauseRuntime(
            savedTs = lastTs,
            graceUntil = graceUntil.filterValues { it > lastTs },
            stayExpiry = stay,
            secondPromptDone = secondPromptDone.filterTo(mutableSetOf()) { it in stay },
            promptsToday = pauseCount.toMap(),
            promptsDayEpoch = dayEpoch,
            leaveStreak = leaveStreak
        )
    }

    /** Returns and clears the usage events recorded since the last call (for the platform to persist). */
    fun drainEvents(): List<UsageEvent> {
        val out = pending.toList()
        pending.clear()
        return out
    }

    override fun onSignal(signal: Signal): List<Reaction> {
        val s = settings()
        if (s.blindMode) return emptyList()
        val now = signal.ts
        lastTs = now
        val firstEver = state.firstSeenTs == null
        if (firstEver) state = state.copy(firstSeenTs = now)
        tick(now)
        var out = handle(signal, s, now)
        if (firstEver && out.isEmpty()) {
            out = listOfNotNull(tryUnprompted(Trigger.WELCOME, now, s))
        }
        state = state.copy(mood = currentMood(now, s))
        return out
    }

    // ---- signal handling -------------------------------------------------------------------

    private fun handle(signal: Signal, s: UserSettings, now: Long): List<Reaction> = when (signal) {
        is Signal.ForegroundApp -> onForeground(signal, s, now)
        is Signal.SessionTick -> onSessionTick(signal, s, now)
        is Signal.PauseChoice -> onPauseChoice(signal, s, now)
        is Signal.ScreenState -> {
            screenOn = signal.on
            if (!signal.on) resetSession()
            emptyList()
        }
        is Signal.Charger -> onCharger(signal, s, now)
        is Signal.BatteryLow -> {
            if (charging) {
                emptyList()
            } else {
                batteryLow = true
                listOfNotNull(tryUnprompted(Trigger.BATTERY_LOW, now, s, mapOf("battery" to signal.pct.toString())))
            }
        }
        is Signal.MusicActive ->
            if (signal.active) listOfNotNull(tryUnprompted(Trigger.MUSIC, now, s)) else emptyList()
        is Signal.CallState -> {
            inCall = signal.inCall
            emptyList()
        }
        is Signal.SpriteTapped -> onTap(signal, s, now)
        is Signal.SpriteDropped -> onDropped(s, now)
        is Signal.UserReaction -> {
            picker.onReaction(signal.lineId, signal.kind)
            log(UsageEventType.LINE_REACTION, now, null, if (signal.kind == ReactionKind.LAUGH) 1 else 0, signal.lineId)
            if (signal.kind == ReactionKind.LAUGH) addXp(BrainConfig.bondXpLaugh)
            emptyList()
        }
        is Signal.NapRequested -> {
            ignoredNapUntil = null
            napStartedAt = now
            napPlannedUntil = now + signal.minutes * 60_000L
            log(UsageEventType.NAP, now, null, signal.minutes)
            listOf(prompted(Trigger.NAP, now, s, emptyMap(), Mood.SLEEPY))
        }
        is Signal.WakeRequested -> {
            endNap(now)
            ignoredNapUntil = s.napUntilTs
            tempMood = null
            listOf(prompted(Trigger.WAKE, now, s, emptyMap(), Mood.CHILL))
        }
        is Signal.Heartbeat -> emptyList()
    }

    private fun onForeground(sig: Signal.ForegroundApp, s: UserSettings, now: Long): List<Reaction> {
        val prevPkg = fgPkg
        val prevMinutes = lastSessionMinutes
        if (sig.pkg == prevPkg) {
            fgCategory = sig.category
            return emptyList()
        }
        fgPkg = sig.pkg
        fgCategory = sig.category
        lastSessionMinutes = 0
        sessionLongFired = 0
        if (sig.category == AppCategory.PROTECTED) return emptyList()

        log(UsageEventType.FOREGROUND, now, sig.pkg)
        val lateNight = isLateNight(now)
        if (lateNight) log(UsageEventType.LATE_NIGHT_OPEN, now, sig.pkg)
        val firstOpen = firstOpenPending
        firstOpenPending = false
        if (firstOpen) addXp(BrainConfig.bondXpDaily)

        if (pauseAllowed(sig.pkg, s, now)) {
            return if (inGrace(sig.pkg, now)) emptyList() else listOf(pauseReaction(sig.pkg, now, s, followUp = false))
        }

        val slots = mapOf("app" to appLabel(sig.pkg))
        val nightKey = TimeUtil.nightKey(now, zone)
        val reaction = when {
            lateNight && nightKey != lateNightKey ->
                (tryAlmostCaring(now, s) ?: tryUnprompted(Trigger.LATE_NIGHT, now, s, slots))?.also { lateNightKey = nightKey }
            firstOpen -> tryUnprompted(Trigger.APP_OPEN, now, s, slots)
            prevPkg != null && prevMinutes >= BrainConfig.longSessionMin ->
                tryUnprompted(Trigger.APP_OPEN, now, s, slots)
            else -> tryCallback(sig.pkg, now, s) ?: repeatOpenLine(sig.pkg, now, s, slots)
        }
        return listOfNotNull(reaction)
    }

    private fun onSessionTick(sig: Signal.SessionTick, s: UserSettings, now: Long): List<Reaction> {
        val pkg = sig.pkg
        if (pkg != fgPkg || fgCategory == AppCategory.PROTECTED) return emptyList()
        dailyMinutes[pkg] = (dailyMinutes[pkg] ?: 0) + 1
        lastSessionMinutes = sig.minutes
        log(UsageEventType.SESSION_MINUTES, now, pkg, 1)

        val expiry = stayExpiry[pkg]
        if (expiry != null && now >= expiry && pkg !in secondPromptDone && pauseAllowed(pkg, s, now)) {
            secondPromptDone += pkg
            return listOf(pauseReaction(pkg, now, s, followUp = true))
        }

        val due = sessionLongFired == 0 || sig.minutes - sessionLongFired >= BrainConfig.sessionLongRepeatMin
        if (sig.minutes >= BrainConfig.sessionLongMin && due && !inGrace(pkg, now)) {
            val slots = mapOf("app" to appLabel(pkg), "minutes" to sig.minutes.toString())
            val r = tryUnprompted(Trigger.SESSION_LONG, now, s, slots)
            if (r != null) {
                sessionLongFired = sig.minutes
                return listOf(r)
            }
        }
        return emptyList()
    }

    private fun onPauseChoice(sig: Signal.PauseChoice, s: UserSettings, now: Long): List<Reaction> {
        val pkg = sig.pkg
        val slots = mapOf(
            "app" to appLabel(pkg),
            "count" to (pauseCount[pkg] ?: 0).toString(),
            "minutes" to (dailyMinutes[pkg] ?: 0).toString()
        )
        return when (sig.choice) {
            PauseChoiceKind.STAY -> {
                val until = now + BrainConfig.stayGraceMinutes * 60_000L
                graceUntil[pkg] = until
                stayExpiry[pkg] = until
                secondPromptDone.remove(pkg)
                leaveStreak = 0
                log(UsageEventType.PAUSE_STAY, now, pkg)
                setTempMood(Mood.SMUG, now)
                listOf(prompted(Trigger.PAUSE_STAY, now, s, slots, null))
            }
            PauseChoiceKind.LEAVE -> {
                graceUntil[pkg] = now + BrainConfig.leaveGraceMinutes * 60_000L
                lastLeaveAt[pkg] = now
                stayExpiry.remove(pkg)
                log(UsageEventType.PAUSE_LEAVE, now, pkg)
                addXp(BrainConfig.bondXpLeave)
                leaveStreak++
                if (leaveStreak >= BrainConfig.impressedLeaveStreak) {
                    leaveStreak = 0
                    if (!impressedAllowed(now)) return listOf(prompted(Trigger.PAUSE_LEAVE, now, s, slots, null))
                    addXp(BrainConfig.bondXpImpressed)
                    setTempMood(Mood.IMPRESSED, now)
                    val earned = slots + ("count" to BrainConfig.impressedLeaveStreak.toString())
                    listOf(prompted(Trigger.IMPRESSED, now, s, earned, null))
                } else {
                    listOf(prompted(Trigger.PAUSE_LEAVE, now, s, slots, null))
                }
            }
        }
    }

    private fun onCharger(sig: Signal.Charger, s: UserSettings, now: Long): List<Reaction> {
        charging = sig.connected
        if (!sig.connected) return emptyList()
        batteryLow = false
        state = state.copy(energy = (state.energy + BrainConfig.chargerEnergyBonus).coerceAtMost(BrainConfig.maxEnergy))
        log(UsageEventType.CHARGER_ON, now, null, sig.batteryPct)
        return listOfNotNull(tryUnprompted(Trigger.CHARGER_ON, now, s, mapOf("battery" to sig.batteryPct.toString())))
    }

    private fun onTap(sig: Signal.SpriteTapped, s: UserSettings, now: Long): List<Reaction> {
        if (hardSilenced(now, s)) return emptyList()
        if (sig.consecutive >= BrainConfig.pokeGrumpyThreshold) setTempMood(Mood.GRUMPY, now)
        val onBreak = state.energy <= 0
        val base = query(Trigger.POKE, now, s, emptyMap(), null)
        val line = picker.pick(base.copy(onBreak = onBreak, escalation = sig.consecutive >= BrainConfig.pokeGrumpyThreshold))
        if (line == null) {
            val mood = currentMood(now, s)
            return listOf(Reaction(ReactionKindOut.SILENT_JUDGE, null, null, mood, CharacterState.JUDGING, 0L, null))
        }
        if (!onBreak) state = state.copy(energy = (state.energy - BrainConfig.energyPerLine).coerceAtLeast(0))
        log(UsageEventType.LINE_SHOWN, now, null, null, line.id)
        val reaction = say(Trigger.POKE, line, now, s, null)
        // Out of energy ("Snacks. Now. Then maybe I'll talk."): show the hungry face instead of the poke squash.
        return listOf(if (onBreak) reaction.copy(state = CharacterState.SNACK) else reaction)
    }

    /** Dropped after a drag: always the FALLING animation; a `dropped` POKE line at most once per cooldown. */
    private fun onDropped(s: UserSettings, now: Long): List<Reaction> {
        if (hardSilenced(now, s)) return emptyList()
        val mood = currentMood(now, s)
        val last = lastDroppedLineTs
        val line = if (last != null && now - last < BrainConfig.droppedLineCooldownMin * 60_000L) {
            null
        } else {
            picker.pick(query(Trigger.POKE, now, s, emptyMap(), null).copy(requiredTag = "dropped"))
        }
        if (line == null) {
            return listOf(Reaction(ReactionKindOut.ANIMATE_ONLY, null, null, mood, CharacterState.FALLING, 0L, null))
        }
        lastDroppedLineTs = now
        log(UsageEventType.LINE_SHOWN, now, null, null, line.id)
        return listOf(Reaction(ReactionKindOut.SAY, line.id, line.text, mood, CharacterState.FALLING, BrainConfig.bubbleMs(line.text), null))
    }

    // ---- memory: callbacks, almost-caring, restore -------------------------------------------

    /** A callback built from a real counted fact (visits, pauses, leaves, stays, late opens, top app minutes); the first fact over its threshold that has a line wins. */
    private fun tryCallback(pkg: String, now: Long, s: UserSettings): Reaction? {
        val app = appLabel(pkg)
        val facts = listOf(
            Triple("fact:app_visits_day", history.appVisitsToday(pkg, now), BrainConfig.callbackVisitsDay),
            Triple("fact:pauses_day", history.pausesToday(now), BrainConfig.callbackPausesDay),
            Triple("fact:leaves_week", history.leavesWeek(now), BrainConfig.callbackLeavesWeek),
            Triple("fact:app_visits_week", history.appVisitsWeek(pkg, now), BrainConfig.callbackVisitsWeek),
            Triple("fact:stays_week", history.staysWeek(now), BrainConfig.callbackStaysWeek),
            Triple("fact:late_opens_week", history.lateOpensWeek(now), BrainConfig.callbackLateOpensWeek)
        )
        for ((tag, count, min) in facts) {
            if (count < min) continue
            // During late night the LATE_NIGHT line owns the moment; quoting late opens then would just repeat it.
            if (tag == "fact:late_opens_week" && isLateNight(now)) continue
            val slots = mapOf("app" to app, "count" to count.toString())
            tryUnprompted(Trigger.CALLBACK, now, s, slots, tag)?.let { return it }
        }
        val top = history.topAppMinutesToday(now)
        if (top != null && top.second >= BrainConfig.callbackTopAppMinutesDay) {
            val slots = mapOf("app" to appLabel(top.first), "minutes" to top.second.toString())
            tryUnprompted(Trigger.CALLBACK, now, s, slots, "fact:top_app_minutes_day")?.let { return it }
        }
        return null
    }

    /** Praise stays rare: at most one IMPRESSED line per [BrainConfig.impressedGapDays], however many Leaves. */
    private fun impressedAllowed(now: Long): Boolean {
        val last = history.lastLineShownTs(IMPRESSED_PREFIX)
        return last == null || now - last >= BrainConfig.impressedGapDays * DAY_MS
    }

    /** The rare soft line: only after several late nights in a week, and at most once per [BrainConfig.almostCaringGapDays]. */
    private fun tryAlmostCaring(now: Long, s: UserSettings): Reaction? {
        if (history.lateNightsWeek(now) < BrainConfig.almostCaringLateNights) return null
        val last = history.lastLineShownTs(ALMOST_CARING_PREFIX)
        if (last != null && now - last < BrainConfig.almostCaringGapDays * DAY_MS) return null
        return tryUnprompted(Trigger.ALMOST_CARING, now, s)
    }

    /**
     * Restores runtime state after a restart: first the saved [BrainState.pause] (survives even when events were
     * cleared), then the usage events newer than it. A save without Pause state (`savedTs == 0`) is rebuilt from the
     * events alone, exactly as before D-017.
     */
    private fun restoreRuntime() {
        val saved = state.pause
        if (saved.savedTs > 0L) seedPause(saved)
        val last = history.lastTs() ?: return
        lastTs = maxOf(lastTs, last)
        val seededDay = dayEpoch
        val today = maxOf(TimeUtil.epochDay(last, zone), seededDay ?: Long.MIN_VALUE)
        if (seededDay != null && seededDay < today) {
            dailyMinutes.clear()
            pauseCount.clear()
        }
        dayEpoch = today
        val savedTs = saved.savedTs
        picker.restoreRecent(history.all().filter { it.type == UsageEventType.LINE_SHOWN }.mapNotNull { it.lineId })
        for (e in history.all()) {
            val pkg = e.pkg
            val isToday = TimeUtil.epochDay(e.ts, zone) == today
            if (e.ts <= savedTs && e.type in SAVED_CHOICE_EVENTS) continue
            when (e.type) {
                UsageEventType.FOREGROUND -> if (isToday) firstOpenPending = false
                UsageEventType.SESSION_MINUTES ->
                    if (isToday && pkg != null) dailyMinutes[pkg] = (dailyMinutes[pkg] ?: 0) + (e.value ?: 1)
                UsageEventType.PAUSE_SHOWN -> if (pkg != null) {
                    if (isToday) pauseCount[pkg] = e.value ?: ((pauseCount[pkg] ?: 0) + 1)
                    val expiry = stayExpiry[pkg]
                    if (e.ts > savedTs && expiry != null && e.ts >= expiry) secondPromptDone += pkg
                }
                UsageEventType.PAUSE_STAY -> if (pkg != null) {
                    val until = e.ts + BrainConfig.stayGraceMinutes * 60_000L
                    graceUntil[pkg] = until
                    stayExpiry[pkg] = until
                    secondPromptDone.remove(pkg)
                    leaveStreak = 0
                }
                UsageEventType.PAUSE_LEAVE -> if (pkg != null) {
                    graceUntil[pkg] = e.ts + BrainConfig.leaveGraceMinutes * 60_000L
                    lastLeaveAt[pkg] = e.ts
                    stayExpiry.remove(pkg)
                    leaveStreak++
                    if (leaveStreak >= BrainConfig.impressedLeaveStreak) leaveStreak = 0
                }
                else -> Unit
            }
        }
    }

    private fun seedPause(p: PauseRuntime) {
        lastTs = p.savedTs
        graceUntil.putAll(p.graceUntil)
        stayExpiry.putAll(p.stayExpiry)
        secondPromptDone.addAll(p.secondPromptDone)
        pauseCount.putAll(p.promptsToday)
        dayEpoch = p.promptsDayEpoch
        leaveStreak = p.leaveStreak
    }

    // ---- speaking --------------------------------------------------------------------------

    /** Speaks an unprompted line if every gate allows it; counts toward the daily cap. */
    /**
     * Naggy "again?" (D-020): the same app opened for the third time today at Naggy or Chaos. Uses only APP_OPEN lines
     * tagged `escalation`, with its own cooldown, so it stays silent until such lines exist and never at Calm.
     */
    private fun repeatOpenLine(pkg: String, now: Long, s: UserSettings, slots: Map<String, String>): Reaction? {
        if (s.nagLevel == NagLevel.CALM) return null
        if (history.appVisitsToday(pkg, now) < BrainConfig.repeatOpenVisits) return null
        return tryUnprompted(Trigger.APP_OPEN, now, s, slots, requiredTag = "escalation", repeatOpen = true)
    }

    private fun tryUnprompted(
        trigger: Trigger,
        now: Long,
        s: UserSettings,
        slots: Map<String, String> = emptyMap(),
        requiredTag: String? = null,
        repeatOpen: Boolean = false
    ): Reaction? {
        if (hardSilenced(now, s) || !screenOn || inQuiet(now, s) || state.energy <= 0) return null
        if (fgCategory == AppCategory.PROTECTED) return null
        val fg = fgPkg
        if (fg != null && fg in s.mutedApps) return null
        val day = TimeUtil.epochDay(now, zone)
        val used = if (state.unpromptedDayEpoch == day) state.unpromptedToday else 0
        if (used >= BrainConfig.maxUnpromptedPerDay(s.nagLevel)) return null
        val lastSpoken = state.lastUnpromptedTs
        if (lastSpoken != null && now - lastSpoken < BrainConfig.minGapMinutes(s.nagLevel) * 60_000L) return null
        val lastOfKind = if (repeatOpen) lastRepeatOpenTs else lastTriggerTs[trigger]
        val cooldownMin = if (repeatOpen) BrainConfig.repeatOpenCooldownMin(s.nagLevel) else BrainConfig.unpromptedCooldownMin(trigger)
        if (lastOfKind != null && now - lastOfKind < cooldownMin * 60_000L) return null
        val line = picker.pick(query(trigger, now, s, slots, null).copy(requiredTag = requiredTag, escalation = repeatOpen)) ?: return null
        if (repeatOpen) lastRepeatOpenTs = now else lastTriggerTs[trigger] = now
        state = state.copy(
            energy = (state.energy - BrainConfig.energyPerLine).coerceAtLeast(0),
            lastUnpromptedTs = now,
            unpromptedDayEpoch = day,
            unpromptedToday = used + 1
        )
        log(UsageEventType.LINE_SHOWN, now, fgPkg, null, line.id)
        return say(trigger, line, now, s, null)
    }

    /** A reply to something the user just did (Pause choice, nap, wake). No gates, no energy cost, never counted. */
    private fun prompted(trigger: Trigger, now: Long, s: UserSettings, slots: Map<String, String>, mood: Mood?): Reaction {
        val line = pick(trigger, now, s, slots, mood)
        if (line == null) {
            val m = mood ?: currentMood(now, s)
            return Reaction(ReactionKindOut.ANIMATE_ONLY, null, null, m, MoodMachine.characterFor(trigger, m), 0L, null)
        }
        log(UsageEventType.LINE_SHOWN, now, null, null, line.id)
        return say(trigger, line, now, s, mood)
    }

    private fun pauseReaction(pkg: String, now: Long, s: UserSettings, followUp: Boolean): Reaction {
        val count = (pauseCount[pkg] ?: 0) + 1
        pauseCount[pkg] = count
        if (!followUp) {
            stayExpiry.remove(pkg)
            secondPromptDone.remove(pkg)
        }
        val label = appLabel(pkg)
        val minutes = dailyMinutes[pkg] ?: 0
        val spec = PauseSpec(pkg, label, minutes, s.stayDelaySec, BrainConfig.stayGraceMinutes)
        val slots = mapOf("app" to label, "minutes" to minutes.toString(), "count" to count.toString())
        val sinceLeave = lastLeaveAt[pkg]?.let { now - it }
        val afterLeave = !followUp && sinceLeave != null && sinceLeave >= 0L && sinceLeave <= BrainConfig.afterLeaveWindowMin * 60_000L
        val query = query(Trigger.PAUSE_PROMPT, now, s, slots, null).copy(tier = count - 1, followUp = followUp, afterLeave = afterLeave)
        val line = picker.pick(query)
        log(UsageEventType.PAUSE_SHOWN, now, pkg, count)
        if (line != null) log(UsageEventType.LINE_SHOWN, now, pkg, null, line.id)
        val mood = currentMood(now, s)
        return Reaction(
            ReactionKindOut.PAUSE_PROMPT, line?.id, line?.text, mood,
            MoodMachine.characterFor(Trigger.PAUSE_PROMPT, mood), BrainConfig.bubbleMs(line?.text), spec
        )
    }

    private fun pick(trigger: Trigger, now: Long, s: UserSettings, slots: Map<String, String>, mood: Mood?): PickedLine? =
        picker.pick(query(trigger, now, s, slots, mood))

    private fun query(trigger: Trigger, now: Long, s: UserSettings, slots: Map<String, String>, mood: Mood?): LineQuery =
        LineQuery(
            trigger = trigger,
            category = fgPkg?.let { fgCategory },
            mood = mood ?: currentMood(now, s),
            roast = s.roast,
            bond = BrainConfig.bondFor(state.bondXp),
            language = s.language,
            slots = slots + ("time" to TimeUtil.clock(now, zone)),
            lateNight = isLateNight(now),
            calm = s.nagLevel == NagLevel.CALM
        )

    private fun say(trigger: Trigger, line: PickedLine, now: Long, s: UserSettings, mood: Mood?): Reaction {
        val m = mood ?: currentMood(now, s)
        return Reaction(
            ReactionKindOut.SAY, line.id, line.text, m,
            MoodMachine.characterFor(trigger, m), BrainConfig.bubbleMs(line.text), null
        )
    }

    // ---- gates and state helpers -----------------------------------------------------------

    /** Napping = an own nap still planned, or a persisted nap in the future that the user has not woken him from. */
    private fun isNapping(now: Long, s: UserSettings): Boolean {
        val own = napPlannedUntil?.let { now < it } ?: false
        val persisted = s.napUntilTs?.let { now < it && it != ignoredNapUntil } ?: false
        return own || persisted
    }
    private fun hardSilenced(now: Long, s: UserSettings): Boolean = inCall || isNapping(now, s)
    private fun inQuiet(now: Long, s: UserSettings): Boolean =
        TimeUtil.inWindow(TimeUtil.minuteOfDay(now, zone), s.quietStartMin, s.quietEndMin)
    private fun isLateNight(now: Long): Boolean =
        TimeUtil.inWindow(TimeUtil.minuteOfDay(now, zone), BrainConfig.lateNightStartMin, BrainConfig.lateNightEndMin)
    private fun inGrace(pkg: String, now: Long): Boolean = graceUntil[pkg]?.let { now < it } ?: false

    private fun pauseTargets(s: UserSettings): List<String> = s.targetApps.sorted()

    private fun pauseAllowed(pkg: String, s: UserSettings, now: Long): Boolean =
        s.pausesEnabled && screenOn && !hardSilenced(now, s) && pkg !in s.mutedApps &&
            fgCategory != AppCategory.PROTECTED && pkg in pauseTargets(s)

    private fun currentMood(now: Long, s: UserSettings): Mood = MoodMachine.resolve(
        temp = tempMood?.takeIf { now < tempMoodUntil },
        batteryLow = batteryLow && !charging,
        lateNight = isLateNight(now),
        quiet = inQuiet(now, s),
        energy = state.energy
    )

    private fun setTempMood(mood: Mood, now: Long) {
        tempMood = mood
        tempMoodUntil = now + BrainConfig.moodHoldMin * 60_000L
    }

    private fun addXp(n: Int) {
        state = state.copy(bondXp = state.bondXp + n)
    }

    private fun resetSession() {
        fgPkg = null
        lastSessionMinutes = 0
        sessionLongFired = 0
    }

    private fun tick(now: Long) {
        val day = TimeUtil.epochDay(now, zone)
        if (dayEpoch != day) {
            dayEpoch = day
            dailyMinutes.clear()
            pauseCount.clear()
            firstOpenPending = true
        }
        val last = lastRegenTs
        if (last == null) {
            lastRegenTs = now
        } else {
            val step = BrainConfig.regenMinutesPerPoint * 60_000L
            val points = ((now - last) / step).toInt()
            if (points > 0) {
                state = state.copy(energy = (state.energy + points).coerceAtMost(BrainConfig.maxEnergy))
                lastRegenTs = last + points * step
            }
        }
        if (tempMood != null && now >= tempMoodUntil) tempMood = null
        val planned = napPlannedUntil
        if (planned != null && now >= planned) endNap(now)
    }

    private fun endNap(now: Long) {
        val started = napStartedAt ?: return
        val minutes = ((now - started) / 60_000L).toInt().coerceAtLeast(0)
        val restored = minOf(BrainConfig.napEnergyBonus, minutes / 2)
        state = state.copy(energy = (state.energy + restored).coerceAtMost(BrainConfig.maxEnergy))
        napStartedAt = null
        napPlannedUntil = null
    }

    private fun log(type: UsageEventType, now: Long, pkg: String?, value: Int? = null, lineId: String? = null) {
        val e = UsageEvent(now, type, pkg, value, lineId)
        pending += e
        history.add(e)
    }

    private companion object {
        const val ALMOST_CARING_PREFIX = "almost_caring"
        const val IMPRESSED_PREFIX = "impressed"
        const val DAY_MS = 86_400_000L
        /** Stay grants older than this can no longer trigger the second prompt, so they are not saved. */
        const val STAY_KEEP_MS = DAY_MS
        /** Choice events at or before the saved time are already in [PauseRuntime]; replaying them would only repeat that. */
        val SAVED_CHOICE_EVENTS = setOf(UsageEventType.PAUSE_STAY, UsageEventType.PAUSE_LEAVE)
    }
}
