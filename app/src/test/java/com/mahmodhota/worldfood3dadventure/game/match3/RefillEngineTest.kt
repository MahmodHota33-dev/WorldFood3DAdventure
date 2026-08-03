package com.mahmodhota.worldfood3dadventure.game.match3

import com.mahmodhota.worldfood3dadventure.game.match3.engine.RefillEngine
import com.mahmodhota.worldfood3dadventure.game.match3.model.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class RefillEngineTest {

    @Test
    fun testRefill() {
        val rows = 2
        val cols = 2
        val tileA = FoodTile(1, FoodTileType.PIZZA)
        
        val initialMap = mapOf(
            BoardPosition(0, 0) to null,
            BoardPosition(0, 1) to null,
            BoardPosition(1, 0) to tileA,
            BoardPosition(1, 1) to null
        )
        
        val refillEngine = RefillEngine(Random(42))
        val result = refillEngine.refill(rows, cols, initialMap)
        
        assertEquals(4, result.size)
        assertEquals(tileA, result[BoardPosition(1, 0)])
        assertNotNull(result[BoardPosition(0, 0)])
        assertNotNull(result[BoardPosition(0, 1)])
        assertNotNull(result[BoardPosition(1, 1)])
    }

    @Test
    fun testRefillMetadataReportsUniqueSpawnedIds() {
        val engine = RefillEngine(Random(7))
        val result = engine.refillWithMetadata(
            rows = 2,
            cols = 2,
            currentTiles = mapOf(
                BoardPosition(0, 0) to null,
                BoardPosition(0, 1) to null,
                BoardPosition(1, 0) to FoodTile(1, FoodTileType.PIZZA),
                BoardPosition(1, 1) to null
            )
        )

        assertEquals(3, result.newTileIds.size)
        assertEquals(result.newTileIds.size, result.newTileIds.distinct().size)
    }
}
