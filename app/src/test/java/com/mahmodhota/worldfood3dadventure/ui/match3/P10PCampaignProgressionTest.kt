package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.MainDispatcherRule
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class P10PCampaignProgressionTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `Level 1 should be unlocked if country is unlocked`() = runTest(mainDispatcherRule.testDispatcher) {
        ProgressionManager.resetForTesting()
        // Germany is initially unlocked
        val germanyProgress = ProgressionManager.getCountryProgress("germany")
        assertTrue("Germany should be unlocked", germanyProgress.isUnlocked)
        // No VM created here, so no onScreenExit needed for this test.
    }

    @Test
    fun `Next level should unlock after previous completion`() = runTest(mainDispatcherRule.testDispatcher) {
        // ...
    }

    @Test
    fun `Replaying completed level should show Replay text`() = runTest(mainDispatcherRule.testDispatcher) {
        // ...
    }
}
