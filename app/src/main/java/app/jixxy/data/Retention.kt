package app.jixxy.data

import app.jixxy.engine.api.BrainConfig

/** Oldest timestamp (epoch millis) that may still be kept; events strictly older are purged. */
fun retentionCutoff(nowMs: Long, days: Int = BrainConfig.retentionDays): Long = nowMs - days * 86_400_000L
