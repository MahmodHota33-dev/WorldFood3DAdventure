package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager
import com.mahmodhota.worldfood3dadventure.data.audio.SfxType
import com.mahmodhota.worldfood3dadventure.game.match3.Match3LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.match3.Match3SpecialConfig
import com.mahmodhota.worldfood3dadventure.game.match3.engine.CascadeResult
import com.mahmodhota.worldfood3dadventure.game.match3.engine.Match3Engine
import com.mahmodhota.worldfood3dadventure.game.match3.model.*
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import com.mahmodhota.worldfood3dadventure.ui.match3.components.Match3MotionTokens
import com.mahmodhota.worldfood3dadventure.telemetry.Match3Telemetry
import com.mahmodhota.worldfood3dadventure.telemetry.Match3TelemetryEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Detailed UI state for the Match-3 game, including animation tracking.
 */
enum class Match3AnimationPhase {
    Idle,
    Selecting,
    Swapping,
    Validating,
    RemovingMatches,
    ApplyingGravity,
    Refilling,
    CheckingCascade,
    InvalidReturning,
    Completed
}

data class Match3UiState(
    val board: Match3Board,
    val score: Int = 0,
    val movesRemaining: Int = 20,
    val collectedCounts: Map<FoodTileType, Int> = emptyMap(),
    val status: GameStatus = GameStatus.PLAYING,
    val selectedPosition: BoardPosition? = null,
    val isAnimating: Boolean = false,
    val matchedPositions: Set<BoardPosition> = emptySet(),
    val goals: List<LevelGoal> = emptyList(),
    val scoreThresholds: StarThresholds = StarThresholds(800, 1200, 1500),
    val comboCount: Int = 0,
    val animationPhase: Match3AnimationPhase = Match3AnimationPhase.Idle,
    val activeTileAnimationIds: Set<Long> = emptySet(),
    val fallDistanceByTileId: Map<Long, Int> = emptyMap(),
    val refillTileIds: Set<Long> = emptySet(),
    val landingTileIds: Set<Long> = emptySet(),
    val comboLabel: String? = null,
    val boardShakeNonce: Int = 0,
    val boardShakeEnabled: Boolean = false,
    val specialEffects: List<SpecialBoardEffect> = emptyList(),
    val boosterInventory: BoosterInventory = BoosterInventory(),
    val selectedBooster: BoosterType? = null,
    val floatingScoreText: String? = null,
    val floatingScoreNonce: Int = 0,
    val floatingScoreAnchor: BoardPosition? = null,
    val specialEffectLabel: String? = null,
    val specialEffectNonce: Int = 0,
    val goalPulseType: FoodTileType? = null,
    val goalPulseNonce: Int = 0,
    val activatedBooster: BoosterType? = null,
    val boosterActivationNonce: Int = 0,
    val spawnedSpecialTiles: Map<BoardPosition, SpecialTileType> = emptyMap(),
    val spawnedSpecialNonce: Int = 0,
    val reshuffleNonce: Int = 0
)

internal fun shouldIgnoreTileSelectionInput(isAnimating: Boolean, status: GameStatus): Boolean {
    return isAnimating || status != GameStatus.PLAYING
}

internal fun canAcceptBoosterRequest(
    isAnimating: Boolean,
    status: GameStatus,
    pendingBooster: BoosterType?,
    inventoryCount: Int
): Boolean {
    if (isAnimating || status != GameStatus.PLAYING) return false
    if (pendingBooster != null) return false
    return inventoryCount > 0
}

internal fun shouldGrantFirstClearReward(rewardedInThisSession: Boolean): Boolean {
    return !rewardedInThisSession
}

internal fun shouldEvaluateGameStatus(status: GameStatus): Boolean {
    return status == GameStatus.PLAYING
}

internal fun movesDeltaForSwapResult(result: SwapResult): Int {
    return if (result is SwapResult.Success) -1 else 0
}

