package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.MainDispatcherRule
import com.mahmodhota.worldfood3dadventure.game.match3.model.EconomyConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class P10YDailyRewardIntegrityTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        ProgressionManager.resetForTesting()
    }

    @Test
    fun `Daily missions must yield exactly 3 missions per day`() {
        val missions = DailyJourneyManager.generateMissions("2026-08-24")
        assertEquals(3, missions.size)
    }

    @Test
    fun `Daily mission rewards must use EconomyConfig values`() {
        val missions = DailyJourneyManager.generateMissions("2026-08-24")
        missions.forEach { mission ->
            // Check that XP/Coins are within reasonable balanced ranges defined in EconomyConfig
            assertTrue("XP reward too high", mission.rewardXp <= EconomyConfig.DAILY_MISSION_XP_DEFAULT + 20)
            assertTrue("Coin reward too high", mission.rewardCoins <= EconomyConfig.DAILY_MISSION_COINS_DEFAULT + 10)
        }
    }
}
