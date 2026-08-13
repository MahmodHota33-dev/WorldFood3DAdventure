package com.mahmodhota.worldfood3dadventure.ui.match3

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import com.mahmodhota.worldfood3dadventure.game.match3.Match3LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.GameStatus
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.ui.match3.components.FoodIcon
import com.mahmodhota.worldfood3dadventure.ui.match3.components.FranceBackground
import com.mahmodhota.worldfood3dadventure.ui.match3.components.GermanyBackground
import com.mahmodhota.worldfood3dadventure.ui.match3.components.ItalyBackground
import com.mahmodhota.worldfood3dadventure.ui.match3.components.JapanBackground
import com.mahmodhota.worldfood3dadventure.ui.match3.components.Match3BoardComposable
import com.mahmodhota.worldfood3dadventure.ui.match3.components.MexicoBackground
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumColors
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumBoosterPanel
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumCompletionDialog
import com.mahmodhota.worldfood3dadventure.ui.match3.components.SpainBackground
import com.mahmodhota.worldfood3dadventure.ui.match3.components.SudanBackground
import com.mahmodhota.worldfood3dadventure.ui.match3.components.TopStatusBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.WorldMapComponent
import com.mahmodhota.worldfood3dadventure.ui.match3.components.WorldMapGeometry
import kotlin.math.min

internal data class Match3RouteValidation(
    val isValid: Boolean,
    val reason: String
)

internal fun validateMatch3Route(
    countryExists: Boolean,
    levelExists: Boolean,
    levelUnlocked: Boolean
): Match3RouteValidation {
    if (!countryExists) return Match3RouteValidation(isValid = false, reason = "invalid_country")
    if (!levelExists) return Match3RouteValidation(isValid = false, reason = "invalid_level")
    if (!levelUnlocked) return Match3RouteValidation(isValid = false, reason = "level_locked")
    return Match3RouteValidation(isValid = true, reason = "ok")
}

