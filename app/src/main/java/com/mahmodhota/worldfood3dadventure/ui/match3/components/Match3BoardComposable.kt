package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.mahmodhota.worldfood3dadventure.ui.match3.Match3AnimationPhase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    specialEffects: List<SpecialBoardEffect> = emptyList()
) {
    var previousBoard by remember { mutableStateOf<Match3Board?>(null) }
    val boardShakeOffsetPx = remember { Animatable(0f) }
    val specialEffectProgress = remember { Animatable(0f) }
    val idleTransition = rememberInfiniteTransition(label = "boardIdle")
    val idlePulse by idleTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "boardIdlePulse"
    )

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

    FixedCoordinateSurface {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                    .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
                val boardPadding = 4.dp
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
                        .scale(1f + idlePulse * 0.0025f)
                        .offset { IntOffset(boardShakeOffsetPx.value.roundToInt(), 0) }
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(PremiumColors.DarkSlate.copy(alpha = 0.96f), PremiumColors.DeepNavy.copy(alpha = 0.96f))
                            )
                        )
                        .border(1.5.dp, PremiumColors.Gold.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                        .padding(2.dp)
                        .clipToBounds()
                        .pointerInput(board, boardSize) {
                            val boardSizePx = boardSize.toPx()
                            // Use local tracking variable so onDrag always has the correct
                            // origin tile — avoids stale Composable parameter capture.
                            var dragOrigin: BoardPosition? = null
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val col = (offset.x / (boardSizePx / board.columns)).toInt()
                                    val row = (offset.y / (boardSizePx / board.rows)).toInt()
                                    if (row in 0 until board.rows && col in 0 until board.columns) {
                                        dragOrigin = BoardPosition(row, col)
                                        onTileClick(BoardPosition(row, col))
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val origin = dragOrigin
                                    if (origin != null) {
                                        val threshold = boardSizePx / board.columns * 0.35f
                                        val targetPos = when {
                                            dragAmount.x > threshold -> origin.copy(column = origin.column + 1)
                                            dragAmount.x < -threshold -> origin.copy(column = origin.column - 1)
                                            dragAmount.y > threshold -> origin.copy(row = origin.row + 1)
                                            dragAmount.y < -threshold -> origin.copy(row = origin.row - 1)
                                            else -> null
                                        }
                                        if (targetPos != null && board.contains(targetPos)) {
                                            dragOrigin = null // consume — prevent repeated triggers in one gesture
                                            onTileClick(targetPos)
                                        }
                                    }
                                },
                                onDragEnd = { dragOrigin = null },
                                onDragCancel = { dragOrigin = null }
                            )
                        }
                ) {
                    val innerBoardSize = boardSize - 4.dp
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
                        val scope = rememberCoroutineScope()
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
                            } else {
                                val easing = when (visualTile.role) {
                                    TileAnimationRole.Falling, TileAnimationRole.Refilling -> Match3MotionTokens.FallEasing
                                    else -> Match3MotionTokens.SwapEasing
                                }
                                val spec = tween<Float>(durationMillis = duration, easing = easing)
                                scope.launch { animX.animateTo(targetX, spec) }
                                animY.animateTo(targetY, spec)
                            }
                        }

                        key(visualTile.tile.id) {
                            Box(
                                modifier = Modifier
                                    .size(tileSize)
                                    .offset {
                                        IntOffset(
                                            x = animX.value.roundToInt(),
                                            y = animY.value.roundToInt()
                                        )
                                    }
                                    .clickable { onTileClick(visualTile.currentLogicalPosition) }
                            ) {
                                FoodTileComposable(
                                    tile = visualTile.tile,
                                    isSelected = isSelected,
                                    isMatched = isMatched,
                                    isLanding = visualTile.role == TileAnimationRole.Landing,
                                    specialEffectType = tileEffect
                                )
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
                                        drawCircle(
                                            color = Color(0xFFFFC857).copy(alpha = 0.34f * (1f - progress / 2f)),
                                            radius = tileSizePx * (0.8f + progress * 1.3f),
                                            center = center
                                        )
                                        drawCircle(
                                            color = Color.White.copy(alpha = 0.2f * (1f - progress)),
                                            radius = tileSizePx * (0.28f + progress * 0.42f),
                                            center = center
                                        )
                                    }
                                    SpecialBoardEffectType.COLOR_CLEAR -> {
                                        effect.affectedPositions.take(Match3SpecialConfig.MaxSpecialParticles * 10).forEach { pos ->
                                            val center = Offset(
                                                x = (pos.column + 0.5f) * tileSizePx,
                                                y = (pos.row + 0.5f) * tileSizePx
                                            )
                                            drawCircle(
                                                color = PremiumColors.Emerald.copy(alpha = 0.12f + (1f - progress) * 0.18f),
                                                radius = tileSizePx * (0.16f + progress * 0.18f),
                                                center = center
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
                }

                if (comboCount > 1) {
                    ComboOverlay(count = comboCount, label = comboLabel)
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
        visible = true
        delay(Match3MotionTokens.ComboOverlayDurationMs.toLong())
        visible = false
    }

    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(initialScale = 0.5f),
        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut(targetScale = 1.5f)
    ) {
        Box(
            modifier = Modifier
                .offset(y = (-120).dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .border(2.dp, PremiumColors.Gold, RoundedCornerShape(24.dp))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "COMBO x$count",
                    color = PremiumColors.Gold,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                if (!label.isNullOrBlank()) {
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
