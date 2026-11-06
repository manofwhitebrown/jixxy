package app.jixxy.character

import app.jixxy.engine.api.ReactionKind

/**
 * Callbacks from [JixxyView]. All methods have empty defaults, so implement only what you need.
 * Called on the main thread.
 */
interface JixxyViewListener {
    /** A tap on the sprite. [count] is the number of consecutive taps, each within 1.5 s of the previous one. */
    fun onTap(count: Int) {}

    /** The sprite was pressed and held without moving. No tap is reported for that gesture. */
    fun onLongPress() {}

    /** The finger moved past touch slop; the host should start moving its window. */
    fun onDragStart() {}

    /** Finger position in raw screen pixels while dragging, so the host can move its window. */
    fun onDragMove(rawX: Float, rawY: Float) {}

    /** The finger was released after a drag, at the given raw screen position. */
    fun onDragEnd(rawX: Float, rawY: Float) {}

    /** The user rated a spoken line from the emoji bar. */
    fun onReaction(lineId: String?, kind: ReactionKind) {}
}
