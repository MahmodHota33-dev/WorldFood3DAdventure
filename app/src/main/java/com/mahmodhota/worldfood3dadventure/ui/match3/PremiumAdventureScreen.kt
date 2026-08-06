package com.mahmodhota.worldfood3dadventure.ui.match3

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.match3.Match3LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.GameStatus
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.ui.match3.components.FoodIcon
import com.mahmodhota.worldfood3dadventure.ui.match3.components.Match3BoardComposable
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumColors
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumBoosterPanel
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumCompletionDialog
import com.mahmodhota.worldfood3dadventure.ui.match3.components.TopStatusBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.WorldMapComponent
import com.mahmodhota.worldfood3dadventure.ui.match3.components.WorldMapGeometry

@Composable
fun PremiumAdventureScreen(
    countryId: String,
    levelNumber: Int,
    onTabSelected: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    val viewModel: Match3ViewModel = remember(countryId, levelNumber) {
        Match3ViewModel(countryId, levelNumber)
    }
    val state = viewModel.uiState
    val levelDef = remember(countryId, levelNumber) {
        Match3LevelRegistry.getLevel(countryId, levelNumber)
    }
    var showCompletionDialog by remember(countryId, levelNumber) { mutableStateOf(false) }
    LaunchedEffect(state.status) {
        showCompletionDialog = state.status == GameStatus.WON || state.status == GameStatus.LOST
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
            val mapHeight = if (compactPhone) 124.dp else 146.dp
            val stripHeight = if (compactPhone) 72.dp else 80.dp
            val boosterHeight = if (compactPhone) 70.dp else 76.dp
            val quickActionsHeight = 44.dp

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

    AnimatedVisibility(
        visible = showCompletionDialog,
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
                PremiumCompletionDialog(
                    isWin = true,
                    score = state.score,
                    stars = stars,
                    collected = state.collectedCounts,
                    xpReward = 50 * stars,
                    coinReward = 10 * stars,
                    onContinue = {
                        showCompletionDialog = false
                        onTabSelected("world")
                    },
                    onReplay = {
                        showCompletionDialog = false
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
                    onContinue = {
                        showCompletionDialog = false
                        onTabSelected("world")
                    },
                    onReplay = {
                        showCompletionDialog = false
                        viewModel.resetGame()
                    }
                )
            }
            GameStatus.PLAYING -> Unit
        }
    }
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
