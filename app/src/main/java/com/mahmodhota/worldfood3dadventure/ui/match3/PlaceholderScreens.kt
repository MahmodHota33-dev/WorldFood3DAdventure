package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionQuery
import com.mahmodhota.worldfood3dadventure.ui.match3.components.BottomNavigationBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumColors
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumGameBackdrop
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumShapes
import com.mahmodhota.worldfood3dadventure.ui.match3.components.TopStatusBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.premiumPanel

@Composable
fun RewardsScreen(
    onTabSelected: (String) -> Unit,
    progressViewModel: GameProgressViewModel = viewModel()
) {
    val gameState by progressViewModel.gameState.collectAsState()
    val unlockedCountries = ProgressionQuery.unlockedCountries(gameState)
    val completedCountries = ProgressionQuery.completedCountries(gameState)
    val totalCountries = ProgressionQuery.totalCountries()
    val totalStars = ProgressionQuery.totalStarsEarned(gameState)
    val coins = ProgressionQuery.totalCoins(gameState)
    val starsToNextUnlock = ProgressionQuery.starsNeededForNextUnlock(gameState)

    val completedList = remember(gameState) {
        gameState.countries.values
            .filter { it.isCompleted }
            .sortedBy { it.countryId }
    }

    Scaffold(
        topBar = { TopStatusBar(onSettingsClick = {}) },
        bottomBar = { BottomNavigationBar(currentTab = "rewards", onTabSelected = onTabSelected) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PremiumGameBackdrop()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                item {
                    RewardsHeroCard(totalStars = totalStars, coins = coins, unlockedCountries = unlockedCountries, totalCountries = totalCountries)
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        RewardMetricCard("Unlock", if (starsToNextUnlock > 0) "$starsToNextUnlock left" else "Ready", Icons.Default.Star, PremiumColors.Gold, Modifier.weight(1f))
                        RewardMetricCard("Completed", completedCountries.toString(), Icons.Default.EmojiEvents, Color(0xFF81C784), Modifier.weight(1f))
                    }
                }
                item {
                    DailyRewardStrip()
                }
                item {
                    Text(
                        text = "COMPLETED COUNTRIES",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                }
                if (completedList.isEmpty()) {
                    item {
                        Surface(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(18.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.25f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Brush.verticalGradient(listOf(PremiumColors.DarkSlate, PremiumColors.DeepNavy)))
                                    .padding(18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Finish levels to unlock more rewards and country milestones.",
                                    color = Color.White.copy(alpha = 0.82f),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(completedList) { country ->
                        RewardCountryCard(
                            countryId = country.countryId,
                            stars = country.levels.values.sumOf { it.bestStars }
                        )
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun RewardsHeroCard(
    totalStars: Int,
    coins: Int,
    unlockedCountries: Int,
    totalCountries: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .premiumPanel()
            .border(1.dp, PremiumColors.Gold.copy(alpha = 0.3f), PremiumShapes.PanelShape),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "REWARDS",
                color = PremiumColors.Gold,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.4.sp
            )
            Text(
                text = "Track stars, coins, unlocks, and country completion rewards.",
                color = Color.White.copy(alpha = 0.76f),
                fontSize = 13.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                RewardMetricCard("⭐ Stars", totalStars.toString(), Icons.Default.Star, PremiumColors.Gold, Modifier.weight(1f))
                RewardMetricCard("🪙 Coins", coins.toString(), Icons.Default.MonetizationOn, PremiumColors.GoldLight, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                RewardMetricCard("Unlocked", "$unlockedCountries / $totalCountries", Icons.Default.CardGiftcard, Color(0xFF72A8FF), Modifier.weight(1f))
                RewardMetricCard("Rank", rewardRank(totalStars), Icons.Default.EmojiEvents, Color(0xFF81C784), Modifier.weight(1f))
            }
            LinearProgressIndicator(
                progress = { (unlockedCountries.toFloat() / totalCountries.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = PremiumColors.Gold,
                trackColor = Color.White.copy(alpha = 0.12f)
            )
        }
    }
}

@Composable
private fun RewardMetricCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .background(Brush.verticalGradient(listOf(PremiumColors.DarkSlate.copy(alpha = 0.95f), PremiumColors.DeepNavy.copy(alpha = 0.95f))))
                .padding(14.dp)
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(10.dp))
            Text(text = value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            Text(text = label.uppercase(), color = Color.White.copy(alpha = 0.72f), fontWeight = FontWeight.Bold, fontSize = 9.sp, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun RewardCountryCard(countryId: String, stars: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.22f))
    ) {
        Row(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(PremiumColors.DarkSlate, PremiumColors.DeepNavy)))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = countryId.uppercase(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                Text(text = "Country reward complete", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = PremiumColors.Gold.copy(alpha = 0.14f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.35f))
            ) {
                Text(
                    text = "★ $stars",
                    color = PremiumColors.Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun DailyRewardStrip() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(Color(0xFF142B49), Color(0xFF0A1527))))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(PremiumColors.Gold, PremiumColors.GoldDark))),
            contentAlignment = Alignment.Center
            ) {
                Text("🎁", fontSize = 22.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("DAILY REWARD", color = PremiumColors.Gold, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text("Return daily for bonus coins and boosters.", color = Color.White.copy(alpha = 0.76f), fontSize = 12.sp)
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = PremiumColors.Gold.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "OPEN",
                    color = PremiumColors.Gold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

private fun rewardRank(totalStars: Int): String {
    return when {
        totalStars >= 300 -> "Legend"
        totalStars >= 150 -> "Master"
        totalStars >= 50 -> "Globetrotter"
        else -> "Rookie"
    }
}
