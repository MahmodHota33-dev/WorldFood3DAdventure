package com.mahmodhota.worldfood3dadventure.game.match3

import com.mahmodhota.worldfood3dadventure.game.match3.engine.MatchDetector
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoardPosition
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTile
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3Board
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialTileType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpecialTileTest {

    @Test
    fun horizontalFourCreatesRowClearAtPreferredPosition() {
        val board = boardOf(
            row(
                FoodTileType.PIZZA,
                FoodTileType.PIZZA,
                FoodTileType.PIZZA,
                FoodTileType.PIZZA
            ),
            row(
                FoodTileType.BASIL,
                FoodTileType.CHEESE,
                FoodTileType.TOMATO,
                FoodTileType.PASTA
            )
        )

        val result = MatchDetector.findMatches(board, preferredPositions = listOf(BoardPosition(0, 2)))

        assertEquals(SpecialTileType.ROW_CLEAR, result.matchGroups.single().creationType)
        assertEquals(BoardPosition(0, 2), result.matchGroups.single().creationPoint)
        assertEquals(SpecialTileType.ROW_CLEAR, result.specialTilesToSpawn[BoardPosition(0, 2)])
    }

    @Test
    fun verticalFourCreatesColumnClearAtPreferredPosition() {
        val board = boardOf(
            row(FoodTileType.PIZZA, FoodTileType.BASIL),
            row(FoodTileType.PIZZA, FoodTileType.CHEESE),
            row(FoodTileType.PIZZA, FoodTileType.TOMATO),
            row(FoodTileType.PIZZA, FoodTileType.PASTA)
        )

        val result = MatchDetector.findMatches(board, preferredPositions = listOf(BoardPosition(2, 0)))

        assertEquals(SpecialTileType.COLUMN_CLEAR, result.matchGroups.single().creationType)
        assertEquals(BoardPosition(2, 0), result.matchGroups.single().creationPoint)
        assertEquals(SpecialTileType.COLUMN_CLEAR, result.specialTilesToSpawn[BoardPosition(2, 0)])
    }

    @Test
    fun fiveInLineCreatesColorBomb() {
        val board = boardOf(
            row(
                FoodTileType.PIZZA,
                FoodTileType.PIZZA,
                FoodTileType.PIZZA,
                FoodTileType.PIZZA,
                FoodTileType.PIZZA
            )
        )

        val result = MatchDetector.findMatches(board, preferredPositions = listOf(BoardPosition(0, 3)))

        assertEquals(SpecialTileType.COLOR_BOMB, result.matchGroups.single().creationType)
        assertEquals(BoardPosition(0, 3), result.matchGroups.single().creationPoint)
    }

    @Test
    fun tShapeCreatesBombAtIntersection() {
        val board = boardOf(
            row(FoodTileType.PASTA, FoodTileType.PIZZA, FoodTileType.PASTA),
            row(FoodTileType.PIZZA, FoodTileType.PIZZA, FoodTileType.PIZZA),
            row(FoodTileType.PASTA, FoodTileType.PIZZA, FoodTileType.BASIL)
        )

        val result = MatchDetector.findMatches(board, preferredPositions = listOf(BoardPosition(1, 1)))

        assertEquals(1, result.matchGroups.size)
        assertEquals(SpecialTileType.BOMB, result.matchGroups.single().creationType)
        assertEquals(BoardPosition(1, 1), result.matchGroups.single().creationPoint)
    }

    @Test
    fun lShapeCreatesBombAtIntersection() {
        val board = boardOf(
            row(FoodTileType.PIZZA, FoodTileType.BASIL, FoodTileType.CHEESE),
            row(FoodTileType.PIZZA, FoodTileType.BASIL, FoodTileType.CHEESE),
            row(FoodTileType.PIZZA, FoodTileType.PIZZA, FoodTileType.PIZZA)
        )

        val result = MatchDetector.findMatches(board, preferredPositions = listOf(BoardPosition(2, 0)))

        assertTrue(result.hasMatches)
        assertEquals(SpecialTileType.BOMB, result.matchGroups.single().creationType)
        assertEquals(BoardPosition(2, 0), result.matchGroups.single().creationPoint)
    }

    private fun boardOf(vararg rows: List<FoodTileType>): Match3Board {
        val tiles = mutableListOf<FoodTile>()
        var nextId = 1L
        rows.forEach { row ->
            row.forEach { type ->
                tiles += FoodTile(nextId++, type)
            }
        }
        return Match3Board(rows.size, rows.first().size, tiles)
    }

    private fun row(vararg types: FoodTileType): List<FoodTileType> = types.toList()
}
