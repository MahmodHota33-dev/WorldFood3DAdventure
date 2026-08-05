package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.ui.match3.components.*

@Composable
fun LevelSelectionScreen(
    countryId: String,
    onLevelSelected: (Int) -> Unit,
    onBackToMap: () -> Unit
) {
    val progress = ProgressionManager.getCountryProgress(countryId)
    val country = LevelRegistry.getCountry(countryId)
    val comingSoon = country?.isComingSoon == true || progress.levels.isEmpty()

    Scaffold(
        topBar = { TopStatusBar(onSettingsClick = {}) },
        bottomBar = { BottomNavigationBar(currentTab = "world", onTabSelected = { onBackToMap() }) },
        containerColor = PremiumColors.DeepNavy
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${countryId.uppercase()} ADVENTURE",
                color = PremiumColors.Gold,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            
            Spacer(Modifier.height(32.dp))

            if (comingSoon) {
                ComingSoonCountryCard(
                    countryName = country?.metadata?.displayName ?: countryId.replaceFirstChar { it.uppercase() },
                    message = country?.metadata?.comingSoonText ?: "This destination is being prepared and will unlock later.",
                    onBackToMap = onBackToMap
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(progress.levels) { level ->
                        LevelCard(
                            levelNumber = level.levelNumber,
                            stars = level.stars,
                            isUnlocked = level.isUnlocked,
                            onClick = { onLevelSelected(level.levelNumber) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComingSoonCountryCard(
    countryName: String,
    message: String,
    onBackToMap: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.WhiteLow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(PremiumColors.DarkSlate, PremiumColors.DeepNavy)))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🇪🇸", fontSize = 56.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                text = countryName.uppercase(),
                color = PremiumColors.Gold,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onBackToMap,
                colors = ButtonDefaults.buttonColors(containerColor = PremiumColors.Gold, contentColor = Color(0xFF1D1A12))
            ) {
                Text("Back to World", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LevelCard(
    levelNumber: Int,
    stars: Int,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .premiumPanel()
            .border(
                if (isUnlocked) 1.5.dp else 1.dp, 
                if (isUnlocked) PremiumColors.Gold.copy(alpha = 0.5f) else PremiumColors.WhiteLow, 
                PremiumShapes.PanelShape
            )
            .clickable(enabled = isUnlocked) {
                com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager.audio.playSfx(com.mahmodhota.worldfood3dadventure.data.audio.SfxType.BUTTON_CLICK)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isUnlocked) {
                Text(
                    text = levelNumber.toString(), 
                    fontSize = 28.sp, 
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(3) { i ->
                        Text(
                            text = "★",
                            color = if (i < stars) PremiumColors.Gold else Color.Gray.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                Text(text = "🔒", fontSize = 28.sp, modifier = Modifier.alpha(0.5f))
            }
        }
    }
}
