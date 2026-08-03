package com.mahmodhota.worldfood3dadventure.game.match3

import com.mahmodhota.worldfood3dadventure.game.match3.engine.CascadeProcessor
import com.mahmodhota.worldfood3dadventure.game.match3.engine.Match3Engine
import com.mahmodhota.worldfood3dadventure.game.match3.engine.MoveFinder
import com.mahmodhota.worldfood3dadventure.game.match3.engine.RefillEngine
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoardPosition
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTile
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3Board
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.SwapResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class tf21Match3EngineTest {

    @Test
    fun testSuccessfulSwap() {
        val engine = Match3Engine(seed = 123)
        val board = engine.createStartBoard()
        val move = MoveFinder.findValidMoves(board).first()

        val result = engine.performSwap(board, move.first, move.second)

        assertTrue(result is SwapResult.Success)
        val success = result as SwapResult.Success
        assertNotEquals(board, success.stableBoard)
        assertTrue(success.scoreGained > 0)
        assertFalse(com.mahmodhota.worldfood3dadventure.game.match3.engine.MatchDetector.findMatches(success.stableBoard).hasMatches)
        assertTrue(MoveFinder.hasValidMove(success.stableBoard))
    }

    @Test
    fun testInvalidSwap() {
        val engine = Match3Engine(seed = 123)
        val board = engine.createStartBoard()

        val result = engine.performSwap(board, BoardPosition(0, 0), BoardPosition(5, 5))

        assertTrue(result is SwapResult.NotAdjacent)
    }

    @Test
    fun testInvalidAdjacentSwapDoesNotMutateBoard() {
        val engine = Match3Engine(seed = 123)
        val board = engine.createStartBoard()
        val validMoves = MoveFinder.findValidMoves(board).toSet()

        var invalidMove: Pair<BoardPosition, BoardPosition>? = null
        outer@ for (r in 0 until board.rows) {
            for (c in 0 until board.columns - 1) {
                val pair = BoardPosition(r, c) to BoardPosition(r, c + 1)
                if (pair !in validMoves && (pair.second to pair.first) !in validMoves) {
                    invalidMove = pair
                    break@outer
                }
            }
        }

        assertNotNull("Expected at least one invalid adjacent move", invalidMove)
        val (p1, p2) = invalidMove!!
        val result = engine.performSwap(board, p1, p2)

        assertTrue(result is SwapResult.NoMatch)
        val restored = board.swap(p1, p2).swap(p1, p2)
        board.allPositions().forEach { pos ->
            assertEquals(board.tileAt(pos), restored.tileAt(pos))
        }
    }

    @Test
    fun rowClearActivationClearsWholeRow() {
        val board = boardOf(
            listOf(
                tile(1, FoodTileType.PIZZA),
                tile(2, FoodTileType.PASTA),
                tile(3, FoodTileType.TOMATO)
            ),
            listOf(
                tile(4, FoodTileType.CHEESE, SpecialTileType.ROW_CLEAR),
                tile(5, FoodTileType.BASIL),
                tile(6, FoodTileType.PIZZA)
            ),
            listOf(
                tile(7, FoodTileType.BASIL),
                tile(8, FoodTileType.TOMATO),
                tile(9, FoodTileType.CHEESE)
            )
        )
        val engine = Match3Engine(seed = 7, allowedTiles = listOf(FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.TOMATO, FoodTileType.CHEESE, FoodTileType.BASIL))

        val result = engine.performSwap(board, BoardPosition(1, 0), BoardPosition(1, 1)) as SwapResult.Success

        assertEquals(3, result.totalMatchedTiles)
    }

    @Test
    fun columnClearActivationClearsWholeColumn() {
        val board = boardOf(
            listOf(
                tile(1, FoodTileType.PIZZA),
                tile(2, FoodTileType.PASTA, SpecialTileType.COLUMN_CLEAR),
                tile(3, FoodTileType.TOMATO)
            ),
            listOf(
                tile(4, FoodTileType.CHEESE),
                tile(5, FoodTileType.BASIL),
                tile(6, FoodTileType.PIZZA)
            ),
            listOf(
                tile(7, FoodTileType.BASIL),
                tile(8, FoodTileType.TOMATO),
                tile(9, FoodTileType.CHEESE)
            )
        )
        val engine = Match3Engine(seed = 8, allowedTiles = listOf(FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.TOMATO, FoodTileType.CHEESE, FoodTileType.BASIL))

        val result = engine.performSwap(board, BoardPosition(0, 1), BoardPosition(1, 1)) as SwapResult.Success

        assertEquals(3, result.totalMatchedTiles)
    }

    @Test
    fun bombActivationClearsThreeByThreeArea() {
        val board = boardOf(
            rowTiles(1, FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.TOMATO, FoodTileType.CHEESE, FoodTileType.BASIL),
            rowTiles(6, FoodTileType.BASIL, FoodTileType.CHEESE, FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.TOMATO),
            listOf(
                tile(11, FoodTileType.TOMATO),
                tile(12, FoodTileType.BASIL),
                tile(13, FoodTileType.CHEESE, SpecialTileType.BOMB),
                tile(14, FoodTileType.PIZZA),
                tile(15, FoodTileType.PASTA)
            ),
            rowTiles(16, FoodTileType.CHEESE, FoodTileType.TOMATO, FoodTileType.BASIL, FoodTileType.PASTA, FoodTileType.PIZZA),
            rowTiles(21, FoodTileType.PASTA, FoodTileType.PIZZA, FoodTileType.CHEESE, FoodTileType.BASIL, FoodTileType.TOMATO)
        )
        val engine = Match3Engine(seed = 9, allowedTiles = baseAllowedTiles())

        val result = engine.performSwap(board, BoardPosition(2, 2), BoardPosition(2, 3)) as SwapResult.Success

        assertEquals(9, result.cascadeSteps.first().matchedPositions.size)
    }

    @Test
    fun colorBombNormalSwapClearsAllOfTargetType() {
        val board = boardOf(
            listOf(
                tile(1, FoodTileType.PIZZA, SpecialTileType.COLOR_BOMB),
                tile(2, FoodTileType.PIZZA),
                tile(3, FoodTileType.BASIL),
                tile(4, FoodTileType.CHEESE)
            ),
            listOf(
                tile(5, FoodTileType.TOMATO),
                tile(6, FoodTileType.PIZZA),
                tile(7, FoodTileType.CHEESE),
                tile(8, FoodTileType.BASIL)
            ),
            listOf(
                tile(9, FoodTileType.BASIL),
                tile(10, FoodTileType.CHEESE),
                tile(11, FoodTileType.PIZZA),
                tile(12, FoodTileType.TOMATO)
            ),
            listOf(
                tile(13, FoodTileType.CHEESE),
                tile(14, FoodTileType.BASIL),
                tile(15, FoodTileType.TOMATO),
                tile(16, FoodTileType.PIZZA)
            )
        )
        val engine = Match3Engine(seed = 10, allowedTiles = baseAllowedTiles())

        val result = engine.performSwap(board, BoardPosition(0, 0), BoardPosition(0, 1)) as SwapResult.Success

        assertEquals(5, result.totalMatchedTiles)
    }

    @Test
    fun linePlusLineCombinationResolvesOnce() {
        val board = boardOf(
            rowTiles(1, FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.TOMATO),
            listOf(
                tile(4, FoodTileType.CHEESE, SpecialTileType.ROW_CLEAR),
                tile(5, FoodTileType.BASIL, SpecialTileType.COLUMN_CLEAR),
                tile(6, FoodTileType.PIZZA)
            ),
            rowTiles(7, FoodTileType.BASIL, FoodTileType.TOMATO, FoodTileType.CHEESE)
        )
        val engine = Match3Engine(seed = 11, allowedTiles = baseAllowedTiles())

        val result = engine.performSwap(board, BoardPosition(1, 0), BoardPosition(1, 1)) as SwapResult.Success

        assertEquals(5, result.totalMatchedTiles)
        assertFalse(com.mahmodhota.worldfood3dadventure.game.match3.engine.MatchDetector.findMatches(result.stableBoard).hasMatches)
        assertTrue(MoveFinder.hasValidMove(result.stableBoard))
    }

    @Test
    fun linePlusBombCombinationClearsCrossBlast() {
        val board = boardOf(
            rowTiles(1, FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.TOMATO, FoodTileType.CHEESE, FoodTileType.BASIL),
            rowTiles(6, FoodTileType.BASIL, FoodTileType.CHEESE, FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.TOMATO),
            listOf(
                tile(11, FoodTileType.TOMATO),
                tile(12, FoodTileType.BASIL, SpecialTileType.ROW_CLEAR),
                tile(13, FoodTileType.CHEESE, SpecialTileType.BOMB),
                tile(14, FoodTileType.PIZZA),
                tile(15, FoodTileType.PASTA)
            ),
            rowTiles(16, FoodTileType.CHEESE, FoodTileType.TOMATO, FoodTileType.BASIL, FoodTileType.PASTA, FoodTileType.PIZZA),
            rowTiles(21, FoodTileType.PASTA, FoodTileType.PIZZA, FoodTileType.CHEESE, FoodTileType.BASIL, FoodTileType.TOMATO)
        )
        val engine = Match3Engine(seed = 12, allowedTiles = baseAllowedTiles())

        val result = engine.performSwap(board, BoardPosition(2, 1), BoardPosition(2, 2)) as SwapResult.Success

        assertEquals(21, result.cascadeSteps.first().matchedPositions.size)
    }

    @Test
    fun bombPlusBombCombinationClearsLargeBlast() {
        val board = boardOf(
            rowTiles(1, FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.TOMATO, FoodTileType.CHEESE, FoodTileType.BASIL),
            rowTiles(6, FoodTileType.BASIL, FoodTileType.CHEESE, FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.TOMATO),
            listOf(
                tile(11, FoodTileType.TOMATO),
                tile(12, FoodTileType.BASIL, SpecialTileType.BOMB),
                tile(13, FoodTileType.CHEESE, SpecialTileType.BOMB),
                tile(14, FoodTileType.PIZZA),
                tile(15, FoodTileType.PASTA)
            ),
            rowTiles(16, FoodTileType.CHEESE, FoodTileType.TOMATO, FoodTileType.BASIL, FoodTileType.PASTA, FoodTileType.PIZZA),
            rowTiles(21, FoodTileType.PASTA, FoodTileType.PIZZA, FoodTileType.CHEESE, FoodTileType.BASIL, FoodTileType.TOMATO)
        )
        val engine = Match3Engine(seed = 13, allowedTiles = baseAllowedTiles())

        val result = engine.performSwap(board, BoardPosition(2, 1), BoardPosition(2, 2)) as SwapResult.Success

        assertEquals(25, result.cascadeSteps.first().matchedPositions.size)
    }

    @Test
    fun colorBombPlusSpecialCanClearWholeBoard() {
        val board = boardOf(
            listOf(
                tile(1, FoodTileType.PIZZA, SpecialTileType.COLOR_BOMB),
                tile(2, FoodTileType.PIZZA, SpecialTileType.ROW_CLEAR),
                tile(3, FoodTileType.BASIL),
                tile(4, FoodTileType.CHEESE)
            ),
            listOf(
                tile(5, FoodTileType.TOMATO),
                tile(6, FoodTileType.PIZZA),
                tile(7, FoodTileType.CHEESE),
                tile(8, FoodTileType.BASIL)
            ),
            listOf(
                tile(9, FoodTileType.BASIL),
                tile(10, FoodTileType.CHEESE),
                tile(11, FoodTileType.PIZZA),
                tile(12, FoodTileType.TOMATO)
            ),
            listOf(
                tile(13, FoodTileType.CHEESE),
                tile(14, FoodTileType.BASIL),
                tile(15, FoodTileType.TOMATO),
                tile(16, FoodTileType.PIZZA)
            )
        )
        val engine = Match3Engine(seed = 14, allowedTiles = baseAllowedTiles())

        val result = engine.performSwap(board, BoardPosition(0, 0), BoardPosition(0, 1)) as SwapResult.Success

        assertEquals(16, result.cascadeSteps.first().matchedPositions.size)
    }

    @Test
    fun specialSpawnBonusAndCollectedCountsOnlyApplyToRemovedTiles() {
        val board = boardOf(
            rowTiles(1, FoodTileType.PIZZA, FoodTileType.PIZZA, FoodTileType.PIZZA, FoodTileType.PIZZA),
            rowTiles(5, FoodTileType.BASIL, FoodTileType.CHEESE, FoodTileType.TOMATO, FoodTileType.PASTA)
        )
        val processor = CascadeProcessor(
            RefillEngine(
                Random(3),
                listOf(FoodTileType.BASIL, FoodTileType.CHEESE, FoodTileType.TOMATO, FoodTileType.PASTA)
            )
        )

        val result = processor.process(board, preferredMatchPositions = listOf(BoardPosition(0, 2)))

        assertEquals(3, result.steps.first().matchedPositions.size)
        assertEquals(450, result.steps.first().scoreAwarded)
        assertEquals(3, result.collectedCounts[FoodTileType.PIZZA])
    }

    @Test
    fun hammerRemovalProducesUniqueTileIds() {
        val board = boardOf(
            rowTiles(1, FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.TOMATO),
            rowTiles(4, FoodTileType.CHEESE, FoodTileType.BASIL, FoodTileType.PIZZA),
            rowTiles(7, FoodTileType.BASIL, FoodTileType.TOMATO, FoodTileType.CHEESE)
        )
        val engine = Match3Engine(seed = 15, allowedTiles = baseAllowedTiles())

        val result = engine.applyHammer(board, BoardPosition(1, 1))

        assertNotNull(result)
        val ids = result!!.finalBoard.allPositions().mapNotNull { result.finalBoard.tileAt(it)?.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun shuffleProducesPlayableStableBoard() {
        val engine = Match3Engine(seed = 16, allowedTiles = baseAllowedTiles())
        val board = engine.createStartBoard()

        val shuffled = engine.shuffleBoard(board)

        assertFalse(com.mahmodhota.worldfood3dadventure.game.match3.engine.MatchDetector.findMatches(shuffled).hasMatches)
        assertTrue(MoveFinder.hasValidMove(shuffled))
    }

    private fun boardOf(vararg rows: List<FoodTile>): Match3Board {
        val tiles = rows.toList().flatten()
        return Match3Board(rows.size, rows.first().size, tiles)
    }

    private fun rowTiles(startId: Long, vararg types: FoodTileType): List<FoodTile> {
        return types.mapIndexed { index, type -> tile(startId + index, type) }
    }

    private fun tile(id: Long, type: FoodTileType, special: SpecialTileType = SpecialTileType.NONE): FoodTile {
        return FoodTile(id = id, type = type, specialType = special)
    }

    private fun baseAllowedTiles(): List<FoodTileType> {
        return listOf(FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.TOMATO, FoodTileType.CHEESE, FoodTileType.BASIL)
    }
}
