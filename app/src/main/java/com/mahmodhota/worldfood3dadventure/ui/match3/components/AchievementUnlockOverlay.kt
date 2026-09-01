package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager
import com.mahmodhota.worldfood3dadventure.data.audio.SfxType
import com.mahmodhota.worldfood3dadventure.game.progress.Achievement
import com.mahmodhota.worldfood3dadventure.game.progress.AchievementManager
import kotlinx.coroutines.delay

@Composable
fun AchievementUnlockOverlay() {
    val newlyUnlocked by AchievementManager.newlyUnlocked
    val currentAchievement = newlyUnlocked.firstOrNull()

    AnimatedVisibility(
        visible = currentAchievement != null,
        enter = fadeIn(tween(400)) + scaleIn(initialScale = 0.8f),
        exit = fadeOut(tween(400)) + scaleOut(targetScale = 1.2f)
    ) {
        currentAchievement?.let { achievement ->
            AchievementCard(achievement) {
                AchievementManager.consumeAchievement()
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement, onDismiss: () -> Unit) {
    LaunchedEffect(achievement.id) {
        GlobalSystemManager.audio.playSfx(SfxType.STAR_EARNED) // Using existing high-quality chime
        GlobalSystemManager.haptics.heavy()
        delay(4000)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier
                .padding(top = 100.dp)
                .widthIn(max = 320.dp)
                .padding(16.dp)
                .premiumPanel(),
            shape = RoundedCornerShape(24.dp),
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(2.dp, PremiumColors.Gold)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏆 ACHIEVEMENT UNLOCKED",
                    color = PremiumColors.Gold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                
                Spacer(Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                        .border(1.dp, PremiumColors.Gold.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(achievement.icon, fontSize = 32.sp)
                }
                
                Spacer(Modifier.height(12.dp))
                
                Text(
                    text = achievement.title.uppercase(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(4.dp))
                
                Text(
                    text = achievement.description,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                if (achievement.rewardXp > 0 || achievement.rewardCoins > 0) {
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (achievement.rewardXp > 0) RewardChip("XP", achievement.rewardXp)
                        if (achievement.rewardCoins > 0) RewardChip("COINS", achievement.rewardCoins)
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardChip(label: String, value: Int) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Text(
            text = "+$value $label",
            color = PremiumColors.Gold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
