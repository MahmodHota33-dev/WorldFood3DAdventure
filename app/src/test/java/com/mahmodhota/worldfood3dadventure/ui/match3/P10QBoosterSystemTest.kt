package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.game.match3.model.*
import com.mahmodhota.worldfood3dadventure.game.match3.engine.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class P10QBoosterSystemTest {

    @Test
    fun testBoosterInventoryPersistence() {
        val inventory = BoosterInventory(hammer = 5, rocket = 4, hand = 3)
        assertEquals(5, inventory.hammer)
        assertEquals(4, inventory.rocket)
        assertEquals(3, inventory.hand)
        
        val updated = inventory.withCount(BoosterType.ROCKET, 10)
        assertEquals(10, updated.rocket)
        assertEquals(5, updated.hammer)
    }

    @Test
    fun testMatch3EngineBoosterMethodsExist() {
        val engine = Match3Engine(allowedTiles = FoodTileType.values().toList())
        val board = Match3Board(8, 8, (0 until 64).map { 
            FoodTile(it.toLong(), FoodTileType.values()[it % FoodTileType.values().size]) 
        })
        
        // Test methods are reachable and return expected types
        val hammerRes = engine.applyHammer(board, BoardPosition(0, 0))
        assertNotNull(hammerRes)
        
        val rocketRes = engine.applyRocket(board, BoardPosition(0, 0))
        assertNotNull(rocketRes)
        
        val handRes = engine.applyHand(board, BoardPosition(0, 0), BoardPosition(0, 1))
        assertNotNull(handRes)
    }
}
