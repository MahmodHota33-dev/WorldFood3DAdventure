package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.game.match3.model.EconomyConfig
import org.junit.Assert.assertTrue
import org.junit.Test

class P10YEconomyIntegrityTest {

    @Test
    fun `Economy rewards must be balanced and positive`() {
        assertTrue(EconomyConfig.LEVEL_VICTORY_XP_PER_STAR > 0)
        assertTrue(EconomyConfig.COUNTRY_COMPLETION_XP_BASE > 0)
        assertTrue(EconomyConfig.FOOD_DISCOVERY_XP > 0)
        assertTrue(EconomyConfig.LEVEL_VICTORY_COINS_PER_STAR > 0)
        assertTrue(EconomyConfig.COUNTRY_COMPLETION_COINS_BASE > 0)
    }

    @Test
    fun `Booster costs must be higher than single level rewards to maintain value`() {
        // Hammer costs 150. Level 1 win gives 30 coins. 
        // This is balanced (5 wins for 1 hammer)
        assertTrue(EconomyConfig.BOOSTER_HAMMER_COST > EconomyConfig.LEVEL_VICTORY_COINS_PER_STAR * 3)
        assertTrue(EconomyConfig.BOOSTER_EXTRA_MOVES_COST >= EconomyConfig.BOOSTER_HAMMER_COST)
    }
}
