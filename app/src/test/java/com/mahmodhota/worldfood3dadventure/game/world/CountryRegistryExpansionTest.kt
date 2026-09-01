package com.mahmodhota.worldfood3dadventure.game.world

import com.mahmodhota.worldfood3dadventure.ui.world3d.GLOBE_COUNTRIES
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CountryRegistryExpansionTest {

    @Test
    fun testTotalCountryCountIs213() {
        // Authoritative registry count
        val registryCount = LevelRegistry.allCountryIds.size
        assertEquals("LevelRegistry should contain 213 countries", 213, registryCount)
        
        // Globe markers count
        val globeCount = GLOBE_COUNTRIES.size
        assertEquals("Globe markers should contain 213 countries", 213, globeCount)
    }

    @Test
    fun testNoDuplicateCountryIds() {
        val ids = LevelRegistry.allCountryIds
        val uniqueIds = ids.toSet()
        assertEquals("There should be no duplicate country IDs", ids.size, uniqueIds.size)
    }

    @Test
    fun testGlobeMarkersMatchRegistry() {
        val registryIds = LevelRegistry.allCountryIds.toSet()
        val globeIds = GLOBE_COUNTRIES.map { it.id }.toSet()
        
        // Verify all registry countries have a marker
        registryIds.forEach { id ->
            assertTrue("Country $id should have a globe marker", globeIds.contains(id))
        }
        
        // Verify all markers are in the registry
        globeIds.forEach { id ->
            assertTrue("Marker $id should be in the LevelRegistry", registryIds.contains(id))
        }
    }
    
    @Test
    fun testAllCountriesHaveValidCoordinates() {
        GLOBE_COUNTRIES.forEach { country ->
            assertTrue("Latitude for ${country.id} must be in range -90..90", country.latDeg in -90f..90f)
            assertTrue("Longitude for ${country.id} must be in range -180..180", country.lonDeg in -180f..180f)
        }
    }
}
