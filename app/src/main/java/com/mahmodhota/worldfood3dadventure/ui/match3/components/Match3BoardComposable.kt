package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.match3.Match3SpecialConfig
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoardPosition
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3Board
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialBoardEffect
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialBoardEffectType
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialTileType
import com.mahmodhota.worldfood3dadventure.ui.match3.Match3AnimationPhase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Renders the interactive Match-3 board with animated tiles.
 */
@Composable
fun Match3BoardComposable(
    board: Match3Board,
    selectedPosition: BoardPosition?,
    matchedPositions: Set<BoardPosition>,
    onTileClick: (BoardPosition) -> Unit,
    modifier: Modifier = Modifier,
    comboCount: Int = 0,
    animationPhase: Match3AnimationPhase = Match3AnimationPhase.Idle,
    activeTileAnimationIds: Set<Long> = emptySet(),
    fallDistanceByTileId: Map<Long, Int> = emptyMap(),
    refillTileIds: Set<Long> = emptySet(),
    landingTileIds: Set<Long> = emptySet(),
    comboLabel: String? = null,
    boardShakeNonce: Int = 0,
    boardShakeEnabled: Boolean = false,
    specialEffects: List<SpecialBoardEffect> = emptyList(),
    floatingScoreText: String? = null,
    floatingScoreNonce: Int = 0,
    floatingScoreAnchor: BoardPosition? = null,
    specialEffectLabel: String? = null,
    specialEffectNonce: Int = 0,
    spawnedSpecialTiles: Map<BoardPosition, SpecialTileType> = emptyMap(),
    spawnedSpecialNonce: Int = 0,
    reshuffleNonce: Int = 0,
    hintedPositions: Set<BoardPosition> = emptySet()
) {
    var previousBoard by remember { mutableStateOf<Match3Board?>(null) }
    val boardShakeOffsetPx = remember { Animatable(0f) }
    val specialEffectProgress = remember { Animatable(0f) }
    val spawnedSpecialProgress = remember { Animatable(0f) }
    val matchGlowProgress = remember { Animatable(0f) }
    val specialFlashAlpha = remember { Animatable(0f) }
    
    LaunchedEffect(specialEffects) {
        if (specialEffects.isNotEmpty()) {
            specialFlashAlpha.snapTo(0.12f)
            specialFlashAlpha.animateTo(0f, tween(350))
        }
    }

    val hintPulseProgress = rememberInfiniteTransition(label = "hintPulse")
    val hintScale by hintPulseProgress.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hintScale"
    )
    var showReshuffleLabel by remember { mutableStateOf(false) }

    LaunchedEffect(reshuffleNonce) {
        if (reshuffleNonce > 0) {
            showReshuffleLabel = true
            delay(1500)
            showReshuffleLabel = false
        }
    }

    LaunchedEffect(boardShakeNonce, boardShakeEnabled) {
        if (boardShakeEnabled && boardShakeNonce > 0) {
            val shake = Match3MotionTokens.BoardShakeOffsetDp * 3f
            boardShakeOffsetPx.snapTo(0f)
            boardShakeOffsetPx.animateTo(shake, tween(Match3MotionTokens.BoardShakeDurationMs / 3, easing = Match3MotionTokens.SwapEasing))
            boardShakeOffsetPx.animateTo(-shake, tween(Match3MotionTokens.BoardShakeDurationMs / 3, easing = Match3MotionTokens.SwapEasing))
            boardShakeOffsetPx.animateTo(0f, tween(Match3MotionTokens.BoardShakeDurationMs / 3, easing = Match3MotionTokens.SwapEasing))
        } else {
            boardShakeOffsetPx.snapTo(0f)
        }
    }

    LaunchedEffect(specialEffects) {
        if (specialEffects.isEmpty()) {
            specialEffectProgress.snapTo(0f)
        } else {
            val duration = specialEffects.maxOf { effect ->
                when (effect.type) {
                    SpecialBoardEffectType.HORIZONTAL_LINE,
                    SpecialBoardEffectType.VERTICAL_LINE -> Match3SpecialConfig.LineClearDurationMs
                    SpecialBoardEffectType.BOMB -> Match3SpecialConfig.BombDurationMs
                    SpecialBoardEffectType.COLOR_CLEAR -> Match3SpecialConfig.ColorClearDurationMs
                }
            }
            specialEffectProgress.snapTo(0f)
            specialEffectProgress.animateTo(1f, tween(duration))
        }
    }

    LaunchedEffect(spawnedSpecialNonce, spawnedSpecialTiles) {
        if (spawnedSpecialNonce <= 0 || spawnedSpecialTiles.isEmpty()) {
            spawnedSpecialProgress.snapTo(0f)
        } else {
            spawnedSpecialProgress.snapTo(0f)
            spawnedSpecialProgress.animateTo(1f, tween(durationMillis = 260))
        }
    }

    LaunchedEffect(matchedPositions) {
        if (matchedPositions.isEmpty()) {
            matchGlowProgress.snapTo(0f)
        } else {
            matchGlowProgress.snapTo(0f)
            matchGlowProgress.animateTo(1f, tween(durationMillis = Match3MotionTokens.MatchPopDurationMs))
        }
    }

    FixedCoordinateSurface {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .padding(1.dp),
            contentAlignment = Alignment.Center
        ) {
            val boardPadding = 0.dp
            val maxAvailableWidth = maxWidth - (boardPadding * 2)
            val maxAvailableHeight = maxHeight - (boardPadding * 2)
            val boardSize = minOf(maxAvailableWidth, maxAvailableHeight).coerceAtLeast(0.dp)

        if (boardSize > 0.dp) {
            Box(contentAlignment = Alignment.Center) {
                val snapshot = remember(
                    previousBoard,
                    board,
                    selectedPosition,
                    matchedPositions,
                    animationPhase,
                    activeTileAnimationIds,
                    fallDistanceByTileId,
                    refillTileIds,
                    landingTileIds
                ) {
                    buildBoardAnimationSnapshot(
                        previousBoard = previousBoard,
                        currentBoard = board,
                        selectedPosition = selectedPosition,
                        matchedPositions = matchedPositions,
                        phase = animationPhase,
                        activeTileAnimationIds = activeTileAnimationIds,
                        fallDistanceByTileId = fallDistanceByTileId,
                        refillTileIds = refillTileIds,
                        landingTileIds = landingTileIds
                    )
                }
                val tileEffectByPosition = remember(specialEffects) {
                    buildMap {
                        specialEffects.forEach { effect ->
                            effect.affectedPositions.forEach { pos ->
                                if (!containsKey(pos)) {
                                    put(pos, effect.type)
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(boardSize)
                        .offset { IntOffset(boardShakeOffsetPx.value.roundToInt(), 0) }
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(PremiumColors.DarkSlate.copy(alpha = 0.88f), PremiumColors.DeepNavy.copy(alpha = 0.88f))
                            )
                        )
                        .border(1.5.dp, PremiumColors.Gold.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                        .padding(1.dp)
                        .clipToBounds()
                        .pointerInput(board, boardSize) {
                            val boardSizePx = boardSize.toPx()
                            val tileExtentPx = boardSizePx / board.columns
                            detectTapGestures { offset ->
                                val clampedX = offset.x.coerceIn(0f, boardSizePx - 1f)
                                val clampedY = offset.y.coerceIn(0f, boardSizePx - 1f)
                                val col = (clampedX / tileExtentPx).toInt()
                                val row = (clampedY / tileExtentPx).toInt()
                                if (row in 0 until board.rows && col in 0 until board.columns) {
                                    android.util.Log.d("Match3Board", "Tap detected at ($row, $col)")
                                    onTileClick(BoardPosition(row, col))
                                }
                            }
                        }
                        .pointerInput(board, boardSize) {
                            val boardSizePx = boardSize.toPx()
                            val tileExtentPx = boardSizePx / board.columns
                            var dragOrigin: BoardPosition? = null
                            var accumulatedDragX = 0f
                            var accumulatedDragY = 0f
                            
                            detectDragGestures(
                                onDragStart = { offset ->
                                    accumulatedDragX = 0f
                                    accumulatedDragY = 0f
                                    val clampedX = offset.x.coerceIn(0f, boardSizePx - 1f)
                                    val clampedY = offset.y.coerceIn(0f, boardSizePx - 1f)
                                    val col = (clampedX / tileExtentPx).toInt()
                                    val row = (clampedY / tileExtentPx).toInt()
                                    if (row in 0 until board.rows && col in 0 until board.columns) {
                                        dragOrigin = BoardPosition(row, col)
                                        onTileClick(BoardPosition(row, col))
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val origin = dragOrigin
                                    if (origin != null) {
                                        accumulatedDragX += dragAmount.x
                                        accumulatedDragY += dragAmount.y
                                        val threshold = tileExtentPx * 0.4f 
                                        val absX = abs(accumulatedDragX)
                                        val absY = abs(accumulatedDragY)
                                        val targetPos = when {
                                            absX >= threshold && absX >= absY ->
                                                origin.copy(column = origin.column + if (accumulatedDragX > 0f) 1 else -1)
                                            absY >= threshold ->
                                                origin.copy(row = origin.row + if (accumulatedDragY > 0f) 1 else -1)
                                            else -> null
                                        }
                                        if (targetPos != null && board.contains(targetPos)) {
                                            dragOrigin = null
                                            accumulatedDragX = 0f
                                            accumulatedDragY = 0f
                                            onTileClick(targetPos)
                                        }
                                    }
                                },
                                onDragEnd = { dragOrigin = null },
                                onDragCancel = { dragOrigin = null }
                            )
                        }
                ) {
                    val innerBoardSize = boardSize - 2.dp
                    val tileSize = innerBoardSize / board.columns
                    val density = LocalDensity.current
                    val tileSizePx = with(density) { tileSize.toPx() }

                    snapshot.tiles.forEach { visualTile ->
                        val isSelected = selectedPosition == visualTile.currentLogicalPosition
                        val isMatched = visualTile.role == TileAnimationRole.Removing
                        val targetX = visualTile.targetLogicalPosition.column * tileSizePx
                        val targetY = visualTile.targetLogicalPosition.row * tileSizePx
                        val initialX = visualTile.previousLogicalPosition.column * tileSizePx
                        val initialY = visualTile.previousLogicalPosition.row * tileSizePx
                        val animX = remember(visualTile.tile.id) { Animatable(initialX) }
                        val animY = remember(visualTile.tile.id) { Animatable(initialY) }
                        val tileEffect = tileEffectByPosition[visualTile.currentLogicalPosition]

                        LaunchedEffect(
                            visualTile.tile.id,
                            visualTile.previousLogicalPosition,
                            visualTile.targetLogicalPosition,
                            visualTile.role,
                            tileSizePx
                        ) {
                            val duration = when (visualTile.role) {
                                TileAnimationRole.Swapping -> Match3MotionTokens.SwapDurationMs
                                TileAnimationRole.InvalidReturning -> Match3MotionTokens.InvalidSwapReturnDurationMs
                                TileAnimationRole.Falling -> Match3MotionTokens.fallDurationForRows(visualTile.fallDistance)
                                TileAnimationRole.Refilling -> Match3MotionTokens.RefillDurationMs
                                else -> 0
                            }

                            if (duration <= 0) {
                                animX.snapTo(targetX)
                                animY.snapTo(targetY)
                            } else if (visualTile.role == TileAnimationRole.Falling || visualTile.role == TileAnimationRole.Refilling) {
                                launch {
                                    animX.animateTo(
                                        targetValue = targetX,
                                        animationSpec = tween(
                                            durationMillis = (duration * 0.72f).toInt().coerceAtLeast(70),
                                            easing = Match3MotionTokens.SwapEasing
                                        )
                                    )
                                }
                                animY.animateTo(
                                    targetValue = targetY,
                                    animationSpec = spring(
                                        dampingRatio = 0.78f,
                                        stiffness = 360f
                                    )
                                )
                            } else {
                                val easing = when (visualTile.role) {
                                    TileAnimationRole.Falling, TileAnimationRole.Refilling -> Match3MotionTokens.FallEasing
                                    else -> Match3MotionTokens.SwapEasing
                                }
                                val spec = tween<Float>(durationMillis = duration, easing = easing)
                                launch { animX.animateTo(targetX, spec) }
                                animY.animateTo(targetY, spec)
                            }
                        }

                        key(visualTile.tile.id) {
                            val isHinted = visualTile.currentLogicalPosition in (hintedPositions ?: emptySet())
                            Box(
                                modifier = Modifier
                                    .size(tileSize)
                                    .offset {
                                        IntOffset(
                                            x = animX.value.roundToInt(),
                                            y = animY.value.roundToInt()
                                        )
                                    }
                                    .graphicsLayer {
                                        if (isHinted) {
                                            scaleX = hintScale
                                            scaleY = hintScale
                                        }
                                    }
                            ) {
                                FoodTileComposable(
                                    tile = visualTile.tile,
                                    isSelected = isSelected,
                                    isMatched = isMatched,
                                    isLanding = visualTile.role == TileAnimationRole.Landing,
                                    isRefilling = visualTile.role == TileAnimationRole.Refilling,
                                    specialEffectType = tileEffect,
                                    spawnedSpecialType = spawnedSpecialTiles[visualTile.currentLogicalPosition],
                                    spawnedSpecialNonce = spawnedSpecialNonce,
                                    tileSize = tileSize // Pass size for icon scaling
                                )
                            }
                        }
                    }

                    if (matchedPositions.isNotEmpty()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val glowAlpha = (1f - matchGlowProgress.value) * 0.28f
                            val ringAlpha = (1f - matchGlowProgress.value) * 0.24f
                            matchedPositions.forEach { pos ->
                                val center = Offset(
                                    x = (pos.column + 0.5f) * tileSizePx,
                                    y = (pos.row + 0.5f) * tileSizePx
                                )
                                drawCircle(
                                    color = PremiumColors.Gold.copy(alpha = glowAlpha),
                                    radius = tileSizePx * 0.46f,
                                    center = center
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = ringAlpha),
                                    radius = tileSizePx * (0.30f + matchGlowProgress.value * 0.24f),
                                    center = center,
                                    style = Stroke(width = tileSizePx * 0.05f)
                                )
                            }
                        }
                    }

                    if (spawnedSpecialTiles.isNotEmpty() && spawnedSpecialProgress.value < 1f) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val progress = spawnedSpecialProgress.value
                            spawnedSpecialTiles.forEach { (pos, type) ->
                                val center = Offset(
                                    x = (pos.column + 0.5f) * tileSizePx,
                                    y = (pos.row + 0.5f) * tileSizePx
                                )
                                when (type) {
                                    SpecialTileType.ROW_CLEAR -> {
                                        val sweepWidth = size.width * (0.22f + progress * 0.72f)
                                        drawRoundRect(
                                            color = PremiumColors.Gold.copy(alpha = 0.30f * (1f - progress)),
                                            topLeft = Offset(center.x - sweepWidth / 2f, center.y - tileSizePx * 0.14f),
                                            size = Size(sweepWidth, tileSizePx * 0.28f),
                                            cornerRadius = CornerRadius(tileSizePx * 0.12f, tileSizePx * 0.12f)
                                        )
                                    }
                                    SpecialTileType.COLUMN_CLEAR -> {
                                        val sweepHeight = size.height * (0.22f + progress * 0.72f)
                                        drawRoundRect(
                                            color = PremiumColors.Gold.copy(alpha = 0.30f * (1f - progress)),
                                            topLeft = Offset(center.x - tileSizePx * 0.14f, center.y - sweepHeight / 2f),
                                            size = Size(tileSizePx * 0.28f, sweepHeight),
                                            cornerRadius = CornerRadius(tileSizePx * 0.12f, tileSizePx * 0.12f)
                                        )
                                    }
                                    SpecialTileType.BOMB -> {
                                        drawCircle(
                                            color = Color(0xFFFFC857).copy(alpha = 0.34f * (1f - progress)),
                                            radius = tileSizePx * (0.48f + progress * 0.52f),
                                            center = center
                                        )
                                        drawCircle(
                                            color = Color.White.copy(alpha = 0.26f * (1f - progress)),
                                            radius = tileSizePx * (0.24f + progress * 0.28f),
                                            center = center
                                        )
                                    }
                                    SpecialTileType.COLOR_BOMB -> {
                                        drawCircle(
                                            color = PremiumColors.Emerald.copy(alpha = 0.22f * (1f - progress)),
                                            radius = tileSizePx * (0.34f + progress * 0.46f),
                                            center = center
                                        )
                                        drawCircle(
                                            color = PremiumColors.Gold.copy(alpha = 0.16f * (1f - progress)),
                                            radius = tileSizePx * (0.20f + progress * 0.30f),
                                            center = center
                                        )
                                        drawCircle(
                                            color = Color.White.copy(alpha = 0.20f * (1f - progress)),
                                            radius = tileSizePx * (0.30f + progress * 0.34f),
                                            center = center,
                                            style = Stroke(width = tileSizePx * 0.05f)
                                        )
                                    }
                                    SpecialTileType.NONE -> Unit
                                }
                            }
                        }
                    }

                    if (specialEffects.isNotEmpty()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val progress = specialEffectProgress.value
                            specialEffects.forEach { effect ->
                                when (effect.type) {
                                    SpecialBoardEffectType.HORIZONTAL_LINE -> {
                                        val y = (effect.origin.row + 0.5f) * tileSizePx
                                        drawRoundRect(
                                            color = PremiumColors.Gold.copy(alpha = 0.22f + (1f - progress) * 0.35f),
                                            topLeft = Offset(0f, y - tileSizePx * 0.18f),
                                            size = Size(size.width, tileSizePx * 0.36f),
                                            cornerRadius = CornerRadius(tileSizePx * 0.18f, tileSizePx * 0.18f)
                                        )
                                        drawRoundRect(
                                            color = Color.White.copy(alpha = 0.10f + (1f - progress) * 0.18f),
                                            topLeft = Offset(0f, y - tileSizePx * 0.07f),
                                            size = Size(size.width, tileSizePx * 0.14f),
                                            cornerRadius = CornerRadius(tileSizePx * 0.08f, tileSizePx * 0.08f)
                                        )
                                    }
                                    SpecialBoardEffectType.VERTICAL_LINE -> {
                                        val x = (effect.origin.column + 0.5f) * tileSizePx
                                        drawRoundRect(
                                            color = PremiumColors.Gold.copy(alpha = 0.22f + (1f - progress) * 0.35f),
                                            topLeft = Offset(x - tileSizePx * 0.18f, 0f),
                                            size = Size(tileSizePx * 0.36f, size.height),
                                            cornerRadius = CornerRadius(tileSizePx * 0.18f, tileSizePx * 0.18f)
                                        )
                                        drawRoundRect(
                                            color = Color.White.copy(alpha = 0.07f + (1f - progress) * 0.14f),
                                            topLeft = Offset(x - tileSizePx * 0.08f, 0f),
                                            size = Size(tileSizePx * 0.16f, size.height),
                                            cornerRadius = CornerRadius(tileSizePx * 0.08f, tileSizePx * 0.08f)
                                        )
                                    }
                                    SpecialBoardEffectType.BOMB -> {
                                        val center = Offset(
                                            x = (effect.origin.column + 0.5f) * tileSizePx,
                                            y = (effect.origin.row + 0.5f) * tileSizePx
                                        )
                                        // Outer Shockwave
                                        drawCircle(
                                            color = Color(0xFFFFD54F).copy(alpha = 0.45f * (1f - progress)),
                                            radius = tileSizePx * (1.0f + progress * 1.8f),
                                            center = center
                                        )
                                        // Inner Flash
                                        drawCircle(
                                            color = Color.White.copy(alpha = 0.5f * (1f - progress)),
                                            radius = tileSizePx * (0.4f + progress * 0.8f),
                                            center = center
                                        )
                                        // Particle Burst
                                        repeat(8) { i ->
                                            val angle = (i.toFloat() / 8f) * 2f * PI.toFloat()
                                            val dist = tileSizePx * 1.5f * progress
                                            drawCircle(
                                                color = Color(0xFFFFA000).copy(alpha = 1f - progress),
                                                radius = tileSizePx * 0.08f,
                                                center = Offset(center.x + cos(angle) * dist, center.y + sin(angle) * dist)
                                            )
                                        }
                                    }
                                    SpecialBoardEffectType.COLOR_CLEAR -> {
                                        effect.affectedPositions.take(Match3SpecialConfig.MaxSpecialParticles * 10).forEachIndexed { index, pos ->
                                            val center = Offset(
                                                x = (pos.column + 0.5f) * tileSizePx,
                                                y = (pos.row + 0.5f) * tileSizePx
                                            )
                                            val drift = ((index % 3) - 1) * tileSizePx * 0.06f * progress
                                            drawCircle(
                                                color = PremiumColors.Emerald.copy(alpha = 0.12f + (1f - progress) * 0.18f),
                                                radius = tileSizePx * (0.16f + progress * 0.18f),
                                                center = Offset(center.x + drift, center.y - drift)
                                            )
                                            drawCircle(
                                                color = Color.White.copy(alpha = 0.08f + (1f - progress) * 0.12f),
                                                radius = tileSizePx * (0.05f + progress * 0.08f),
                                                center = center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    FloatingScoreOverlay(
                        text = floatingScoreText,
                        nonce = floatingScoreNonce,
                        anchor = floatingScoreAnchor,
                        boardSizePx = tileSizePx * board.columns,
                        tileSizePx = tileSizePx
                    )
                    SpecialEffectOverlay(
                        text = specialEffectLabel,
                        nonce = specialEffectNonce
                    )
                    
                    // P10-B: Special activation flash
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = specialFlashAlpha.value))
                    )
                }

                if (comboCount > 1) {
                    ComboOverlay(count = comboCount, label = comboLabel)
                }

                if (showReshuffleLabel) {
                    ReshuffleOverlay()
                }
            }
        }

            SideEffect {
                previousBoard = board
            }
        }
    }
}

@Composable
private fun ComboOverlay(count: Int, label: String?) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(count) {
        if (count <= 1) return@LaunchedEffect
        visible = true
        delay(Match3MotionTokens.ComboOverlayDurationMs.toLong())
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(150)) + scaleIn(initialScale = 0.4f, animationSpec = spring(dampingRatio = 0.55f, stiffness = 400f)),
        exit = fadeOut(tween(300)) + scaleOut(targetScale = 1.4f)
    ) {
        Box(
            modifier = Modifier
                .offset(y = (-130).dp) // P10-B: Slightly higher
                .background(
                    Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.65f), Color.Transparent)),
                    RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .border(2.5.dp, PremiumColors.Gold, RoundedCornerShape(24.dp))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "COMBO x$count",
                    color = PremiumColors.Gold,
                    fontSize = (24 + count).coerceAtMost(36).sp, // P10-B: Dynamic size
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                if (!label.isNullOrBlank()) {
                    Text(
                        text = label.uppercase(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ReshuffleOverlay() {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + scaleIn(initialScale = 0.8f),
        exit = fadeOut() + scaleOut(targetScale = 1.2f)
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .border(2.dp, PremiumColors.Gold, RoundedCornerShape(16.dp))
                .padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "NO MORE MOVES!",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "RESHUFFLING...",
                    color = PremiumColors.Gold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}

@Composable
private fun BoxScope.FloatingScoreOverlay(
    text: String?,
    nonce: Int,
    anchor: BoardPosition?,
    boardSizePx: Float,
    tileSizePx: Float
) {
    if (text.isNullOrBlank()) return
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(nonce, text) {
        if (nonce <= 0) return@LaunchedEffect
        visible = true
        delay(Match3MotionTokens.ScoreFloatingDurationMs)
        visible = false
    }
    if (!visible) return
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = Match3MotionTokens.ScoreFloatingDurationMs.toInt()),
        label = "floatingScoreProgress"
    )
    val anchorX = ((anchor?.column ?: 3) + 0.5f) * tileSizePx
    val anchorY = ((anchor?.row ?: 3) + 0.32f) * tileSizePx
    val clampedX = anchorX.coerceIn(tileSizePx * 1.35f, boardSizePx - tileSizePx * 1.35f)
    val clampedY = anchorY.coerceIn(tileSizePx * 0.85f, boardSizePx - tileSizePx * 0.85f)
    val rise = tileSizePx * 1.15f * progress // P10-B: Higher rise
    val alpha = (1f - progress * 0.88f).coerceIn(0f, 1f)
    val scale = 0.96f + (0.24f * progress) // P10-B: More scale

    Box(
        modifier = Modifier
            .align(Alignment.TopStart)
            .offset { IntOffset(clampedX.roundToInt(), (clampedY - rise).roundToInt()) }
            .graphicsLayer {
                this.alpha = alpha
                scaleX = scale
                scaleY = scale
            }
            .background(Color.Black.copy(alpha = 0.38f), RoundedCornerShape(18.dp))
            .border(1.2.dp, PremiumColors.Gold.copy(alpha = 0.75f), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun SpecialEffectOverlay(
    text: String?,
    nonce: Int
) {
    if (text.isNullOrBlank()) return
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(nonce, text) {
        if (nonce <= 0) return@LaunchedEffect
        visible = true
        delay(700) // P10-B: Longer duration
        visible = false
    }
    if (!visible) return

    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.fadeIn(animationSpec = tween(120)) + androidx.compose.animation.scaleIn(initialScale = 0.85f),
        exit = androidx.compose.animation.fadeOut(animationSpec = tween(250)) + androidx.compose.animation.scaleOut(targetScale = 1.15f)
    ) {
        Box(
            modifier = Modifier
                .offset(y = (-84).dp)
                .background(
                    Brush.verticalGradient(listOf(PremiumColors.DeepNavy.copy(alpha = 0.92f), Color.Black.copy(alpha = 0.92f))),
                    RoundedCornerShape(20.dp)
                )
                .border(1.5.dp, PremiumColors.Gold.copy(alpha = 0.88f), RoundedCornerShape(20.dp))
                .padding(horizontal = 22.dp, vertical = 8.dp)
        ) {
            Text(
                text = text.uppercase(),
                color = PremiumColors.Gold,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 1.2.sp
            )
        }
    }
}
