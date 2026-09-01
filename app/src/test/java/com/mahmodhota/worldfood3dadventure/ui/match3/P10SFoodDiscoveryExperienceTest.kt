package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class P10SFoodDiscoveryExperienceTest {

    @Test
    fun testGlobalFoodDiscoveryCount() {
        val allIds = LevelRegistry.allCountryIds
        val totalFoods = allIds.sumOf { LevelRegistry.getRepresentativeFoods(it).size }
        // 213 countries * roughly 6-8 foods each
        assertTrue("Total foods should be significant", totalFoods > 600)
    }

    @Test
    fun testCountryGrouping() {
        val germanyFoods = LevelRegistry.getRepresentativeFoods("germany")
        assertTrue(germanyFoods.any { it.name == "PRETZEL" })
        
        val italyFoods = LevelRegistry.getRepresentativeFoods("italy")
        assertTrue(italyFoods.any { it.name == "PIZZA" })
    }

    @Test
    fun testContinentFiltering() {
        val europeCountries = LevelRegistry.allCountries.filter { it.continent.displayName == "Europe" }
        assertTrue(europeCountries.any { it.levelId == "germany" })
        assertTrue(europeCountries.any { it.levelId == "france" })
    }
}
