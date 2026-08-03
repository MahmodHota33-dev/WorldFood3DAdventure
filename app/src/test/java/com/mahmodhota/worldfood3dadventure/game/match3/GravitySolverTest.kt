package com.mahmodhota.worldfood3dadventure.game.match3

import com.mahmodhota.worldfood3dadventure.game.match3.engine.GravitySolver
import com.mahmodhota.worldfood3dadventure.game.match3.model.*
import org.junit.Assert.*
import org.junit.Test

class GravitySolverTest {

    @Test
    fun testSimpleGravity() {
        val rows = 3
        val cols = 1
        val tileA = FoodTile(1, FoodTileType.PIZZA)
        val tileB = FoodTile(2, FoodTileType.PASTA)
        
        // Initial state: A at top, middle empty, B at bottom
        // Board map uses (row, col)
        val initialMap = mapOf(
            BoardPosition(0, 0) to tileA,
            BoardPosition(1, 0) to null,
            BoardPosition(2, 0) to tileB
        )
        
        val result = GravitySolver.applyGravity(rows, cols, initialMap)
        
        // Expected: A and B at bottom, top empty
        assertEquals(tileA, result[BoardPosition(1, 0)])
        assertEquals(tileB, result[BoardPosition(2, 0)])
        assertNull(result[BoardPosition(0, 0)])
    }

    @Test
    fun testGravityMetadataTracksFallDistance() {
        val rows = 4
        val cols = 1
        val tileA = FoodTile(1, FoodTileType.PIZZA)

        val initialMap = mapOf(
            BoardPosition(0, 0) to tileA,
            BoardPosition(1, 0) to null,
            BoardPosition(2, 0) to null,
            BoardPosition(3, 0) to null
        )

        val result = GravitySolver.applyGravityWithMetadata(rows, cols, initialMap)

        assertEquals(tileA, result.tiles[BoardPosition(3, 0)])
        assertEquals(3, result.fallDistanceByTileId[1L])
    }
}
