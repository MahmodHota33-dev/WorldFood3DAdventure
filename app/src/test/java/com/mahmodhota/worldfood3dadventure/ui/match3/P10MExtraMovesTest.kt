package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.MainDispatcherRule
import com.mahmodhota.worldfood3dadventure.game.match3.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class P10MExtraMovesTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `useExtraMoves should increase moves and set status to PLAYING`() = runTest {
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
        
        // Wait for board initialization
        advanceUntilIdle()
        
        val initialMoves = viewModel.uiState.movesRemaining
        viewModel.onBoosterSelected(BoosterType.EXTRA_MOVES)
        
        // Wait for useExtraMoves coroutine
        advanceUntilIdle()
        
        assertEquals(initialMoves + 5, viewModel.uiState.movesRemaining)
        assertEquals(GameStatus.PLAYING, viewModel.uiState.status)
    }
}