@Composable
fun PremiumAdventureScreen(
    countryId: String,
    levelNumber: Int,
    onReturn: () -> Unit,
    onNextLevelSelected: (Int) -> Unit,
    onTabSelected: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    // Validate route before creating ViewModel
    val routeValidation = remember(countryId, levelNumber) {
        val countryExists = LevelRegistry.getCountry(countryId) != null
        val levelExists = Match3LevelRegistry.getLevel(countryId, levelNumber) != null
        val countryProgress = ProgressionManager.getCountryProgress(countryId)
        val levelProgress = countryProgress.levels.firstOrNull { it.levelNumber == levelNumber }
        val levelUnlocked = levelProgress?.isUnlocked ?: (countryProgress.isUnlocked && levelNumber == 1)
        validateMatch3Route(
            countryExists = countryExists,
            levelExists = levelExists,
            levelUnlocked = levelUnlocked
        )
    }
    
    LaunchedEffect(routeValidation.isValid, routeValidation.reason, countryId, levelNumber) {
        if (!routeValidation.isValid) {
            Log.w(
                "PremiumAdventureScreen",
                "Invalid match3 route country=$countryId level=$levelNumber reason=${routeValidation.reason}"
            )
            onTabSelected("world")
        }
    }
    
    if (!routeValidation.isValid) {
        return
    }
    
    val viewModel: Match3ViewModel = remember(countryId, levelNumber) {
       Match3ViewModel(countryId, levelNumber)
    }
    val state = viewModel.uiState
    val levelDef = remember(countryId, levelNumber) {
       Match3LevelRegistry.getLevel(countryId, levelNumber)
    }
    DisposableEffect(viewModel) {
       onDispose { viewModel.onScreenExit() }
    }

    var mapScale by remember { mutableStateOf(1f) }
    var mapOffset by remember { mutableStateOf(Offset.Zero) }
    val transformState = androidx.compose.foundation.gestures.rememberTransformableState { _, zoomChange, offsetChange, _ ->
        mapScale = (mapScale * zoomChange).coerceIn(0.75f, 3f)
        mapOffset += offsetChange
    }

    Scaffold(
        topBar = { TopStatusBar(onSettingsClick = onSettingsClick) },
        containerColor = PremiumColors.DeepNavy
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            val compactPhone = maxHeight < 760.dp
            val isTabletLandscape = shouldUseTabletLandscapeLayout(maxWidth, maxHeight)
            val mapHeight = if (compactPhone) 124.dp else 146.dp
            val stripHeight = if (compactPhone) 72.dp else 80.dp
            val boosterHeight = if (compactPhone) 70.dp else 76.dp
            val quickActionsHeight = 44.dp
            val tabletQuickActionsHeight = 54.dp
            val tabletSidePanelWidth = tabletGameplaySidePanelWidth(maxWidth)
            val tabletPanelGap = 12.dp
            val countryMeta = remember(countryId) { LevelRegistry.getCountry(countryId)?.metadata }
            val countryProgress = ProgressionManager.getCountryProgress(countryId)
            val totalLevels = countryProgress.levels.size.coerceAtLeast(1)
            val completedLevels = countryProgress.levels.count { it.isCompleted }
            val countryStars = countryProgress.totalStars

            Box(modifier = Modifier.fillMaxSize()) {
                CountryGameplayBackground(countryId = countryId)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                if (isTabletLandscape) {
                                    listOf(
                                        Color(0x6403060D),
                                        Color(0x1202060D),
                                        Color(0x3F020408)
                                    )
                                } else {
                                    listOf(
                                        Color(0x7803060D),
                                        Color(0x1802060D),
                                        Color(0x52020408)
                                    )
                                }
                            )
                        )
                )
                if (isTabletLandscape) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(tabletPanelGap)
                        ) {
                            TabletDestinationPanel(
                                countryId = countryId,
                                levelNumber = levelNumber,
                                levelTitle = levelDef?.title ?: "Adventure Puzzle",
                                metadataName = countryMeta?.displayName ?: countryId.replaceFirstChar { it.uppercase() },
                                metadataFlag = countryMeta?.flagEmoji ?: "🌍",
                                stars = countryStars,
                                completedLevels = completedLevels,
                                totalLevels = totalLevels,
                                mapScale = mapScale,
                                mapOffset = mapOffset,
                                transformState = transformState,
                                modifier = Modifier
                                    .width(tabletSidePanelWidth)
                                    .fillMaxHeight()
                            )

                            BoxWithConstraints(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                val boardEdge = tabletBoardSize(maxWidth, maxHeight)
                                Box(
                                    modifier = Modifier
                                        .size(boardEdge)
                                        .clip(RoundedCornerShape(22.dp))
                                ) {
                                    Match3BoardComposable(
                                        board = state.board,
                                        selectedPosition = state.selectedPosition,
                                        matchedPositions = state.matchedPositions,
                                        onTileClick = { viewModel.onTileSelected(it) },
                                        modifier = Modifier.fillMaxSize(),
                                        comboCount = state.comboCount,
                                        animationPhase = state.animationPhase,
                                        activeTileAnimationIds = state.activeTileAnimationIds,
                                        fallDistanceByTileId = state.fallDistanceByTileId,
                                        refillTileIds = state.refillTileIds,
                                        landingTileIds = state.landingTileIds,
                                        comboLabel = state.comboLabel,
                                        boardShakeNonce = state.boardShakeNonce,
                                        boardShakeEnabled = state.boardShakeEnabled,
                                        specialEffects = state.specialEffects,
                                        floatingScoreText = state.floatingScoreText,
                                        floatingScoreNonce = state.floatingScoreNonce,
                                        floatingScoreAnchor = state.floatingScoreAnchor,
                                        specialEffectLabel = state.specialEffectLabel,
                                        specialEffectNonce = state.specialEffectNonce,
                                        spawnedSpecialTiles = state.spawnedSpecialTiles,
                                        spawnedSpecialNonce = state.spawnedSpecialNonce
                                    )
                                }
                            }

                            TabletGameplayPanel(
                                countryName = countryMeta?.displayName ?: countryId.replaceFirstChar { it.uppercase() },
                                levelNumber = levelNumber,
                                movesRemaining = state.movesRemaining,
                                goals = state.goals,
                                collected = state.collectedCounts,
                                score = state.score,
                                boosterInventory = state.boosterInventory,
                                selectedBooster = state.selectedBooster,
                                activatedBooster = state.activatedBooster,
                                activationNonce = state.boosterActivationNonce,
                                onBoosterSelected = viewModel::onBoosterSelected,
                                boostersEnabled = !state.isAnimating && state.status == GameStatus.PLAYING,
                                modifier = Modifier
                                    .width(tabletSidePanelWidth)
                                    .fillMaxHeight()
                            )
                        }

                        AdventureQuickActions(
                            onTabSelected = onTabSelected,
                            modifier = Modifier.height(tabletQuickActionsHeight)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CompactAdventureMapHeader(
                            mapScale = mapScale,
                            mapOffset = mapOffset,
                            transformState = transformState,
                            selectedCountryId = countryId,
                            mapHeight = mapHeight
                        )

                        CompactGameplayInfoStrip(
                            countryName = countryId.replaceFirstChar { it.uppercase() },
                            levelNumber = levelNumber,
                            levelTitle = levelDef?.title ?: "Adventure Puzzle",
                            movesRemaining = state.movesRemaining,
                            goals = state.goals,
                            collected = state.collectedCounts,
                            score = state.score,
                            goalPulseType = state.goalPulseType,
                            goalPulseNonce = state.goalPulseNonce,
                            modifier = Modifier.height(stripHeight)
                        )

                        PremiumBoosterPanel(
                            inventory = state.boosterInventory,
                            selectedBooster = state.selectedBooster,
                            activatedBooster = state.activatedBooster,
                            activationNonce = state.boosterActivationNonce,
                            onBoosterSelected = viewModel::onBoosterSelected,
                            modifier = Modifier.height(boosterHeight),
                            isHorizontal = true,
                            enabled = !state.isAnimating && state.status == com.mahmodhota.worldfood3dadventure.game.match3.model.GameStatus.PLAYING
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .heightIn(min = 220.dp)
                        ) {
                            Match3BoardComposable(
                                board = state.board,
                                selectedPosition = state.selectedPosition,
                                matchedPositions = state.matchedPositions,
                                onTileClick = { viewModel.onTileSelected(it) },
                                modifier = Modifier.fillMaxSize(),
                                comboCount = state.comboCount,
                                animationPhase = state.animationPhase,
                                activeTileAnimationIds = state.activeTileAnimationIds,
                                fallDistanceByTileId = state.fallDistanceByTileId,
                                refillTileIds = state.refillTileIds,
                                landingTileIds = state.landingTileIds,
                                comboLabel = state.comboLabel,
                                boardShakeNonce = state.boardShakeNonce,
                                boardShakeEnabled = state.boardShakeEnabled,
                                specialEffects = state.specialEffects,
                                floatingScoreText = state.floatingScoreText,
                                floatingScoreNonce = state.floatingScoreNonce,
                                floatingScoreAnchor = state.floatingScoreAnchor,
                                specialEffectLabel = state.specialEffectLabel,
                                specialEffectNonce = state.specialEffectNonce,
                                spawnedSpecialTiles = state.spawnedSpecialTiles,
                                spawnedSpecialNonce = state.spawnedSpecialNonce
                            )
                        }

                        AdventureQuickActions(
                            onTabSelected = onTabSelected,
                            modifier = Modifier.height(quickActionsHeight)
                        )
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = state.status == GameStatus.WON || state.status == GameStatus.LOST,
        enter = fadeIn() + scaleIn(initialScale = 0.96f),
        exit = fadeOut() + scaleOut(targetScale = 0.96f)
    ) {
        when (state.status) {
            GameStatus.WON -> {
                val stars = when {
                    state.score >= state.scoreThresholds.threeStars -> 3
                    state.score >= state.scoreThresholds.twoStars -> 2
                    else -> 1
                }
                val hasNext = remember(countryId, levelNumber) {
                    Match3LevelRegistry.getLevel(countryId, levelNumber + 1) != null
                }
                val isChapterFinale = countryId == "sudan" && levelNumber == 15

                PremiumCompletionDialog(
                    isWin = true,
                    score = state.score,
                    stars = stars,
                    collected = state.collectedCounts,
                    xpReward = 50 * stars,
                    coinReward = 10 * stars,
                    unlockedCountry = ProgressionManager.newlyUnlockedCountry,
                    hasNextLevel = hasNext,
                    onContinue = {
                        onReturn()
                    },
                    onNextLevel = {
                        onNextLevelSelected(levelNumber + 1)
                    },
                    onReplay = {
                        viewModel.resetGame()
                    }
                )
            }
            GameStatus.LOST -> {
                PremiumCompletionDialog(
                    isWin = false,
                    score = state.score,
                    stars = 0,
                    collected = state.collectedCounts,
                    xpReward = 0,
                    coinReward = 0,
                    unlockedCountry = null,
                    hasNextLevel = false,
                    onContinue = {
                        onReturn()
                    },
                    onReplay = {
                        viewModel.resetGame()
                    }
                )
            }
            GameStatus.PLAYING -> Unit
        }
    }
}

