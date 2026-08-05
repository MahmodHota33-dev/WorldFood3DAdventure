package com.mahmodhota.worldfood3dadventure.game.world

import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import org.junit.Assert.*
import org.junit.Test

class CountryProgressionChainTest {

    @Test
    fun testGermanyInitiallyUnlocked() {
        assertTrue(CountryProgressionChain.isInitiallyUnlocked("germany"))
    }

    @Test
    fun testItalyNotInitiallyUnlocked() {
        assertFalse(CountryProgressionChain.isInitiallyUnlocked("italy"))
    }

    @Test
    fun testAllCountriesHaveSpecs() {
        val countryIds = listOf("germany", "italy", "france", "spain", "japan", "mexico", "sudan")
        for (id in countryIds) {
            assertNotNull("Country $id should have a spec", CountryProgressionChain.getSpec(id))
        }
    }

    @Test
    fun testUnlockProgressionOrder() {
        val germany = CountryProgressionChain.getSpec("germany")!!
        val italy = CountryProgressionChain.getSpec("italy")!!
        val france = CountryProgressionChain.getSpec("france")!!

        assertTrue("Germany should have 0 star requirement", germany.requiredStarsToUnlock == 0)
        assertTrue("Italy should require more stars than Germany", italy.requiredStarsToUnlock > germany.requiredStarsToUnlock)
        assertTrue("France should require more stars than Italy", france.requiredStarsToUnlock > italy.requiredStarsToUnlock)
    }

    @Test
    fun testGermanyItalyFranceChainOrder() {
        val ids = CountryProgressionChain.UNLOCK_ORDER.map { it.countryId }
        assertEquals(listOf("germany", "italy", "france", "spain"), ids.take(4))
    }

    @Test
    fun testSpainIsPlaceholderLocked() {
        val spec = CountryProgressionChain.getSpec("spain")!!
        assertEquals(0, spec.totalLevels)
        assertFalse(CountryProgressionChain.canUnlock("spain", spec.requiredStarsToUnlock))
    }

    @Test
    fun testCanUnlockWithZeroStars() {
        assertTrue("Germany should be unlockable with 0 stars", CountryProgressionChain.canUnlock("germany", 0))
    }

    @Test
    fun testCannotUnlockWithInsufficientStars() {
        assertFalse("Italy should not be unlockable with 0 stars", CountryProgressionChain.canUnlock("italy", 0))
    }

    @Test
    fun testCanUnlockWithSufficientStars() {
        val spec = CountryProgressionChain.getSpec("italy")!!
        assertTrue("Italy should be unlockable with sufficient stars", CountryProgressionChain.canUnlock("italy", spec.requiredStarsToUnlock))
    }

    @Test
    fun testTotalLevels() {
        assertEquals("Germany should have 15 levels", 15, CountryProgressionChain.getTotalLevels("germany"))
        assertEquals("Italy should have 15 levels", 15, CountryProgressionChain.getTotalLevels("italy"))
        assertEquals("France should have 15 levels", 15, CountryProgressionChain.getTotalLevels("france"))
        assertEquals("Spain should have 0 levels", 0, CountryProgressionChain.getTotalLevels("spain"))
        assertEquals("Japan should have 10 levels", 10, CountryProgressionChain.getTotalLevels("japan"))
        assertEquals("Mexico should have 10 levels", 10, CountryProgressionChain.getTotalLevels("mexico"))
        assertEquals("Sudan should have 10 levels", 10, CountryProgressionChain.getTotalLevels("sudan"))
    }

    @Test
    fun testCompletionRewards() {
        val germany = CountryProgressionChain.getSpec("germany")!!
        val sudan = CountryProgressionChain.getSpec("sudan")!!

        assertEquals("Germany completion should award 150 coins", 150, germany.rewardCoinsOnCompletion)
        assertTrue("Sudan completion should award more than Germany", sudan.rewardCoinsOnCompletion > germany.rewardCoinsOnCompletion)
    }

    @Test
    fun testInvalidCountryReturnsNull() {
        assertNull("Invalid country should return null", CountryProgressionChain.getSpec("invalid"))
    }

    @Test
    fun testCanUnlockInvalidCountryReturnsFalse() {
        assertFalse("Cannot unlock invalid country", CountryProgressionChain.canUnlock("invalid", 1000))
    }
}
