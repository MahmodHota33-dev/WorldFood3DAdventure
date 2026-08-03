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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mahmodhota.worldfood3dadventure.game.progress.PlayerLevelProgression
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionQuery
import com.mahmodhota.worldfood3dadventure.ui.match3.components.BottomNavigationBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumColors
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumGameBackdrop
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumShapes
import com.mahmodhota.worldfood3dadventure.ui.match3.components.TopStatusBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.premiumPanel

@Composable
fun ProfileScreenV2(
    onTabSelected: (String) -> Unit,
    progressViewModel: GameProgressViewModel = viewModel()
) {
    val gameState by progressViewModel.gameState.collectAsState()
    val player = gameState.player
    val stats = gameState.stats
    val completedLevels = ProgressionQuery.totalLevelsCompleted(gameState)
    val totalCountries = ProgressionQuery.totalCountries().coerceAtLeast(1)
    val completedCountries = ProgressionQuery.completedCountries(gameState)
    val xpSnapshot = PlayerLevelProgression.snapshot(totalXp = player.xp, storedLevel = player.level)
    val travelRank = rememberTravelRank(xpSnapshot.level)

    Scaffold(
        topBar = { TopStatusBar(onSettingsClick = {}) },
        bottomBar = { BottomNavigationBar(currentTab = "profile", onTabSelected = onTabSelected) },
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
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                item {
                    ProfileHeroCard(
                        username = player.username,
                        level = xpSnapshot.level,
                        xp = xpSnapshot.xpIntoCurrentLevel,
                        xpToNext = xpSnapshot.xpForNextLevel,
                        travelRank = travelRank,
                        completedLevels = completedLevels,
                        completedCountries = completedCountries,
                        totalCountries = totalCountries
                    )
                }
                item {
                    Text(
                        text = "PLAYER STATISTICS",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.3.sp
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        ProfileStatCard("Stars", player.totalStars.toString(), Icons.Default.Star, PremiumColors.Gold, Modifier.weight(1f))
                        ProfileStatCard("Coins", player.coins.toString(), Icons.Default.Star, Color(0xFF72A8FF), Modifier.weight(1f))
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        ProfileStatCard("Levels", completedLevels.toString(), Icons.Default.EmojiEvents, Color(0xFF81C784), Modifier.weight(1f))
                        ProfileStatCard("Countries", "$completedCountries/$totalCountries", Icons.Default.EmojiEvents, PremiumColors.GoldLight, Modifier.weight(1f))
                    }
                }
                item {
                    ProfileSummaryCard(
                        completedLevels = completedLevels,
                        completedCountries = completedCountries,
                        totalCountries = totalCountries,
                        highestCombo = stats.highestCombo,
                        rank = travelRank
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeroCard(
    username: String,
    level: Int,
    xp: Int,
    xpToNext: Int,
    travelRank: String,
    completedLevels: Int,
    completedCountries: Int,
    totalCountries: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .premiumPanel()
            .border(1.dp, PremiumColors.Gold.copy(alpha = 0.35f), PremiumShapes.PanelShape),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(PremiumColors.Gold, PremiumColors.GoldDark)))
                        .border(2.dp, PremiumColors.GoldLight.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👨‍🍳", fontSize = 40.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = username.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "TRAVEL RANK · $travelRank",
                        color = PremiumColors.Gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Level $level  •  XP $xp / $xpToNext",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((xp.toFloat() / xpToNext.toFloat()).coerceIn(0f, 1f))
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Brush.horizontalGradient(listOf(PremiumColors.GoldLight, PremiumColors.Gold)))
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ProfileBadge("Traveler", travelRank)
                ProfileBadge("Wins", completedLevels.toString())
                ProfileBadge("Countries", "$completedCountries/$totalCountries")
            }
        }
    }
}

@Composable
private fun ProfileStatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(PremiumColors.DarkSlate.copy(alpha = 0.96f), PremiumColors.DeepNavy.copy(alpha = 0.96f))
                    )
                )
                .padding(16.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(10.dp))
            Text(text = value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 25.sp)
            Text(text = label.uppercase(), color = Color.White.copy(alpha = 0.72f), fontWeight = FontWeight.Bold, fontSize = 9.sp, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun ProfileSummaryCard(
    completedLevels: Int,
    completedCountries: Int,
    totalCountries: Int,
    highestCombo: Int,
    rank: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .premiumPanel()
            .border(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.25f), PremiumShapes.PanelShape),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "ACHIEVEMENT SNAPSHOT",
                color = PremiumColors.Gold,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            SummaryLine("Levels completed", completedLevels.toString())
            SummaryLine("Countries unlocked", "$completedCountries / $totalCountries")
            SummaryLine("Highest combo", highestCombo.toString())
            SummaryLine("Travel rank", rank)
        }
    }
}

@Composable
private fun ProfileBadge(title: String, value: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = Color.White.copy(alpha = 0.58f), fontSize = 9.sp, letterSpacing = 0.8.sp)
            Text(text = value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

private fun rememberTravelRank(level: Int): String {
    return when {
        level >= 30 -> "World Legend"
        level >= 20 -> "Globe Master"
        level >= 10 -> "Explorer"
        else -> "Rising Chef"
    }
}
