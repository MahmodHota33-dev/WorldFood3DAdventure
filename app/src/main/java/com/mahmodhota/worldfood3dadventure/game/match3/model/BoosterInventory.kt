package com.mahmodhota.worldfood3dadventure.game.match3.model

import com.mahmodhota.worldfood3dadventure.game.match3.Match3SpecialConfig

data class BoosterInventory(
    val hammer: Int = 3,
    val rocket: Int = 2,
    val hand: Int = 1,
    val extraMoves: Int = 3,
    val shuffle: Int = 2
) {
    fun countFor(type: BoosterType): Int {
        return when (type) {
            BoosterType.HAMMER -> hammer
            BoosterType.ROCKET -> rocket
            BoosterType.HAND -> hand
            BoosterType.EXTRA_MOVES -> extraMoves
            BoosterType.SHUFFLE -> shuffle
        }
    }

    fun withCount(type: BoosterType, count: Int): BoosterInventory {
        val safeCount = count.coerceAtLeast(0)
        return when (type) {
            BoosterType.HAMMER -> copy(hammer = safeCount)
            BoosterType.ROCKET -> copy(rocket = safeCount)
            BoosterType.HAND -> copy(hand = safeCount)
            BoosterType.EXTRA_MOVES -> copy(extraMoves = safeCount)
            BoosterType.SHUFFLE -> copy(shuffle = safeCount)
        }
    }
}
