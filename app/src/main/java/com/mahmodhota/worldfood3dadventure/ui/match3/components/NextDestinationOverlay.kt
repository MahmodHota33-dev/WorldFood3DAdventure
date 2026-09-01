package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager
import com.mahmodhota.worldfood3dadventure.data.audio.SfxType
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryUnlockSpec
import kotlinx.coroutines.delay

@Composable
fun NextDestinationOverlay(
    nextCountry: CountryUnlockSpec?,
    currentStars: Int,
    onTravel: () -> Unit,
    onBackToMap: () -> Unit
) {
    if (nextCountry == null) {
        // World Journey Complete
        WorldCompleteOverlay {
            GlobalSystemManager.audio.playSfx(SfxType.BUTTON_CLICK)
            onBackToMap()
        }
        return
    }

    val isLocked = currentStars < nextCountry.requiredStarsToUnlock
    val countryDef = remember(nextCountry.countryId) { LevelRegistry.getCountry(nextCountry.countryId) }
    val metadata = countryDef?.metadata
    val flag = metadata?.flagEmoji ?: "🌍"
    val name = metadata?.displayName ?: nextCountry.displayName

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .premiumPanel()
                .border(2.dp, if (isLocked) Color.Gray else PremiumColors.Gold, RoundedCornerShape(32.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NEXT DESTINATION",
                color = if (isLocked) Color.Gray else PremiumColors.Gold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            
            Spacer(Modifier.height(32.dp))
            
            Box(contentAlignment = Alignment.Center) {
                Text(text = "✈️", fontSize = 48.sp, modifier = Modifier.graphicsLayer { alpha = 0.4f; rotationZ = 45f })
                Text(text = flag, fontSize = 72.sp)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = name.uppercase(),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            if (isLocked) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.05f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DESTINATION LOCKED", color = Color(0xFFFF5252), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "${nextCountry.requiredStarsToUnlock} Stars needed to unlock",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "$currentStars / ${nextCountry.requiredStarsToUnlock} ⭐",
                                color = PremiumColors.Gold,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Ready for your next food adventure?",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(48.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!isLocked) {
                    Button(
                        onClick = {
                            GlobalSystemManager.audio.playSfx(SfxType.BUTTON_CLICK)
                            onTravel()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("TRAVEL NOW", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }
                }
                
                Button(
                    onClick = {
                        GlobalSystemManager.audio.playSfx(SfxType.BUTTON_CLICK)
                        onBackToMap()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("EXPLORE MAP", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun WorldCompleteOverlay(onFinish: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("🌍", fontSize = 120.sp)
            Spacer(Modifier.height(24.dp))
            Text("WORLD JOURNEY COMPLETE!", color = PremiumColors.Gold, fontSize = 26.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Text("You've mastered the culinary arts of 213 countries!", color = Color.White, textAlign = TextAlign.Center)
            Spacer(Modifier.height(48.dp))
            Button(onClick = onFinish, colors = ButtonDefaults.buttonColors(containerColor = PremiumColors.Gold)) {
                Text("REVISIT WORLD", color = PremiumColors.DeepNavy, fontWeight = FontWeight.Black)
            }
        }
    }
}
