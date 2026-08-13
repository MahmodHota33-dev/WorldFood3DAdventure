package com.mahmodhota.worldfood3dadventure.game.match3.model

/**
 * Result of a player's swap attempt.
 */
sealed class SwapResult {
    data class Success(
        val initialBoard: Match3Board,
        val swappedBoard: Match3Board,
        val stableBoard: Match3Board,
        val cascadeSteps: List<CascadeStep>,
        val scoreGained: Int,
        val totalMatchedTiles: Int,
        val collectedCounts: Map<FoodTileType, Int>,
        val wasReshuffled: Boolean = false
    ) : SwapResult()

    object NotAdjacent : SwapResult()
    object OutOfBounds : SwapResult()
    object NoMatch : SwapResult()
}
