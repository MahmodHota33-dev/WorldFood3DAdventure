package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mahmodhota.worldfood3dadventure.data.progress.model.PersistedGameState
import com.mahmodhota.worldfood3dadventure.game.progress.Achievement
import com.mahmodhota.worldfood3dadventure.game.progress.AchievementRegistry
import com.mahmodhota.worldfood3dadventure.game.progress.PlayerLevelProgression
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionQuery
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.Continent
import com.mahmodhota.worldfood3dadventure.ui.match3.components.BottomNavigationBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumColors
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumGameBackdrop
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumShapes
import com.mahmodhota.worldfood3dadventure.ui.match3.components.TopStatusBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.premiumPanel

@Composable
fun ProfileScreenV2(
    onTabSelected: (String) -> Unit,
    onSettingsClick: () -> Unit,
    progressViewModel: GameProgressViewModel = viewModel()
) {
    val gameState by progressViewModel.gameState.collectAsState()
    val player = gameState.player
    val stats = gameState.stats
    val completedLevels = ProgressionQuery.totalLevelsCompleted(gameState)
    val totalCountries = ProgressionQuery.totalCountries().coerceAtLeast(1)
    val visitedCountries = ProgressionQuery.visitedCountries(gameState)
    val completedCountries = ProgressionQuery.completedCountries(gameState)
    val unlockedAchievements = gameState.unlockedAchievements
    
    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }

    val xpSnapshot = PlayerLevelProgression.snapshot(totalXp = player.xp, storedLevel = player.level)
    val travelRank = travelRankForLevel(xpSnapshot.level)
    val xpProgress = (xpSnapshot.xpIntoCurrentLevel.toFloat() / xpSnapshot.xpForNextLevel.toFloat()).coerceIn(0f, 1f)

    Scaffold(
        topBar = { TopStatusBar(onSettingsClick = onSettingsClick) },
        bottomBar = { BottomNavigationBar(currentTab = "profile", onTabSelected = onTabSelected) },
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
                val metricGap = if (compact) 8.dp else 10.dp

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 840.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        ProfileHeroCard(
                            username = player.username,
                            level = xpSnapshot.level,
                            xp = xpSnapshot.xpIntoCurrentLevel,
                            xpToNext = xpSnapshot.xpForNextLevel,
                            xpProgress = xpProgress,
                            travelRank = travelRank,
                            visitedCountries = visitedCountries,
                            completedCountries = completedCountries,
                            totalCountries = totalCountries,
                            compact = compact
                        )
                    }
                    
                    item {
                        Text(
                            text = "GLOBAL JOURNEY",
                            color = Color.White,
                            fontSize = if (compact) 12.sp else 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                    }
                    
                    item {
                        ContinentProgressSection(gameState, compact)
                    }

                    item {
                        Text(
                            text = "PLAYER DASHBOARD",
                            color = Color.White,
                            fontSize = if (compact) 12.sp else 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(metricGap), modifier = Modifier.fillMaxWidth()) {
                            ProfileStatCard("Stars", player.totalStars.toString(), Icons.Default.Star, PremiumColors.Gold, Modifier.weight(1f), compact)
                            ProfileStatCard("Coins", player.coins.toString(), Icons.Default.MonetizationOn, Color(0xFF72A8FF), Modifier.weight(1f), compact)
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(metricGap), modifier = Modifier.fillMaxWidth()) {
                            ProfileStatCard("Foods", "${ProgressionQuery.totalFoodsDiscovered(gameState)}/${ProgressionQuery.totalAvailableFoods()}", Icons.Default.Restaurant, Color(0xFFFFA726), Modifier.weight(1f), compact)
                            ProfileStatCard("Achievements", "${unlockedAchievements.size}/${AchievementRegistry.achievements.size}", Icons.Default.EmojiEvents, PremiumColors.GoldLight, Modifier.weight(1f), compact)
                        }
                    }
                    
                    item {
                        AchievementSection(unlockedAchievements, compact) {
                            selectedAchievement = it
                        }
                    }

                    item {
                        ProfileSummaryCard(
                            completedLevels = completedLevels,
                            visitedCountries = visitedCountries,
                            completedCountries = completedCountries,
                            totalCountries = totalCountries,
                            highestCombo = stats.highestCombo,
                            favoriteFood = ProgressionQuery.getMostCollectedFood(gameState)?.name ?: "None",
                            playTimeText = formatPlayTime(stats.totalPlayTimeMillis),
                            rank = travelRank,
                            compact = compact
                        )
                    }
                }
            }
        }
    }

    if (selectedAchievement != null) {
        AchievementDetailDialog(
            achievement = selectedAchievement!!,
            isUnlocked = selectedAchievement!!.id in unlockedAchievements,
            onDismiss = { selectedAchievement = null }
        )
    }
}

