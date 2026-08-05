package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mahmodhota.worldfood3dadventure.game.progress.CountryProgress
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionQuery
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.france.FranceFoodBookEntries
import com.mahmodhota.worldfood3dadventure.game.world.france.FrancePresentation
import com.mahmodhota.worldfood3dadventure.ui.match3.components.BottomNavigationBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumColors
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumGameBackdrop
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumShapes
import com.mahmodhota.worldfood3dadventure.ui.match3.components.TopStatusBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.premiumPanel

private enum class RewardState { AVAILABLE, CLAIMED, LOCKED, COMING_SOON }

private data class CountryRewardUi(
    val countryId: String,
    val countryName: String,
    val flag: String,
    val stars: Int,
    val levelsCompleted: Int,
    val totalLevels: Int,
    val state: RewardState
)

@Composable
fun RewardsScreen(
    onTabSelected: (String) -> Unit,
    progressViewModel: GameProgressViewModel = viewModel()
) {
    val gameState by progressViewModel.gameState.collectAsState()
    val unlockedCountries = ProgressionQuery.unlockedCountries(gameState)
    val completedCountries = ProgressionQuery.completedCountries(gameState)
    val totalCountries = ProgressionQuery.totalCountries().coerceAtLeast(1)
    val totalStars = ProgressionQuery.totalStarsEarned(gameState)
    val coins = ProgressionQuery.totalCoins(gameState)
    val starsToNextUnlock = ProgressionQuery.starsNeededForNextUnlock(gameState)
    val franceMilestones = FrancePresentation.milestones(gameState)
    val franceProgress = ProgressionQuery.countryProgressFor(gameState, "france")

    val countryRewards = remember(gameState) {
        ProgressionQuery.countryProgressMap(gameState)
            .values
            .sortedBy { it.levelId }
            .mapNotNull { progress ->
                LevelRegistry.getCountry(progress.levelId)?.let { country ->
                    progress.toRewardUi(
                        countryName = country.metadata.displayName,
                        flag = country.metadata.flagEmoji
                    )
                }
            }
    }

    val availableRewards = countryRewards.count { it.state == RewardState.AVAILABLE }
    val claimedRewards = countryRewards.count { it.state == RewardState.CLAIMED }
    val lockedRewards = countryRewards.count { it.state == RewardState.LOCKED || it.state == RewardState.COMING_SOON }

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

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                val compact = maxWidth < 390.dp
                val horizontalGap = if (compact) 8.dp else 10.dp

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 840.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    item {
                        RewardsHeroCard(
                            totalStars = totalStars,
                            coins = coins,
                            unlockedCountries = unlockedCountries,
                            totalCountries = totalCountries,
                            availableRewards = availableRewards,
                            compact = compact
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(horizontalGap), modifier = Modifier.fillMaxWidth()) {
                            RewardMetricCard(
                                label = "Next Unlock",
                                value = if (starsToNextUnlock > 0) "$starsToNextUnlock stars left" else "Ready now",
                                icon = Icons.Default.Star,
                                tint = PremiumColors.Gold,
                                modifier = Modifier.weight(1f),
                                compact = compact
                            )
                            RewardMetricCard(
                                label = "Claimed Rewards",
                                value = claimedRewards.toString(),
                                icon = Icons.Default.CheckCircle,
                                tint = Color(0xFF81C784),
                                modifier = Modifier.weight(1f),
                                compact = compact
                            )
                        }
                    }
                    item {
                        DailyRewardStrip(compact = compact)
                    }
                    item {
                        FranceRewardsPanel(
                            progress = franceProgress,
                            milestones = franceMilestones,
                            compact = compact
                        )
                    }
                    item {
                        Text(
                            text = "COUNTRY REWARD TRACKER",
                            color = Color.White,
                            fontSize = if (compact) 12.sp else 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                    }
                    if (countryRewards.isEmpty()) {
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
                                        text = "No reward progress yet. Complete levels to unlock country rewards.",
                                        color = Color.White.copy(alpha = 0.82f),
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(countryRewards, key = { it.countryId }) { reward ->
                            RewardCountryCard(
                                reward = reward,
                                compact = compact
                            )
                        }
                    }
                    item {
                        if (lockedRewards > 0) {
                            Text(
                                text = "$lockedRewards rewards remain locked until their countries are unlocked.",
                                color = Color.White.copy(alpha = 0.72f),
                                fontSize = if (compact) 11.sp else 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun CountryProgress.toRewardUi(
    countryName: String,
    flag: String
): CountryRewardUi {
    val totalLevels = levels.size.coerceAtLeast(1)
    val levelsCompleted = levels.count { it.isCompleted }
    val state = when {
        LevelRegistry.getCountry(levelId)?.isComingSoon == true -> RewardState.COMING_SOON
        isCompleted -> RewardState.CLAIMED
        isUnlocked -> RewardState.AVAILABLE
        else -> RewardState.LOCKED
    }
    return CountryRewardUi(
        countryId = levelId,
        countryName = countryName,
        flag = flag,
        stars = totalStars,
        levelsCompleted = levelsCompleted,
        totalLevels = totalLevels,
        state = state
    )
}

@Composable
private fun FranceRewardsPanel(
    progress: CountryProgress,
    milestones: List<com.mahmodhota.worldfood3dadventure.game.world.france.FranceMilestoneUi>,
    compact: Boolean
) {
    val totalLevels = 15
    val completedLevels = progress.levels.count { it.isCompleted }
    val completionPercent = (completedLevels * 100) / totalLevels
    val foodsDiscovered = ((completedLevels * FranceFoodBookEntries.entries.size) + totalLevels - 1) / totalLevels
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .premiumPanel()
            .border(1.dp, PremiumColors.Gold.copy(alpha = 0.28f), PremiumShapes.PanelShape),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(if (compact) 14.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "FRANCE REWARDS",
                color = PremiumColors.Gold,
                fontSize = if (compact) 14.sp else 15.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.1.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                RewardMetricCard(
                    label = "France",
                    value = "$completionPercent%",
                    icon = Icons.Default.EmojiEvents,
                    tint = PremiumColors.Gold,
                    modifier = Modifier.weight(1f),
                    compact = compact
                )
                RewardMetricCard(
                    label = "Foods",
                    value = "$foodsDiscovered / ${FranceFoodBookEntries.entries.size}",
                    icon = Icons.Default.CardGiftcard,
                    tint = Color(0xFF72A8FF),
                    modifier = Modifier.weight(1f),
                    compact = compact
                )
            }
            milestones.forEach { milestone ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (milestone.achieved) PremiumColors.Gold.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (milestone.achieved) PremiumColors.Gold.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.12f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = milestone.title,
                                color = if (milestone.achieved) PremiumColors.Gold else Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (milestone.achieved) "UNLOCKED" else "LOCKED",
                                color = if (milestone.achieved) PremiumColors.Gold else Color.White.copy(alpha = 0.52f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = milestone.description,
                            color = Color.White.copy(alpha = if (milestone.achieved) 0.8f else 0.5f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardsHeroCard(
    totalStars: Int,
    coins: Int,
    unlockedCountries: Int,
    totalCountries: Int,
    availableRewards: Int,
    compact: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .premiumPanel()
            .border(1.dp, PremiumColors.Gold.copy(alpha = 0.3f), PremiumShapes.PanelShape),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.padding(if (compact) 14.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "REWARDS",
                color = PremiumColors.Gold,
                fontSize = if (compact) 24.sp else 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.3.sp
            )
            Text(
                text = "Available rewards are highlighted in gold. Claimed rewards are marked in green.",
                color = Color.White.copy(alpha = 0.76f),
                fontSize = if (compact) 12.sp else 13.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                RewardMetricCard("Stars", totalStars.toString(), Icons.Default.Star, PremiumColors.Gold, Modifier.weight(1f), compact)
                RewardMetricCard("Coins", coins.toString(), Icons.Default.MonetizationOn, PremiumColors.GoldLight, Modifier.weight(1f), compact)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                RewardMetricCard("Unlocked", "$unlockedCountries / $totalCountries", Icons.Default.CardGiftcard, Color(0xFF72A8FF), Modifier.weight(1f), compact)
                RewardMetricCard("Available", availableRewards.toString(), Icons.Default.EmojiEvents, Color(0xFFF6C453), Modifier.weight(1f), compact)
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
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    compact: Boolean
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
                .padding(if (compact) 12.dp else 14.dp)
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(if (compact) 20.dp else 24.dp))
            Spacer(Modifier.height(8.dp))
            Text(text = value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = if (compact) 18.sp else 22.sp, maxLines = 1)
            Text(text = label.uppercase(), color = Color.White.copy(alpha = 0.72f), fontWeight = FontWeight.Bold, fontSize = 9.sp, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun RewardCountryCard(
    reward: CountryRewardUi,
    compact: Boolean
) {
    val stateColor = when (reward.state) {
        RewardState.AVAILABLE -> PremiumColors.Gold
        RewardState.CLAIMED -> Color(0xFF81C784)
        RewardState.LOCKED -> Color(0xFF8FA1B7)
        RewardState.COMING_SOON -> Color(0xFF9EACC0)
    }
    val stateText = when (reward.state) {
        RewardState.AVAILABLE -> "AVAILABLE"
        RewardState.CLAIMED -> "CLAIMED"
        RewardState.LOCKED -> "LOCKED"
        RewardState.COMING_SOON -> "COMING SOON"
    }
    val buttonText = when (reward.state) {
        RewardState.AVAILABLE -> "Play to Claim"
        RewardState.CLAIMED -> "Claimed"
        RewardState.LOCKED -> "Locked"
        RewardState.COMING_SOON -> "Coming Soon"
    }
    val buttonEnabled = reward.state == RewardState.AVAILABLE

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, stateColor.copy(alpha = 0.28f))
    ) {
        Column(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(PremiumColors.DarkSlate, PremiumColors.DeepNavy)))
                .padding(if (compact) 12.dp else 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = reward.flag, fontSize = if (compact) 20.sp else 22.sp)
                    Column {
                        Text(
                            text = reward.countryName.uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (compact) 13.sp else 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${reward.levelsCompleted}/${reward.totalLevels} levels completed",
                            color = Color.White.copy(alpha = 0.72f),
                            fontSize = 11.sp
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = stateColor.copy(alpha = 0.16f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, stateColor.copy(alpha = 0.42f))
                ) {
                    Text(
                        text = stateText,
                        color = stateColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.9.sp,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "★ ${reward.stars} stars",
                    color = PremiumColors.Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 13.sp else 14.sp
                )
                Button(
                    onClick = {},
                    enabled = buttonEnabled,
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (reward.state) {
                            RewardState.AVAILABLE -> PremiumColors.Gold.copy(alpha = 0.92f)
                            RewardState.CLAIMED -> Color(0xFF2D5E43)
                            RewardState.LOCKED -> Color(0xFF2A3544)
                            RewardState.COMING_SOON -> Color(0xFF233042)
                        },
                        contentColor = when (reward.state) {
                            RewardState.AVAILABLE -> Color(0xFF1D1A12)
                            RewardState.CLAIMED -> Color(0xFFD8F6E2)
                            RewardState.LOCKED -> Color(0xFFC7D2DF)
                            RewardState.COMING_SOON -> Color(0xFFC7D2DF)
                        },
                        disabledContainerColor = when (reward.state) {
                            RewardState.AVAILABLE -> PremiumColors.Gold.copy(alpha = 0.45f)
                            RewardState.CLAIMED -> Color(0xFF2D5E43)
                            RewardState.LOCKED -> Color(0xFF2A3544)
                            RewardState.COMING_SOON -> Color(0xFF233042)
                        },
                        disabledContentColor = when (reward.state) {
                            RewardState.AVAILABLE -> Color(0xFF1D1A12).copy(alpha = 0.8f)
                            RewardState.CLAIMED -> Color(0xFFD8F6E2)
                            RewardState.LOCKED -> Color(0xFFC7D2DF)
                            RewardState.COMING_SOON -> Color(0xFFC7D2DF)
                        }
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    if (reward.state == RewardState.CLAIMED) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.size(4.dp))
                    } else if (reward.state == RewardState.LOCKED || reward.state == RewardState.COMING_SOON) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.size(4.dp))
                    }
                    Text(
                        text = buttonText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyRewardStrip(compact: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(Color(0xFF142B49), Color(0xFF0A1527))))
                .padding(if (compact) 12.dp else 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 52.dp else 58.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(PremiumColors.Gold, PremiumColors.GoldDark))),
                contentAlignment = Alignment.Center
            ) {
                Text("🎁", fontSize = if (compact) 20.sp else 22.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("DAILY REWARD", color = PremiumColors.Gold, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Text(
                    "Return daily for bonus coins and boosters.",
                    color = Color.White.copy(alpha = 0.76f),
                    fontSize = if (compact) 11.sp else 12.sp
                )
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
