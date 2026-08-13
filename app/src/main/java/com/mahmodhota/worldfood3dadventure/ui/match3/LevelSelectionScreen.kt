package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager
import com.mahmodhota.worldfood3dadventure.data.audio.SfxType
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3LevelDefinition
import com.mahmodhota.worldfood3dadventure.game.progress.CountryProgress
import com.mahmodhota.worldfood3dadventure.game.progress.Match3LevelProgress
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryDefinition
import com.mahmodhota.worldfood3dadventure.ui.match3.components.*

private data class CountryAccent(
    val start: Color,
    val end: Color,
    val chip: Color
)

private data class LevelUiModel(
    val levelNumber: Int,
    val title: String?,
    val stars: Int,
    val isUnlocked: Boolean,
    val isCompleted: Boolean,
    val isNextRecommended: Boolean,
    val previewFood: FoodTileType?,
    val lockHint: String?
)

@Composable
fun LevelSelectionScreen(
    countryId: String,
    onLevelSelected: (Int) -> Unit,
    onBackToMap: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val progress = ProgressionManager.getCountryProgress(countryId)
    val country = LevelRegistry.getCountry(countryId)
    val accent = remember(countryId) { countryAccentFor(countryId) }
    val levelModels = remember(countryId, country, progress) {
        buildLevelUiModels(country, progress)
    }
    val comingSoon = country?.isComingSoon == true || levelModels.isEmpty()

    val starsEarned = levelModels.sumOf { it.stars }
    val totalLevels = levelModels.size
    val completedLevels = levelModels.count { it.isCompleted }
    val maxStars = totalLevels * 3
    val starProgressTarget = if (maxStars > 0) {
        (starsEarned.toFloat() / maxStars.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val starProgress by animateFloatAsState(
        targetValue = starProgressTarget,
        animationSpec = tween(durationMillis = 700),
        label = "countryStarProgress"
    )

    Scaffold(
        topBar = { TopStatusBar(onSettingsClick = onSettingsClick) },
        bottomBar = { BottomNavigationBar(currentTab = "world", onTabSelected = { onBackToMap() }) },
        containerColor = PremiumColors.DeepNavy
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            val isTabletLayout = maxWidth >= 600.dp
            val navInsetBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            val gridBottomPadding = gridBottomContentPadding(navInsetBottom, isTabletLayout)

            Column(modifier = Modifier.fillMaxSize()) {
            CountryProgressHeader(
                countryId = countryId,
                country = country,
                accent = accent,
                starsEarned = starsEarned,
                maxStars = maxStars,
                totalLevels = totalLevels,
                completedLevels = completedLevels,
                starProgress = starProgress,
                isTabletLayout = isTabletLayout,
                onBackToMap = onBackToMap
            )

            Spacer(Modifier.height(if (isTabletLayout) 8.dp else 12.dp))

            if (comingSoon) {
                ComingSoonCountryCard(
                    countryName = country?.metadata?.displayName ?: countryId.replaceFirstChar { it.uppercase() },
                    countryFlag = country?.metadata?.flagEmoji ?: "🌍",
                    message = country?.metadata?.comingSoonText ?: "This destination is being prepared and will unlock later.",
                    onBackToMap = onBackToMap
                )
            } else {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    val minCellSize = gridMinCellSize(maxWidth)
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = minCellSize),
                        horizontalArrangement = Arrangement.spacedBy(if (isTabletLayout) 12.dp else 10.dp),
                        verticalArrangement = Arrangement.spacedBy(if (isTabletLayout) 12.dp else 10.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = gridBottomPadding)
                    ) {
                        items(levelModels) { level ->
                            LevelCard(
                                level = level,
                                accent = accent,
                                isTabletLayout = isTabletLayout,
                                onClick = { onLevelSelected(level.levelNumber) }
                            )
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun ComingSoonCountryCard(
    countryName: String,
    countryFlag: String,
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
            Text(countryFlag, fontSize = 56.sp)
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
private fun CountryProgressHeader(
    countryId: String,
    country: CountryDefinition?,
    accent: CountryAccent,
    starsEarned: Int,
    maxStars: Int,
    totalLevels: Int,
    completedLevels: Int,
    starProgress: Float,
    isTabletLayout: Boolean,
    onBackToMap: () -> Unit
) {
    val displayName = country?.metadata?.displayName ?: countryId.replaceFirstChar { it.uppercase() }
    val flag = country?.metadata?.flagEmoji ?: "🌍"
    val subtitle = country?.metadata?.travelDescription?.takeIf { it.isNotBlank() } ?: "Culinary Adventure"

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.start.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accent.start.copy(alpha = 0.60f),
                            accent.end.copy(alpha = 0.28f),
                            PremiumColors.DeepNavy
                        )
                    )
                )
                .padding(
                    horizontal = if (isTabletLayout) 14.dp else 14.dp,
                    vertical = if (isTabletLayout) 9.dp else 12.dp
                )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = flag, fontSize = if (isTabletLayout) 28.sp else 30.sp)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName.uppercase(),
                        color = Color.White,
                        fontSize = if (isTabletLayout) 18.sp else 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.0.sp
                    )
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.80f),
                        fontSize = if (isTabletLayout) 11.sp else 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                TextButton(onClick = onBackToMap) {
                    Text("World", color = Color(0xFF8AC6FF))
                }
            }

            Spacer(Modifier.height(if (isTabletLayout) 8.dp else 10.dp))

            Text(
                text = "★ $starsEarned / $maxStars STARS",
                color = PremiumColors.Gold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(if (isTabletLayout) 5.dp else 6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.13f))
                    .semantics { contentDescription = "Country stars $starsEarned out of $maxStars" }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(starProgress)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    PremiumColors.GoldLight,
                                    PremiumColors.Gold
                                )
                            )
                        )
                )
            }
            Spacer(Modifier.height(if (isTabletLayout) 6.dp else 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                HeaderMetricChip(
                    label = "Levels",
                    value = totalLevels.toString(),
                    accent = accent.chip
                )
                HeaderMetricChip(
                    label = "Completed",
                    value = completedLevels.toString(),
                    accent = Color(0xFF50D198)
                )
            }
        }
    }
}

