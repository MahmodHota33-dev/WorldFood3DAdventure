package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager
import com.mahmodhota.worldfood3dadventure.data.audio.SfxType
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressManager
import com.mahmodhota.worldfood3dadventure.game.progress.CountryProgress
import com.mahmodhota.worldfood3dadventure.game.progress.DailyMission
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionQuery
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.ui.match3.components.*

private enum class RewardState { LOCKED, AVAILABLE, CLAIMED, COMING_SOON }

private data class RewardUi(
    val countryId: String,
    val countryName: String,
    val flag: String,
    val stars: Int,
    val maxStars: Int,
    val levelsCompleted: Int,
    val totalLevels: Int,
    val state: RewardState
)

@Composable
fun RewardsScreen(
    onTabSelected: (String) -> Unit,
    onSettingsClick: () -> Unit,
    progressViewModel: GameProgressViewModel = viewModel()
) {
    val gameState by progressViewModel.gameState.collectAsState()
    val totalStars = ProgressionQuery.totalStarsEarned(gameState)
    val coins = ProgressionQuery.totalCoins(gameState)
    val starsToNextUnlock = ProgressionQuery.starsNeededForNextUnlock(gameState)
    val totalCountries = ProgressionQuery.totalCountries()
    val completedCountries = ProgressionQuery.completedCountries(gameState)
    val journey = gameState.dailyJourney

    val countryRewards = remember(gameState) {
        ProgressionQuery.countryProgressMap(gameState)
            .values
            .sortedBy { entry -> 
                LevelRegistry.allCountryIds.indexOf(entry.levelId).let { if (it == -1) 999 else it }
            }
            .mapNotNull { progress ->
                LevelRegistry.getCountry(progress.levelId)?.let { country ->
                    progress.toRewardUi(
                        countryName = country.metadata.displayName,
                        flag = country.metadata.flagEmoji
                    )
                }
            }
    }

    Scaffold(
        topBar = { TopStatusBar(onSettingsClick = onSettingsClick) },
        bottomBar = { BottomNavigationBar(currentTab = "rewards", onTabSelected = onTabSelected) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PremiumGameBackdrop()

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                val compact = maxWidth < 390.dp

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 800.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        RewardsHeroCard(
                            totalStars = totalStars,
                            coins = coins,
                            starsToNext = starsToNextUnlock,
                            streak = journey.streak,
                            compact = compact
                        )
                    }

                    // P10-W: Today's Journey (Daily Missions)
                    item {
                        Text(
                            text = "TODAY'S JOURNEY",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    items(journey.missions, key = { it.id }) { mission ->
                        DailyMissionCard(mission = mission, compact = compact) {
                            GlobalSystemManager.audio.playSfx(SfxType.COIN_COLLECT)
                            GameProgressManager.repository.claimDailyMissionReward(mission.id)
                        }
                    }

                    if (completedCountries >= totalCountries) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = PremiumColors.Gold.copy(alpha = 0.15f),
                                border = BorderStroke(2.dp, PremiumColors.Gold),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            ) {
                                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🏆", fontSize = 64.sp)
                                    Spacer(Modifier.height(16.dp))
                                    Text("GLOBAL MASTER CHEF", color = PremiumColors.Gold, fontWeight = FontWeight.Black, fontSize = 22.sp, letterSpacing = 1.5.sp)
                                    Text("You have explored every corner of the world!", color = Color.White, textAlign = TextAlign.Center, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "MILESTONE REWARDS",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    items(countryRewards) { reward ->
                        RewardItemCard(reward = reward, compact = compact)
                    }
                }
            }
        }
    }
}

private fun CountryProgress.toRewardUi(countryName: String, flag: String): RewardUi {
    val totalLevels = levels.size.coerceAtLeast(1)
    val levelsCompleted = levels.count { it.isCompleted }
    val maxStars = totalLevels * 3
    val state = when {
        LevelRegistry.getCountry(levelId)?.isComingSoon == true -> RewardState.COMING_SOON
        isCompleted -> RewardState.CLAIMED
        isUnlocked -> RewardState.AVAILABLE
        else -> RewardState.LOCKED
    }
    return RewardUi(
        countryId = levelId,
        countryName = countryName,
        flag = flag,
        stars = totalStars,
        maxStars = maxStars,
        levelsCompleted = levelsCompleted,
        totalLevels = totalLevels,
        state = state
    )
}

