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
import androidx.compose.ui.draw.alpha
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
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryUnlockSpec
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import com.mahmodhota.worldfood3dadventure.ui.match3.components.Match3MotionTokens.RewardCountDurationMs
import kotlinx.coroutines.delay

@Composable
fun VictoryOverlay(
    countryId: String,
    levelNumber: Int,
    stars: Int,
    score: Int,
    xpReward: Int,
    coinReward: Int,
    collectedFood: FoodTileType?,
    isCountryComplete: Boolean,
    hasNextLevel: Boolean,
    onContinue: () -> Unit,
    onBackToMap: () -> Unit
) {
    val country = remember(countryId) { LevelRegistry.getCountry(countryId) }
    val metadata = country?.metadata
    val flag = metadata?.flagEmoji ?: "🌍"
    val name = metadata?.displayName ?: countryId.uppercase()

    var showHeader by remember { mutableStateOf(false) }
    var showStars by remember { mutableIntStateOf(0) }
    var showFood by remember { mutableStateOf(false) }
    var showRewards by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        showHeader = true
        delay(400)
        repeat(stars) { i ->
            delay(300)
            showStars = i + 1
        }
        delay(400)
        showFood = true
        delay(400)
        showRewards = true
        delay(600)
        showButtons = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .premiumPanel()
                .border(2.dp, PremiumColors.Gold, RoundedCornerShape(28.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            AnimatedVisibility(
                visible = showHeader,
                enter = fadeIn() + scaleIn(initialScale = 0.8f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isCountryComplete) "COUNTRY COMPLETE!" else "LEVEL COMPLETE!",
                        color = PremiumColors.Gold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = flag, fontSize = 24.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "$name LEVEL $levelNumber",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Stars
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(3) { i ->
                    val active = i < stars && i < showStars
                    val starScale by animateFloatAsState(
                        targetValue = if (active) 1.2f else 0.8f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f)
                    )
                    Text(
                        text = if (active) "⭐" else "☆",
                        fontSize = 52.sp,
                        color = if (active) PremiumColors.Gold else Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.scale(starScale)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Food Discovered
            AnimatedVisibility(
                visible = showFood,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "FOOD DISCOVERED",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .background(Color.White.copy(alpha = 0.05f), CircleShape)
                            .border(1.dp, PremiumColors.Gold.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (collectedFood != null) {
                            FoodIcon(type = collectedFood, size = 48.dp)
                        } else {
                            Text("🥘", fontSize = 40.sp)
                        }
                    }
                    if (collectedFood != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = collectedFood.name.replace('_', ' ').lowercase().capitalize(),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Rewards (XP & Coins)
            AnimatedVisibility(
                visible = showRewards,
                enter = fadeIn()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RewardItem(label = "XP", value = xpReward, icon = "✨", color = Color(0xFF4CAF50))
                    RewardItem(label = "COINS", value = coinReward, icon = "🪙", color = PremiumColors.Gold)
                }
            }

            Spacer(Modifier.height(40.dp))

            // Buttons
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val alreadyCompleted = remember(countryId) { 
                        ProgressionManager.getCountryProgress(countryId).isCompleted 
                    }
                    val primaryText = when {
                        isCountryComplete -> "NEXT DESTINATION"
                        hasNextLevel -> "NEXT LEVEL"
                        alreadyCompleted -> "REPLAY ADVENTURE"
                        else -> "CONTINUE JOURNEY"
                    }
                    VictoryButton(
                        text = primaryText,
                        containerColor = Color(0xFF4CAF50),
                        onClick = onContinue
                    )
                    VictoryButton(
                        text = "BACK TO MAP",
                        containerColor = Color.Transparent,
                        borderColor = Color.White.copy(alpha = 0.3f),
                        onClick = onBackToMap
                    )
                }
            }
        }
        
        // P10-C: Passport stamp decoration if country complete
        if (isCountryComplete && showButtons) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.15f },
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = "PASSPORT\nAPPROVED",
                    modifier = Modifier.padding(32.dp).graphicsLayer { rotationZ = -15f },
                    color = PremiumColors.Gold,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 44.sp
                )
            }
        }
    }
}

@Composable
private fun RewardItem(label: String, value: Int, icon: String, color: Color) {
    val displayValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(RewardCountDurationMs)
    )
    Column(
        modifier = Modifier
            .width(100.dp)
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 20.sp)
        Text(text = label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = "+$displayValue", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun VictoryButton(
    text: String,
    containerColor: Color,
    onClick: () -> Unit,
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
            Text(text = text.uppercase(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 1.sp)
        }
    }
}

private fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
