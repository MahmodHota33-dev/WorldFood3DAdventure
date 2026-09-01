package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.MainDispatcherRule
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressManager
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressRepository
import com.mahmodhota.worldfood3dadventure.data.progress.model.PersistedGameState
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType
import com.mahmodhota.worldfood3dadventure.game.match3.model.EconomyConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class P10YBoosterEconomyTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockRepository: GameProgressRepository = mock()
    private val gameStateFlow = MutableStateFlow(PersistedGameState())

    @Before
    fun setup() {
        whenever(mockRepository.state).thenReturn(gameStateFlow)
        GameProgressManager.setRepositoryForTesting(mockRepository)
        ProgressionManager.resetForTesting()
    }

    @Test
    fun `Booster purchase logic verify`() {
        // Just verify EconomyConfig is used in BoosterConfig
        assertEquals(EconomyConfig.BOOSTER_HAMMER_COST, com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterConfig.HAMMER_COST)
    }
}
