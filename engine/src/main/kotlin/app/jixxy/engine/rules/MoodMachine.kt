package app.jixxy.engine.rules

import app.jixxy.engine.api.BrainConfig
import app.jixxy.engine.api.CharacterState
import app.jixxy.engine.api.Mood
import app.jixxy.engine.api.Trigger

/** Pure mood and animation mapping. */
object MoodMachine {
    /**
     * Resolves the mood. Priority: earned IMPRESSED, PANICKING (low battery), SOFT (late night, so he is
     * never a roaster then), a temporary GRUMPY/SMUG, SLEEPY (quiet hours or low energy), else CHILL.
     */
    fun resolve(temp: Mood?, batteryLow: Boolean, lateNight: Boolean, quiet: Boolean, energy: Int): Mood = when {
        temp == Mood.IMPRESSED -> Mood.IMPRESSED
        batteryLow -> Mood.PANICKING
        lateNight -> Mood.SOFT
        temp != null -> temp
        quiet || energy < BrainConfig.lowEnergy -> Mood.SLEEPY
        else -> Mood.CHILL
    }

    /** Sprite animation for a reaction to [trigger] while in [mood]. */
    fun characterFor(trigger: Trigger, mood: Mood): CharacterState = when (trigger) {
        Trigger.NAP -> CharacterState.SLEEP
        Trigger.WAKE -> CharacterState.WAKE_GRUMPY
        Trigger.POKE -> CharacterState.POKED
        Trigger.CHARGER_ON -> CharacterState.CHARGING
        Trigger.BATTERY_LOW -> CharacterState.LOW_BATTERY
        Trigger.MUSIC -> CharacterState.MUSIC
        Trigger.IMPRESSED -> CharacterState.IMPRESSED
        Trigger.PAUSE_LEAVE -> CharacterState.HAPPY
        Trigger.PAUSE_PROMPT -> CharacterState.PEEK
        Trigger.PAUSE_STAY -> CharacterState.JUDGING
        Trigger.WELCOME -> CharacterState.STRETCH
        Trigger.ALMOST_CARING -> CharacterState.SOFT
        else -> when (mood) {
            Mood.SLEEPY -> CharacterState.YAWN
            Mood.SMUG -> CharacterState.SMUG
            Mood.PANICKING -> CharacterState.PANIC
            Mood.IMPRESSED -> CharacterState.IMPRESSED
            Mood.SOFT -> CharacterState.SOFT
            Mood.GRUMPY, Mood.CHILL -> CharacterState.IDLE
        }
    }
}
