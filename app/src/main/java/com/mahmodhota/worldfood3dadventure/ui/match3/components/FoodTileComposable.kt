package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import com.mahmodhota.worldfood3dadventure.game.match3.Match3SpecialConfig
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTile
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialBoardEffectType
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialTileType

private val TilePalettes = listOf(
    Triple(Color(0xFF4A7AB5), Color(0xFF2D5A8C), Color(0xFF1A3D66)),
    Triple(Color(0xFF6B7A30), Color(0xFF4A5620), Color(0xFF2E360F)),
    Triple(Color(0xFF9A6E22), Color(0xFF705010), Color(0xFF4A3508)),
    Triple(Color(0xFF8C3558), Color(0xFF622540), Color(0xFF3D1428)),
    Triple(Color(0xFF4E6BAC), Color(0xFF304075), Color(0xFF1A2550)),
    Triple(Color(0xFF2E7A6A), Color(0xFF1A5648), Color(0xFF0E3830))
)

/**
 * Renders a single food tile with a premium glass effect and animated food icon.
 */
@Composable
fun FoodTileComposable(
    tile: FoodTile,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isMatched: Boolean = false,
    isLanding: Boolean = false,
    isRefilling: Boolean = false,
    specialEffectType: SpecialBoardEffectType? = null,
    spawnedSpecialType: SpecialTileType? = null,
    spawnedSpecialNonce: Int = 0,
    tileSize: androidx.compose.ui.unit.Dp = 50.dp
) {
    val palette = tilePalette(tile.type.ordinal)
    val spawnScale = remember(tile.id) { Animatable(1f) }
    val spawnAlpha = remember(tile.id) { Animatable(1f) }
    val hasSpecialTile = tile.specialType != SpecialTileType.NONE
    val specialPulse = if (hasSpecialTile) {
        val specialPulseTransition = rememberInfiniteTransition(label = "specialPulse")
        specialPulseTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1250, easing = LinearEasing)),
            label = "specialPulseValue"
        ).value
    } else {
        0f
    }

    LaunchedEffect(tile.id, isRefilling) {
        if (isRefilling) {
            spawnScale.snapTo(0.88f)
            spawnAlpha.snapTo(0.68f)
            spawnScale.animateTo(1f, tween(durationMillis = 180, easing = FastOutSlowInEasing))
            spawnAlpha.animateTo(1f, tween(durationMillis = 155, easing = FastOutSlowInEasing))
        } else {
            spawnScale.snapTo(1f)
            spawnAlpha.snapTo(1f)
        }
    }

    val scale by animateFloatAsState(
        targetValue = when {
            isMatched -> 0.01f
            isLanding -> 1.0f
            isSelected -> Match3MotionTokens.SelectionScale
            else -> 1.0f
        },
        animationSpec = if (isMatched) {
            keyframes {
                durationMillis = Match3MotionTokens.MatchPopDurationMs
                1.15f at Match3MotionTokens.MatchAnticipationMs using FastOutSlowInEasing
                0.75f at (Match3MotionTokens.MatchPopDurationMs - 30)
                0.01f at Match3MotionTokens.MatchPopDurationMs
            }
        } else if (isLanding) {
            keyframes {
                durationMillis = Match3MotionTokens.LandingCompressionDurationMs + Match3MotionTokens.LandingRecoveryDurationMs
                Match3MotionTokens.LandingCompressionScale at Match3MotionTokens.LandingCompressionDurationMs
                1.04f at Match3MotionTokens.LandingCompressionDurationMs + (Match3MotionTokens.LandingRecoveryDurationMs / 2)
                1.0f at Match3MotionTokens.LandingCompressionDurationMs + Match3MotionTokens.LandingRecoveryDurationMs
            }
        } else {
            tween(
                durationMillis = Match3MotionTokens.SelectionDurationMs,
                easing = FastOutSlowInEasing
            )
        },
        label = "tileScale"
    )
    val tileAlpha by animateFloatAsState(
        targetValue = if (isMatched) 0f else 1f,
        animationSpec = tween(if (isMatched) Match3MotionTokens.MatchPopDurationMs else Match3MotionTokens.SelectionDurationMs),
        label = "tileAlpha"
    )
    val rotation by animateFloatAsState(
        targetValue = if (isMatched) 10f else 0f,
        animationSpec = keyframes {
            durationMillis = Match3MotionTokens.MatchPopDurationMs
            -6f at Match3MotionTokens.MatchAnticipationMs
            10f at Match3MotionTokens.MatchPopDurationMs - 32
            0f at Match3MotionTokens.MatchPopDurationMs
        },
        label = "tileRotation"
    )
    val shimmerShift by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.45f,
        animationSpec = tween(160),
        label = "shimmerShift"
    )
    val matchBurstProgress by animateFloatAsState(
        targetValue = if (isMatched) 1f else 0f,
        animationSpec = tween(durationMillis = Match3MotionTokens.MatchPopDurationMs),
        label = "matchBurst"
    )
    val iceDamageShake = remember(tile.blocker) { Animatable(0f) }
    LaunchedEffect(tile.blocker) {
        if (tile.blocker.isIce) {
            iceDamageShake.animateTo(3f, tween(40, easing = LinearEasing))
            iceDamageShake.animateTo(-3f, tween(40, easing = LinearEasing))
            iceDamageShake.animateTo(0f, tween(40, easing = LinearEasing))
        }
    }
    val spawnedPulseProgress = remember(tile.id) { Animatable(1f) }
    LaunchedEffect(spawnedSpecialType, spawnedSpecialNonce) {
        if (spawnedSpecialType != null && spawnedSpecialNonce > 0) {
            spawnedPulseProgress.snapTo(0f)
            spawnedPulseProgress.animateTo(1f, tween(durationMillis = 240, easing = FastOutSlowInEasing))
        } else {
            spawnedPulseProgress.snapTo(1f)
        }
    }

    Box(
        modifier = modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale * spawnScale.value
                scaleY = scale * spawnScale.value
                alpha = tileAlpha * spawnAlpha.value
                rotationZ = rotation
                translationX = iceDamageShake.value
            }
            .shadow(if (isSelected) 14.dp else 6.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        palette.first.copy(alpha = 0.98f),
                        palette.second.copy(alpha = 0.99f),
                        palette.third.copy(alpha = 1.00f)
                    )
                )
            )
            .border(
                width = if (isSelected) 2.5.dp else 1.4.dp,
                color = if (isSelected) PremiumColors.Gold else Color.White.copy(alpha = 0.18f),
                shape = RoundedCornerShape(18.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle Inner Highlight
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .border(1.dp, Color.White.copy(alpha = 0.13f), RoundedCornerShape(16.dp))
        )

        // Stronger top-left gloss diffuse
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.22f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        // Selection Glow
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(PremiumColors.Gold.copy(alpha = 0.34f), Color.Transparent)
                        )
                    )
            )
        }
        if (hasSpecialTile) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PremiumColors.Gold.copy(alpha = 0.12f + specialPulse * 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        if (spawnedSpecialType != null && spawnedSpecialNonce > 0 && spawnedPulseProgress.value < 1f) {
            val pulseAlpha = (1f - spawnedPulseProgress.value) * 0.55f
            val ringAlpha = (1f - spawnedPulseProgress.value) * 0.65f
            val pulseColor = when (spawnedSpecialType) {
                SpecialTileType.ROW_CLEAR,
                SpecialTileType.COLUMN_CLEAR -> PremiumColors.Gold
                SpecialTileType.BOMB -> Color(0xFFFFC857)
                SpecialTileType.COLOR_BOMB -> PremiumColors.Emerald
                SpecialTileType.NONE -> PremiumColors.Gold
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                pulseColor.copy(alpha = pulseAlpha),
                                Color.Transparent
                            )
                        )
                    )
                    .border(
                        width = 1.4.dp,
                        color = Color.White.copy(alpha = ringAlpha),
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        start = Offset(-120f + shimmerShift * 260f, -120f),
                        end = Offset(shimmerShift * 260f, 220f)
                    )
                )
        )

        if (isMatched) {
            val particleCount = if (specialEffectType != null) 12 else Match3MotionTokens.MaxMatchParticles
            val particleColor = when (specialEffectType) {
                SpecialBoardEffectType.COLOR_CLEAR -> PremiumColors.Emerald.copy(alpha = 0.5f + (1f - matchBurstProgress) * 0.5f)
                SpecialBoardEffectType.BOMB -> Color(0xFFFFD54F).copy(alpha = 0.6f + (1f - matchBurstProgress) * 0.4f)
                else -> PremiumColors.Gold.copy(alpha = 0.45f + (1f - matchBurstProgress) * 0.5f)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxRadius = size.minDimension * 0.15f
                val centerX = size.width * 0.5f
                val centerY = size.height * 0.5f
                
                // Particle Burst
                repeat(particleCount) { i ->
                    val angle = (i.toFloat() / particleCount.toFloat()) * 2f * PI.toFloat()
                    val dist = maxRadius * 2.5f * matchBurstProgress
                    val x = centerX + cos(angle) * dist
                    val y = centerY + sin(angle) * dist
                    val r = (maxRadius * (0.35f + (1f - matchBurstProgress) * 0.5f)).coerceAtLeast(1.5f)
                    drawCircle(color = particleColor, radius = r, center = Offset(x, y))
                }
                
                // Center Glow core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(particleColor.copy(alpha = (1f - matchBurstProgress) * 0.75f), Color.Transparent),
                        center = Offset(centerX, centerY),
                        radius = centerX * (0.5f + matchBurstProgress)
                    ),
                    radius = centerX * (0.5f + matchBurstProgress),
                    center = Offset(centerX, centerY)
                )
            }
        }

        // The Food Icon
        FoodIcon(
            type = tile.type, 
            size = tileSize * 0.72f, // Dynamic scaling based on tile size
            modifier = Modifier.padding(1.dp)
        )

        // Special Tile Overlays with Premium Style
        when (tile.specialType) {
            SpecialTileType.ROW_CLEAR -> SpecialOverlay(icon = "↔")
            SpecialTileType.COLUMN_CLEAR -> SpecialOverlay(icon = "↕")
            SpecialTileType.BOMB -> SpecialOverlay(icon = "💣")
            SpecialTileType.COLOR_BOMB -> SpecialOverlay(icon = "🌈")
            else -> {}
        }

        // P10-B: Ice Blocker Overlay
        if (tile.blocker.isIce) {
            IceBlockerOverlay(state = tile.blocker)
        }
    }
}

