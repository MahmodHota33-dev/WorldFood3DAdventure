package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.game.match3.model.BoardPosition
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTile
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.GameStatus
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3Board
import com.mahmodhota.worldfood3dadventure.game.match3.model.SwapResult
import com.mahmodhota.worldfood3dadventure.ui.match3.components.shouldFireCompletionCallback
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase84BMatch3GuardsTest {

    @Test
    fun overlappingResolutionJobsPreventedWhileAnimating() {
        assertTrue(shouldIgnoreTileSelectionInput(isAnimating = true, status = GameStatus.PLAYING))
        assertFalse(shouldIgnoreTileSelectionInput(isAnimating = false, status = GameStatus.PLAYING))
    }

    @Test
    fun boosterDoubleTapConsumesOnceByPendingGuard() {
        val firstRequest = canAcceptBoosterRequest(
            isAnimating = false,
            status = GameStatus.PLAYING,
            pendingBooster = null,
            inventoryCount = 1
        )
        val secondRequest = canAcceptBoosterRequest(
            isAnimating = false,
            status = GameStatus.PLAYING,
            pendingBooster = BoosterType.HAMMER,
            inventoryCount = 1
        )
        assertTrue(firstRequest)
        assertFalse(secondRequest)
    }

    @Test
    fun inputReturnsToIdleAfterCancellationCleanup() {
        val board = Match3Board(
            rows = 1,
            columns = 1,
            tiles = listOf(FoodTile(id = 1L, type = FoodTileType.PIZZA))
        )
        val dirtyState = Match3UiState(
            board = board,
            isAnimating = true,
            selectedPosition = BoardPosition(0, 0),
            animationPhase = Match3AnimationPhase.RemovingMatches
        )

        val cleaned = dirtyState.resetTransientUi(isAnimating = false)

        assertFalse(cleaned.isAnimating)
        assertEquals(Match3AnimationPhase.Idle, cleaned.animationPhase)
    }

    @Test
    fun continueCallbackExecutesOnce() {
        assertTrue(shouldFireCompletionCallback(closing = false, callbackFired = false))
        assertFalse(shouldFireCompletionCallback(closing = true, callbackFired = true))
    }

    @Test
    fun validMoveDecrementsExactlyOnce() {
        val delta = movesDeltaForSwapResult(
            SwapResult.Success(
                initialBoard = Match3Board(1, 2, listOf(FoodTile(1L, FoodTileType.PIZZA), FoodTile(2L, FoodTileType.PASTA))),
                swappedBoard = Match3Board(1, 2, listOf(FoodTile(2L, FoodTileType.PASTA), FoodTile(1L, FoodTileType.PIZZA))),
                stableBoard = Match3Board(1, 2, listOf(FoodTile(2L, FoodTileType.PASTA), FoodTile(1L, FoodTileType.PIZZA))),
                cascadeSteps = emptyList(),
                scoreGained = 0,
                totalMatchedTiles = 3,
                collectedCounts = emptyMap()
            )
        )
        assertEquals(-1, delta)
    }

    @Test
    fun invalidSwapDecrementsZeroTimes() {
        assertEquals(0, movesDeltaForSwapResult(SwapResult.NoMatch))
        assertEquals(0, movesDeltaForSwapResult(SwapResult.NotAdjacent))
    }

    @Test
    fun gameStatusEvaluationRunsOnlyDuringActivePlay() {
        assertTrue(shouldEvaluateGameStatus(GameStatus.PLAYING))
        assertFalse(shouldEvaluateGameStatus(GameStatus.WON))
        assertFalse(shouldEvaluateGameStatus(GameStatus.LOST))
    }
}
