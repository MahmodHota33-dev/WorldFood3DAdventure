package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.BuildConfig
import com.mahmodhota.worldfood3dadventure.game.progress.CountryProgress
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryMetadata
import com.mahmodhota.worldfood3dadventure.ui.match3.effects.SparkleEffect

/**
 * A glowing marker on the world map representing a country.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CountryNodeComposable(
    country: CountryMetadata,
    progress: CountryProgress,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    compactMode: Boolean = false,
    showLabel: Boolean = true,
    deemphasized: Boolean = false,
    labelOffset: androidx.compose.ui.geometry.Offset = androidx.compose.ui.geometry.Offset.Zero,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        label = "pressScale"
    )
    val selectedScale by animateFloatAsState(
        targetValue = when {
            isSelected -> 1.32f
            progress.isUnlocked -> 1.03f
            else -> 0.9f
        },
        label = "selectedScale"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = when {
            isSelected -> 0.95f
            progress.isUnlocked -> 0.6f
            else -> 0.2f
        },
        label = "glowAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Reduced size for crowded regions
    val nodeSize = when {
        compactMode && isSelected -> 40.dp
        compactMode -> 34.dp
        else -> 42.dp
    }

    Box(
        modifier = modifier
            .scale(pressScale)
            .graphicsLayer {
                scaleX = selectedScale
                scaleY = selectedScale
            }
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = if (BuildConfig.DEBUG) onLongClick else null
            ),
        contentAlignment = Alignment.Center
    ) {
        // Sparkles for completed countries
        if (progress.isCompleted && !deemphasized) {
            SparkleEffect(modifier = Modifier.size(56.dp), count = 4)
        }

        // Glow effect for active/selected
        if (progress.isUnlocked) {
            Box(
                modifier = Modifier
                    .size(if (compactMode) 56.dp else 66.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                PremiumColors.Gold.copy(alpha = glowAlpha * pulseAlpha),
                                PremiumColors.GoldLight.copy(alpha = glowAlpha * 0.45f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )
        }

        // Node Circle
        Surface(
            modifier = Modifier
                .size(nodeSize)
                .shadow(if (isSelected) 18.dp else 10.dp, CircleShape)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                        color = when {
                            isSelected -> PremiumColors.Gold
                            progress.isUnlocked -> Color.White.copy(alpha = 0.85f)
                            else -> Color.Gray.copy(alpha = 0.5f)
                        },
                        shape = CircleShape
                    ),
                color = when {
                    progress.isCompleted -> PremiumColors.Emerald.copy(alpha = 0.95f)
                    progress.isUnlocked -> PremiumColors.DarkSlate.copy(alpha = 0.96f)
                    else -> Color.Black.copy(alpha = 0.72f)
                },
                tonalElevation = 6.dp
        ) {
                Box(contentAlignment = Alignment.Center) {
                    if (!progress.isUnlocked) {
                        Text(text = "🔒", fontSize = 14.sp, color = Color.White.copy(alpha = 0.65f))
                    } else {
                        Text(text = country.flagEmoji, fontSize = if (compactMode) 16.sp else 20.sp)
                    }
                }
        }

        // Progress/Stars Overlay
        if (progress.isUnlocked && !progress.isCompleted && !compactMode) {
            val stars = progress.totalStars
            if (stars > 0) {
                Surface(
                    modifier = Modifier
                        .offset(y = 20.dp)
                        .shadow(4.dp, CircleShape),
                    color = PremiumColors.Gold,
                    shape = CircleShape
                ) {
                    Text(
                        text = "⭐ $stars",
                        color = PremiumColors.DeepNavy,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }
        
        if (progress.isCompleted && !compactMode) {
             Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = Color(0xFF4CAF50),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(16.dp)
                    .background(Color.White, CircleShape)
            )
        }

        // Label with background for readability
        if (showLabel) {
            Surface(
                color = Color.Black.copy(alpha = if (compactMode) 0.65f else 0.75f),
                shape = RoundedCornerShape(8.dp),
                border = if (isSelected) BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.5f)) else null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = if (compactMode) 24.dp else 30.dp)
                    .offset(x = labelOffset.x.dp, y = labelOffset.y.dp)
            ) {
                Text(
                    text = country.displayName.uppercase(),
                    color = if (isSelected) PremiumColors.Gold else Color.White,
                    fontSize = if (compactMode) 9.sp else 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
        }
    }
}
