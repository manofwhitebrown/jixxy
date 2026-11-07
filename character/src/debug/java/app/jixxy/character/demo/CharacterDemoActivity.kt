package app.jixxy.character.demo

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import app.jixxy.character.JixxyView
import app.jixxy.character.JixxyViewListener
import app.jixxy.character.PauseCardView
import app.jixxy.engine.api.CharacterState
import app.jixxy.engine.api.PauseSpec
import app.jixxy.engine.api.ReactionKind

/**
 * Debug-only demo (T-C2-6): shows the cat on a plain stage with one button per [CharacterState], a test speech
 * bubble, a test Pause card and a drag area. It needs no permission and talks to nothing, so the Owner can judge
 * the character straight after installing. Lives in `character/src/debug`, so release builds never contain it.
 *
 * The sample lines are copied from `content/gold_lines.md`; they are demo text, not the line bank.
 */
class CharacterDemoActivity : Activity() {

    private lateinit var cat: JixxyView
    private lateinit var stage: FrameLayout
    private lateinit var log: TextView
    private var pauseCard: PauseCardView? = null
    private var lineIndex = 0
    private var lightStage = false
    private val stageLocation = IntArray(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BACKGROUND)
        }

        log = TextView(this).apply {
            setTextColor(CREAM)
            textSize = 14f
            setPadding(dp(16), dp(16), dp(16), dp(8))
            text = "Drag him around. Tap him. Press the buttons."
        }
        root.addView(log, LinearLayout.LayoutParams(MATCH, WRAP))

        stage = FrameLayout(this).apply { setBackgroundColor(STAGE) }
        cat = JixxyView(this).apply { listener = demoListener() }
        stage.addView(cat, FrameLayout.LayoutParams(dp(120), dp(120), Gravity.CENTER))
        root.addView(stage, LinearLayout.LayoutParams(MATCH, dp(STAGE_HEIGHT_DP)))

        val buttons = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), dp(8), dp(8), dp(24))
        }
        addRow(buttons, listOf(
            "Test bubble" to { showBubble() },
            "Long bubble" to { showBubble(LONG_LINE) },
            "Reset" to { cat.setCharacterState(CharacterState.IDLE) },
        ))
        addRow(buttons, listOf(
            "Pause card (3 s wait)" to { showPauseCard(stayDelaySec = 3) },
            "Pause card (no wait)" to { showPauseCard(stayDelaySec = 0) },
            "Light / dark stage" to { toggleStage() },
        ))
        CharacterState.values().toList().chunked(COLUMNS).forEach { states ->
            addRow(buttons, states.map { state -> state.name to { cat.setCharacterState(state); log.text = "State: ${state.name}" } })
        }
        root.addView(ScrollView(this).apply { addView(buttons) }, LinearLayout.LayoutParams(MATCH, 0, 1f))

        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        cat.setScreenActive(true)
    }

    override fun onPause() {
        cat.setScreenActive(false)
        super.onPause()
    }

    private fun showBubble(text: String = SAMPLE_LINES[lineIndex % SAMPLE_LINES.size]) {
        cat.showBubble(text, BUBBLE_MS, "demo_$lineIndex")
        lineIndex++
    }

    /** Swaps the stage between dark and light so halos or fringes around the sprite show up on both. */
    private fun toggleStage() {
        lightStage = !lightStage
        stage.setBackgroundColor(if (lightStage) STAGE_LIGHT else STAGE)
        log.text = if (lightStage) "Light stage" else "Dark stage"
    }

    private fun showPauseCard(stayDelaySec: Int) {
        pauseCard?.let { stage.removeView(it) }
        val card = PauseCardView(this)
        card.bind(PauseSpec(pkg = "demo.app", appLabel = "Demo app", minutesToday = 42, stayEnabledAfterSec = stayDelaySec), PAUSE_LINE) { choice ->
            log.text = "Pause card: $choice"
            stage.removeView(card)
            if (pauseCard === card) pauseCard = null
        }
        pauseCard = card
        stage.addView(card, FrameLayout.LayoutParams(dp(300), WRAP, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL))
    }

    private fun demoListener() = object : JixxyViewListener {
        override fun onTap(count: Int) { log.text = "Tap x$count" }
        override fun onLongPress() { log.text = "Long press" }
        override fun onDragStart() { log.text = "Dragging" }
        override fun onDragMove(rawX: Float, rawY: Float) = moveCat(rawX, rawY)
        override fun onDragEnd(rawX: Float, rawY: Float) {
            moveCat(rawX, rawY)
            log.text = "Dropped"
        }
        override fun onReaction(lineId: String?, kind: ReactionKind) { log.text = "Reaction $kind on $lineId" }
    }

    /** Keeps the sprite centred under the finger, inside the stage. */
    private fun moveCat(rawX: Float, rawY: Float) {
        stage.getLocationOnScreen(stageLocation)
        val x = (rawX - stageLocation[0] - cat.width / 2f).coerceIn(0f, (stage.width - cat.width).toFloat())
        val y = (rawY - stageLocation[1] - cat.height / 2f).coerceIn(0f, (stage.height - cat.height).toFloat())
        cat.translationX = x - (stage.width - cat.width) / 2f
        cat.translationY = y - (stage.height - cat.height) / 2f
    }

    private fun addRow(parent: LinearLayout, items: List<Pair<String, () -> Unit>>) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        items.forEach { (label, action) ->
            row.addView(
                Button(this).apply {
                    text = label
                    textSize = 12f
                    setOnClickListener { action() }
                },
                LinearLayout.LayoutParams(0, WRAP, 1f),
            )
        }
        parent.addView(row, LinearLayout.LayoutParams(MATCH, WRAP))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private companion object {
        const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
        const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT
        const val COLUMNS = 3
        const val STAGE_HEIGHT_DP = 300
        const val BUBBLE_MS = 4_000L
        val BACKGROUND = Color.parseColor("#16141A")
        val STAGE = Color.parseColor("#2A2631")
        val STAGE_LIGHT = Color.parseColor("#F6EFE0")
        val CREAM = Color.parseColor("#F6EFE0")
        val SAMPLE_LINES = listOf(
            "Respect. That was smooth.",
            "Charger on. I'm sitting in the sun. Metaphorically.",
            "Leave now, or I start rating the thumbnails.",
        )
        const val LONG_LINE = "Another two hours on reels? Legend. Sleep is optional, apparently, and so is your alarm."
        const val PAUSE_LINE = "Pause: will this still sound good tomorrow?"
    }
}
