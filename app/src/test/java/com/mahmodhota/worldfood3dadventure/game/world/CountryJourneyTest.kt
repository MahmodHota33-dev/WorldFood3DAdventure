package com.mahmodhota.worldfood3dadventure.game.world

import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CountryJourneyTest {

    @Test
    fun testLevelCountAcrossAllCountries() {
        val allIds = LevelRegistry.allCountryIds
        assertEquals("Should have 213 countries total", 213, allIds.size)

        allIds.forEach { id ->
            val country = LevelRegistry.getCountry(id)
            assertNotNull("Country $id should exist in registry", country)
            assertEquals("Country $id should have 15 levels", 15, country!!.levels.size)
        }
    }

    @Test
    fun testCountryProgressionChainE2E() {
        val germany = CountryProgressionChain.getSpec("germany")
        assertNotNull(germany)
        assertTrue(germany!!.unlockedInitially)

        val italy = CountryProgressionChain.getNextCountrySpec("germany")
        assertNotNull(italy)
        assertEquals("italy", italy!!.countryId)
        
        val lastCountryId = LevelRegistry.allCountryIds.last()
        val noNext = CountryProgressionChain.getNextCountrySpec(lastCountryId)
        assertEquals(null, noNext)
    }

    @Test
    fun testFoodThemesExistForAllCountries() {
        LevelRegistry.allCountryIds.forEach { id ->
            val foods = LevelRegistry.getRepresentativeFoods(id)
            assertTrue("Country $id should have at least one representative food", foods.isNotEmpty())
        }
    }
}
