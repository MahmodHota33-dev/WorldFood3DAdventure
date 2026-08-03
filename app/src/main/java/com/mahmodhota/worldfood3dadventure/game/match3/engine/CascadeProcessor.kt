package com.mahmodhota.worldfood3dadventure.game.match3.engine

import com.mahmodhota.worldfood3dadventure.game.match3.Match3SpecialConfig
import com.mahmodhota.worldfood3dadventure.game.match3.model.*

/**
 * Result of the entire cascade sequence.
 */
data class CascadeResult(
    val finalBoard: Match3Board,
    val steps: List<CascadeStep>,
    val totalScore: Int,
    val totalMatchedTiles: Int,
    val collectedCounts: Map<FoodTileType, Int>
)

data class ForcedBoardResolution(
    val clearedPositions: Set<BoardPosition>,
    val createdSpecialTiles: Map<BoardPosition, SpecialTileType> = emptyMap(),
    val forcedSpecialOverrides: Map<BoardPosition, SpecialTileType> = emptyMap(),
    val forcedColorTargets: Map<BoardPosition, FoodTileType> = emptyMap(),
    val visualEffects: List<SpecialBoardEffect> = emptyList(),
    val scoreBonus: Int = 0
)

private data class StepResolution(
    val clearedPositions: Set<BoardPosition>,
    val createdSpecialTiles: Map<BoardPosition, SpecialTileType>,
    val specialEffects: List<SpecialBoardEffect>,
    val scoreBonus: Int
)

private data class ExpandedClearance(
    val clearedPositions: Set<BoardPosition>,
    val effects: List<SpecialBoardEffect>
)

/**
 * Processes recursive matches, gravity, and refills until the board is stable.
 */
