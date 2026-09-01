package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry

@Composable
fun LevelIntroOverlay(
    countryId: String,
    levelNumber: Int,
    goals: List<LevelGoal>,
    isVisible: Boolean
) {
    val country = remember(countryId) { LevelRegistry.getCountry(countryId) }
    val metadata = country?.metadata
    val flag = metadata?.flagEmoji ?: "🌍"
    val name = metadata?.displayName ?: countryId.uppercase()
    
    val difficulty = when {
        levelNumber <= 5 -> "RELAXED"
        levelNumber <= 10 -> "STIMULATING"
        else -> "CHALLENGING"
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(400)),
        exit = fadeOut(tween(600)) + scaleOut(targetScale = 1.1f, animationSpec = tween(600))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF040810)) // Deep night background
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Animated background pattern (subtle)
            val infiniteTransition = rememberInfiniteTransition(label = "introBg")
            val bgAlpha by infiniteTransition.animateFloat(
                initialValue = 0.05f,
                targetValue = 0.12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bgAlpha"
            )
            
            val planeOffset by infiniteTransition.animateFloat(
                initialValue = -100f,
                targetValue = 1000f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "planeOffset"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = bgAlpha }
                    .background(
                        Brush.radialGradient(
                            listOf(PremiumColors.Gold.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
            )
            
            // Plane travel decoration
            Text(
                text = "✈️",
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = planeOffset.dp, y = 40.dp)
                    .graphicsLayer { alpha = 0.3f }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ARRIVING AT",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = flag,
                    fontSize = 72.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = name.uppercase(),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PremiumColors.Gold.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "LEVEL $levelNumber",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = PremiumColors.Gold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.05f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = difficulty,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(Modifier.height(48.dp))

                Text(
                    text = "FOOD MISSION",
                    color = PremiumColors.Gold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )

                Spacer(Modifier.height(16.dp))

                goals.forEach { goal ->
                    GoalDisplay(goal)
                }
                
                Spacer(Modifier.height(60.dp))
                
                // Loading indicator or simple "Preparing..."
                Text(
                    text = "PREPARING CULINARY ADVENTURE...",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun GoalDisplay(goal: LevelGoal) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .widthIn(min = 200.dp)
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        when (goal) {
            is LevelGoal.CollectFood -> {
                FoodIcon(type = goal.type, size = 28.dp)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Collect ${goal.amount}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            is LevelGoal.ScoreTarget -> {
                Text("⭐", fontSize = 20.sp)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Reach ${goal.target}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