@Composable
private fun RewardsHeroCard(
    totalStars: Int,
    coins: Int,
    starsToNext: Int,
    streak: Int,
    compact: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .premiumPanel()
            .border(1.dp, PremiumColors.Gold.copy(alpha = 0.35f), PremiumShapes.PanelShape),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(if (compact) 16.dp else 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(if (compact) 60.dp else 72.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFFE91E63), Color(0xFF880E4F))))
                        .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White, modifier = Modifier.size(if (compact) 30.dp else 36.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("TRAVEL REWARDS", color = PremiumColors.Gold, fontWeight = FontWeight.Black, fontSize = if (compact) 18.sp else 22.sp, letterSpacing = 1.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥 $streak DAY STREAK", color = Color(0xFFFFA726), fontSize = 11.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.width(8.dp))
                        Text("World Traveler Progress", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RewardMetricChip("Total Stars", totalStars.toString(), Icons.Default.Star, PremiumColors.Gold, Modifier.weight(1f))
                RewardMetricChip("Coins", coins.toString(), Icons.Default.EmojiEvents, Color(0xFF72A8FF), Modifier.weight(1f))
            }

            if (starsToNext > 0) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🚀", fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Next country unlocks in $starsToNext stars!",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyMissionCard(mission: DailyMission, compact: Boolean, onClaim: () -> Unit) {
    val progress = (mission.currentProgress.toFloat() / mission.target.toFloat()).coerceIn(0f, 1f)
    val accent = if (mission.isCompleted) PremiumColors.Gold else Color.White.copy(alpha = 0.4f)
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .premiumPanel()
            .border(1.dp, accent.copy(alpha = 0.2f), PremiumShapes.PanelShape),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                        .border(1.dp, accent.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when(mission.type) {
                        com.mahmodhota.worldfood3dadventure.game.progress.MissionType.TRAVEL -> "✈️"
                        com.mahmodhota.worldfood3dadventure.game.progress.MissionType.FOOD -> "🍲"
                        com.mahmodhota.worldfood3dadventure.game.progress.MissionType.STARS -> "⭐"
                        else -> "🎯"
                    }
                    Text(icon, fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(mission.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(mission.description, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                }
                
                if (mission.isCompleted && !mission.isClaimed) {
                    Button(
                        onClick = onClaim,
                        colors = ButtonDefaults.buttonColors(containerColor = PremiumColors.Gold),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("CLAIM", color = PremiumColors.DeepNavy, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                } else if (mission.isClaimed) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF66D19D), modifier = Modifier.size(24.dp))
                } else {
                    Text("${mission.currentProgress} / ${mission.target}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(if (mission.isCompleted) PremiumColors.Gold else Color(0xFF4AADCC))
                )
            }
            
            if (!mission.isClaimed) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (mission.rewardXp > 0) MissionRewardChip("XP", mission.rewardXp)
                    if (mission.rewardCoins > 0) MissionRewardChip("COINS", mission.rewardCoins)
                    mission.rewardBooster?.let { MissionRewardChip(it.name, 1) }
                }
            }
        }
    }
}

@Composable
private fun MissionRewardChip(label: String, value: Int) {
    Text(
        text = "+$value $label",
        color = PremiumColors.Gold.copy(alpha = 0.8f),
        fontSize = 9.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun RewardItemCard(reward: RewardUi, compact: Boolean) {
    val statusColor = when (reward.state) {
        RewardState.CLAIMED -> Color(0xFF66D19D)
        RewardState.AVAILABLE -> PremiumColors.Gold
        else -> Color.Gray
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .premiumPanel()
            .border(1.dp, statusColor.copy(alpha = 0.25f), PremiumShapes.PanelShape),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(if (compact) 12.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 50.dp else 56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, statusColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(reward.flag, fontSize = if (compact) 24.sp else 28.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(reward.countryName.uppercase(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Text(
                    text = "${reward.levelsCompleted}/${reward.totalLevels} Levels Completed",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (reward.levelsCompleted.toFloat() / reward.totalLevels.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                    color = statusColor,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                val stateLabel = when (reward.state) {
                    RewardState.CLAIMED -> "CLAIMED"
                    RewardState.AVAILABLE -> "IN PROGRESS"
                    RewardState.COMING_SOON -> "COMING SOON"
                    RewardState.LOCKED -> "LOCKED"
                }
                Text(stateLabel, color = statusColor, fontWeight = FontWeight.Black, fontSize = 10.sp, letterSpacing = 1.sp)
                Text("${reward.stars} ★", color = PremiumColors.Gold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun RewardMetricChip(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text(label.uppercase(), color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
        }
    }
}
