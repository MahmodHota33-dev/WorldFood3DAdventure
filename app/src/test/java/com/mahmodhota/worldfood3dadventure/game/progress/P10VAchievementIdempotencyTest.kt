package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.data.progress.model.PersistedGameState
import com.mahmodhota.worldfood3dadventure.data.progress.model.PlayerProgress
import org.junit.Assert.assertEquals
import org.junit.Test

class P10VAchievementIdempotencyTest {

    @Test
    fun `Multiple evaluations should not duplicate rewards`() {
        val state1 = PersistedGameState(
            player = PlayerProgress(totalStars = 50),
            unlockedAchievements = emptySet()
        )
        
        val newlyUnlocked1 = AchievementManager.evaluate(state1)
        assertEquals(1, newlyUnlocked1.size)
        assertEquals("stars_50", newlyUnlocked1.first().id)
        
        // After first evaluation, the internal queue has 1.
        // If we evaluate again with the same IDs as "already unlocked" (simulated), it should be 0.
        val state2 = state1.copy(unlockedAchievements = setOf("stars_50"))
        val newlyUnlocked2 = AchievementManager.evaluate(state2)
        assertEquals(0, newlyUnlocked2.size)
    }
}
