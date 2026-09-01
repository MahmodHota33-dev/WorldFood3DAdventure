package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import kotlinx.coroutines.delay

@Composable
fun FailureOverlay(
    countryId: String,
    levelNumber: Int,
    goals: List<LevelGoal>,
    collected: Map<com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType, Int>,
    score: Int,
    onRetry: () -> Unit,
    onBackToMap: () -> Unit,
    onBuyExtraMoves: () -> Unit,
    canAffordExtraMoves: Boolean,
    extraMovesCost: Int
) {
    val country = remember(countryId) { LevelRegistry.getCountry(countryId) }
    val metadata = country?.metadata
    val flag = metadata?.flagEmoji ?: "🌍"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .premiumPanel()
                .border(2.dp, Color(0xFF6B7280), RoundedCornerShape(28.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ALMOST THERE!",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "$flag LEVEL $levelNumber",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            // Progress Summary
            Text(
                text = "MISSION PROGRESS",
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp
            )
            Spacer(Modifier.height(16.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                goals.forEach { goal ->
                    GoalProgressRow(goal, collected, score)
                }
            }

            Spacer(Modifier.height(40.dp))

            // Smart Help: Offer Extra Moves if close
            val isVeryClose = goals.any { goal ->
                when (goal) {
                    is LevelGoal.CollectFood -> {
                        val current = collected[goal.type] ?: 0
                        (goal.amount - current) <= 5
                    }
                    is LevelGoal.ScoreTarget -> (goal.target - score) <= (goal.target * 0.15)
                }
            }

            if (isVeryClose) {
                SmartHelpBox(
                    cost = extraMovesCost,
                    canAfford = canAffordExtraMoves,
                    onBuy = onBuyExtraMoves
                )
                Spacer(Modifier.height(24.dp))
            }

            // Buttons
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FailureButton(
                    text = "TRY AGAIN",
                    containerColor = PremiumColors.Gold,
                    textColor = PremiumColors.DeepNavy,
                    onClick = onRetry
                )
                FailureButton(
                    text = "BACK TO MAP",
                    containerColor = Color.Transparent,
                    borderColor = Color.White.copy(alpha = 0.3f),
                    onClick = onBackToMap
                )
            }
        }
    }
}

@Composable
private fun GoalProgressRow(
    goal: LevelGoal,
    collected: Map<com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType, Int>,
    score: Int
) {
    val current = when (goal) {
        is LevelGoal.CollectFood -> collected[goal.type] ?: 0
        is LevelGoal.ScoreTarget -> score
    }
    val target = when (goal) {
        is LevelGoal.CollectFood -> goal.amount
        is LevelGoal.ScoreTarget -> goal.target
    }
    val progress = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (goal is LevelGoal.CollectFood) {
            FoodIcon(type = goal.type, size = 24.dp)
        } else {
            Text("⭐", fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (goal is LevelGoal.CollectFood) goal.type.name.replace('_', ' ').lowercase().capitalize() else "Score",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$current / $target",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(PremiumColors.Gold, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun SmartHelpBox(
    cost: Int,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = PremiumColors.Gold.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "YOU'RE VERY CLOSE!",
                color = PremiumColors.Gold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Get +5 extra moves to finish this level.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Surface(
                onClick = onBuy,
                enabled = canAfford,
                shape = RoundedCornerShape(12.dp),
                color = if (canAfford) PremiumColors.Gold else Color.Gray.copy(alpha = 0.3f),
                modifier = Modifier.height(44.dp).fillMaxWidth(0.8f)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("💰", fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "GET +5 MOVES ($cost)",
                        color = PremiumColors.DeepNavy,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
            if (!canAfford) {
                Text(
                    text = "Not enough coins",
                    color = Color(0xFFFF5252),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun FailureButton(
    text: String,
    containerColor: Color,
    onClick: () -> Unit,
    textColor: Color = Color.White,
    borderColor: Color = Color.Transparent
) {
    Surface(
        onClick = {
            com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager.audio.playSfx(com.mahmodhota.worldfood3dadventure.data.audio.SfxType.BUTTON_CLICK)
            onClick()
        },
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = if (borderColor != Color.Transparent) BorderStroke(1.dp, borderColor) else null,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text.uppercase(), color = textColor, fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 1.sp)
        }
    }
}

private fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
