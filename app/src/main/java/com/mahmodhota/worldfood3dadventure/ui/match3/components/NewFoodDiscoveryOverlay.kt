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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager
import com.mahmodhota.worldfood3dadventure.data.audio.SfxType
import kotlinx.coroutines.delay

@Composable
fun NewFoodDiscoveryOverlay(
    food: FoodTileType,
    countryId: String,
    onDismiss: () -> Unit
) {
    val country = remember(countryId) { LevelRegistry.getCountry(countryId) }
    val flag = country?.metadata?.flagEmoji ?: "🌍"
    val countryName = country?.metadata?.displayName ?: countryId.uppercase()

    LaunchedEffect(Unit) {
        GlobalSystemManager.audio.playSfx(SfxType.FOOD_DISCOVERY_CHIME)
        delay(400)
        GlobalSystemManager.audio.playSfx(SfxType.FOOD_REVEAL)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "discoveryPulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .premiumPanel()
                .border(2.dp, PremiumColors.Gold, RoundedCornerShape(32.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "NEW DISCOVERY!",
                color = PremiumColors.Gold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            
            Spacer(Modifier.height(32.dp))
            
            Box(contentAlignment = Alignment.Center) {
                // Animated Background Glow
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .graphicsLayer { alpha = glowAlpha }
                        .background(
                            Brush.radialGradient(listOf(PremiumColors.Gold, Color.Transparent)),
                            CircleShape
                        )
                )
                
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.size(110.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, PremiumColors.Gold)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        FoodIcon(type = food, size = 64.dp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = food.name.replace('_', ' ').uppercase(),
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(flag, fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = countryName.uppercase(),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(32.dp))
            
            Text(
                text = "Added to your collection.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PremiumColors.Gold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("COLLECT", color = PremiumColors.DeepNavy, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            }
        }
    }
}
