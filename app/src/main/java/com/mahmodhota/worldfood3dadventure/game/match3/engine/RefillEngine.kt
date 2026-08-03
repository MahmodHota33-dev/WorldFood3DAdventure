package com.mahmodhota.worldfood3dadventure.game.match3.engine

import com.mahmodhota.worldfood3dadventure.game.match3.model.BoardPosition
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTile
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import kotlin.random.Random

/**
 * Generates new tiles to fill empty spaces.
 */
class RefillEngine(
    private val random: Random = Random.Default,
    private val allowedTiles: List<FoodTileType> = FoodTileType.values().toList()
) {
    data class RefillResult(
        val tiles: Map<BoardPosition, FoodTile>,
        val newTileIds: Set<Long>
    )
    private var nextTileId: Long = 10000L // Offset for refilled tiles to avoid ID collisions

    /**
     * Fills null spaces in the tile map with new random tiles.
     */
    fun refill(rows: Int, cols: Int, currentTiles: Map<BoardPosition, FoodTile?>): Map<BoardPosition, FoodTile> {
        return refillWithMetadata(rows, cols, currentTiles).tiles
    }

    fun refillWithMetadata(
        rows: Int,
        cols: Int,
        currentTiles: Map<BoardPosition, FoodTile?>
    ): RefillResult {
        val finalTiles = mutableMapOf<BoardPosition, FoodTile>()
        val newTileIds = mutableSetOf<Long>()

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val pos = BoardPosition(r, c)
                val existing = currentTiles[pos]
                if (existing != null) {
                    finalTiles[pos] = existing
                } else {
                    val created = createRandomTile()
                    finalTiles[pos] = created
                    newTileIds.add(created.id)
                }
            }
        }

        return RefillResult(finalTiles, newTileIds)
    }

    private fun createRandomTile(): FoodTile {
        val type = allowedTiles[random.nextInt(allowedTiles.size)]
        return FoodTile(id = nextTileId++, type = type)
    }

    fun setIdCounter(startId: Long) {
        nextTileId = startId
    }
}
