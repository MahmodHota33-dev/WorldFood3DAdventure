package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager
import com.mahmodhota.worldfood3dadventure.data.audio.SfxType

@Composable
fun WelcomeOnboardingScreen(
    onStart: () -> Unit,
    onSkip: () -> Unit
) {
    LaunchedEffect(Unit) {
        GlobalSystemManager.audio.playSfx(SfxType.VICTORY) // Play a festive sound on startup
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumColors.DeepNavy)
    ) {
        PremiumGameBackdrop()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "WORLD FOOD\nADVENTURE",
                color = PremiumColors.Gold,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp,
                letterSpacing = 2.sp
            )
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = "Taste the world.\nExplore 213 countries.\nMaster every adventure.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            
            Spacer(Modifier.height(48.dp))
            
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        Brush.radialGradient(listOf(PremiumColors.Gold.copy(alpha = 0.15f), Color.Transparent))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🌍", fontSize = 72.sp)
            }
            
            Spacer(Modifier.height(64.dp))
            
            Button(
                onClick = {
                    GlobalSystemManager.audio.playSfx(SfxType.BUTTON_CLICK)
                    onStart()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PremiumColors.Gold),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    "START JOURNEY",
                    color = PremiumColors.DeepNavy,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            TextButton(onClick = onSkip) {
                Text(
                    "I ALREADY PLAYED",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
