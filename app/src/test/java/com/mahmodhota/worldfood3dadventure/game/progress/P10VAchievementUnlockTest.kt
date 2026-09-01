package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.data.progress.model.PersistedGameState
import com.mahmodhota.worldfood3dadventure.data.progress.model.PlayerProgress
import com.mahmodhota.worldfood3dadventure.data.progress.model.CountryGameProgress
import com.mahmodhota.worldfood3dadventure.data.progress.model.LevelProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class P10VAchievementUnlockTest {

    @Test
    fun `Achievement should unlock when star requirement is met`() {
        val state = PersistedGameState(
            player = PlayerProgress(totalStars = 50),
            unlockedAchievements = emptySet()
        )
        
        val newlyUnlocked = AchievementManager.evaluate(state)
        assertTrue("Star Collector should be unlocked", newlyUnlocked.any { it.id == "stars_50" })
    }

    @Test
    fun `Achievement should not unlock if already unlocked`() {
        val state = PersistedGameState(
            player = PlayerProgress(totalStars = 50),
            unlockedAchievements = setOf("stars_50")
        )
        
        val newlyUnlocked = AchievementManager.evaluate(state)
        assertTrue("No new achievements should be unlocked", newlyUnlocked.isEmpty())
    }

    @Test
    fun `Countries Mastered achievement should unlock correctly`() {
        val countries = mapOf(
            "germany" to CountryGameProgress(countryId = "germany", isCompleted = true)
        )
        val state = PersistedGameState(
            countries = countries,
            unlockedAchievements = emptySet()
        )
        
        val newlyUnlocked = AchievementManager.evaluate(state)
        assertTrue("First Destination should be unlocked", newlyUnlocked.any { it.id == "travel_first" })
    }
}
