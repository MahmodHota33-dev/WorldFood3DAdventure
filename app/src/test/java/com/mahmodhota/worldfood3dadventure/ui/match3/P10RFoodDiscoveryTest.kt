package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class P10RFoodDiscoveryTest {

    @Test
    fun testFoodMappingConsistency() {
        val germanyFoods = LevelRegistry.getRepresentativeFoods("germany")
        assertTrue("Germany should have foods", germanyFoods.isNotEmpty())
        
        val italyFoods = LevelRegistry.getRepresentativeFoods("italy")
        assertTrue("Italy should have foods", italyFoods.isNotEmpty())
    }

    @Test
    fun test213CountriesFoodData() {
        val allIds = LevelRegistry.allCountryIds
        assertEquals(213, allIds.size)
        
        allIds.forEach { id ->
            val foods = LevelRegistry.getRepresentativeFoods(id)
            assertTrue("Country $id should have food data", foods.isNotEmpty())
        }
    }
}