@Composable
private fun ProfileHeroCard(
    username: String,
    level: Int,
    xp: Int,
    xpToNext: Int,
    xpProgress: Float,
    travelRank: String,
    visitedCountries: Int,
    completedCountries: Int,
    totalCountries: Int,
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
            modifier = Modifier.padding(if (compact) 14.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(if (compact) 80.dp else 92.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(PremiumColors.Gold, PremiumColors.GoldDark)))
                        .border(2.dp, PremiumColors.GoldLight.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👨‍🍳", fontSize = if (compact) 34.sp else 40.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = username.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = if (compact) 18.sp else 22.sp,
                        letterSpacing = 1.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "TRAVEL RANK · $travelRank",
                        color = PremiumColors.Gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (compact) 11.sp else 12.sp,
                        letterSpacing = 0.8.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Level $level",
                            color = Color.White.copy(alpha = 0.84f),
                            fontSize = if (compact) 11.sp else 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "XP $xp / $xpToNext",
                            color = Color.White.copy(alpha = 0.72f),
                            fontSize = if (compact) 10.sp else 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(xpProgress)
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Brush.horizontalGradient(listOf(PremiumColors.GoldLight, PremiumColors.Gold)))
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ProfileBadge("Visited", "$visitedCountries/$totalCountries", Modifier.weight(1f))
                ProfileBadge("Completed", completedCountries.toString(), Modifier.weight(1f))
                ProfileBadge("Level", level.toString(), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProfileStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    compact: Boolean
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
                .padding(if (compact) 12.dp else 14.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(if (compact) 20.dp else 24.dp))
            Spacer(Modifier.height(8.dp))
            Text(text = value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = if (compact) 18.sp else 23.sp, maxLines = 1)
            Text(
                text = label.uppercase(),
                color = Color.White.copy(alpha = 0.72f),
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 1.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ProfileSummaryCard(
    completedLevels: Int,
    visitedCountries: Int,
    completedCountries: Int,
    totalCountries: Int,
    highestCombo: Int,
    favoriteFood: String,
    playTimeText: String,
    rank: String,
    compact: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .premiumPanel()
            .border(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.25f), PremiumShapes.PanelShape),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(if (compact) 14.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "PROFILE SUMMARY",
                color = PremiumColors.Gold,
                fontSize = if (compact) 12.sp else 13.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            SummaryLine("Levels completed", completedLevels.toString())
            SummaryLine("Countries visited", "$visitedCountries / $totalCountries")
            SummaryLine("Countries completed", completedCountries.toString())
            SummaryLine("Highest combo", highestCombo.toString())
            SummaryLine("Favorite food", favoriteFood)
            SummaryLine("Play time", playTimeText)
            SummaryLine("Travel rank", rank)
        }
    }
}

@Composable
private fun ContinentProgressSection(state: PersistedGameState, compact: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth().premiumPanel(),
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Continent.entries.forEach { continent ->
                val countriesInContinent = LevelRegistry.allCountries.filter { it.continent == continent }
                val masteredInContinent = countriesInContinent.count { state.countries[it.levelId]?.isCompleted == true }
                val totalInContinent = countriesInContinent.size.coerceAtLeast(1)
                val progress = masteredInContinent.toFloat() / totalInContinent.toFloat()

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(continent.displayName.uppercase(), color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Black)
                        Text("$masteredInContinent / $totalInContinent", color = PremiumColors.Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f))) {
                        Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(PremiumColors.Gold))
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementSection(unlockedIds: Set<String>, compact: Boolean, onSelect: (Achievement) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().premiumPanel(),
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("RECENT ACHIEVEMENTS", color = PremiumColors.Gold, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            Spacer(Modifier.height(14.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AchievementRegistry.achievements.take(5).forEach { achievement ->
                    val isUnlocked = achievement.id in unlockedIds
                    Surface(
                        onClick = { onSelect(achievement) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isUnlocked) Color.White.copy(alpha = 0.05f) else Color.Transparent,
                        border = if (isUnlocked) androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.2f)) else null
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).background(if (isUnlocked) PremiumColors.Gold.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (isUnlocked) achievement.icon else "🔒", fontSize = 18.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(achievement.title, color = if (isUnlocked) Color.White else Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(achievement.description, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            if (isUnlocked) {
                                Text("🏆", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
            
            TextButton(onClick = { /* Could navigate to full list if needed */ }, modifier = Modifier.align(Alignment.End)) {
                Text("VIEW ALL", color = PremiumColors.Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AchievementDetailDialog(achievement: Achievement, isUnlocked: Boolean, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = PremiumColors.DeepNavy,
            border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier.size(80.dp).background(Color.White.copy(alpha = 0.05f), CircleShape).border(2.dp, if (isUnlocked) PremiumColors.Gold else Color.Gray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (isUnlocked) achievement.icon else "🔒", fontSize = 42.sp)
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(achievement.title.uppercase(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                    Text(achievement.category.name, color = PremiumColors.Gold.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                
                Text(achievement.description, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center, fontSize = 14.sp)
                
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("REWARD", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (achievement.rewardXp > 0) Text("+${achievement.rewardXp} XP", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        if (achievement.rewardCoins > 0) Text("+${achievement.rewardCoins} Coins", color = PremiumColors.Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PremiumColors.Gold)) {
                    Text("CLOSE", fontWeight = FontWeight.Bold, color = PremiumColors.DeepNavy)
                }
            }
        }
    }
}

@Composable
private fun ProfileBadge(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = Color.White.copy(alpha = 0.58f), fontSize = 9.sp, letterSpacing = 0.8.sp, maxLines = 1)
            Text(
                text = value,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun travelRankForLevel(level: Int): String {
    return when {
        level >= 30 -> "World Legend"
        level >= 20 -> "Globe Master"
        level >= 10 -> "Explorer"
        else -> "Rising Chef"
    }
}

private fun formatPlayTime(totalPlayTimeMillis: Long): String {
    val totalSeconds = (totalPlayTimeMillis / 1000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    val hours = minutes / 60L
    val displayMinutes = minutes % 60L
    return if (hours > 0L) {
        String.format("%dh %02dm", hours, displayMinutes)
    } else {
        String.format("%dm %02ds", displayMinutes, seconds)
    }
}
