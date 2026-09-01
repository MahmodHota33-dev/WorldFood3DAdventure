package com.mahmodhota.worldfood3dadventure.ui.world3d

import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class P10NWorldMapLayoutTest {

    @Test
    fun testWorldJourneyStatisticsIntegrity() {
        val totalCountries = LevelRegistry.allCountryIds.size
        assertEquals("Baseline countries should be 213", 213, totalCountries)
    }

    @Test
    fun testContinentFilterEntries() {
        val entries = com.mahmodhota.worldfood3dadventure.game.world.model.Continent.entries
        assertEquals("Standard 6 continents should exist", 6, entries.size)
    }

    @Test
    fun testCountryMarkerCoordinatesStability() {
        // Sample check for Germany (Baseline)
        val germany = GLOBE_COUNTRIES.find { it.id == "germany" }
        assertNotNull(germany)
        assertEquals(51.165f, germany?.latDeg ?: 0f, 0.001f)
    }
}
