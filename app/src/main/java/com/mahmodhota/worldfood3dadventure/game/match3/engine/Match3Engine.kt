package com.mahmodhota.worldfood3dadventure.game.match3.engine

import com.mahmodhota.worldfood3dadventure.game.match3.Match3SpecialConfig
import com.mahmodhota.worldfood3dadventure.game.match3.model.*
import kotlin.random.Random

/**
 * Main coordinator for the Match-3 logic.
 */
class Match3Engine(
    seed: Long = Random.nextLong(),
    val allowedTiles: List<FoodTileType> = FoodTileType.values().toList()
) {
    private val random = Random(seed)
    private val refillEngine = RefillEngine(random, allowedTiles)
    private val cascadeProcessor = CascadeProcessor(refillEngine)
    private val generator = BoardGenerator(random)

    fun createStartBoard(rows: Int = 8, cols: Int = 8): Match3Board {
        return generator.generate(rows, cols, allowedTiles)
    }

    fun performSwap(board: Match3Board, pos1: BoardPosition, pos2: BoardPosition): SwapResult {
        if (!board.contains(pos1) || !board.contains(pos2)) {
            return SwapResult.OutOfBounds
        }

        if (!pos1.isAdjacent(pos2)) {
            return SwapResult.NotAdjacent
        }

        val swappedBoard = board.swap(pos1, pos2)
        val specialResolution = buildSpecialSwapResolution(swappedBoard, pos1, pos2)
        val cascadeResult = when {
            specialResolution != null -> {
                cascadeProcessor.process(
                    initialBoard = swappedBoard,
                    forcedInitialResolution = specialResolution
                )
            }
            else -> {
                val initialMatches = MatchDetector.findMatches(swappedBoard, listOf(pos2, pos1))
                if (!initialMatches.hasMatches) {
                    return SwapResult.NoMatch
                }
                cascadeProcessor.process(
                    initialBoard = swappedBoard,
                    preferredMatchPositions = listOf(pos2, pos1)
                )
            }
        }

        val (stableBoard, reshuffled) = ensurePlayableBoardWithStatus(cascadeResult.finalBoard)
        return SwapResult.Success(
            initialBoard = board,
            swappedBoard = swappedBoard,
            stableBoard = stableBoard,
            cascadeSteps = cascadeResult.steps,
            scoreGained = cascadeResult.totalScore,
            totalMatchedTiles = cascadeResult.totalMatchedTiles,
            collectedCounts = cascadeResult.collectedCounts,
            wasReshuffled = reshuffled
        )
    }

    data class HammerResult(
        val finalBoard: Match3Board,
        val steps: List<CascadeStep>,
        val totalScore: Int,
        val collectedCounts: Map<FoodTileType, Int>,
        val wasReshuffled: Boolean
    )

    fun applyHammer(board: Match3Board, position: BoardPosition): HammerResult? {
        if (!board.contains(position)) return null
        val cascadeResult = cascadeProcessor.process(
            initialBoard = board,
            forcedInitialResolution = ForcedBoardResolution(
                clearedPositions = setOf(position),
                scoreBonus = Match3SpecialConfig.HammerScorePerTile
            )
        )
        val (stableBoard, reshuffled) = ensurePlayableBoardWithStatus(cascadeResult.finalBoard)
        return HammerResult(
            finalBoard = stableBoard,
            steps = cascadeResult.steps,
            totalScore = cascadeResult.totalScore,
            collectedCounts = cascadeResult.collectedCounts,
            wasReshuffled = reshuffled
        )
    }

    fun shuffleBoard(board: Match3Board): Match3Board {
        return generator.generate(board.rows, board.columns, allowedTiles)
    }

    private fun ensurePlayableBoardWithStatus(board: Match3Board): Pair<Match3Board, Boolean> {
        if (!MatchDetector.findMatches(board).hasMatches && MoveFinder.hasValidMove(board)) {
            return board to false
        }
        return generator.generate(board.rows, board.columns, allowedTiles) to true
    }

    private fun buildSpecialSwapResolution(
        swappedBoard: Match3Board,
        originalPos1: BoardPosition,
        originalPos2: BoardPosition
    ): ForcedBoardResolution? {
        val tileAtFirst = swappedBoard.tileAt(originalPos1) ?: return null
        val tileAtSecond = swappedBoard.tileAt(originalPos2) ?: return null

        if (tileAtFirst.specialType == SpecialTileType.NONE && tileAtSecond.specialType == SpecialTileType.NONE) {
            return null
        }

        if (tileAtFirst.specialType == SpecialTileType.COLOR_BOMB && tileAtSecond.specialType == SpecialTileType.COLOR_BOMB) {
            return ForcedBoardResolution(
                clearedPositions = swappedBoard.allPositions().toSet(),
                visualEffects = listOf(
                    SpecialBoardEffect(
                        type = SpecialBoardEffectType.COLOR_CLEAR,
                        origin = originalPos1,
                        affectedPositions = swappedBoard.allPositions().toSet()
                    )
                )
            )
        }

        if (tileAtFirst.specialType == SpecialTileType.COLOR_BOMB || tileAtSecond.specialType == SpecialTileType.COLOR_BOMB) {
            val colorBombPos = if (tileAtFirst.specialType == SpecialTileType.COLOR_BOMB) originalPos1 else originalPos2
            val partnerPos = if (colorBombPos == originalPos1) originalPos2 else originalPos1
            val partnerTile = swappedBoard.tileAt(partnerPos) ?: return null
            val targetType = partnerTile.type
            val matchingPositions = swappedBoard.allPositions()
                .filter { swappedBoard.tileAt(it)?.type == targetType }
                .toSet()
            val forcedOverrides = if (partnerTile.specialType != SpecialTileType.NONE && partnerTile.specialType != SpecialTileType.COLOR_BOMB) {
                matchingPositions.associateWith { partnerTile.specialType }
            } else {
                emptyMap()
            }
            return ForcedBoardResolution(
                clearedPositions = matchingPositions + colorBombPos,
                forcedSpecialOverrides = forcedOverrides,
                forcedColorTargets = mapOf(colorBombPos to targetType),
                visualEffects = listOf(
                    SpecialBoardEffect(
                        type = SpecialBoardEffectType.COLOR_CLEAR,
                        origin = colorBombPos,
                        affectedPositions = matchingPositions + colorBombPos,
                        affectedTileType = targetType
                    )
                )
            )
        }

        if (tileAtFirst.specialType != SpecialTileType.NONE && tileAtSecond.specialType != SpecialTileType.NONE) {
            val comboClear = when {
                isLineClear(tileAtFirst.specialType) && isLineClear(tileAtSecond.specialType) -> {
                    specialAreaFor(swappedBoard, originalPos1, tileAtFirst.specialType) +
                        specialAreaFor(swappedBoard, originalPos2, tileAtSecond.specialType)
                }
                (isLineClear(tileAtFirst.specialType) && tileAtSecond.specialType == SpecialTileType.BOMB) ||
                    (isLineClear(tileAtSecond.specialType) && tileAtFirst.specialType == SpecialTileType.BOMB) -> {
                    lineBombArea(swappedBoard, if (tileAtFirst.specialType == SpecialTileType.BOMB) originalPos1 else originalPos2)
                }
                tileAtFirst.specialType == SpecialTileType.BOMB && tileAtSecond.specialType == SpecialTileType.BOMB -> {
                    doubleBombArea(swappedBoard, originalPos1, originalPos2)
                }
                else -> {
                    specialAreaFor(swappedBoard, originalPos1, tileAtFirst.specialType) +
                        specialAreaFor(swappedBoard, originalPos2, tileAtSecond.specialType)
                }
            }

            return ForcedBoardResolution(
                clearedPositions = comboClear + originalPos1 + originalPos2
            )
        }

        val specialPos = if (tileAtFirst.specialType != SpecialTileType.NONE) originalPos1 else originalPos2
        val specialTile = swappedBoard.tileAt(specialPos) ?: return null
        return ForcedBoardResolution(
            clearedPositions = specialAreaFor(swappedBoard, specialPos, specialTile.specialType) + specialPos
        )
    }

    private fun specialAreaFor(
        board: Match3Board,
        origin: BoardPosition,
        specialType: SpecialTileType
    ): Set<BoardPosition> {
        return when (specialType) {
            SpecialTileType.ROW_CLEAR -> (0 until board.columns).map { BoardPosition(origin.row, it) }.toSet()
            SpecialTileType.COLUMN_CLEAR -> (0 until board.rows).map { BoardPosition(it, origin.column) }.toSet()
            SpecialTileType.BOMB -> positionsInRadius(board, origin, Match3SpecialConfig.BombBlastRadius)
            SpecialTileType.COLOR_BOMB -> board.allPositions().toSet()
            SpecialTileType.NONE -> emptySet()
        }
    }

    private fun lineBombArea(
        board: Match3Board,
        center: BoardPosition
    ): Set<BoardPosition> {
        val area = mutableSetOf<BoardPosition>()
        for (row in center.row - Match3SpecialConfig.LineBombCrossRadius..center.row + Match3SpecialConfig.LineBombCrossRadius) {
            if (row in 0 until board.rows) {
                for (column in 0 until board.columns) {
                    area.add(BoardPosition(row, column))
                }
            }
        }
        for (column in center.column - Match3SpecialConfig.LineBombCrossRadius..center.column + Match3SpecialConfig.LineBombCrossRadius) {
            if (column in 0 until board.columns) {
                for (row in 0 until board.rows) {
                    area.add(BoardPosition(row, column))
                }
            }
        }
        return area
    }

    private fun doubleBombArea(
        board: Match3Board,
        first: BoardPosition,
        second: BoardPosition
    ): Set<BoardPosition> {
        return positionsInRadius(board, first, Match3SpecialConfig.DoubleBombBlastRadius) +
            positionsInRadius(board, second, Match3SpecialConfig.DoubleBombBlastRadius)
    }

    private fun positionsInRadius(
        board: Match3Board,
        center: BoardPosition,
        radius: Int
    ): Set<BoardPosition> {
        val positions = mutableSetOf<BoardPosition>()
        for (row in center.row - radius..center.row + radius) {
            for (column in center.column - radius..center.column + radius) {
                val position = BoardPosition(row, column)
                if (board.contains(position)) {
                    positions.add(position)
                }
            }
        }
        return positions
    }

    private fun isLineClear(type: SpecialTileType): Boolean {
        return type == SpecialTileType.ROW_CLEAR || type == SpecialTileType.COLUMN_CLEAR
    }
}
