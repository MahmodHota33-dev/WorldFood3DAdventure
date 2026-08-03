package com.mahmodhota.worldfood3dadventure.telemetry

import android.util.Log
import com.mahmodhota.worldfood3dadventure.BuildConfig

enum class Match3TelemetryEvent {
    MOVE_START,
    MOVE_VALID,
    MOVE_INVALID,
    MATCH_FOUND,
    MATCH_COUNT,
    SPECIAL_CREATED,
    SPECIAL_ACTIVATED,
    BOOSTER_USED,
    HAMMER_USED,
    SHUFFLE_USED,
    EXTRA_MOVES_USED,
    CASCADE_START,
    CASCADE_END,
    REFILL_START,
    REFILL_END,
    BOARD_STABLE,
    LEVEL_WIN,
    LEVEL_LOSE,
    REWARD_GRANTED,
    XP_GRANTED,
    COINS_GRANTED,
    PROGRESS_SAVED,
    WORLD_UNLOCK,
    PASSPORT_UPDATED,
    PROFILE_UPDATED
}

object Match3Telemetry {
    private const val TAG = "Match3Telemetry"

    fun levelId(countryId: String, levelNumber: Int): String {
        return "$countryId-$levelNumber"
    }

    fun log(
        event: Match3TelemetryEvent,
        levelId: String,
        countryId: String,
        remainingMoves: Int,
        score: Int,
        comboCount: Int,
        cascadeCount: Int,
        detail: String? = null
    ) {
        if (!BuildConfig.DEBUG) return

        val message = StringBuilder(128)
            .append(event.name)
            .append(" ts=").append(System.currentTimeMillis())
            .append(" level=").append(levelId)
            .append(" country=").append(countryId)
            .append(" moves=").append(remainingMoves)
            .append(" score=").append(score)
            .append(" combo=").append(comboCount)
            .append(" cascade=").append(cascadeCount)

        if (!detail.isNullOrEmpty()) {
            message.append(' ').append(detail)
        }

        Log.d(TAG, message.toString())
    }
}
