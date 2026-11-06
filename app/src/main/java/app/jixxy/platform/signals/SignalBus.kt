package app.jixxy.platform.signals

import app.jixxy.engine.api.Signal
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** In-process fan-in for every [Signal]. Never blocks a detector: on overflow the oldest signal is dropped. */
class SignalBus {
    private val flow = MutableSharedFlow<Signal>(extraBufferCapacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /** Collected by the dispatcher (T-PLATFORM-5). */
    val signals: SharedFlow<Signal> = flow.asSharedFlow()

    fun emit(signal: Signal) {
        flow.tryEmit(signal)
    }
}
