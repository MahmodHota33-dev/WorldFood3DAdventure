package com.mahmodhota.worldfood3dadventure.game.match3.engine

import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTile
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3Board
import kotlin.random.Random

/**
 * Generates initial Match-3 boards with specific constraints.
 */
class BoardGenerator(
    private val random: Random = Random.Default,
    private val maxAttempts: Int = 1000
) {
    private var nextTileId: Long = 1L

    /**
     * Generates a fully populated board with no initial matches and at least one valid move.
     */
    fun generate(
        rows: Int = 8,
        cols: Int = 8,
        allowedTiles: List<FoodTileType> = FoodTileType.values().toList()
    ): Match3Board {
        repeat(maxAttempts) {
            val board = generateSmartBoard(rows, cols, allowedTiles)
            if (MoveFinder.hasValidMove(board)) {
                return board
            }
        }
        throw IllegalStateException("Failed to generate a valid board after $maxAttempts attempts")
    }

    private fun generateSmartBoard(rows: Int, cols: Int, allowedTiles: List<FoodTileType>): Match3Board {
        val tileArray = arrayOfNulls<FoodTile>(rows * cols)
        
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val forbidden = mutableListOf<FoodTileType>()
                
                // Avoid horizontal matches
                if (c >= 2) {
                    val t1 = tileArray[r * cols + (c - 1)]?.type
                    val t2 = tileArray[r * cols + (c - 2)]?.type
                    if (t1 != null && t1 == t2) forbidden.add(t1)
                }
                // Avoid vertical matches
                if (r >= 2) {
                    val t1 = tileArray[(r - 1) * cols + c]?.type
                    val t2 = tileArray[(r - 2) * cols + c]?.type
                    if (t1 != null && t1 == t2) forbidden.add(t1)
                }
                
                // Optimized selection from allowed tiles
                var type = allowedTiles[random.nextInt(allowedTiles.size)]
                if (forbidden.isNotEmpty()) {
                    var retry = 0
                    while (type in forbidden && retry < 10) {
                        type = allowedTiles[random.nextInt(allowedTiles.size)]
                        retry++
                    }
                }
                
                tileArray[r * cols + c] = FoodTile(id = nextTileId++, type = type)
            }
        }
        
        return Match3Board(rows, cols, tileArray.map { it!! })
    }

    private fun createRandomTile(allowedTiles: List<FoodTileType>): FoodTile {
        val type = allowedTiles[random.nextInt(allowedTiles.size)]
        return FoodTile(id = nextTileId++, type = type)
    }

    /**
     * Resets the tile ID counter.
     */
    fun resetIdCounter() {
        nextTileId = 1L
    }
}
