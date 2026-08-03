package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressManager
import com.mahmodhota.worldfood3dadventure.data.progress.model.PersistedGameState
import com.mahmodhota.worldfood3dadventure.game.progress.PlayerLevelProgression

/**
 * Modern top status bar for the game hub.
 */
@Composable
fun TopStatusBar(onSettingsClick: () -> Unit) {
    val gameState by GameProgressManager.repository.state.collectAsState(initial = PersistedGameState())
    val player = gameState.player
    val xpSnapshot = PlayerLevelProgression.snapshot(totalXp = player.xp, storedLevel = player.level)
    val xpProgress = xpSnapshot.xpIntoCurrentLevel.toFloat() / xpSnapshot.xpForNextLevel.toFloat()

    FixedOrderSurface {
        Surface(modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
            Row(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                PremiumColors.DarkSlate.copy(alpha = 0.94f),
                                PremiumColors.DeepNavy.copy(alpha = 0.96f)
                            )
                        )
                    )
                    .border(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.32f), RoundedCornerShape(26.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(54.dp),
                    shape = CircleShape,
                    color = PremiumColors.WhiteLow.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.35f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LEVEL ${xpSnapshot.level}",
                            color = PremiumColors.Gold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            letterSpacing = 1.1.sp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "TRAVEL RANK",
                            color = Color.White.copy(alpha = 0.52f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    XpProgressBar(progress = xpProgress)
                }

                Spacer(Modifier.width(10.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatCapsule(icon = Icons.Default.Star, value = player.totalStars.toString(), color = Color(0xFF72A8FF))
                        StatCapsule(icon = Icons.Default.MonetizationOn, value = player.coins.toString(), color = PremiumColors.Gold)
                    }
                    Spacer(Modifier.height(6.dp))
                    StatCapsule(icon = Icons.Default.Favorite, value = player.lives.toString(), color = Color(0xFFFF5D6A))
                }
            }
        }
    }
}

@Composable
private fun StatCapsule(icon: ImageVector, value: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(PremiumShapes.CapsuleShape)
            .background(
                Brush.horizontalGradient(
                    listOf(PremiumColors.DarkSlate.copy(alpha = 0.95f), PremiumColors.DeepNavy.copy(alpha = 0.8f))
                )
            )
            .border(1.dp, color.copy(alpha = 0.4f), PremiumShapes.CapsuleShape)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Text(
            text = value, 
            color = Color.White, 
            fontWeight = FontWeight.Bold, 
        fontSize = 13.sp,
            maxLines = 1,
            modifier = Modifier.semantics { contentDescription = value }
        )
    }
}

@Composable
private fun XpProgressBar(progress: Float) {
    val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "xpProgress")
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(7.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.1f))
            .border(0.5.dp, Color.White.copy(alpha = 0.2f), CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(Color(0xFF81C784), Color(0xFF4CAF50))))
        )
    }
}
