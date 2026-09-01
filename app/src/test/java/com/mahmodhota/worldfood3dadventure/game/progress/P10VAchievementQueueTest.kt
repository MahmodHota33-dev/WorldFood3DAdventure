package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.data.progress.model.PersistedGameState
import com.mahmodhota.worldfood3dadventure.data.progress.model.PlayerProgress
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class P10VAchievementQueueTest {

    @Before
    fun setup() {
        AchievementManager.resetForTesting()
    }

    @Test
    fun `Multiple unblocks should be queued and consumable one by one`() {
        // Star master (100) should also trigger Star Collector (50) if both are unlocked at once
        val state = PersistedGameState(
            player = PlayerProgress(totalStars = 100),
            unlockedAchievements = emptySet()
        )
        
        AchievementManager.evaluate(state)
        
        val queue = AchievementManager.newlyUnlocked.value
        assertTrue("At least 2 achievements should be queued", queue.size >= 2)
        
        val firstId = queue[0].id
        AchievementManager.consumeAchievement()
        
        assertEquals(queue.size - 1, AchievementManager.newlyUnlocked.value.size)
        assertTrue("Next achievement should be different", AchievementManager.newlyUnlocked.value.first().id != firstId)
    }
}

private fun assertTrue(message: String, condition: Boolean) {
    if (!condition) throw AssertionError(message)
}