@Composable
private fun HeaderMetricChip(label: String, value: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.06f),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label.uppercase(), color = Color.White.copy(alpha = 0.72f), fontSize = 9.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun LevelCard(
    level: LevelUiModel,
    accent: CountryAccent,
    isTabletLayout: Boolean,
    onClick: () -> Unit
) {
    val cardStateLabel = when {
        !level.isUnlocked -> "locked"
        level.isCompleted -> "completed replayable"
        level.isNextRecommended -> "next recommended"
        else -> "uncompleted"
    }
    val starText = "${level.stars} of 3 stars"
    val contentDesc = "Level ${level.levelNumber} $cardStateLabel, $starText"

    Box(
        modifier = Modifier
            .aspectRatio(if (isTabletLayout) 0.72f else 0.80f)
            .premiumPanel()
            .border(
                when {
                    level.isNextRecommended -> 1.8.dp
                    level.isUnlocked -> 1.3.dp
                    else -> 1.dp
                },
                when {
                    level.isNextRecommended -> PremiumColors.Gold
                    level.isUnlocked -> accent.start.copy(alpha = 0.52f)
                    else -> PremiumColors.WhiteLow.copy(alpha = 0.62f)
                },
                PremiumShapes.PanelShape
            )
            .clickable(enabled = level.isUnlocked) {
                GlobalSystemManager.audio.playSfx(SfxType.BUTTON_CLICK)
                onClick()
            }
            .alpha(if (level.isUnlocked) 1f else 0.72f)
            .semantics { contentDescription = contentDesc },
        contentAlignment = Alignment.Center
    ) {
        val iconContainerSize = if (isTabletLayout) 50.dp else 42.dp
        val iconSize = if (isTabletLayout) 34.dp else 28.dp
        val badgeFont = if (isTabletLayout) 10.sp else 9.sp
        val levelFont = if (isTabletLayout) 13.sp else 12.sp
        val titleFont = if (isTabletLayout) 12.sp else 11.sp
        val starFont = if (isTabletLayout) 14.sp else 13.sp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accent.start.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = if (isTabletLayout) 10.dp else 8.dp, vertical = if (isTabletLayout) 9.dp else 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val badgeText = when {
                level.isNextRecommended -> "NEXT"
                level.isCompleted -> "REPLAY"
                !level.isUnlocked -> "LOCKED"
                else -> null
            }
            if (badgeText != null) {
                Text(
                    text = badgeText,
                    color = if (level.isNextRecommended) PremiumColors.Gold else Color.White.copy(alpha = 0.86f),
                    fontSize = badgeFont,
                    fontWeight = FontWeight.ExtraBold
                )
            } else {
                Spacer(Modifier.height(if (isTabletLayout) 16.dp else 14.dp))
            }

            Box(
                modifier = Modifier
                    .size(iconContainerSize)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.11f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (level.previewFood != null) {
                    FoodIcon(type = level.previewFood, size = iconSize)
                } else {
                    Text("🍽️", fontSize = 20.sp, modifier = Modifier.alpha(0.72f))
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LEVEL ${level.levelNumber}",
                    fontSize = levelFont,
                    color = PremiumColors.Gold,
                    fontWeight = FontWeight.ExtraBold
                )
                if (!level.title.isNullOrBlank()) {
                    Text(
                        text = level.title,
                        fontSize = titleFont,
                        color = Color.White.copy(alpha = 0.90f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Spacer(Modifier.height(if (isTabletLayout) 16.dp else 14.dp))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(3) { i ->
                    Text(
                        text = if (i < level.stars) "★" else "☆",
                        color = if (level.isUnlocked) {
                            if (i < level.stars) PremiumColors.Gold else Color.White.copy(alpha = 0.35f)
                        } else Color.White.copy(alpha = 0.30f),
                        fontSize = starFont
                    )
                }
            }

            if (!level.isUnlocked) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔒", fontSize = if (isTabletLayout) 16.sp else 15.sp, modifier = Modifier.alpha(0.8f))
                    level.lockHint?.let {
                        Text(
                            text = it,
                            color = Color.White.copy(alpha = 0.66f),
                            fontSize = 9.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Spacer(Modifier.height(if (isTabletLayout) 20.dp else 18.dp))
            }
        }
    }
}

internal fun gridMinCellSize(maxWidth: androidx.compose.ui.unit.Dp): androidx.compose.ui.unit.Dp =
    when {
        maxWidth >= 1100.dp -> 200.dp
        maxWidth >= 900.dp -> 184.dp
        maxWidth >= 700.dp -> 164.dp
        maxWidth >= 600.dp -> 148.dp
        maxWidth >= 420.dp -> 114.dp
        else -> 104.dp
    }

internal fun gridBottomContentPadding(
    navInsetBottom: androidx.compose.ui.unit.Dp,
    isTabletLayout: Boolean
): androidx.compose.ui.unit.Dp {
    val base = if (isTabletLayout) 112.dp else 92.dp
    return navInsetBottom + base
}

private fun countryAccentFor(countryId: String): CountryAccent =
    when (countryId) {
        "germany" -> CountryAccent(Color(0xFF1A3B1E), Color(0xFF0D1F12), Color(0xFF6DB07A))
        "italy" -> CountryAccent(Color(0xFF3B2C16), Color(0xFF221407), Color(0xFFBF8B56))
        "france" -> CountryAccent(Color(0xFF0D1845), Color(0xFF060E28), Color(0xFF7F91E6))
        "spain" -> CountryAccent(Color(0xFF461208), Color(0xFF1E0804), Color(0xFFD8A44A))
        "japan" -> CountryAccent(Color(0xFF170F3D), Color(0xFF0A0820), Color(0xFFC44A63))
        "mexico" -> CountryAccent(Color(0xFF0C2E3C), Color(0xFF061820), Color(0xFF45B5A1))
        "sudan" -> CountryAccent(Color(0xFF2C1404), Color(0xFF160A02), Color(0xFFD69742))
        else -> CountryAccent(Color(0xFF162944), Color(0xFF0A1628), Color(0xFF8BAFD6))
    }

private fun buildLevelUiModels(
    country: CountryDefinition?,
    progress: CountryProgress
): List<LevelUiModel> {
    val defs = country?.levels?.sortedBy { it.levelNumber }.orEmpty()
    val defsByLevel = defs.associateBy { it.levelNumber }
    val progressByLevel = progress.levels.associateBy { it.levelNumber }

    val levelNumbers = if (defs.isNotEmpty()) {
        defs.map { it.levelNumber }
    } else {
        progress.levels.map { it.levelNumber }.sorted()
    }
    if (levelNumbers.isEmpty()) return emptyList()

    val merged = levelNumbers.map { levelNumber ->
        val p = progressByLevel[levelNumber]
        Match3LevelProgress(
            levelNumber = levelNumber,
            isUnlocked = p?.isUnlocked ?: (progress.isUnlocked && levelNumber == 1),
            isCompleted = p?.isCompleted ?: false,
            stars = clampStars(p?.stars ?: 0),
            highScore = p?.highScore ?: 0
        )
    }
    val nextRecommended = nextRecommendedLevelNumber(merged)

    return merged.map { p ->
        val def = defsByLevel[p.levelNumber]
        LevelUiModel(
            levelNumber = p.levelNumber,
            title = def?.title?.takeIf { it.isNotBlank() },
            stars = clampStars(p.stars),
            isUnlocked = p.isUnlocked,
            isCompleted = p.isCompleted,
            isNextRecommended = p.isUnlocked && p.levelNumber == nextRecommended,
            previewFood = def?.let { selectPreviewFoodType(it) },
            lockHint = if (p.isUnlocked) null else "Complete Level ${maxOf(1, p.levelNumber - 1)} first"
        )
    }
}

internal fun clampStars(stars: Int): Int = stars.coerceIn(0, 3)

internal fun nextRecommendedLevelNumber(levels: List<Match3LevelProgress>): Int? {
    val sorted = levels.sortedBy { it.levelNumber }
    return sorted.firstOrNull { it.isUnlocked && !it.isCompleted }?.levelNumber
        ?: sorted.firstOrNull { it.isUnlocked }?.levelNumber
}

internal fun selectPreviewFoodType(level: Match3LevelDefinition): FoodTileType? {
    val goalFood = level.goals.filterIsInstance<LevelGoal.CollectFood>().firstOrNull()?.type
    return goalFood ?: level.allowedTiles.firstOrNull()
}