@Composable
private fun IceBlockerOverlay(state: com.mahmodhota.worldfood3dadventure.game.match3.model.BlockerState) {
    val infiniteTransition = rememberInfiniteTransition(label = "icePulse")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(1.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = if (state == com.mahmodhota.worldfood3dadventure.game.match3.model.BlockerState.ICE_2)
                        listOf(Color(0xE8E1F5FE), Color(0xD0B3E5FC), Color(0xE8E1F5FE))
                    else
                        listOf(Color(0xA0E1F5FE), Color(0x80B3E5FC), Color(0xA0E1F5FE)),
                    start = Offset(0f, 0f),
                    end = Offset(180f, 180f)
                )
            )
            .graphicsLayer {
                alpha = shimmer
            }
            .border(1.8.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val s = size.width
            val crackColor = Color.White.copy(alpha = 0.5f)
            
            // Draw cracks - more dense for ICE_2
            if (state == com.mahmodhota.worldfood3dadventure.game.match3.model.BlockerState.ICE_2) {
                drawLine(crackColor, Offset(s * 0.15f, s * 0.15f), Offset(s * 0.45f, s * 0.45f), strokeWidth = 2.5f)
                drawLine(crackColor, Offset(s * 0.45f, s * 0.45f), Offset(s * 0.35f, s * 0.85f), strokeWidth = 2.5f)
                drawLine(crackColor, Offset(s * 0.85f, s * 0.20f), Offset(s * 0.55f, s * 0.50f), strokeWidth = 2.5f)
                drawLine(crackColor, Offset(s * 0.55f, s * 0.50f), Offset(s * 0.75f, s * 0.75f), strokeWidth = 2.5f)
            } else {
                // Single prominent crack for ICE_1
                drawLine(crackColor, Offset(s * 0.25f, s * 0.35f), Offset(s * 0.75f, s * 0.65f), strokeWidth = 2f)
            }
            
            // Frosty highlight (dynamic)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White.copy(alpha = 0.20f), Color.Transparent)
                ),
                radius = s * 0.4f,
                center = Offset(s * (0.3f + (1f - shimmer) * 0.1f), s * (0.3f + (1f - shimmer) * 0.1f))
            )
        }
    }
}

private fun tilePalette(index: Int): Triple<Color, Color, Color> {
    return TilePalettes[index % TilePalettes.size]
}

@Composable
private fun SpecialOverlay(icon: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.1f)),
        contentAlignment = Alignment.BottomEnd
    ) {
        Surface(
            color = PremiumColors.Gold.copy(alpha = 0.9f),
            shape = RoundedCornerShape(topStart = 8.dp),
            modifier = Modifier.size(18.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = icon, fontSize = 10.sp)
            }
        }
    }
}
