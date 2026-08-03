package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.match3.Match3SpecialConfig
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTile
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialBoardEffectType
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialTileType

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
    specialEffectType: SpecialBoardEffectType? = null
) {
    val palette = tilePalette(tile.type.ordinal)
    val scale by animateFloatAsState(
        targetValue = when {
            isMatched -> 0.02f
            isLanding -> 1.02f
            isSelected -> Match3MotionTokens.SelectionScale
            else -> 1.0f
        },
        animationSpec = if (isMatched) {
            keyframes {
                durationMillis = Match3MotionTokens.MatchPopDurationMs
                1.08f at Match3MotionTokens.MatchAnticipationMs using FastOutSlowInEasing
                0.88f at (Match3MotionTokens.MatchPopDurationMs - 34)
                0.02f at Match3MotionTokens.MatchPopDurationMs
            }
        } else if (isLanding) {
            keyframes {
                durationMillis = Match3MotionTokens.LandingCompressionDurationMs + Match3MotionTokens.LandingRecoveryDurationMs
                Match3MotionTokens.LandingCompressionScale at Match3MotionTokens.LandingCompressionDurationMs / 2
                1.02f at Match3MotionTokens.LandingCompressionDurationMs
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
    val alpha by animateFloatAsState(
        targetValue = if (isMatched) 0f else 1f,
        animationSpec = tween(if (isMatched) Match3MotionTokens.MatchPopDurationMs else Match3MotionTokens.SelectionDurationMs),
        label = "tileAlpha"
    )
    val shimmerShift by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.45f,
        animationSpec = tween(160),
        label = "shimmerShift"
    )

    Box(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .scale(scale)
            .alpha(alpha)
            .shadow(if (isSelected) 10.dp else 4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        palette.first.copy(alpha = 0.95f),
                        palette.second.copy(alpha = 0.96f)
                    )
                )
            )
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) PremiumColors.Gold else Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle Inner Highlight
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
        )

        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.18f), Color.Transparent)
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
                            listOf(PremiumColors.Gold.copy(alpha = 0.3f), Color.Transparent)
                        )
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
            val particleCount = if (specialEffectType != null) {
                Match3SpecialConfig.MaxSpecialParticles.coerceAtMost(5)
            } else {
                3
            }
            repeat(particleCount) { i ->
                Box(
                    modifier = Modifier
                        .size((4 + i * 2).dp)
                        .offset(x = ((i - 2) * 6).dp, y = (-8 - i * 4).dp)
                        .background(
                            when (specialEffectType) {
                                SpecialBoardEffectType.COLOR_CLEAR -> PremiumColors.Emerald.copy(alpha = 0.62f)
                                SpecialBoardEffectType.BOMB -> Color(0xFFFFC857).copy(alpha = 0.62f)
                                else -> PremiumColors.Gold.copy(alpha = 0.55f)
                            },
                            CircleShape
                        )
                )
            }
        }

        // The Food Icon
        FoodIcon(
            type = tile.type, 
            size = 40.dp,
            modifier = Modifier.padding(2.dp)
        )

        // Special Tile Overlays with Premium Style
        when (tile.specialType) {
            SpecialTileType.ROW_CLEAR -> SpecialOverlay(icon = "↔")
            SpecialTileType.COLUMN_CLEAR -> SpecialOverlay(icon = "↕")
            SpecialTileType.BOMB -> SpecialOverlay(icon = "💣")
            SpecialTileType.COLOR_BOMB -> SpecialOverlay(icon = "🌈")
            else -> {}
        }
    }
}

private fun tilePalette(index: Int): Pair<Color, Color> {
    val palettes = listOf(
        Pair(Color(0xFF3B6A96), Color(0xFF22486E)),
        Pair(Color(0xFF5F6B2D), Color(0xFF3E4819)),
        Pair(Color(0xFF805B1A), Color(0xFF5D400F)),
        Pair(Color(0xFF7A2F4B), Color(0xFF4D1C2F)),
        Pair(Color(0xFF405A8F), Color(0xFF25365E)),
        Pair(Color(0xFF2D6A5A), Color(0xFF1A4A3C))
    )
    return palettes[index % palettes.size]
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
