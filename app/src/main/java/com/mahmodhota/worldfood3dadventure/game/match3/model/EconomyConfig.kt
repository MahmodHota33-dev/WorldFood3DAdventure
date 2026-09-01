package com.mahmodhota.worldfood3dadventure.game.match3.model

/**
 * Centralized authority for game economy balance.
 */
object EconomyConfig {
    // XP Rewards
    const val LEVEL_VICTORY_XP_PER_STAR = 50
    const val COUNTRY_COMPLETION_XP_BASE = 500
    const val FOOD_DISCOVERY_XP = 100
    
    // Coin Rewards
    const val LEVEL_VICTORY_COINS_PER_STAR = 10
    const val COUNTRY_COMPLETION_COINS_BASE = 100
    
    // Daily Mission Rewards
    const val DAILY_MISSION_XP_DEFAULT = 50
    const val DAILY_MISSION_COINS_DEFAULT = 20

    // Booster Costs
    const val BOOSTER_HAMMER_COST = 150
    const val BOOSTER_ROCKET_COST = 200
    const val BOOSTER_HAND_COST = 100
    const val BOOSTER_SHUFFLE_COST = 150
    const val BOOSTER_EXTRA_MOVES_COST = 250
}
