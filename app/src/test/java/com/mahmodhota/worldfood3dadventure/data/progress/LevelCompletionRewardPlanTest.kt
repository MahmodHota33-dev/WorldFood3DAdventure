package com.mahmodhota.worldfood3dadventure.data.progress

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LevelCompletionRewardPlanTest {

    @Test
    fun firstClearRewardsGrantedOnce() {
        val first = buildLevelCompletionRewardPlan(
            currentBestStars = 0,
            currentBestScore = 0,
            alreadyCompleted = false,
            incomingStars = 2,
            incomingScore = 1200,
            xpReward = 100,
            coinReward = 40
        )
        val replay = buildLevelCompletionRewardPlan(
            currentBestStars = first.updatedBestStars,
            currentBestScore = first.updatedBestScore,
            alreadyCompleted = true,
            incomingStars = 2,
            incomingScore = 1200,
            xpReward = 100,
            coinReward = 40
        )

        assertTrue(first.isFirstClear)
        assertEquals(100, first.xpDelta)
        assertEquals(40, first.coinsDelta)
        assertFalse(replay.isFirstClear)
        assertEquals(0, replay.xpDelta)
        assertEquals(0, replay.coinsDelta)
    }

    @Test
    fun replayCanImproveBestScoreAndStarsWithoutDuplicateXpCoins() {
        val replay = buildLevelCompletionRewardPlan(
            currentBestStars = 1,
            currentBestScore = 800,
            alreadyCompleted = true,
            incomingStars = 3,
            incomingScore = 1600,
            xpReward = 150,
            coinReward = 60
        )

        assertEquals(3, replay.updatedBestStars)
        assertEquals(1600, replay.updatedBestScore)
        assertEquals(2, replay.starsDelta)
        assertEquals(0, replay.xpDelta)
        assertEquals(0, replay.coinsDelta)
    }
}
