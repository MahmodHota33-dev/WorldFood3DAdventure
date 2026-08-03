package com.mahmodhota.worldfood3dadventure.game.match3.model

import com.mahmodhota.worldfood3dadventure.game.match3.Match3SpecialConfig

data class BoosterInventory(
    val hammer: Int = Match3SpecialConfig.DefaultHammerCount,
    val shuffle: Int = Match3SpecialConfig.DefaultShuffleCount,
    val extraMoves: Int = Match3SpecialConfig.DefaultExtraMovesCount
) {
    fun countFor(type: BoosterType): Int {
        return when (type) {
            BoosterType.HAMMER -> hammer
            BoosterType.SHUFFLE -> shuffle
            BoosterType.EXTRA_MOVES -> extraMoves
        }
    }

    fun withCount(type: BoosterType, count: Int): BoosterInventory {
        val safeCount = count.coerceAtLeast(0)
        return when (type) {
            BoosterType.HAMMER -> copy(hammer = safeCount)
            BoosterType.SHUFFLE -> copy(shuffle = safeCount)
            BoosterType.EXTRA_MOVES -> copy(extraMoves = safeCount)
        }
    }
}
