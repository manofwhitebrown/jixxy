package app.jixxy.engine.api

/** Jixxy's current emotional colouring. Drives line selection and sprite state. */
enum class Mood { CHILL, SLEEPY, GRUMPY, SMUG, PANICKING, IMPRESSED, SOFT }

/** How sharp the user allows Jixxy to be. A line is eligible only if its roast <= the user's level. */
enum class RoastLevel(val n: Int) { GENTLE(1), NORMAL(2), SAVAGE(3) }

/** How often Jixxy speaks up on his own (D-020). Pauses are unaffected. Default is [NAGGY]. */
enum class NagLevel { CALM, NAGGY, CHAOS }

/** Relationship depth. Grows with time together and positive interactions; never decreases from absence. */
enum class Bond { STRANGER, ROOMMATE, FRIEND, RIDE_OR_DIE }

/** Coarse app classification. [PROTECTED] (banking, payments, passwords, dialer, maps, health): Jixxy NEVER reacts or pauses. */
enum class AppCategory { SOCIAL, VIDEO, GAME, MESSAGING, PRODUCTIVITY, BROWSER, SHOPPING, OTHER, PROTECTED }

/** Animation states the character module can render. */
enum class CharacterState {
    IDLE, BLINK, YAWN, SLEEP, SNORE, WAKE_GRUMPY, SMUG, PANIC, HAPPY, IMPRESSED,
    POKED, DRAGGED, FALLING, SNACK, STRETCH, MUSIC, JUDGING, SOFT,
    /** Added by D-010: peeks over an edge before a Pause line, charger connected, battery low. */
    PEEK, CHARGING, LOW_BATTERY
}

/** Why Jixxy might speak. Each line in the line bank belongs to exactly one trigger. */
enum class Trigger {
    WELCOME, APP_OPEN, SESSION_LONG, LATE_NIGHT, CHARGER_ON, BATTERY_LOW, NAP, WAKE, POKE, MUSIC,
    PAUSE_PROMPT, PAUSE_STAY, PAUSE_LEAVE, CALLBACK, ALMOST_CARING, IMPRESSED, WEEKLY
}

/** User feedback on a spoken line (emoji bar). */
enum class ReactionKind { LAUGH, SLEEPY }

/** The user's choice on a Pause card. */
enum class PauseChoiceKind { STAY, LEAVE }

/** What the platform should do with a [Reaction]. */
enum class ReactionKindOut { SAY, PAUSE_PROMPT, ANIMATE_ONLY, SILENT_JUDGE }
