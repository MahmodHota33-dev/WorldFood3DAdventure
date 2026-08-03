package com.mahmodhota.worldfood3dadventure.game.world

import org.junit.Assert.*
import org.junit.Test

class RegistryTest {

    @Test
    fun testCountryRegistration() {
        val germany = LevelRegistry.getCountry("germany")
        assertNotNull(germany)
        assertEquals("Germany", germany?.metadata?.displayName)
        assertTrue(germany!!.levels.isNotEmpty())
    }

    @Test
    fun testAllCountriesPresence() {
        val countries = LevelRegistry.allCountries
        assertTrue(countries.any { it.levelId == "germany" })
        assertTrue(countries.any { it.levelId == "italy" })
        assertTrue(countries.any { it.levelId == "france" })
        assertTrue(countries.any { it.levelId == "japan" })
        assertTrue(countries.any { it.levelId == "mexico" })
        assertTrue(countries.any { it.levelId == "sudan" })
    }

    @Test
    fun testLevelExpansionIntegrity() {
        val germany = LevelRegistry.getCountry("germany")
        // Step 2 added 5 levels, total should be 15
        assertEquals(15, germany?.levels?.size)
        
        val level15 = germany?.levels?.find { it.levelNumber == 15 }
        assertNotNull(level15)
        val scoreTarget = level15
            ?.goals
            ?.filterIsInstance<com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal.ScoreTarget>()
            ?.firstOrNull()
            ?.target
        assertNotNull(scoreTarget)
        assertTrue(scoreTarget!! >= 15000)
    }

    @Test
    fun testMissingCountry() {
        val result = LevelRegistry.getCountry("mars")
        assertNull(result)
    }
}
