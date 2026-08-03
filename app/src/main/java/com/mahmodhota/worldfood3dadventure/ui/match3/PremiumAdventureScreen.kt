package com.mahmodhota.worldfood3dadventure.ui.match3

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.match3.Match3LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.ui.match3.components.FoodIcon
import com.mahmodhota.worldfood3dadventure.ui.match3.components.Match3BoardComposable
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumColors
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumBoosterPanel
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
    DisposableEffect(viewModel) {
        onDispose { viewModel.onScreenExit() }
    }

    var mapScale by remember { mutableStateOf(1f) }
    var mapOffset by remember { mutableStateOf(Offset.Zero) }
    val transformState = androidx.compose.foundation.gestures.rememberTransformableState { zoomChange, offsetChange, _ ->
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
            val mapHeight = if (compactPhone) 112.dp else 132.dp
            val stripHeight = if (compactPhone) 70.dp else 78.dp
            val boosterHeight = if (compactPhone) 72.dp else 78.dp
            val quickActionsHeight = 46.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    modifier = Modifier.height(stripHeight)
                )

                PremiumBoosterPanel(
                    inventory = state.boosterInventory,
                    selectedBooster = state.selectedBooster,
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
                        specialEffects = state.specialEffects
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
            .border(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
    ) {
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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${countryName.uppercase()} • LEVEL $levelNumber",
                    color = PremiumColors.Gold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = levelTitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier
                    .width(72.dp)
                    .semantics { contentDescription = "Moves remaining $movesRemaining" }
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("MOVES", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
                    Text(
                        text = movesRemaining.toString(),
                        color = if (movesRemaining < 5) Color(0xFFFF6B6B) else Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier
                    .widthIn(min = 96.dp, max = 132.dp)
                    .semantics { contentDescription = "Goal $goalLabel progress $goalProgress" }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (goalIconType != null) {
                        FoodIcon(type = goalIconType, size = 20.dp)
                    } else {
                        Text("⭐", fontSize = 14.sp)
                    }
                    Spacer(Modifier.width(6.dp))
                    Column {
                        Text(
                            text = goalLabel,
                            color = Color.White.copy(alpha = 0.78f),
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = goalProgress,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
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