@Composable
private fun CountryGameplayBackground(countryId: String) {
    when (countryId) {
        "germany" -> GermanyBackground()
        "italy" -> ItalyBackground()
        "france" -> FranceBackground()
        "spain" -> SpainBackground()
        "japan" -> JapanBackground()
        "mexico" -> MexicoBackground()
        "sudan" -> SudanBackground()
    }
}

@Composable
private fun TabletDestinationPanel(
    countryId: String,
    levelNumber: Int,
    levelTitle: String,
    metadataName: String,
    metadataFlag: String,
    stars: Int,
    completedLevels: Int,
    totalLevels: Int,
    mapScale: Float,
    mapOffset: Offset,
    transformState: androidx.compose.foundation.gestures.TransformableState,
    modifier: Modifier = Modifier
) {
    val (accentStart, accentEnd) = countryAccentGradient(countryId)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = PremiumColors.DeepNavy.copy(alpha = 0.78f),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentStart.copy(alpha = 0.52f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accentStart.copy(alpha = 0.30f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = metadataName.uppercase(),
                color = PremiumColors.Gold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$metadataFlag  LEVEL $levelNumber",
                color = Color.White.copy(alpha = 0.88f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = levelTitle,
                color = Color.White.copy(alpha = 0.80f),
                fontStyle = FontStyle.Italic,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            CompactAdventureMapHeader(
                mapScale = mapScale,
                mapOffset = mapOffset,
                transformState = transformState,
                selectedCountryId = countryId,
                mapHeight = 170.dp
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.07f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.16f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "★ $stars stars",
                        color = PremiumColors.Gold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$completedLevels / $totalLevels levels completed",
                        color = Color.White.copy(alpha = 0.78f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TabletGameplayPanel(
    countryName: String,
    levelNumber: Int,
    movesRemaining: Int,
    goals: List<LevelGoal>,
    collected: Map<FoodTileType, Int>,
    score: Int,
    boosterInventory: com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterInventory,
    selectedBooster: com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType?,
    activatedBooster: com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType?,
    activationNonce: Int,
    onBoosterSelected: (com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType) -> Unit,
    boostersEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val goalData = summarizePrimaryGoal(goals, collected, score)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = PremiumColors.DeepNavy.copy(alpha = 0.82f),
        border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.07f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.34f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = countryName.uppercase(),
                        color = Color.White.copy(alpha = 0.70f),
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "LEVEL $levelNumber",
                        color = PremiumColors.Gold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Moves $movesRemaining",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.07f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "GOAL",
                        color = PremiumColors.Gold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (goalData.iconType != null) {
                            FoodIcon(type = goalData.iconType, size = 22.dp)
                        } else {
                            Text("⭐", fontSize = 14.sp)
                        }
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text(
                                text = goalData.label,
                                color = Color.White.copy(alpha = 0.80f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = goalData.progress,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Text(
                text = "BOOSTERS",
                color = PremiumColors.Gold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp
            )
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                PremiumBoosterPanel(
                    inventory = boosterInventory,
                    selectedBooster = selectedBooster,
                    activatedBooster = activatedBooster,
                    activationNonce = activationNonce,
                    onBoosterSelected = onBoosterSelected,
                    isHorizontal = false,
                    enabled = boostersEnabled
                )
            }
        }
    }
}

private data class GoalSummary(
    val label: String,
    val progress: String,
    val iconType: FoodTileType?
)

private fun summarizePrimaryGoal(
    goals: List<LevelGoal>,
    collected: Map<FoodTileType, Int>,
    score: Int
): GoalSummary {
    val primaryGoal = goals.firstOrNull()
    return when (primaryGoal) {
        is LevelGoal.CollectFood -> {
            val current = collected[primaryGoal.type] ?: 0
            GoalSummary(
                label = primaryGoal.type.name.replace('_', ' '),
                progress = "$current/${primaryGoal.amount}",
                iconType = primaryGoal.type
            )
        }
        is LevelGoal.ScoreTarget -> GoalSummary(
            label = "Score Target",
            progress = "$score/${primaryGoal.target}",
            iconType = null
        )
        null -> GoalSummary(
            label = "Mission",
            progress = "--",
            iconType = null
        )
    }
}

private fun countryAccentGradient(countryId: String): Pair<Color, Color> =
    when (countryId) {
        "germany" -> Pair(Color(0xFF1A3B1E), Color(0xFF0D1F12))
        "italy" -> Pair(Color(0xFF3B2C16), Color(0xFF221407))
        "france" -> Pair(Color(0xFF0D1845), Color(0xFF060E28))
        "spain" -> Pair(Color(0xFF461208), Color(0xFF1E0804))
        "japan" -> Pair(Color(0xFF170F3D), Color(0xFF0A0820))
        "mexico" -> Pair(Color(0xFF0C2E3C), Color(0xFF061820))
        "sudan" -> Pair(Color(0xFF2C1404), Color(0xFF160A02))
        else -> Pair(Color(0xFF162944), Color(0xFF0A1628))
    }

internal fun shouldUseTabletLandscapeLayout(maxWidth: Dp, maxHeight: Dp): Boolean =
    maxWidth >= 900.dp && maxWidth > maxHeight

internal fun tabletGameplaySidePanelWidth(maxWidth: Dp): Dp =
    when {
        maxWidth >= 1300.dp -> 240.dp
        maxWidth >= 1100.dp -> 228.dp
        else -> 210.dp
    }

internal fun tabletBoardSize(centerWidth: Dp, centerHeight: Dp): Dp {
    val available = min(centerWidth.value, centerHeight.value).dp
    return if (available < 420.dp) available else available.coerceAtMost(520.dp)
}

@Composable
private fun CompactAdventureMapHeader(
    mapScale: Float,
    mapOffset: Offset,
    transformState: androidx.compose.foundation.gestures.TransformableState,
    selectedCountryId: String,
    mapHeight: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(mapHeight)
            .clip(RoundedCornerShape(22.dp))
            .background(PremiumColors.DeepNavy)
            .border(1.3.dp, PremiumColors.WhiteLow.copy(alpha = 0.56f), RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0x203BA3FF),
                        Color.Transparent
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )
        WorldMapComponent(
            mapScale = mapScale,
            offset = mapOffset,
            state = transformState,
            selectedCountryId = selectedCountryId,
            onCountryClick = {},
            onCountryLongClick = {},
            gameplayMode = true,
            focusCountryId = selectedCountryId,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CompactGameplayInfoStrip(
    countryName: String,
    levelNumber: Int,
    levelTitle: String,
    movesRemaining: Int,
    goals: List<LevelGoal>,
    collected: Map<FoodTileType, Int>,
    score: Int,
    goalPulseType: FoodTileType? = null,
    goalPulseNonce: Int = 0,
    modifier: Modifier = Modifier
) {
    val primaryGoal = goals.firstOrNull()
    val goalLabel: String
    val goalProgress: String
    val goalIconType: FoodTileType?

    when (primaryGoal) {
        is LevelGoal.CollectFood -> {
            val current = collected[primaryGoal.type] ?: 0
            goalLabel = primaryGoal.type.name.replace('_', ' ')
            goalProgress = "$current/${primaryGoal.amount}"
            goalIconType = primaryGoal.type
        }
        is LevelGoal.ScoreTarget -> {
            goalLabel = "Score Target"
            goalProgress = "$score/${primaryGoal.target}"
            goalIconType = null
        }
        null -> {
            goalLabel = "Mission"
            goalProgress = "--"
            goalIconType = null
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = PremiumColors.DarkSlate,
        border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.45f))
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val compactWidth = maxWidth < 380.dp
            val titleFont = if (compactWidth) 10.sp else 11.sp
            val subtitleFont = if (compactWidth) 11.sp else 12.sp
            val movesValueFont = if (compactWidth) 18.sp else 20.sp
            val goalProgressFont = if (compactWidth) 11.sp else 12.sp
            val goalLabelFont = if (compactWidth) 8.sp else 9.sp
            val horizontalInset = if (compactWidth) 8.dp else 10.dp
            val verticalInset = if (compactWidth) 6.dp else 8.dp
            var goalPulseVisible by remember { mutableStateOf(false) }
            LaunchedEffect(goalPulseNonce) {
                if (goalPulseNonce > 0) {
                    goalPulseVisible = true
                    kotlinx.coroutines.delay(240)
                    goalPulseVisible = false
                }
            }
            val pulseScale by animateFloatAsState(
                targetValue = if (goalPulseVisible) 1.035f else 1f,
                animationSpec = spring(dampingRatio = 0.72f, stiffness = 520f),
                label = "goalPulseScale"
            )
            val pulseBorderAlpha by animateFloatAsState(
                targetValue = if (goalPulseVisible) 0.9f else 0.45f,
                animationSpec = spring(dampingRatio = 0.78f, stiffness = 560f),
                label = "goalPulseBorder"
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .heightIn(min = 68.dp)
                    .padding(horizontal = horizontalInset, vertical = verticalInset),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.4f)
                        .padding(end = 2.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${countryName.uppercase()} • LEVEL $levelNumber",
                        color = PremiumColors.Gold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = titleFont,
                        letterSpacing = 0.8.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = levelTitle,
                        color = Color.White.copy(alpha = 0.9f),
                        fontStyle = FontStyle.Italic,
                        fontSize = subtitleFont,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier
                        .weight(0.78f)
                        .widthIn(min = 72.dp)
                        .semantics { contentDescription = "Moves remaining $movesRemaining" }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = if (compactWidth) 4.dp else 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "MOVES",
                            color = Color.White.copy(alpha = 0.74f),
                            fontSize = if (compactWidth) 8.sp else 9.sp,
                            maxLines = 1
                        )
                        Text(
                            text = movesRemaining.toString(),
                            color = if (movesRemaining < 5) Color(0xFFFF6B6B) else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = movesValueFont,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier
                        .weight(1.1f)
                        .widthIn(min = if (compactWidth) 108.dp else 116.dp)
                        .semantics { contentDescription = "Goal $goalLabel progress $goalProgress" }
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.2.dp,
                            color = (if (goalPulseType != null) PremiumColors.Gold else Color.White).copy(alpha = pulseBorderAlpha),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (compactWidth) 6.dp else 8.dp, vertical = if (compactWidth) 5.dp else 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (goalIconType != null) {
                            FoodIcon(type = goalIconType, size = if (compactWidth) 18.dp else 20.dp)
                        } else {
                            Text("⭐", fontSize = if (compactWidth) 13.sp else 14.sp)
                        }
                        Spacer(Modifier.width(if (compactWidth) 4.dp else 6.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = goalLabel,
                                color = Color.White.copy(alpha = 0.78f),
                                fontSize = goalLabelFont,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = goalProgress,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = goalProgressFont,
                                maxLines = 1,
                                overflow = TextOverflow.Clip
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdventureQuickActions(
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuickActionChip(
            title = "WORLD",
            emoji = "🌍",
            modifier = Modifier.weight(1f)
        ) { onTabSelected("world") }
        QuickActionChip(
            title = "REWARDS",
            emoji = "🎁",
            modifier = Modifier.weight(1f)
        ) { onTabSelected("rewards") }
        QuickActionChip(
            title = "PROFILE",
            emoji = "👤",
            modifier = Modifier.weight(1f)
        ) { onTabSelected("profile") }
    }
}

@Composable
private fun QuickActionChip(
    title: String,
    emoji: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(PremiumColors.DarkSlate.copy(alpha = 0.92f), PremiumColors.DeepNavy.copy(alpha = 0.92f))
                    )
                )
                .border(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 12.sp)
            Spacer(Modifier.width(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
