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
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
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
import com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager
import com.mahmodhota.worldfood3dadventure.data.audio.SfxType
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import com.mahmodhota.worldfood3dadventure.ui.match3.components.Match3MotionTokens.RewardCountDurationMs
import kotlinx.coroutines.delay

@Composable
fun CountryCompleteOverlay(
    countryId: String,
    stars: Int,
    xpReward: Int,
    coinReward: Int,
    onContinue: () -> Unit
) {
    val country = remember(countryId) { LevelRegistry.getCountry(countryId) }
    val metadata = country?.metadata
    val flag = metadata?.flagEmoji ?: "🌍"
    val name = metadata?.displayName ?: countryId.uppercase()
    val foods = remember(countryId) { LevelRegistry.getRepresentativeFoods(countryId) }

    var showMain by remember { mutableStateOf(false) }
    var stampVisible by remember { mutableStateOf(false) }
    var foodVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showMain = true
        delay(800)
        GlobalSystemManager.audio.playSfx(SfxType.PASSPORT_STAMP)
        stampVisible = true
        delay(600)
        foodVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .premiumPanel()
                .border(2.5.dp, PremiumColors.Gold, RoundedCornerShape(32.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "MISSION ACCOMPLISHED!",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "COUNTRY MASTERED",
                color = PremiumColors.Gold,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = flag, fontSize = 32.sp)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = name.uppercase(),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "15 / 15 LEVELS MASTERED",
                        color = PremiumColors.Gold.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Passport Stamp Animation
            val stampScale by animateFloatAsState(
                targetValue = if (stampVisible) 1f else 3f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f)
            )
            val stampAlpha by animateFloatAsState(
                targetValue = if (stampVisible) 1f else 0f,
                animationSpec = tween(300)
            )

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .graphicsLayer {
                        scaleX = stampScale
                        scaleY = stampScale
                        alpha = stampAlpha
                        rotationZ = -15f
                    }
                    .background(Color.Transparent, CircleShape)
                    .border(4.dp, PremiumColors.Gold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PASSPORT", color = PremiumColors.Gold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    Text("APPROVED", color = PremiumColors.Gold, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text(flag, fontSize = 24.sp)
                }
            }

            Spacer(Modifier.height(40.dp))

            // Food Collection
            AnimatedVisibility(
                visible = foodVisible,
                enter = fadeIn() + expandVertically()
            ) {
                val progress = ProgressionManager.getCountryProgress(countryId)
                val discovered = progress.discoveredFoods
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "FOOD COLLECTION",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${discovered.size} / ${foods.size} DISCOVERED",
                        color = PremiumColors.Gold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        foods.take(5).forEach { food ->
                            val isFound = discovered.contains(food.name)
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(
                                        if (isFound) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.2f), 
                                        RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        1.dp, 
                                        if (isFound) PremiumColors.Gold.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f), 
                                        RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isFound) {
                                    FoodIcon(type = food, size = 28.dp)
                                } else {
                                    Text("🔒", fontSize = 16.sp, modifier = Modifier.graphicsLayer { alpha = 0.4f })
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            // Rewards
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                RewardItemCompact(label = "XP", value = xpReward, color = Color(0xFF4CAF50))
                RewardItemCompact(label = "COINS", value = coinReward, color = PremiumColors.Gold)
            }

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PremiumColors.Gold),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    "NEXT DESTINATION",
                    color = PremiumColors.DeepNavy,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun RewardItemCompact(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "+$value $label", color = color, fontSize = 16.sp, fontWeight = FontWeight.Black)
    }
}
