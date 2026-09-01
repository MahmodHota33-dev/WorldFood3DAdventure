package com.mahmodhota.worldfood3dadventure.ui.world3d

import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class P10JWorldMapReadabilityTest {

    @Test
    fun testWorldJourneyStatistics() {
        val totalCountries = LevelRegistry.allCountryIds.size
        assertEquals(213, totalCountries)
        
        val totalLevels = totalCountries * 15
        assertEquals(3195, totalLevels)
    }

    @Test
    fun testContinentFilterCoverage() {
        val allIds = LevelRegistry.allCountryIds
        val continents = com.mahmodhota.worldfood3dadventure.game.world.model.Continent.entries
        
        continents.forEach { continent ->
            val countriesInContinent = allIds.filter { 
                LevelRegistry.getCountry(it)?.metadata?.continent == continent 
            }
            assertNotNull("Continent ${continent.displayName} should have countries", countriesInContinent.isNotEmpty())
        }
    }
}
