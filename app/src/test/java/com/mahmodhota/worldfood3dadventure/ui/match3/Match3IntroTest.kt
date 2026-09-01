package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3Board
import com.mahmodhota.worldfood3dadventure.game.match3.engine.Match3Engine
import com.mahmodhota.worldfood3dadventure.MainDispatcherRule
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressManager
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressRepository
import com.mahmodhota.worldfood3dadventure.data.progress.model.PersistedGameState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class Match3IntroTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockRepository: GameProgressRepository = mock()
    private val gameStateFlow = MutableStateFlow(PersistedGameState())

    @Before
    fun setup() {
        whenever(mockRepository.state).thenReturn(gameStateFlow)
        GameProgressManager.setRepositoryForTesting(mockRepository)
    }

    @Test
    fun `ViewModel should initially show travel intro`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = Match3ViewModel(
            countryId = "germany",
            levelNumber = 1,
            engineFactory = { tiles ->
                // Return a mock engine that doesn't do much
                Match3Engine(allowedTiles = tiles)
            },
            playSfx = {}, // Provide mock to avoid GlobalSystemManager dependency
            startHintTimerAutomatically = false
        )
        
        assertTrue("Intro should be visible at start", viewModel.uiState.showTravelIntro)
        
        // Wait for board initialization (delay 2200ms)
        advanceTimeBy(3000)
        
        assertFalse("Intro should be hidden after initialization", viewModel.uiState.showTravelIntro)
    }
}
