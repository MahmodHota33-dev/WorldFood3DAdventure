package com.mahmodhota.worldfood3dadventure.game.match3

import com.mahmodhota.worldfood3dadventure.game.match3.model.BoardPosition
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTile
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3Board
import com.mahmodhota.worldfood3dadventure.ui.match3.Match3AnimationPhase
import com.mahmodhota.worldfood3dadventure.ui.match3.components.TileAnimationRole
import com.mahmodhota.worldfood3dadventure.ui.match3.components.buildBoardAnimationSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Match3BoardAnimationSnapshotTest {

    @Test
    fun refillTilesUseSpawnRowAboveBoard() {
        val previousBoard = Match3Board(
            2,
            2,
            listOf(
                FoodTile(1, FoodTileType.PIZZA),
                FoodTile(2, FoodTileType.PASTA),
                FoodTile(3, FoodTileType.TOMATO),
                FoodTile(4, FoodTileType.CHEESE)
            )
        )
        val currentBoard = Match3Board(
            2,
            2,
            listOf(
                FoodTile(10, FoodTileType.BASIL),
                FoodTile(2, FoodTileType.PASTA),
                FoodTile(3, FoodTileType.TOMATO),
                FoodTile(4, FoodTileType.CHEESE)
            )
        )

        val snapshot = buildBoardAnimationSnapshot(
            previousBoard = previousBoard,
            currentBoard = currentBoard,
            selectedPosition = null,
            matchedPositions = emptySet(),
            phase = Match3AnimationPhase.ApplyingGravity,
            activeTileAnimationIds = emptySet(),
            fallDistanceByTileId = emptyMap(),
            refillTileIds = setOf(10L),
            landingTileIds = emptySet()
        )

        val refillTile = snapshot.tiles.first { it.tile.id == 10L }
        assertEquals(TileAnimationRole.Refilling, refillTile.role)
        assertTrue(refillTile.previousLogicalPosition.row < 0)
    }

    @Test
    fun matchedTilesStayAddressableDuringRemovalPhase() {
        val board = Match3Board(
            2,
            2,
            listOf(
                FoodTile(1, FoodTileType.PIZZA),
                FoodTile(2, FoodTileType.PASTA),
                FoodTile(3, FoodTileType.TOMATO),
                FoodTile(4, FoodTileType.CHEESE)
            )
        )

        val snapshot = buildBoardAnimationSnapshot(
            previousBoard = board,
            currentBoard = board,
            selectedPosition = null,
            matchedPositions = setOf(BoardPosition(0, 1)),
            phase = Match3AnimationPhase.RemovingMatches,
            activeTileAnimationIds = emptySet(),
            fallDistanceByTileId = emptyMap(),
            refillTileIds = emptySet(),
            landingTileIds = emptySet()
        )

        val removingTile = snapshot.tiles.first { it.tile.id == 2L }
        assertEquals(TileAnimationRole.Removing, removingTile.role)
        assertTrue(snapshot.removedTileIds.contains(2L))
    }
}
