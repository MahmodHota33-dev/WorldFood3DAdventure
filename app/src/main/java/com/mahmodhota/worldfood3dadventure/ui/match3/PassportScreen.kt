package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mahmodhota.worldfood3dadventure.game.progress.CountryProgress
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionQuery
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import com.mahmodhota.worldfood3dadventure.ui.match3.components.BottomNavigationBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumColors
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumGameBackdrop
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumShapes
import com.mahmodhota.worldfood3dadventure.ui.match3.components.TopStatusBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.premiumPanel

private enum class PassportStampState { LOCKED, VISITED, COMPLETED, COMING_SOON }

private data class PassportEntryUi(
    val countryId: String,
    val countryName: String,
    val flag: String,
    val description: String,
    val stars: Int,
    val levelsCompleted: Int,
    val totalLevels: Int,
    val state: PassportStampState
)

@Composable
fun PassportScreen(
    onTabSelected: (String) -> Unit,
    onSettingsClick: () -> Unit,
    progressViewModel: GameProgressViewModel = viewModel()
) {
    val gameState by progressViewModel.gameState.collectAsState()
    val totalCountries = ProgressionQuery.totalCountries().coerceAtLeast(1)
    val visitedCountries = ProgressionQuery.visitedCountries(gameState)
    val completedCountries = ProgressionQuery.completedCountries(gameState)
    val stars = ProgressionQuery.totalStarsEarned(gameState)
    val progress = visitedCountries.toFloat() / totalCountries.toFloat()
    
    val knownStates = remember { mutableStateMapOf<String, PassportStampState>() }
    var highlightedCountryId by remember { mutableStateOf<String?>(null) }

    val passportEntries = remember(gameState) {
        ProgressionQuery.countryProgressMap(gameState)
            .values
            .sortedBy { entry -> 
                LevelRegistry.allCountryIds.indexOf(entry.levelId).let { if (it == -1) 999 else it }
            }
            .mapNotNull { countryProgress ->
                LevelRegistry.getCountry(countryProgress.levelId)?.let { country ->
                    countryProgress.toPassportEntry(
                        countryName = country.metadata.displayName,
                        flag = country.metadata.flagEmoji,
                        description = country.metadata.travelDescription
                    )
                }
            }
    }
    
    val featuredCountry = remember(passportEntries) {
        passportEntries.firstOrNull { it.state == PassportStampState.VISITED }
            ?: passportEntries.firstOrNull { it.state == PassportStampState.COMPLETED }
            ?: passportEntries.firstOrNull()
    }

    LaunchedEffect(passportEntries) {
        var newlyActivatedId: String? = null
        passportEntries.forEach { entry ->
            val previous = knownStates[entry.countryId]
            if (previous != null &&
                previous != entry.state &&
                (entry.state == PassportStampState.VISITED || entry.state == PassportStampState.COMPLETED)
            ) {
                newlyActivatedId = entry.countryId
            }
            knownStates[entry.countryId] = entry.state
        }
        if (newlyActivatedId != null) {
            highlightedCountryId = newlyActivatedId
            kotlinx.coroutines.delay(1500)
            if (highlightedCountryId == newlyActivatedId) {
                highlightedCountryId = null
            }
        }
    }
    val pageTurnTilt by animateFloatAsState(
        targetValue = if (highlightedCountryId != null) -6f else 0f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 380f),
        label = "passportPageTurnTilt"
    )
    val pageTurnScale by animateFloatAsState(
        targetValue = if (highlightedCountryId != null) 0.985f else 1f,
        animationSpec = tween(durationMillis = 420),
        label = "passportPageTurnScale"
    )

    Scaffold(
        topBar = { TopStatusBar(onSettingsClick = onSettingsClick) },
        bottomBar = { BottomNavigationBar(currentTab = "book", onTabSelected = onTabSelected) },
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
                val cardMinSize = if (compact) 144.dp else 158.dp

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 820.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PassportHeroCard(
                        visited = visitedCountries,
                        completed = completedCountries,
                        total = totalCountries,
                        stars = stars,
                        progress = progress,
                        compact = compact
                    )

                    featuredCountry?.let { entry ->
                        PassportFeatureCard(
                            entry = entry,
                            compact = compact
                        )
                    }

                    Text(
                        text = "COUNTRY STAMPS",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = if (compact) 12.sp else 13.sp,
                        letterSpacing = 1.2.sp
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = cardMinSize),
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                rotationY = pageTurnTilt
                                scaleX = pageTurnScale
                                scaleY = pageTurnScale
                                transformOrigin = TransformOrigin(0f, 0.5f)
                            },
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(passportEntries, key = { it.countryId }) { entry ->
                            PassportStampCard(
                                entry = entry,
                                compact = compact,
                                highlighted = highlightedCountryId == entry.countryId
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun CountryProgress.toPassportEntry(
    countryName: String,
    flag: String,
    description: String
): PassportEntryUi {
    val totalLevels = levels.size.coerceAtLeast(1)
    val levelsCompleted = levels.count { it.isCompleted }
    val state = when {
        LevelRegistry.getCountry(levelId)?.isComingSoon == true -> PassportStampState.COMING_SOON
        isCompleted -> PassportStampState.COMPLETED
        isUnlocked && levelsCompleted > 0 -> PassportStampState.VISITED
        else -> PassportStampState.LOCKED
    }
    return PassportEntryUi(
        countryId = levelId,
        countryName = countryName,
        flag = flag,
        description = description,
        stars = totalStars,
        levelsCompleted = levelsCompleted,
        totalLevels = totalLevels,
        state = state
    )
}

@Composable
private fun PassportHeroCard(
    visited: Int,
    completed: Int,
    total: Int,
    stars: Int,
    progress: Float,
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
                        .size(if (compact) 82.dp else 94.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(PremiumColors.Gold, PremiumColors.GoldDark)))
                        .border(2.dp, PremiumColors.GoldLight.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("PASSPORT", color = PremiumColors.DeepNavy, fontSize = if (compact) 11.sp else 13.sp, fontWeight = FontWeight.ExtraBold)
                        Text("✈️", fontSize = if (compact) 25.sp else 30.sp)
                    }
                }

                Spacer(Modifier.size(14.dp))

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "WORLD PASSPORT",
                        color = PremiumColors.Gold,
                        fontSize = if (compact) 20.sp else 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "$visited / $total countries visited",
                        color = Color.White.copy(alpha = 0.86f),
                        fontSize = if (compact) 12.sp else 13.sp,
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                PassportInfoChip("VISITED", visited.toString(), Modifier.weight(1f))
                PassportInfoChip("COMPLETED", completed.toString(), Modifier.weight(1f))
                PassportInfoChip("STARS", stars.toString(), Modifier.weight(1f))
            }
            Text(
                text = "Collect stamps by finishing levels. Locked countries remain sealed until unlocked.",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = if (compact) 11.sp else 12.sp,
                lineHeight = if (compact) 15.sp else 16.sp
            )
        }
    }
}