class Match3ViewModel(
    val countryId: String,
    val levelNumber: Int,
    private val engineFactory: (List<FoodTileType>) -> Match3Engine = { Match3Engine(allowedTiles = it) },
    private val playSfx: (SfxType) -> Unit = { GlobalSystemManager.audio.playSfx(it) },
    private val hapticLight: () -> Unit = { GlobalSystemManager.haptics.light() },
    private val hapticMedium: () -> Unit = { GlobalSystemManager.haptics.medium() },
    private val hapticHeavy: () -> Unit = { GlobalSystemManager.haptics.heavy() },
    private val boosterInventoryProvider: () -> BoosterInventory = { ProgressionManager.boosterInventory },
    private val consumeBooster: suspend (BoosterType) -> BoosterInventory? = { ProgressionManager.consumeBooster(it) },
    private val completeLevelProgress: (String, Int, Int, Int) -> Unit = { country, level, score, stars ->
        ProgressionManager.completeLevel(country, level, score, stars)
    }
) : ViewModel() {
    companion object {
        private const val DefaultBoardRows = 8
        private const val DefaultBoardColumns = 8
    }

    private val levelDefinition = Match3LevelRegistry.getLevel(countryId, levelNumber)
        ?: throw IllegalArgumentException("Invalid level: $countryId $levelNumber")

    private val engine = engineFactory(levelDefinition.allowedTiles)
    private val telemetryLevelId = Match3Telemetry.levelId(countryId, levelNumber)
    private var resolutionJob: Job? = null
    private var boardInitJob: Job? = null
    private var victoryFeedbackJob: Job? = null
    private var pendingBooster: BoosterType? = null
    private var rewardedInThisSession = false

    var uiState by mutableStateOf(
        Match3UiState(
            board = createBootstrapBoard(
                rows = DefaultBoardRows,
                cols = DefaultBoardColumns,
                allowedTiles = levelDefinition.allowedTiles
            ),
            movesRemaining = levelDefinition.moves,
            goals = levelDefinition.goals,
            scoreThresholds = levelDefinition.scoreThresholds,
            boosterInventory = boosterInventoryProvider(),
            isAnimating = true,
            animationPhase = Match3AnimationPhase.Validating
        )
    )
        private set

    init {
        initializeBoardAsync()
    }
    
    /**
     * Atomically cancel existing resolution job and start a new one.
     * Prevents overlapping swap/cascade/booster jobs.
     */
    private fun launchResolutionJob(block: suspend () -> Unit) {
        resolutionJob?.cancel()
        resolutionJob = viewModelScope.launch {
            try {
                block()
            } finally {
                if (uiState.isAnimating) {
                    clearTransientAnimationState(isAnimating = false)
                }
            }
        }
    }

    private fun initializeBoardAsync() {
        boardInitJob?.cancel()
        boardInitJob = viewModelScope.launch {
            val generatedBoard = withContext(Dispatchers.Default) {
                engine.createStartBoard(DefaultBoardRows, DefaultBoardColumns)
            }
            uiState = Match3UiState(
                board = generatedBoard,
                movesRemaining = levelDefinition.moves,
                goals = levelDefinition.goals,
                scoreThresholds = levelDefinition.scoreThresholds,
                boosterInventory = boosterInventoryProvider()
            )
        }
    }

    private fun createBootstrapBoard(
        rows: Int,
        cols: Int,
        allowedTiles: List<FoodTileType>
    ): Match3Board {
        val safeTiles = allowedTiles.ifEmpty { FoodTileType.values().toList() }
        val tiles = mutableListOf<FoodTile>()
        var tileId = 1L
        for (row in 0 until rows) {
            for (column in 0 until cols) {
                val type = safeTiles[(row + (column * 2)) % safeTiles.size]
                tiles.add(FoodTile(id = tileId++, type = type))
            }
        }
        return Match3Board(rows, cols, tiles)
    }

    fun onTileSelected(position: BoardPosition) {
        if (shouldIgnoreTileSelectionInput(uiState.isAnimating, uiState.status)) return

        if (uiState.selectedBooster == BoosterType.HAMMER) {
            useHammer(position)
            return
        }

        playSfx(SfxType.TILE_SELECT)
        hapticLight()

        val selected = uiState.selectedPosition
        if (selected == null) {
            uiState = uiState.copy(
                selectedPosition = position,
                animationPhase = Match3AnimationPhase.Selecting
            )
        } else {
            if (selected == position) {
                uiState = uiState.copy(
                    selectedPosition = null,
                    animationPhase = Match3AnimationPhase.Idle
                )
            } else if (selected.isAdjacent(position)) {
                performSwap(selected, position)
            } else {
                uiState = uiState.copy(
                    selectedPosition = position,
                    animationPhase = Match3AnimationPhase.Selecting
                )
            }
        }
    }

    fun onBoosterSelected(type: BoosterType) {
        if (!canAcceptBoosterRequest(
                isAnimating = uiState.isAnimating,
                status = uiState.status,
                pendingBooster = pendingBooster,
                inventoryCount = uiState.boosterInventory.countFor(type)
            )
        ) return

        when (type) {
            BoosterType.HAMMER -> {
                playSfx(SfxType.BUTTON_CLICK)
                hapticLight()
                uiState = uiState.copy(
                    selectedBooster = if (uiState.selectedBooster == type) null else type,
                    selectedPosition = null,
                    animationPhase = Match3AnimationPhase.Idle
                )
            }
            BoosterType.SHUFFLE -> {
                pendingBooster = BoosterType.SHUFFLE
                useShuffle()
            }
            BoosterType.EXTRA_MOVES -> {
                pendingBooster = BoosterType.EXTRA_MOVES
                useExtraMoves()
            }
        }
    }

    private fun performSwap(pos1: BoardPosition, pos2: BoardPosition) {
        launchResolutionJob {
            // Safety net: if this coroutine terminates for ANY reason (exception,
            // cancellation, or normal flow) while the board is locked, unlock it.
            // Normal completion paths set isAnimating = false themselves, so the
            // condition below is only true on unexpected termination.
            Match3Telemetry.log(
                event = Match3TelemetryEvent.MOVE_START,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = uiState.comboCount,
                cascadeCount = 0,
                detail = "from=$pos1 to=$pos2"
            )
            val firstId = uiState.board.tileAt(pos1)?.id
            val secondId = uiState.board.tileAt(pos2)?.id
            val swappedIds = setOfNotNull(firstId, secondId)
            uiState = uiState.copy(
                isAnimating = true,
                selectedPosition = null,
                selectedBooster = null,
                animationPhase = Match3AnimationPhase.Swapping,
                activeTileAnimationIds = swappedIds,
                fallDistanceByTileId = emptyMap(),
                refillTileIds = emptySet(),
                landingTileIds = emptySet(),
                specialEffects = emptyList(),
                specialEffectLabel = null,
                spawnedSpecialTiles = emptyMap()
            )

            val result = withContext(Dispatchers.Default) { engine.performSwap(uiState.board, pos1, pos2) }
            when (result) {
                is SwapResult.Success -> {
                    Match3Telemetry.log(
                        event = Match3TelemetryEvent.MOVE_VALID,
                        levelId = telemetryLevelId,
                        countryId = countryId,
                        remainingMoves = uiState.movesRemaining,
                        score = uiState.score,
                        comboCount = uiState.comboCount,
                        cascadeCount = result.cascadeSteps.size,
                        detail = "matched=${result.totalMatchedTiles} score=${result.scoreGained}"
                    )
                    playSfx(SfxType.SWAP_VALID)
                    hapticLight()
                    uiState = uiState.copy(board = result.swappedBoard)
                    delay(Match3MotionTokens.SwapDurationMs.toLong())
                    playCascadeResult(
                        result.stableBoard,
                        result.cascadeSteps,
                        result.scoreGained,
                        result.collectedCounts,
                        movesDelta = movesDeltaForSwapResult(result),
                        reshuffled = result.wasReshuffled
                    )
                }
                else -> {
                    Match3Telemetry.log(
                        event = Match3TelemetryEvent.MOVE_INVALID,
                        levelId = telemetryLevelId,
                        countryId = countryId,
                        remainingMoves = uiState.movesRemaining,
                        score = uiState.score,
                        comboCount = uiState.comboCount,
                        cascadeCount = 0,
                        detail = "from=$pos1 to=$pos2"
                    )
                    playSfx(SfxType.SWAP_INVALID)
                    hapticMedium()
                    val swapped = uiState.board.swap(pos1, pos2)
                    uiState = uiState.copy(
                        animationPhase = Match3AnimationPhase.Swapping,
                        board = swapped
                    )
                    delay(Match3MotionTokens.InvalidSwapOutDurationMs.toLong())
                    uiState = uiState.copy(
                        animationPhase = Match3AnimationPhase.InvalidReturning,
                        board = swapped.swap(pos1, pos2)
                    )
                    delay(Match3MotionTokens.InvalidSwapReturnDurationMs.toLong())
                    clearTransientAnimationState(isAnimating = false)
                }
            }
        }
    }

    private fun useHammer(position: BoardPosition) {
        launchResolutionJob {
            val updatedInventory = consumeBooster(BoosterType.HAMMER) ?: return@launchResolutionJob
            Match3Telemetry.log(
                event = Match3TelemetryEvent.BOOSTER_USED,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = uiState.comboCount,
                cascadeCount = 0,
                detail = "type=HAMMER"
            )
            Match3Telemetry.log(
                event = Match3TelemetryEvent.HAMMER_USED,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = uiState.comboCount,
                cascadeCount = 0,
                detail = "target=$position"
            )
            playSfx(SfxType.MATCH_SMALL)
            hapticMedium()
            uiState = uiState.copy(
                boosterInventory = updatedInventory,
                selectedBooster = null,
                selectedPosition = null,
                isAnimating = true,
                animationPhase = Match3AnimationPhase.RemovingMatches,
                matchedPositions = setOf(position),
                specialEffects = emptyList(),
                boardShakeNonce = uiState.boardShakeNonce + 1,
                boardShakeEnabled = true,
                activatedBooster = BoosterType.HAMMER,
                boosterActivationNonce = uiState.boosterActivationNonce + 1,
                spawnedSpecialTiles = emptyMap()
            )
            delay(Match3MotionTokens.MatchPopDurationMs.toLong())
            val result = withContext(Dispatchers.Default) { engine.applyHammer(uiState.board, position) }
            if (result == null) {
                clearTransientAnimationState(isAnimating = false)
                return@launchResolutionJob
            }
            playCascadeResult(
                result.finalBoard,
                result.steps,
                result.totalScore,
                result.collectedCounts,
                movesDelta = 0,
                reshuffled = result.wasReshuffled
            )
        }
    }

    private fun useShuffle() {
        launchResolutionJob {
            val updatedInventory = consumeBooster(BoosterType.SHUFFLE) ?: return@launchResolutionJob
            Match3Telemetry.log(
                event = Match3TelemetryEvent.BOOSTER_USED,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = uiState.comboCount,
                cascadeCount = 0,
                detail = "type=SHUFFLE"
            )
            Match3Telemetry.log(
                event = Match3TelemetryEvent.SHUFFLE_USED,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = uiState.comboCount,
                cascadeCount = 0
            )
            val shuffledBoard = withContext(Dispatchers.Default) { engine.shuffleBoard(uiState.board) }
            playSfx(SfxType.CASCADE)
            hapticMedium()
            uiState = uiState.copy(
                board = shuffledBoard,
                boosterInventory = updatedInventory,
                selectedBooster = null,
                selectedPosition = null,
                boardShakeNonce = uiState.boardShakeNonce + 1,
                boardShakeEnabled = true,
                activatedBooster = BoosterType.SHUFFLE,
                boosterActivationNonce = uiState.boosterActivationNonce + 1,
                spawnedSpecialTiles = emptyMap()
            )
            delay(Match3SpecialConfig.SpecialComboDurationMs.toLong())
            uiState = uiState.copy(boardShakeEnabled = false)
            pendingBooster = null
        }
    }

    private fun useExtraMoves() {
        launchResolutionJob {
            val updatedInventory = consumeBooster(BoosterType.EXTRA_MOVES) ?: return@launchResolutionJob
            Match3Telemetry.log(
                event = Match3TelemetryEvent.BOOSTER_USED,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = uiState.comboCount,
                cascadeCount = 0,
                detail = "type=EXTRA_MOVES"
            )
            Match3Telemetry.log(
                event = Match3TelemetryEvent.EXTRA_MOVES_USED,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = uiState.comboCount,
                cascadeCount = 0,
                detail = "bonus=${Match3SpecialConfig.ExtraMovesBoostAmount}"
            )
            playSfx(SfxType.STAR_EARNED)
            hapticLight()
            uiState = uiState.copy(
                movesRemaining = uiState.movesRemaining + Match3SpecialConfig.ExtraMovesBoostAmount,
                boosterInventory = updatedInventory,
                selectedBooster = null,
                selectedPosition = null,
                activatedBooster = BoosterType.EXTRA_MOVES,
                boosterActivationNonce = uiState.boosterActivationNonce + 1,
                isAnimating = false,
                animationPhase = Match3AnimationPhase.Idle
            )
            pendingBooster = null
        }
    }

    private suspend fun playCascadeResult(
        finalBoard: Match3Board,
        cascadeSteps: List<CascadeStep>,
        scoreGained: Int,
        collectedCounts: Map<FoodTileType, Int>,
        movesDelta: Int,
        reshuffled: Boolean = false
    ) {
        Match3Telemetry.log(
            event = Match3TelemetryEvent.CASCADE_START,
            levelId = telemetryLevelId,
            countryId = countryId,
            remainingMoves = uiState.movesRemaining,
            score = uiState.score,
            comboCount = uiState.comboCount,
            cascadeCount = cascadeSteps.size
        )
        cascadeSteps.forEachIndexed { index, step ->
            val feedback = resolveCascadeFeedback(
                stepIndex = index,
                matchedCount = step.matchedPositions.size,
                specialSpawnCount = step.specialSpawnCount,
                specialEffects = step.specialEffects
            )
            val impactfulStep = step.specialSpawnCount > 0 ||
                step.specialEffects.isNotEmpty() ||
                step.matchedPositions.size >= 5
            val pulseGoalType = step.matchedPositions
                .asSequence()
                .mapNotNull { pos -> uiState.board.tileAt(pos)?.type }
                .firstOrNull { type ->
                    uiState.goals.any { goal -> goal is LevelGoal.CollectFood && goal.type == type }
                }
            val hasScoreGoal = uiState.goals.any { it is LevelGoal.ScoreTarget }
            val shouldPulseGoal = pulseGoalType != null || hasScoreGoal
            val goalPulseNonce = if (shouldPulseGoal) uiState.goalPulseNonce + 1 else uiState.goalPulseNonce
            val floatingScoreNonce = uiState.floatingScoreNonce + 1
            val specialEffectLabel = specialEffectEventLabel(step.specialEffects)
            val specialEffectNonce = if (specialEffectLabel != null) uiState.specialEffectNonce + 1 else uiState.specialEffectNonce

            when (feedback.haptic) {
                HapticFeedbackStrength.LIGHT -> hapticLight()
                HapticFeedbackStrength.MEDIUM -> hapticMedium()
                HapticFeedbackStrength.HEAVY -> hapticHeavy()
                HapticFeedbackStrength.NONE -> Unit
            }
            playSfx(feedback.sfxType)

            Match3Telemetry.log(
                event = Match3TelemetryEvent.MATCH_FOUND,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = index + 1,
                cascadeCount = index + 1,
                detail = "tiles=${step.matchedPositions.size}"
            )
            Match3Telemetry.log(
                event = Match3TelemetryEvent.MATCH_COUNT,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = index + 1,
                cascadeCount = index + 1,
                detail = "count=${step.matchedPositions.size}"
            )
            if (step.specialSpawnCount > 0) {
                Match3Telemetry.log(
                    event = Match3TelemetryEvent.SPECIAL_CREATED,
                    levelId = telemetryLevelId,
                    countryId = countryId,
                    remainingMoves = uiState.movesRemaining,
                    score = uiState.score,
                    comboCount = index + 1,
                    cascadeCount = index + 1,
                    detail = "count=${step.specialSpawnCount}"
                )
            }
            if (step.specialEffects.isNotEmpty()) {
                Match3Telemetry.log(
                    event = Match3TelemetryEvent.SPECIAL_ACTIVATED,
                    levelId = telemetryLevelId,
                    countryId = countryId,
                    remainingMoves = uiState.movesRemaining,
                    score = uiState.score,
                    comboCount = index + 1,
                    cascadeCount = index + 1,
                    detail = "effects=${step.specialEffects.size}"
                )
            }
            uiState = uiState.copy(
                animationPhase = Match3AnimationPhase.RemovingMatches,
                matchedPositions = step.matchedPositions,
                comboCount = index + 1,
                comboLabel = feedback.comboLabel,
                activeTileAnimationIds = emptySet(),
                fallDistanceByTileId = emptyMap(),
                refillTileIds = emptySet(),
                landingTileIds = emptySet(),
                specialEffects = step.specialEffects,
                boardShakeNonce = if (impactfulStep) uiState.boardShakeNonce + 1 else uiState.boardShakeNonce,
                boardShakeEnabled = impactfulStep,
                floatingScoreText = floatingScoreLabel(step.scoreAwarded, index + 1),
                floatingScoreNonce = floatingScoreNonce,
                floatingScoreAnchor = scoreAnchorPosition(step.matchedPositions),
                specialEffectLabel = specialEffectLabel,
                specialEffectNonce = specialEffectNonce,
                goalPulseType = pulseGoalType,
                goalPulseNonce = goalPulseNonce,
                spawnedSpecialTiles = emptyMap()
            )
            delay(Match3MotionTokens.MatchPopDurationMs.toLong())
            uiState = uiState.copy(
                matchedPositions = emptySet(),
                specialEffects = emptyList(),
                goalPulseType = null
            )

            Match3Telemetry.log(
                event = Match3TelemetryEvent.REFILL_START,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = index + 1,
                cascadeCount = index + 1,
                detail = "newTiles=${step.refillTileIds.size}"
            )
            val landingTileIds = (step.fallDistanceByTileId.keys + step.refillTileIds).toSet()
            uiState = uiState.copy(
                animationPhase = Match3AnimationPhase.ApplyingGravity,
                board = step.boardAfterStep,
                fallDistanceByTileId = step.fallDistanceByTileId,
                refillTileIds = step.refillTileIds,
                landingTileIds = landingTileIds,
                boardShakeEnabled = false,
                spawnedSpecialTiles = step.createdSpecialTiles,
                spawnedSpecialNonce = if (step.createdSpecialTiles.isNotEmpty()) {
                    uiState.spawnedSpecialNonce + 1
                } else {
                    uiState.spawnedSpecialNonce
                }
            )

            val maxDrop = step.fallDistanceByTileId.values.maxOrNull() ?: 0
            val fallDuration = Match3MotionTokens.fallDurationForRows(maxDrop)
            val refillDuration = if (step.refillTileIds.isNotEmpty()) Match3MotionTokens.RefillDurationMs else 0
            val settleDuration = maxOf(fallDuration, refillDuration).coerceAtLeast(36)
            delay(settleDuration.toLong())
            Match3Telemetry.log(
                event = Match3TelemetryEvent.REFILL_END,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = index + 1,
                cascadeCount = index + 1
            )

            uiState = uiState.copy(animationPhase = Match3AnimationPhase.CheckingCascade)
            delay(Match3MotionTokens.CascadePauseMs.toLong())
        }

        val newCollected = uiState.collectedCounts.toMutableMap()
        collectedCounts.forEach { (type, count) ->
            newCollected[type] = (newCollected[type] ?: 0) + count
        }

        uiState = uiState.copy(
            board = finalBoard,
            score = uiState.score + scoreGained,
            movesRemaining = (uiState.movesRemaining + movesDelta).coerceAtLeast(0),
            collectedCounts = newCollected,
            boosterInventory = boosterInventoryProvider(),
            isAnimating = false,
            comboCount = 0,
            animationPhase = Match3AnimationPhase.Idle,
            activeTileAnimationIds = emptySet(),
            fallDistanceByTileId = emptyMap(),
            refillTileIds = emptySet(),
            landingTileIds = emptySet(),
            comboLabel = null,
            boardShakeEnabled = reshuffled,
            boardShakeNonce = if (reshuffled) uiState.boardShakeNonce + 1 else uiState.boardShakeNonce,
            reshuffleNonce = if (reshuffled) uiState.reshuffleNonce + 1 else uiState.reshuffleNonce,
            specialEffects = emptyList(),
            selectedBooster = null,
            floatingScoreText = null,
            floatingScoreNonce = 0,
            floatingScoreAnchor = null,
            specialEffectLabel = null,
            specialEffectNonce = 0,
            goalPulseType = null,
            goalPulseNonce = 0,
            activatedBooster = null,
            boosterActivationNonce = 0,
            spawnedSpecialTiles = emptyMap(),
            spawnedSpecialNonce = 0
        )

        if (reshuffled) {
            playSfx(SfxType.CASCADE) // Or use a shuffle sfx if available
            hapticMedium()
            delay(Match3MotionTokens.BoardShakeDurationMs.toLong())
            uiState = uiState.copy(boardShakeEnabled = false)
        }

        Match3Telemetry.log(
            event = Match3TelemetryEvent.BOARD_STABLE,
            levelId = telemetryLevelId,
            countryId = countryId,
            remainingMoves = uiState.movesRemaining,
            score = uiState.score,
            comboCount = uiState.comboCount,
            cascadeCount = cascadeSteps.size
        )
        Match3Telemetry.log(
            event = Match3TelemetryEvent.CASCADE_END,
            levelId = telemetryLevelId,
            countryId = countryId,
            remainingMoves = uiState.movesRemaining,
            score = uiState.score,
            comboCount = uiState.comboCount,
            cascadeCount = cascadeSteps.size
        )

        checkGameStatus()
    }

    private fun checkGameStatus() {
        if (!shouldEvaluateGameStatus(uiState.status)) return

        val won = uiState.goals.all { goal ->
            when (goal) {
                is LevelGoal.ScoreTarget -> uiState.score >= goal.target
                is LevelGoal.CollectFood -> (uiState.collectedCounts[goal.type] ?: 0) >= goal.amount
            }
        }

        if (won) {
            val victorySfx = when (countryId) {
                "italy" -> SfxType.ITALY_VICTORY
                "japan" -> SfxType.JAPAN_VICTORY
                "mexico" -> SfxType.MEXICO_VICTORY
                else -> SfxType.VICTORY
            }
            Match3Telemetry.log(
                event = Match3TelemetryEvent.LEVEL_WIN,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = uiState.comboCount,
                cascadeCount = 0
            )
            playSfx(victorySfx)
            hapticHeavy()
            victoryFeedbackJob?.cancel()
            victoryFeedbackJob = viewModelScope.launch {
                delay(Match3MotionTokens.VictoryRewardDelayMs)
                playSfx(SfxType.STAR_EARNED)
                hapticMedium()
                delay(Match3MotionTokens.VictoryXpDelayMs)
                playSfx(SfxType.XP_GAINED)
                hapticLight()
            }
            uiState = uiState.copy(status = GameStatus.WON, animationPhase = Match3AnimationPhase.Completed)
            val stars = when {
                uiState.score >= uiState.scoreThresholds.threeStars -> 3
                uiState.score >= uiState.scoreThresholds.twoStars -> 2
                else -> 1
            }
            Match3Telemetry.log(
                event = Match3TelemetryEvent.REWARD_GRANTED,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = uiState.comboCount,
                cascadeCount = 0,
                detail = "stars=$stars"
            )
            if (shouldGrantFirstClearReward(rewardedInThisSession)) {
                rewardedInThisSession = true
                completeLevelProgress(countryId, levelNumber, uiState.score, stars)
            }
        } else if (uiState.movesRemaining <= 0) {
            Match3Telemetry.log(
                event = Match3TelemetryEvent.LEVEL_LOSE,
                levelId = telemetryLevelId,
                countryId = countryId,
                remainingMoves = uiState.movesRemaining,
                score = uiState.score,
                comboCount = uiState.comboCount,
                cascadeCount = 0
            )
            playSfx(SfxType.DEFEAT)
            uiState = uiState.copy(status = GameStatus.LOST, animationPhase = Match3AnimationPhase.Completed)
        }
    }

    fun resetGame() {
        resolutionJob?.cancel()
        boardInitJob?.cancel()
        victoryFeedbackJob?.cancel()
        pendingBooster = null
        rewardedInThisSession = false
        uiState = uiState.copy(
            score = 0,
            movesRemaining = levelDefinition.moves,
            collectedCounts = emptyMap(),
            status = GameStatus.PLAYING,
            selectedPosition = null,
            isAnimating = true,
            matchedPositions = emptySet(),
            goals = levelDefinition.goals,
            scoreThresholds = levelDefinition.scoreThresholds,
            comboCount = 0,
            animationPhase = Match3AnimationPhase.Validating,
            activeTileAnimationIds = emptySet(),
            fallDistanceByTileId = emptyMap(),
            refillTileIds = emptySet(),
            landingTileIds = emptySet(),
            comboLabel = null,
            boardShakeEnabled = false,
            specialEffects = emptyList(),
            boosterInventory = boosterInventoryProvider(),
            selectedBooster = null,
            floatingScoreText = null,
            floatingScoreNonce = 0,
            floatingScoreAnchor = null,
            specialEffectLabel = null,
            specialEffectNonce = 0,
            goalPulseType = null,
            goalPulseNonce = 0,
            activatedBooster = null,
            boosterActivationNonce = 0,
            spawnedSpecialTiles = emptyMap(),
            spawnedSpecialNonce = 0
        )
        initializeBoardAsync()
    }

    fun onScreenExit() {
        resolutionJob?.cancel()
        boardInitJob?.cancel()
        victoryFeedbackJob?.cancel()
        pendingBooster = null
        clearTransientAnimationState(isAnimating = false)
    }

    override fun onCleared() {
        resolutionJob?.cancel()
        boardInitJob?.cancel()
        victoryFeedbackJob?.cancel()
        super.onCleared()
    }

    private fun clearTransientAnimationState(isAnimating: Boolean) {
        uiState = uiState.resetTransientUi(isAnimating)
    }
}
