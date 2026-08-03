package com.mahmodhota.worldfood3dadventure.game.match3.engine

import com.mahmodhota.worldfood3dadventure.game.match3.model.BoardPosition
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTile
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3Board

/**
 * Handles the downward movement of tiles into empty spaces.
 */
object GravitySolver {
    data class GravityResult(
        val tiles: Map<BoardPosition, FoodTile?>,
        val fallDistanceByTileId: Map<Long, Int>
    )

    /**
     * Collapses tiles downward to fill null spaces.
     * Note: This assumes empty spaces are represented as missing tiles or nulls in a temporary structure.
     * Since Match3Board is immutable, we use a list of nullable tiles for intermediate steps.
     */
    fun applyGravity(rows: Int, cols: Int, currentTiles: Map<BoardPosition, FoodTile?>): Map<BoardPosition, FoodTile?> {
        return applyGravityWithMetadata(rows, cols, currentTiles).tiles
    }

    fun applyGravityWithMetadata(
        rows: Int,
        cols: Int,
        currentTiles: Map<BoardPosition, FoodTile?>
    ): GravityResult {
        val newTiles = mutableMapOf<BoardPosition, FoodTile?>()
        val fallDistanceByTileId = mutableMapOf<Long, Int>()

        for (c in 0 until cols) {
            val columnTiles = mutableListOf<Pair<FoodTile, Int>>()
            // Collect all existing tiles in this column from bottom to top
            for (r in rows - 1 downTo 0) {
                val tile = currentTiles[BoardPosition(r, c)]
                if (tile != null) {
                    columnTiles.add(tile to r)
                }
            }

            // Place them back from bottom to top
            for (r in rows - 1 downTo 0) {
                val targetPos = BoardPosition(r, c)
                val tileIndex = (rows - 1) - r
                if (tileIndex < columnTiles.size) {
                    val (tile, sourceRow) = columnTiles[tileIndex]
                    newTiles[targetPos] = tile
                    val distance = r - sourceRow
                    if (distance > 0) {
                        fallDistanceByTileId[tile.id] = distance
                    }
                } else {
                    newTiles[targetPos] = null
                }
            }
        }

        return GravityResult(newTiles, fallDistanceByTileId)
    }
}
