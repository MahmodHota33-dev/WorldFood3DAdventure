package com.mahmodhota.worldfood3dadventure.ui.world3d

import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class P10IWorldMapPolishTest {

    @Test
    fun testCountryRegistryIntegrity() {
        val allCountries = LevelRegistry.allCountryIds
        assertEquals("Should have 213 countries", 213, allCountries.size)
        
        allCountries.forEach { id ->
            val country = LevelRegistry.getCountry(id)
            assertNotNull("Country $id should exist", country)
            assertEquals("Country $id should have 15 levels", 15, country!!.levels.size)
        }
    }

    @Test
    fun testGlobeMarkersExist() {
        assertEquals("Globe country data should have 213 entries", 213, GLOBE_COUNTRIES.size)
    }

    @Test
    fun testInitialUnlockState() {
        val germanyUnlocked = CountryProgressionChain.isInitiallyUnlocked("germany")
        assertEquals(true, germanyUnlocked)
    }
}
