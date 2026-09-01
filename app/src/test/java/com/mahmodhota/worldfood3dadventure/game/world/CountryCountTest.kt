package com.mahmodhota.worldfood3dadventure.game.world

import org.junit.Assert.assertEquals
import org.junit.Test

class CountryCountTest {

    @Test
    fun testTotalCountryCount() {
        // Built-in (7) + EXTRA_COUNTRIES (206) = 213
        val totalCountries = LevelRegistry.allCountryIds.size
        assertEquals("Total country count should be 213", 213, totalCountries)
    }

    @Test
    fun testNoDuplicateIds() {
        val allIds = LevelRegistry.allCountryIds
        val uniqueIds = allIds.distinct()
        assertEquals("There should be no duplicate country IDs", allIds.size, uniqueIds.size)
    }

    @Test
    fun testExtraCountriesRegistryCount() {
        // 213 total - 7 built-in = 206
        assertEquals("EXTRA_COUNTRIES should have 206 entries", 206, GlobalContentRegistry.EXTRA_COUNTRIES.size)
    }
}
