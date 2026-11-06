package app.jixxy.ui.weekly

import android.content.Context
import app.jixxy.engine.api.StateStore
import app.jixxy.engine.api.UserSettings
import app.jixxy.engine.lines.LineBankLoader
import app.jixxy.engine.review.WeeklyReview
import app.jixxy.engine.review.WeeklyReviewBuilder
import app.jixxy.engine.selector.BankLinePicker
import java.time.ZoneId
import kotlin.random.Random

/** Builds this week's review from the on-device event log and the bundled line bank. Call off the main thread. */
object WeeklyLoader {
    suspend fun load(
        context: Context,
        store: StateStore,
        settings: UserSettings,
        appLabel: (String) -> String,
        now: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault(),
    ): WeeklyReview {
        val (from, to) = WeeklyCardLogic.range(now, zone)
        val events = store.eventsBetween(from, to)
        val bank = LineBankLoader.fromTexts(lineTexts(context, settings.language))
        val builder = WeeklyReviewBuilder(BankLinePicker(bank, Random.Default), zone, appLabel)
        return builder.build(events, from, to, store.load().bondXp, settings.roast, settings.language)
    }

    private fun lineTexts(context: Context, language: String): List<String> {
        fun texts(lang: String): List<String> =
            (context.assets.list("lines/$lang") ?: emptyArray()).filter { it.endsWith(".json") }.sorted().map { name ->
                context.assets.open("lines/$lang/$name").bufferedReader().use { it.readText() }
            }
        return texts(language).ifEmpty { texts("en") }
    }
}
