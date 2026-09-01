package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.data.progress.model.*
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterInventory
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProgressionQueryTest {

    private lateinit var state: PersistedGameState

    @Before
    fun setUp() {
        // Create a test state with Germany and Italy countries
        val germanCountry = CountryGameProgress(
            countryId = "germany",
            isUnlocked = true,
            isCompleted = false,
            levels = mapOf(
                1 to LevelProgress(1, "germany", isUnlocked = true, isCompleted = true, bestStars = 3, bestScore = 5000),
                2 to LevelProgress(2, "germany", isUnlocked = true, isCompleted = true, bestStars = 2, bestScore = 4000),
                3 to LevelProgress(3, "germany", isUnlocked = true, isCompleted = false, bestStars = 0, bestScore = 0),
                4 to LevelProgress(4, "germany", isUnlocked = false, isCompleted = false, bestStars = 0, bestScore = 0)
            )
        )

        val italyCountry = CountryGameProgress(
            countryId = "italy",
            isUnlocked = false,
            isCompleted = false,
            levels = mapOf()
        )

        val player = PlayerProgress(
            lives = 5,
            coins = 250,
            xp = 1500,
            level = 2,
            totalStars = 5,
            username = "TestPlayer",
            boosterInventory = BoosterInventory()
        )

        state = PersistedGameState(
            player = player,
            countries = mapOf(
                "germany" to germanCountry,
                "italy" to italyCountry
            )
        )
    }

    @Test
    fun testTotalCountries() {
        assertEquals("Should have all countries", LevelRegistry.allCountryIds.size, ProgressionQuery.totalCountries())
    }

    @Test
    fun testUnlockedCountries() {
        assertEquals("Should have 1 unlocked country", 1, ProgressionQuery.unlockedCountries(state))
    }

    @Test
    fun testCompletedCountries() {
        assertEquals("Should have 0 completed countries", 0, ProgressionQuery.completedCountries(state))
    }

    @Test
    fun testTotalStarsEarned() {
        assertEquals("Should have 5 total stars", 5, ProgressionQuery.totalStarsEarned(state))
    }

    @Test
    fun testTotalCoins() {
        assertEquals("Should have 250 coins", 250, ProgressionQuery.totalCoins(state))
    }

    @Test
    fun testTotalXp() {
        assertEquals("Should have 1500 XP", 1500, ProgressionQuery.totalXp(state))
    }

    @Test
    fun testCompletedLevelsForCountry() {
        assertEquals("Germany should have 2 completed levels", 2, ProgressionQuery.completedLevelsForCountry(state, "germany"))
    }

    @Test
    fun testTotalStarsForCountry() {
        assertEquals("Germany should have 5 total stars", 5, ProgressionQuery.totalStarsForCountry(state, "germany"))
    }

    @Test
    fun testCompletionPercentageForCountry() {
        val percentage = ProgressionQuery.completionPercentageForCountry(state, "germany")
        assertEquals("Germany should be 13% complete (2/15)", (2 * 100) / 15, percentage)
    }

    @Test
    fun testStarsNeededToUnlockItaly() {
        val starsNeeded = ProgressionQuery.starsNeededToUnlockCountry("italy")
        assertEquals("Italy should require 30 stars", 30, starsNeeded)
    }

    @Test
    fun testStarsRemainingToUnlockItaly() {
        val remaining = ProgressionQuery.starsRemainingToUnlock("italy", 5)
        assertEquals("Should need 25 more stars to unlock Italy", 25, remaining)
    }

    @Test
    fun testCanUnlockGermany() {
        assertTrue("Germany should be unlockable with 0 stars", ProgressionQuery.canUnlockCountry("germany", 0))
    }

    @Test
    fun testCannotUnlockItalyWithInsufficientStars() {
        assertFalse("Italy should not be unlockable with 5 stars", ProgressionQuery.canUnlockCountry("italy", 5))
    }

    @Test
    fun testCanUnlockItalyWithSufficientStars() {
        assertTrue("Italy should be unlockable with 30 stars", ProgressionQuery.canUnlockCountry("italy", 30))
    }

    @Test
    fun testTotalLevelsAttempted() {
        val attempted = ProgressionQuery.totalLevelsAttempted(state)
        assertEquals("Should have 3 attempted levels (unlocked or completed)", 3, attempted)
    }

    @Test
    fun testTotalLevelsCompleted() {
        val completed = ProgressionQuery.totalLevelsCompleted(state)
        assertEquals("Should have 2 completed levels", 2, completed)
    }

    @Test
    fun testValidProgressionState() {
        assertTrue("State should be valid", ProgressionQuery.hasValidProgressionState(state))
    }

    @Test
    fun testInvalidProgressionState() {
        // Create invalid state: level 3 completed but level 1 not unlocked
        val invalidCountry = CountryGameProgress(
            countryId = "germany",
            isUnlocked = true,
            isCompleted = false,
            levels = mapOf(
                1 to LevelProgress(1, "germany", isUnlocked = false, isCompleted = false),
                3 to LevelProgress(3, "germany", isUnlocked = false, isCompleted = true)
            )
        )
        val invalidState = state.copy(countries = mapOf("germany" to invalidCountry))
        assertFalse("State should be invalid", ProgressionQuery.hasValidProgressionState(invalidState))
    }
}
