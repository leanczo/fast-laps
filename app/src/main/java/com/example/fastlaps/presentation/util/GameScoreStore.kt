package com.example.fastlaps.presentation.util

import android.content.Context

object GameScoreKeys {
    const val REACTION_TIME = "best_reaction_time"
    const val PIT_STOP_TIME = "best_pit_stop_time"
    const val TRIVIA_SCORE = "best_trivia_score"
    const val CIRCUIT_GUESS_SCORE = "best_circuit_guess_score"
}

object GameScoreStore {
    private const val PREFS_NAME = "settings"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getBestLong(context: Context, key: String): Long =
        prefs(context).getLong(key, 0L)

    fun getBestInt(context: Context, key: String): Int =
        prefs(context).getInt(key, 0)

    fun saveBestLong(context: Context, key: String, value: Long) {
        prefs(context).edit().putLong(key, value).apply()
    }

    fun saveBestInt(context: Context, key: String, value: Int) {
        prefs(context).edit().putInt(key, value).apply()
    }

    fun clear(context: Context, key: String) {
        prefs(context).edit().remove(key).apply()
    }
}
