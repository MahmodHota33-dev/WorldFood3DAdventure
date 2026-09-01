package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.MainDispatcherRule
import com.mahmodhota.worldfood3dadventure.game.match3.model.*
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class P10UStateIntegrityTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        ProgressionManager.resetForTesting()
    }

    @Test
    fun `Booster selection should be blocked during board initialization`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = Match3ViewModel(
            countryId = "italy",
            levelNumber = 1,
            playSfx = {},
            hapticLight = {},
            hapticMedium = {},
            hapticHeavy = {},
            boosterInventoryProvider = { BoosterInventory(hammer = 1) },
            startHintTimerAutomatically = false
        )
        
        // Initializing state has isAnimating = true
        assertTrue(viewModel.uiState.isAnimating)
        
        viewModel.onBoosterSelected(BoosterType.HAMMER)
        assertNull(viewModel.uiState.selectedBooster)
        
        advanceUntilIdle()
        assertFalse(viewModel.uiState.isAnimating)
        viewModel.onScreenExit()
    }

    @Test
    fun `Selecting then de-selecting a booster should return to IDLE`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = Match3ViewModel(
            countryId = "italy",
            levelNumber = 1,
            playSfx = {},
            hapticLight = {},
            hapticMedium = {},
            hapticHeavy = {},
            boosterInventoryProvider = { BoosterInventory(hammer = 1) },
            startHintTimerAutomatically = false,
            backgroundDispatcher = mainDispatcherRule.testDispatcher
        )
        advanceUntilIdle()

        // Select Hammer
        viewModel.onBoosterSelected(BoosterType.HAMMER)
        assertEquals(BoosterType.HAMMER, viewModel.uiState.selectedBooster)

        // Cancel Hammer by retapping
        viewModel.onBoosterSelected(BoosterType.HAMMER)
        assertNull(viewModel.uiState.selectedBooster)
        viewModel.onScreenExit()
    }

    @Test
    fun `Input should be ignored if game status is WON`() = runTest(mainDispatcherRule.testDispatcher) {
         assertTrue(shouldIgnoreTileSelectionInput(isAnimating = false, status = GameStatus.WON))
         assertTrue(shouldIgnoreTileSelectionInput(isAnimating = true, status = GameStatus.PLAYING))
         assertFalse(shouldIgnoreTileSelectionInput(isAnimating = false, status = GameStatus.PLAYING))
    }

    @Test
    fun `Extra moves application should reset status to PLAYING`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = Match3ViewModel(
            countryId = "italy",
            levelNumber = 1,
            playSfx = {},
            hapticLight = {},
            hapticMedium = {},
            hapticHeavy = {},
            boosterInventoryProvider = { BoosterInventory(extraMoves = 1) },
            consumeBooster = { BoosterInventory(extraMoves = 0) },
            startHintTimerAutomatically = false
        )
        advanceUntilIdle()
        
        // Use a private setter simulation or logic check
        // If we trigger EXTRA_MOVES booster, status should be PLAYING
        viewModel.onBoosterSelected(BoosterType.EXTRA_MOVES)
        advanceUntilIdle()
        
        assertEquals(GameStatus.PLAYING, viewModel.uiState.status)
        assertFalse(viewModel.uiState.isAnimating)
        viewModel.onScreenExit()
    }
}
