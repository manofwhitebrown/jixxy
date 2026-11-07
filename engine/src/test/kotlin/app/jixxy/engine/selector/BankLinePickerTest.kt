package app.jixxy.engine.selector

import app.jixxy.engine.api.AppCategory
import app.jixxy.engine.api.Bond
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.ReactionKind
import app.jixxy.engine.api.RoastLevel
import app.jixxy.engine.api.Trigger
import app.jixxy.engine.lines.Line
import app.jixxy.engine.lines.LineBank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class BankLinePickerTest {
    private fun line(
        id: String, trigger: Trigger = Trigger.APP_OPEN, cats: Set<AppCategory> = setOf(AppCategory.SOCIAL),
        moods: Set<Mood> = setOf(Mood.CHILL), roast: Int = 1, bondMin: Int = 0, text: String = "Hi {app}",
        slots: Set<String> = setOf("app"), tags: Set<String> = emptySet(), lang: String = "en"
    ) = Line(id, trigger, cats, moods, roast, bondMin, lang, text, slots, tags, false)

    private fun query(
        trigger: Trigger = Trigger.APP_OPEN, cat: AppCategory? = AppCategory.SOCIAL, mood: Mood = Mood.CHILL,
        roast: RoastLevel = RoastLevel.NORMAL, bond: Bond = Bond.STRANGER, slots: Map<String, String> = mapOf("app" to "Insta"),
        lang: String = "en", onBreak: Boolean = false, followUp: Boolean = false, tier: Int = 0, requiredTag: String? = null,
        escalation: Boolean = false, lateNight: Boolean = false, afterLeave: Boolean = false
    ) = LineQuery(trigger, cat, mood, roast, bond, lang, slots, tier = tier, followUp = followUp, lateNight = lateNight,
        onBreak = onBreak, requiredTag = requiredTag, escalation = escalation, afterLeave = afterLeave)

    private fun picker(vararg l: Line, seed: Int = 1) = BankLinePicker(LineBank(l.toList()), Random(seed))

    @Test fun afterLeaveLinesAreReservedForTheReturnPrompt() {
        val plain = line("plain", Trigger.PAUSE_PROMPT)
        val back = line("back", Trigger.PAUSE_PROMPT, tags = setOf("after_leave"))
        assertEquals("plain", picker(plain, back).pick(query(Trigger.PAUSE_PROMPT))!!.id)
        assertEquals("back", picker(plain, back).pick(query(Trigger.PAUSE_PROMPT, afterLeave = true))!!.id)
        assertEquals("plain", picker(plain).pick(query(Trigger.PAUSE_PROMPT, afterLeave = true))!!.id)
    }

    @Test fun fillsSlots() {
        assertEquals("Hi Insta", picker(line("a")).pick(query())!!.text)
    }

    @Test fun respectsRoastLevel() {
        val p = picker(line("hot", roast = 3), line("mild", roast = 1))
        repeat(20) { assertEquals("mild", p.pick(query(roast = RoastLevel.GENTLE))?.id ?: "mild") }
        assertNull(picker(line("hot", roast = 3)).pick(query(roast = RoastLevel.NORMAL)))
    }

    @Test fun respectsBondAndCategory() {
        assertNull(picker(line("a", bondMin = 2)).pick(query(bond = Bond.ROOMMATE)))
        assertNotNull(picker(line("a", bondMin = 2)).pick(query(bond = Bond.FRIEND)))
        assertNull(picker(line("a")).pick(query(cat = AppCategory.GAME)))
        assertNotNull(picker(line("any", cats = emptySet())).pick(query(cat = AppCategory.GAME)))
        assertNotNull(picker(line("any", cats = emptySet())).pick(query(cat = null)))
        assertNull(picker(line("a")).pick(query(cat = null)))
    }

    @Test fun neverUsesALineWhoseSlotIsMissing() {
        assertNull(picker(line("a", slots = setOf("app", "minutes"))).pick(query()))
    }

    @Test fun prefersMatchingMoodButFallsBack() {
        val p = picker(line("chill"), line("grumpy", moods = setOf(Mood.GRUMPY)))
        repeat(15) { assertEquals("grumpy", p.pick(query(mood = Mood.GRUMPY))?.id ?: "grumpy") }
        assertEquals("chill", picker(line("chill")).pick(query(mood = Mood.SMUG))!!.id)
    }

    @Test fun fallsBackToEnglishWhenLanguageHasNoLines() {
        assertEquals("a", picker(line("a")).pick(query(lang = "hi"))!!.id)
        assertEquals("h", picker(line("a"), line("h", lang = "hi")).pick(query(lang = "hi"))!!.id)
    }

    @Test fun onBreakPrefersTaggedAndOrdinaryPokesExcludeThem() {
        val p = picker(
            line("break", Trigger.POKE, emptySet(), tags = setOf("on_break"), slots = emptySet(), text = "On break"),
            line("poke", Trigger.POKE, emptySet(), slots = emptySet(), text = "Ow")
        )
        assertEquals("break", p.pick(query(Trigger.POKE, null, slots = emptyMap(), onBreak = true))!!.id)
        repeat(10) { assertEquals("poke", p.pick(query(Trigger.POKE, null, slots = emptyMap()))?.id ?: "poke") }
    }

    @Test fun followUpPrefersTaggedLines() {
        val p = picker(line("plain"), line("later", tags = setOf("followup")))
        assertEquals("later", p.pick(query(followUp = true))!!.id)
    }

    @Test fun smallPoolDoesNotRepeatBackToBack() {
        val p = picker(*(1..10).map { line("l$it") }.toTypedArray())
        var prev: String? = null
        repeat(200) {
            val id = p.pick(query())!!.id
            assertTrue("repeat of $id", id != prev)
            prev = id
        }
    }

    @Test fun dedupeRingOver250PicksNeverRepeatsWithinTheWindow() {
        val p = picker(*(1..400).map { line("l$it", cats = emptySet(), slots = emptySet(), text = "x") }.toTypedArray(), seed = 7)
        val seen = ArrayDeque<String>()
        repeat(300) {
            val id = p.pick(query(cat = null, slots = emptyMap()))!!.id
            assertTrue("$id repeated within window", id !in seen)
            seen.addLast(id)
            if (seen.size > 200) seen.removeFirst()
        }
        assertEquals(200, p.recentIds().size)
    }

    @Test fun laughMakesALineLikelierAndSleepyLessLikely() {
        val bank = LineBank((1..10).map { line("l$it") })
        fun countOf(id: String, react: (BankLinePicker) -> Unit): Int {
            val p = BankLinePicker(bank, Random(5)).also(react)
            return (1..500).count { p.pick(query())!!.id == id }
        }
        val neutral = countOf("l1") {}
        val liked = countOf("l1") { repeat(3) { _ -> it.onReaction("l1", ReactionKind.LAUGH) } }
        val disliked = countOf("l1") { repeat(3) { _ -> it.onReaction("l1", ReactionKind.SLEEPY) } }
        assertTrue("liked $liked <= neutral $neutral", liked > neutral)
        assertTrue("disliked $disliked >= neutral $neutral", disliked < neutral)
    }

    @Test fun deterministicForASeed() {
        fun run() = picker(*(1..30).map { line("l$it") }.toTypedArray(), seed = 42).let { p -> (1..20).map { p.pick(query())!!.id } }
        assertEquals(run(), run())
    }

    @Test fun reservedTagsAreNeverUsedUnlessAsked() {
        val p = picker(
            line("plain", Trigger.POKE, emptySet(), slots = emptySet()),
            line("drop", Trigger.POKE, emptySet(), slots = emptySet(), tags = setOf("dropped")),
            line("esc", Trigger.POKE, emptySet(), slots = emptySet(), tags = setOf("escalation"))
        )
        repeat(30) { assertEquals("plain", p.pick(query(Trigger.POKE, null, slots = emptyMap()))?.id ?: "plain") }
        assertEquals("drop", p.pick(query(Trigger.POKE, null, slots = emptyMap(), requiredTag = "dropped"))!!.id)
        assertEquals("esc", p.pick(query(Trigger.POKE, null, slots = emptyMap(), escalation = true))!!.id)
    }

    @Test fun requiredTagWithNoMatchGivesNull() {
        assertNull(picker(line("a", Trigger.POKE, emptySet(), slots = emptySet())).pick(query(Trigger.POKE, null, slots = emptyMap(), requiredTag = "dropped")))
    }

    @Test fun pausePromptTierSelectsTaggedLines() {
        val p = picker(
            line("t0", Trigger.PAUSE_PROMPT, tags = setOf("tier0")), line("t1", Trigger.PAUSE_PROMPT, tags = setOf("tier1")),
            line("t2", Trigger.PAUSE_PROMPT, tags = setOf("tier2")), line("fu", Trigger.PAUSE_PROMPT, tags = setOf("followup"))
        )
        assertEquals("t0", p.pick(query(Trigger.PAUSE_PROMPT, tier = 0))!!.id)
        assertEquals("t1", p.pick(query(Trigger.PAUSE_PROMPT, tier = 1))!!.id)
        assertEquals("t2", p.pick(query(Trigger.PAUSE_PROMPT, tier = 7))!!.id)
        assertEquals("fu", p.pick(query(Trigger.PAUSE_PROMPT, followUp = true))!!.id)
    }

    @Test fun lateNightPrefersSoftLines() {
        val p = picker(line("loud", Trigger.LATE_NIGHT), line("soft", Trigger.LATE_NIGHT, tags = setOf("soft")))
        assertEquals("soft", p.pick(query(Trigger.LATE_NIGHT, lateNight = true))!!.id)
    }
}
