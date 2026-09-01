package com.mahmodhota.worldfood3dadventure.game.match3.model

/**
 * Pricing and configuration for gameplay boosters.
 * Delegates to central EconomyConfig for balance.
 */
object BoosterConfig {
    const val HAMMER_COST = EconomyConfig.BOOSTER_HAMMER_COST
    const val ROCKET_COST = EconomyConfig.BOOSTER_ROCKET_COST
    const val HAND_COST = EconomyConfig.BOOSTER_HAND_COST
    const val SHUFFLE_COST = EconomyConfig.BOOSTER_SHUFFLE_COST
    const val EXTRA_MOVES_COST = EconomyConfig.BOOSTER_EXTRA_MOVES_COST
    
    const val EXTRA_MOVES_COUNT = 5

    fun costFor(type: BoosterType): Int = when (type) {
        BoosterType.HAMMER -> HAMMER_COST
        BoosterType.ROCKET -> ROCKET_COST
        BoosterType.HAND -> HAND_COST
        BoosterType.SHUFFLE -> SHUFFLE_COST
        BoosterType.EXTRA_MOVES -> EXTRA_MOVES_COST
    }
}
