package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.game.match3.model.*
import com.mahmodhota.worldfood3dadventure.game.match3.engine.MoveFinder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Match3BoosterHelpTest {

    @Test
    fun `Booster costs are correctly configured`() {
        assertEquals(EconomyConfig.BOOSTER_HAMMER_COST, BoosterConfig.HAMMER_COST)
        assertEquals(EconomyConfig.BOOSTER_SHUFFLE_COST, BoosterConfig.SHUFFLE_COST)
        assertEquals(EconomyConfig.BOOSTER_EXTRA_MOVES_COST, BoosterConfig.EXTRA_MOVES_COST)
    }

    @Test
    fun `MoveFinder identifies valid moves`() {
        // 3x3 board with a potential move
        val tiles = mutableListOf<FoodTile>()
        // APPLE, APPLE, BREAD
        // BREAD, BREAD, APPLE
        // APPLE, BREAD, BREAD
        val layout = listOf(
            FoodTileType.APPLE, FoodTileType.APPLE, FoodTileType.BREAD,
            FoodTileType.BREAD, FoodTileType.BREAD, FoodTileType.APPLE,
            FoodTileType.APPLE, FoodTileType.BREAD, FoodTileType.BREAD
        )
        layout.forEachIndexed { i, type -> tiles.add(FoodTile(i.toLong(), type)) }
        val board = Match3Board(3, 3, tiles)
        
        // No natural matches
        assertFalse(com.mahmodhota.worldfood3dadventure.game.match3.engine.MatchDetector.hasAnyMatch(board))
        
        // Swapping (0,2) BREAD with (1,2) APPLE would not work.
        // Swapping (0,1) APPLE with (1,1) BREAD would not work.
        // Let's create a guaranteed move:
        // APPLE, APPLE, BREAD -> Swap (0,2) with something to make 3 apples?
        // Let's just use a simpler one.
        val simpleTiles = mutableListOf<FoodTile>()
        // APPLE, APPLE, BREAD
        // BREAD, BREAD, APPLE
        // BREAD, BREAD, BREAD
        val simpleLayout = listOf(
            FoodTileType.APPLE, FoodTileType.APPLE, FoodTileType.BREAD,
            FoodTileType.BREAD, FoodTileType.BREAD, FoodTileType.APPLE,
            FoodTileType.BREAD, FoodTileType.BREAD, FoodTileType.BREAD
        )
        simpleLayout.forEachIndexed { i, type -> simpleTiles.add(FoodTile(i.toLong(), type)) }
        val simpleBoard = Match3Board(3, 3, simpleTiles)
        
        // If we put an APPLE at (1,2) and (0,0),(0,1) are apples... 
        // Swap (0,2) with (1,2) if (1,2) was APPLE.
        val moveTiles = mutableListOf<FoodTile>()
        val moveLayout = listOf(
            FoodTileType.APPLE, FoodTileType.APPLE, FoodTileType.BREAD,
            FoodTileType.BREAD, FoodTileType.BREAD, FoodTileType.APPLE,
            FoodTileType.BREAD, FoodTileType.BREAD, FoodTileType.BREAD
        )
        // Wait, index (1,2) is index 5.
        // (0,0)=0, (0,1)=1, (0,2)=2
        // (1,0)=3, (1,1)=4, (1,2)=5
        // (2,0)=6, (2,1)=7, (2,2)=8
        // Let's make (1,2) APPLE. (0,2) is BREAD.
        // Swap (0,2) with (1,2) -> (0,2) becomes APPLE.
        // Then (0,0)=APPLE, (0,1)=APPLE, (0,2)=APPLE -> Match!
        
        assertTrue(MoveFinder.hasValidMove(simpleBoard))
        val moves = MoveFinder.findValidMoves(simpleBoard)
        assertTrue(moves.any { (p1, p2) -> 
            (p1 == BoardPosition(0, 2) && p2 == BoardPosition(1, 2)) ||
            (p1 == BoardPosition(1, 2) && p2 == BoardPosition(0, 2))
        })
    }

    @Test
    fun `Deadlock detection identifies board with no moves`() {
        val tiles = mutableListOf<FoodTile>()
        // 4x4 board where every tile is a unique type
        val types = FoodTileType.values().toList()
        for (i in 0 until 16) {
            tiles.add(FoodTile(i.toLong(), types[i]))
        }
        val board = Match3Board(4, 4, tiles)
        assertFalse(MoveFinder.hasValidMove(board))
    }
}