class CascadeProcessor(
    private val refillEngine: RefillEngine,
    private val maxCascades: Int = 20
) {

    fun process(
        initialBoard: Match3Board,
        preferredMatchPositions: List<BoardPosition> = emptyList(),
        forcedInitialResolution: ForcedBoardResolution? = null
    ): CascadeResult {
        val steps = mutableListOf<CascadeStep>()
        var currentBoard = initialBoard
        var cascadeIndex = 1
        var totalScore = 0
        var totalMatched = 0
        val collected = mutableMapOf<FoodTileType, Int>()
        var pendingForcedResolution = forcedInitialResolution
        var pendingPreferredPositions = preferredMatchPositions

        while (cascadeIndex <= maxCascades) {
            val stepResolution = if (pendingForcedResolution != null) {
                val resolved = resolveForcedStep(currentBoard, pendingForcedResolution!!)
                pendingForcedResolution = null
                pendingPreferredPositions = emptyList()
                resolved
            } else {
                val matchResult = MatchDetector.findMatches(currentBoard, pendingPreferredPositions)
                pendingPreferredPositions = emptyList()
                if (!matchResult.hasMatches) break
                resolveMatchedStep(currentBoard, matchResult)
            }

            if (stepResolution.clearedPositions.isEmpty() && stepResolution.createdSpecialTiles.isEmpty()) {
                break
            }

            totalMatched += stepResolution.clearedPositions.size
            val scoreAwarded = calculateScore(stepResolution.clearedPositions.size, cascadeIndex) + stepResolution.scoreBonus
            totalScore += scoreAwarded

            stepResolution.clearedPositions.forEach { pos ->
                currentBoard.tileAt(pos)?.let { tile ->
                    collected[tile.type] = (collected[tile.type] ?: 0) + 1
                }
            }

            val intermediateMap = mutableMapOf<BoardPosition, FoodTile?>()
            for (r in 0 until currentBoard.rows) {
                for (c in 0 until currentBoard.columns) {
                    val pos = BoardPosition(r, c)
                    intermediateMap[pos] = if (stepResolution.clearedPositions.contains(pos)) {
                        null
                    } else {
                        currentBoard.tileAt(pos)
                    }
                }
            }

            stepResolution.createdSpecialTiles.forEach { (pos, type) ->
                val originalTile = currentBoard.tileAt(pos)
                if (originalTile != null) {
                    intermediateMap[pos] = originalTile.copy(specialType = type)
                }
            }

            val gravityResult = GravitySolver.applyGravityWithMetadata(currentBoard.rows, currentBoard.columns, intermediateMap)
            val refillResult = refillEngine.refillWithMetadata(currentBoard.rows, currentBoard.columns, gravityResult.tiles)

            val tileList = mutableListOf<FoodTile>()
            for (r in 0 until currentBoard.rows) {
                for (c in 0 until currentBoard.columns) {
                    tileList.add(refillResult.tiles[BoardPosition(r, c)]!!)
                }
            }

            currentBoard = Match3Board(currentBoard.rows, currentBoard.columns, tileList)
            steps.add(
                CascadeStep(
                    matchedPositions = stepResolution.clearedPositions,
                    boardAfterStep = currentBoard,
                    fallDistanceByTileId = gravityResult.fallDistanceByTileId,
                    refillTileIds = refillResult.newTileIds,
                    specialSpawnCount = stepResolution.createdSpecialTiles.size,
                    createdSpecialTiles = stepResolution.createdSpecialTiles,
                    specialEffects = stepResolution.specialEffects,
                    scoreAwarded = scoreAwarded
                )
            )

            cascadeIndex++
        }

        return CascadeResult(currentBoard, steps, totalScore, totalMatched, collected)
    }

    private fun resolveMatchedStep(board: Match3Board, matchResult: MatchResult): StepResolution {
        val createdSpecialTiles = matchResult.specialTilesToSpawn
        val baseClearedPositions = matchResult.uniquePositions - createdSpecialTiles.keys
        val expanded = expandSpecialClearance(board, baseClearedPositions)
        return StepResolution(
            clearedPositions = expanded.clearedPositions,
            createdSpecialTiles = createdSpecialTiles,
            specialEffects = expanded.effects,
            scoreBonus = createdSpecialTiles.size * Match3SpecialConfig.SpecialCreationScoreBonus
        )
    }

    private fun resolveForcedStep(
        board: Match3Board,
        resolution: ForcedBoardResolution
    ): StepResolution {
        val expanded = expandSpecialClearance(
            board = board,
            startingPositions = resolution.clearedPositions,
            forcedSpecialOverrides = resolution.forcedSpecialOverrides,
            forcedColorTargets = resolution.forcedColorTargets
        )
        return StepResolution(
            clearedPositions = expanded.clearedPositions,
            createdSpecialTiles = resolution.createdSpecialTiles,
            specialEffects = resolution.visualEffects + expanded.effects,
            scoreBonus = resolution.scoreBonus
        )
    }

    private fun calculateScore(matchedCount: Int, cascadeMultiplier: Int): Int {
        val baseScore = 100
        return matchedCount * baseScore * cascadeMultiplier
    }

    private fun expandSpecialClearance(
        board: Match3Board,
        startingPositions: Set<BoardPosition>,
        forcedSpecialOverrides: Map<BoardPosition, SpecialTileType> = emptyMap(),
        forcedColorTargets: Map<BoardPosition, FoodTileType> = emptyMap()
    ): ExpandedClearance {
        val totalCleared = startingPositions.toMutableSet()
        val toProcess = startingPositions
            .sortedWith(compareBy<BoardPosition>({ it.row }, { it.column }))
            .toMutableList()
        val processedSpecials = mutableSetOf<BoardPosition>()
        val effects = mutableListOf<SpecialBoardEffect>()

        var index = 0
        while (index < toProcess.size) {
            val pos = toProcess[index++]
            val tile = board.tileAt(pos) ?: continue
            val effectiveSpecial = forcedSpecialOverrides[pos] ?: tile.specialType

            if (effectiveSpecial != SpecialTileType.NONE && processedSpecials.add(pos)) {
                val effectArea = getSpecialEffectArea(
                    board = board,
                    pos = pos,
                    specialType = effectiveSpecial,
                    targetType = forcedColorTargets[pos] ?: tile.type
                )
                effects.add(buildEffect(effectiveSpecial, pos, effectArea, forcedColorTargets[pos] ?: tile.type))
                effectArea
                    .sortedWith(compareBy<BoardPosition>({ it.row }, { it.column }))
                    .forEach { effectPos ->
                        if (totalCleared.add(effectPos)) {
                            toProcess.add(effectPos)
                        }
                    }
            }
        }

        return ExpandedClearance(totalCleared, effects)
    }

    private fun buildEffect(
        specialType: SpecialTileType,
        origin: BoardPosition,
        affectedPositions: Set<BoardPosition>,
        affectedTileType: FoodTileType
    ): SpecialBoardEffect {
        val effectType = when (specialType) {
            SpecialTileType.ROW_CLEAR -> SpecialBoardEffectType.HORIZONTAL_LINE
            SpecialTileType.COLUMN_CLEAR -> SpecialBoardEffectType.VERTICAL_LINE
            SpecialTileType.BOMB -> SpecialBoardEffectType.BOMB
            SpecialTileType.COLOR_BOMB -> SpecialBoardEffectType.COLOR_CLEAR
            SpecialTileType.NONE -> SpecialBoardEffectType.BOMB
        }
        return SpecialBoardEffect(
            type = effectType,
            origin = origin,
            affectedPositions = affectedPositions,
            affectedTileType = if (specialType == SpecialTileType.COLOR_BOMB) affectedTileType else null
        )
    }

    private fun getSpecialEffectArea(
        board: Match3Board,
        pos: BoardPosition,
        specialType: SpecialTileType,
        targetType: FoodTileType
    ): Set<BoardPosition> {
        return when (specialType) {
            SpecialTileType.ROW_CLEAR -> {
                (0 until board.columns).map { BoardPosition(pos.row, it) }.toSet()
            }
            SpecialTileType.COLUMN_CLEAR -> {
                (0 until board.rows).map { BoardPosition(it, pos.column) }.toSet()
            }
            SpecialTileType.BOMB -> {
                positionsInRadius(board, pos, Match3SpecialConfig.BombBlastRadius)
            }
            SpecialTileType.COLOR_BOMB -> {
                board.allPositions().filter { board.tileAt(it)?.type == targetType }.toSet()
            }
            SpecialTileType.NONE -> emptySet()
        }
    }

    private fun positionsInRadius(
        board: Match3Board,
        center: BoardPosition,
        radius: Int
    ): Set<BoardPosition> {
        val area = mutableSetOf<BoardPosition>()
        for (r in center.row - radius..center.row + radius) {
            for (c in center.column - radius..center.column + radius) {
                val pos = BoardPosition(r, c)
                if (board.contains(pos)) {
                    area.add(pos)
                }
            }
        }
        return area
    }
}