@Composable
private fun PassportFeatureCard(
    entry: PassportEntryUi,
    compact: Boolean
) {
    val totalLevels = CountryProgressionChain.getSpec(entry.countryId)?.totalLevels ?: 15
    val completionPercent = (entry.levelsCompleted * 100) / totalLevels
    val stampText = when (entry.state) {
        PassportStampState.COMPLETED -> "PASSPORT STAMPED"
        PassportStampState.VISITED -> "ENTRY VERIFIED"
        PassportStampState.COMING_SOON -> "COMING SOON"
        PassportStampState.LOCKED -> "LOCKED VISA"
    }
    val foods = LevelRegistry.getRepresentativeFoods(entry.countryId)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .premiumPanel()
            .border(1.dp, PremiumColors.Gold.copy(alpha = 0.28f), PremiumShapes.PanelShape),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(if (compact) 14.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(if (compact) 70.dp else 84.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFF1EA7FF), Color(0xFF0D2E5C))))
                        .border(2.dp, PremiumColors.Gold.copy(alpha = 0.48f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(entry.flag, fontSize = if (compact) 28.sp else 34.sp)
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = entry.countryName.uppercase(),
                        color = PremiumColors.Gold,
                        fontSize = if (compact) 18.sp else 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.1.sp
                    )
                    Text(
                        text = entry.description,
                        color = Color.White.copy(alpha = 0.78f),
                        fontSize = if (compact) 11.sp else 12.sp,
                        lineHeight = if (compact) 15.sp else 16.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                PassportInfoChip("COMPLETE", "$completionPercent%", Modifier.weight(1f))
                PassportInfoChip("STARS", entry.stars.toString(), Modifier.weight(1f))
                PassportInfoChip("LEVELS", "${entry.levelsCompleted}/$totalLevels", Modifier.weight(1f))
            }
            
            PassportInfoChip("STAMP", stampText, Modifier.fillMaxWidth())

            if (foods.isNotEmpty()) {
                Text(
                    text = "Signature foods: ${foods.joinToString("  •  ") { it.name.replace('_', ' ') }}",
                    color = Color.White.copy(alpha = 0.74f),
                    fontSize = if (compact) 11.sp else 12.sp,
                    lineHeight = if (compact) 15.sp else 16.sp
                )
            }
        }
    }
}

