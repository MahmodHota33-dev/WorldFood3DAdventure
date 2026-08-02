package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
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
fun PassportScreen(
    onTabSelected: (String) -> Unit,
    progressViewModel: GameProgressViewModel = viewModel()
) {
    val gameState by progressViewModel.gameState.collectAsState()
    val countries = gameState.countries
    val total = ProgressionQuery.totalCountries().coerceAtLeast(1)
    val visited = ProgressionQuery.visitedCountries(gameState)
    val visitedProgress = visited.toFloat() / total.toFloat()
    val stars = ProgressionQuery.totalStarsEarned(gameState)

    Scaffold(
        topBar = { TopStatusBar(onSettingsClick = {}) },
        bottomBar = { BottomNavigationBar(currentTab = "book", onTabSelected = onTabSelected) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PremiumGameBackdrop()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PassportHeroCard(
                    visited = visited,
                    total = total,
                    stars = stars,
                    progress = visitedProgress
                )

                Text(
                    text = "COUNTRY STAMPS",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    letterSpacing = 1.2.sp
                )

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 128.dp),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(ProgressionQuery.countryProgressMap(gameState).values.toList().sortedBy { it.levelId }) { countryProgress ->
                        val country = remember(countryProgress.levelId) {
                            com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry.getCountry(countryProgress.levelId)
                        } ?: return@items
                        PassportStampCard(
                            countryName = country.metadata.displayName,
                            flag = country.metadata.flagEmoji,
                            description = country.metadata.travelDescription,
                            isUnlocked = countryProgress.isUnlocked,
                            isCompleted = countryProgress.isCompleted,
                            stars = countryProgress.totalStars
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PassportHeroCard(
    visited: Int,
    total: Int,
    stars: Int,
    progress: Float
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
                        .size(94.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(PremiumColors.Gold, PremiumColors.GoldDark)))
                        .border(2.dp, PremiumColors.GoldLight.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("PASSPORT", color = PremiumColors.DeepNavy, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        Text("✈️", fontSize = 30.sp)
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "WORLD PASSPORT",
                        color = PremiumColors.Gold,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.4.sp
                    )
                    Text(
                        text = "$visited / $total countries visited",
                        color = Color.White.copy(alpha = 0.86f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = PremiumColors.Gold,
                        trackColor = Color.White.copy(alpha = 0.12f)
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                PassportInfoChip("STAMPS", visited.toString(), Modifier.weight(1f))
                PassportInfoChip("TOTAL", total.toString(), Modifier.weight(1f))
                PassportInfoChip("STARS", stars.toString(), Modifier.weight(1f))
            }
            Text(
                text = "Issued for premium world travel. Collect stamps to complete every route.",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = PremiumColors.Gold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = "TRAVEL PASSPORT",
                        color = PremiumColors.Gold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                Text(
                    text = "No. 3D-${visited}${total}",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
private fun PassportStampCard(
    countryName: String,
    flag: String,
    description: String,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    stars: Int
) {
    val cardBrush = if (isCompleted) {
        Brush.linearGradient(listOf(Color(0xFF193E32), Color(0xFF0D1E2E)))
    } else if (isUnlocked) {
        Brush.linearGradient(listOf(PremiumColors.DarkSlate, PremiumColors.DeepNavy))
    } else {
        Brush.linearGradient(listOf(Color(0xFF111827), Color(0xFF070B13)))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.86f),
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCompleted) PremiumColors.Gold.copy(alpha = 0.5f) else PremiumColors.WhiteLow.copy(alpha = 0.25f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardBrush)
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .then(
                                if (isCompleted) {
                                    Modifier.background(Brush.radialGradient(listOf(PremiumColors.Gold, PremiumColors.GoldDark)))
                                } else {
                                    Modifier.background(Color.White.copy(alpha = 0.08f))
                                }
                            )
                            .border(
                                1.dp,
                                if (isCompleted) PremiumColors.GoldLight.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = flag, fontSize = 24.sp, color = if (isUnlocked) Color.Unspecified else Color.White.copy(alpha = 0.2f))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = countryName.uppercase(),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isCompleted) "COMPLETED" else if (isUnlocked) "VISITED" else "LOCKED",
                            color = if (isCompleted) PremiumColors.Gold else Color.White.copy(alpha = 0.72f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isCompleted) PremiumColors.Gold.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCompleted) PremiumColors.Gold.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.14f)
                    )
                ) {
                    Text(
                        text = if (isCompleted) "STAMPED" else if (isUnlocked) "VISITED" else "LOCKED",
                        color = if (isCompleted) PremiumColors.Gold else Color.White.copy(alpha = 0.75f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Text(
                    text = description,
                    color = Color.White.copy(alpha = if (isUnlocked) 0.78f else 0.38f),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) { index ->
                        Text(
                            text = "★",
                            fontSize = 10.sp,
                            color = if (index < stars) PremiumColors.Gold else Color.White.copy(alpha = 0.25f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PassportInfoChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.07f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Text(text = label, color = Color.White.copy(alpha = 0.55f), fontSize = 8.sp, letterSpacing = 0.8.sp)
            Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
