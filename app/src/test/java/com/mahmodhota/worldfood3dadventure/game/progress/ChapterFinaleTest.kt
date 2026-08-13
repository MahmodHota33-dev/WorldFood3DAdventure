package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.data.progress.buildLevelCompletionRewardPlan
import org.junit.Assert.*
import org.junit.Test

class ChapterFinaleTest {

    @Test
    fun rewardPlanForFirstClearGrantsBonus() {
        val plan = buildLevelCompletionRewardPlan(
            currentBestStars = 0,
            currentBestScore = 0,
            alreadyCompleted = false,
            incomingStars = 3,
            incomingScore = 5000,
            xpReward = 100,
            coinReward = 50
        )
        
        assertTrue(plan.isFirstClear)
        assertEquals(100, plan.xpDelta)
        assertEquals(50, plan.coinsDelta)
        assertEquals(3, plan.starsDelta)
    }

    @Test
    fun rewardPlanForReplayDoesNotGrantBonus() {
        val plan = buildLevelCompletionRewardPlan(
            currentBestStars = 2,
            currentBestScore = 3000,
            alreadyCompleted = true,
            incomingStars = 3,
            incomingScore = 5000,
            xpReward = 100,
            coinReward = 50
        )
        
        assertFalse(plan.isFirstClear)
        assertEquals(0, plan.xpDelta)
        assertEquals(0, plan.coinsDelta)
        assertEquals(1, plan.starsDelta) // Improved stars still counted for total
    }
}