@Composable
private fun PassportStampCard(
    entry: PassportEntryUi,
    compact: Boolean,
    highlighted: Boolean
) {
    val cardScale by animateFloatAsState(
        targetValue = if (highlighted) 1.035f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 440f),
        label = "passportCardScale"
    )
    val cardGlow by animateFloatAsState(
        targetValue = if (highlighted) 0.62f else 0.22f,
        animationSpec = tween(durationMillis = 380),
        label = "passportCardGlow"
    )
    val stampColor = when (entry.state) {
        PassportStampState.COMPLETED -> PremiumColors.Gold
        PassportStampState.VISITED -> Color(0xFF65D9A1)
        PassportStampState.COMING_SOON -> Color(0xFF8FA1B7)
        PassportStampState.LOCKED -> Color(0xFF8FA1B7)
    }
    val cardBrush = when (entry.state) {
        PassportStampState.COMPLETED -> Brush.linearGradient(listOf(Color(0xFF193E32), Color(0xFF0D1E2E)))
        PassportStampState.VISITED -> Brush.linearGradient(listOf(PremiumColors.DarkSlate, PremiumColors.DeepNavy))
        PassportStampState.COMING_SOON -> Brush.linearGradient(listOf(Color(0xFF111827), Color(0xFF070B13)))
        PassportStampState.LOCKED -> Brush.linearGradient(listOf(Color(0xFF111827), Color(0xFF070B13)))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(if (compact) 0.90f else 0.86f)
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            },
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            stampColor.copy(alpha = if (entry.state == PassportStampState.LOCKED || entry.state == PassportStampState.COMING_SOON) 0.22f else 0.45f)
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
                            .size(if (compact) 46.dp else 52.dp)
                            .clip(CircleShape)
                            .then(
                                if (entry.state == PassportStampState.COMPLETED) {
                                    Modifier.background(Brush.radialGradient(listOf(PremiumColors.Gold, PremiumColors.GoldDark)))
                                } else {
                                    Modifier.background(Color.White.copy(alpha = 0.08f))
                                }
                            )
                            .border(1.dp, stampColor.copy(alpha = 0.7f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = entry.flag,
                            fontSize = if (compact) 21.sp else 24.sp,
                            color = if (entry.state == PassportStampState.LOCKED || entry.state == PassportStampState.COMING_SOON) Color.White.copy(alpha = 0.25f) else Color.Unspecified
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.countryName.uppercase(),
                            color = Color.White,
                            fontSize = if (compact) 12.sp else 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = when (entry.state) {
                                PassportStampState.COMPLETED -> "COMPLETED"
                                PassportStampState.VISITED -> "VISITED"
                                PassportStampState.COMING_SOON -> "COMING SOON"
                                PassportStampState.LOCKED -> "LOCKED"
                            },
                            color = stampColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
                AnimatedVisibility(
                    visible = highlighted && (entry.state == PassportStampState.VISITED || entry.state == PassportStampState.COMPLETED),
                    enter = fadeIn(tween(160)) + scaleIn(initialScale = 0.82f),
                    exit = fadeOut(tween(160))
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = stampColor.copy(alpha = cardGlow),
                        border = androidx.compose.foundation.BorderStroke(1.dp, stampColor.copy(alpha = 0.72f))
                    ) {
                        Text(
                            text = if (entry.state == PassportStampState.COMPLETED) "PASSPORT STAMPED" else "ENTRY STAMPED",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = stampColor.copy(alpha = 0.13f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, stampColor.copy(alpha = 0.38f))
                ) {
                    Text(
                        text = when (entry.state) {
                            PassportStampState.COMPLETED -> "PASSPORT STAMPED"
                            PassportStampState.VISITED -> "ENTRY VERIFIED"
                            PassportStampState.COMING_SOON -> "COMING SOON"
                            PassportStampState.LOCKED -> "LOCKED VISA"
                        },
                        color = stampColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.9.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Text(
                    text = entry.description,
                    color = Color.White.copy(alpha = if (entry.state == PassportStampState.LOCKED || entry.state == PassportStampState.COMING_SOON) 0.40f else 0.78f),
                    fontSize = if (compact) 10.sp else 11.sp,
                    lineHeight = if (compact) 14.sp else 15.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                        repeat(3) { index ->
                            Text(
                                text = "★",
                                fontSize = 10.sp,
                                color = if (index < entry.stars.coerceAtMost(3)) PremiumColors.Gold else Color.White.copy(alpha = 0.25f)
                            )
                        }
                    }
                    Text(
                        text = "${entry.levelsCompleted}/${entry.totalLevels} LVL",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
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
