package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.sin

@Composable
fun PremiumGameBackdrop(
    modifier: Modifier = Modifier,
    parallax: Offset = Offset.Zero
) {
    val transition = rememberInfiniteTransition(label = "premiumBackdrop")
    val lightPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(26000, easing = LinearEasing), RepeatMode.Restart),
        label = "lightPhase"
    )
    val particlePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(18000, easing = LinearEasing), RepeatMode.Restart),
        label = "particlePhase"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        PremiumColors.DeepNavy,
                        Color(0xFF071B33),
                        Color(0xFF041020)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF24456F).copy(alpha = 0.34f), Color.Transparent),
                    center = Offset(size.width * 0.52f, size.height * 0.12f + parallax.y * 0.04f),
                    radius = size.minDimension * 0.9f
                )
            )

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1A365D).copy(alpha = 0.38f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.08f + parallax.y * 0.04f),
                    radius = size.minDimension * 0.95f
                )
            )

            repeat(4) { index ->
                val centerX = size.width * (0.18f + index * 0.22f) + parallax.x * (0.02f + index * 0.004f)
                val centerY = size.height * (0.16f + index * 0.07f) + parallax.y * 0.02f
                val alpha = 0.12f - index * 0.02f
                drawCircle(
                    color = Color(0xFF8DE3FF).copy(alpha = alpha),
                    radius = size.minDimension * (0.23f + index * 0.08f),
                    center = Offset(centerX, centerY)
                )
            }

            repeat(5) { index ->
                val x = size.width * (0.1f + index * 0.18f)
                val y = size.height * (0.06f + index * 0.08f)
                val height = size.height * (0.55f + index * 0.06f)
                drawLine(
                    color = Color.White.copy(alpha = 0.045f),
                    start = Offset(x, y),
                    end = Offset(x + size.width * 0.08f, y + height),
                    strokeWidth = 1.5f
                )
            }

            repeat(8) { index ->
                val x = ((particlePhase * size.width * 1.2f) + index * 130f + parallax.x * 0.1f) % (size.width + 240f) - 120f
                val y = ((index * 83f) + particlePhase * size.height * 0.14f + parallax.y * 0.08f) % size.height
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f + (index % 3) * 0.02f),
                    radius = 1.8f + (index % 4) * 0.8f,
                    center = Offset(x, y)
                )
            }

            repeat(4) { index ->
                val offsetX = size.width * (0.15f + index * 0.28f) + sin((lightPhase * 2f + index) * Math.PI.toFloat()) * 18f
                val offsetY = size.height * (0.18f + index * 0.16f)
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.045f),
                    topLeft = Offset(offsetX - 180f, offsetY),
                    size = Size(360f, 26f),
                    cornerRadius = CornerRadius(100f, 100f)
                )
            }
        }
    }
}
