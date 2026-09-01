package com.mahmodhota.worldfood3dadventure.game.world

import org.junit.Assert.assertEquals
import org.junit.Test

class TotalLevelIntegrityTest {

    @Test
    fun testTotalLevelCountAcrossAllCountries() {
        val allCountryIds = LevelRegistry.allCountryIds
        assertEquals("Should have exactly 213 countries", 213, allCountryIds.size)
        
        var totalLevels = 0
        allCountryIds.forEach { id ->
            val country = LevelRegistry.getCountry(id)
            val levelCount = country?.levels?.size ?: 0
            assertEquals("Country $id should have exactly 15 levels", 15, levelCount)
            totalLevels += levelCount
        }
        
        assertEquals("Total level count should be 3,195", 3195, totalLevels)
    }
}
