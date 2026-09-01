package com.mahmodhota.worldfood3dadventure.game.match3.engine

import com.mahmodhota.worldfood3dadventure.game.match3.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class BlockerSystemTest {

    // Use a set of tiles where we can easily avoid accidental matches on refill
    private val refillEngine = RefillEngine(Random(42), listOf(FoodTileType.APPLE, FoodTileType.BREAD, FoodTileType.CHEESE, FoodTileType.TOMATO))
    private val processor = CascadeProcessor(refillEngine)

    @Test
    fun `ICE_2 should be damaged to ICE_1 and the tile should remain`() {
        // Setup: 5x5 board to avoid edge issues.
        // Row 2 has 3 Apples, middle has ICE_2.
        // Other rows have alternating types to avoid matches.
        val rows = 5
        val cols = 5
        val tiles = mutableListOf<FoodTile>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val foodType = when {
                    r == 2 && c in 1..3 -> FoodTileType.APPLE
                    (r + c) % 2 == 0 -> FoodTileType.BREAD
                    else -> FoodTileType.CHEESE
                }
                val blocker = if (r == 2 && c == 2) BlockerState.ICE_2 else BlockerState.NONE
                tiles.add(FoodTile(id = (r * cols + c).toLong(), type = foodType, blocker = blocker))
            }
        }
        val board = Match3Board(rows, cols, tiles)
        val originalId = board.tileAt(BoardPosition(2, 2))?.id
        
        val result = processor.process(board)
        
        // Since no tiles below row 2 were cleared, (2, 2) should stay at (2, 2)
        val finalTile = result.finalBoard.allPositions()
            .mapNotNull { result.finalBoard.tileAt(it) }
            .find { it.id == originalId }
            
        assertNotNull("Original tile should still exist", finalTile)
        assertEquals("Ice should be damaged to ICE_1", BlockerState.ICE_1, finalTile?.blocker)
        assertEquals(FoodTileType.APPLE, finalTile?.type)
    }

    @Test
    fun `ICE_1 should be damaged to NONE and the tile should remain in the first step`() {
        val rows = 5
        val cols = 5
        val tiles = mutableListOf<FoodTile>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val foodType = when {
                    r == 2 && c in 1..3 -> FoodTileType.APPLE
                    (r + c) % 2 == 0 -> FoodTileType.BREAD
                    else -> FoodTileType.CHEESE
                }
                val blocker = if (r == 2 && c == 2) BlockerState.ICE_1 else BlockerState.NONE
                tiles.add(FoodTile(id = (r * cols + c).toLong(), type = foodType, blocker = blocker))
            }
        }
        val board = Match3Board(rows, cols, tiles)
        val originalId = board.tileAt(BoardPosition(2, 2))?.id
        
        val result = processor.process(board)
        
        assertTrue(result.steps.isNotEmpty())
        
        val firstStepBoard = result.steps[0].boardAfterStep
        val tileAfterFirstStep = firstStepBoard.allPositions()
            .mapNotNull { firstStepBoard.tileAt(it) }
            .find { it.id == originalId }
        
        assertEquals("Ice should be gone after first step", BlockerState.NONE, tileAfterFirstStep?.blocker)
        assertEquals("Tile should still be APPLE after first step", FoodTileType.APPLE, tileAfterFirstStep?.type)
    }

    @Test
    fun `Special tile RowClear damages ice`() {
        val rows = 5
        val cols = 5
        val tiles = mutableListOf<FoodTile>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                // (2, 0) has ICE_2
                // We'll use a type that won't form natural matches
                val foodType = if (r == 2) FoodTileType.APPLE else FoodTileType.BREAD
                val bState = if (r == 2 && c == 0) BlockerState.ICE_2 else BlockerState.NONE
                tiles.add(FoodTile(id = (r * cols + c).toLong(), type = foodType, blocker = bState))
            }
        }
        val board = Match3Board(rows, cols, tiles)
        
        // Force trigger a RowClear effect at (2, 2)
        val forcedRes = ForcedBoardResolution(clearedPositions = (0 until cols).map { BoardPosition(2, it) }.toSet())
        val result = processor.process(board, forcedInitialResolution = forcedRes)
        
        val finalTile = result.finalBoard.allPositions()
            .mapNotNull { result.finalBoard.tileAt(it) }
            .find { it.id == 10L }
            
        assertNotNull("Original frozen tile should still exist", finalTile)
        assertEquals("Ice should be damaged to ICE_1", BlockerState.ICE_1, finalTile?.blocker)
    }
}
