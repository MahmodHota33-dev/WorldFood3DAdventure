package com.mahmodhota.worldfood3dadventure.ui.world3d

import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.Continent
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class P10EWorldDiscoveryTest {

    @Test
    fun testAllCountriesHaveContinentAssigned() {
        LevelRegistry.allCountryIds.forEach { id ->
            val country = LevelRegistry.getCountry(id)
            assertNotNull("Country $id metadata should exist", country?.metadata)
            assertNotNull("Country $id should have a continent", country?.metadata?.continent)
        }
    }

    @Test
    fun testContinentGrouping() {
        val germany = LevelRegistry.getCountry("germany")?.metadata
        assertEquals(Continent.EUROPE, germany?.continent)

        val japan = LevelRegistry.getCountry("japan")?.metadata
        assertEquals(Continent.ASIA, japan?.continent)

        val mexico = LevelRegistry.getCountry("mexico")?.metadata
        assertEquals(Continent.NORTH_AMERICA, mexico?.continent)
    }

    @Test
    fun testMarkerStateResolution() {
        // Germany is initially unlocked
        val germanyUnlocked = CountryProgressionChain.isInitiallyUnlocked("germany")
        assertTrue(germanyUnlocked)
        
        // Locked state check (e.g. Korea which is in EXTRA_COUNTRIES)
        val koreaUnlocked = CountryProgressionChain.isInitiallyUnlocked("korea")
        assertEquals(false, koreaUnlocked)
    }

    @Test
    fun testTotalLevelConsistency() {
        LevelRegistry.allCountryIds.forEach { id ->
            assertEquals(15, CountryProgressionChain.getTotalLevels(id))
        }
    }
}
