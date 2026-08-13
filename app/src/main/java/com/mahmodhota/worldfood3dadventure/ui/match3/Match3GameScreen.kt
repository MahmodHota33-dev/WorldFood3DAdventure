package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mahmodhota.worldfood3dadventure.game.match3.model.GameStatus
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.ui.match3.components.*
import com.mahmodhota.worldfood3dadventure.ui.theme.WorldFood3DAdventureTheme

/**
 * Main game screen for Match-3 levels.
 */
@Composable
fun Match3GameScreen(
    countryId: String,
    levelNumber: Int,
    onBackToMap: () -> Unit
) {
    val viewModel: Match3ViewModel = remember(countryId, levelNumber) {
        Match3ViewModel(countryId, levelNumber)
    }
    val state = viewModel.uiState
    DisposableEffect(viewModel) {
        onDispose { viewModel.onScreenExit() }
    }
    
    val backgroundColor = Color(0xFF080B14) // deep navy for all — background composables handle identity

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            when (countryId) {
                "germany" -> GermanyBackground()
                "italy"   -> ItalyBackground()
                "france"  -> FranceBackground()
                "spain"   -> SpainBackground()
                "japan"   -> JapanBackground()
                "mexico"  -> MexicoBackground()
                "sudan"   -> SudanBackground()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Header: Moves and Score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PremiumMovesPanel(moves = state.movesRemaining)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .premiumPanel()
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Level $levelNumber", 
                            color = Color.White, 
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        Text(text = countryId.uppercase(), color = Color.White.copy(alpha = 0.72f), fontSize = 10.sp, letterSpacing = 1.sp)
                    }
                    InfoPanel(label = "Score", value = state.score.toString())
                }

                Spacer(Modifier.height(12.dp))

                PremiumBoosterPanel(
                    inventory = state.boosterInventory,
                    selectedBooster = state.selectedBooster,
                    activatedBooster = state.activatedBooster,
                    activationNonce = state.boosterActivationNonce,
                    onBoosterSelected = viewModel::onBoosterSelected,
                    modifier = Modifier.fillMaxWidth(),
                    isHorizontal = true,
                    enabled = !state.isAnimating && state.status == GameStatus.PLAYING
                )

                Spacer(Modifier.height(12.dp))

                // Board — weight(1f) takes all remaining vertical space
                Match3BoardComposable(
                    board = state.board,
                    selectedPosition = state.selectedPosition,
                    matchedPositions = state.matchedPositions,
                    onTileClick = { viewModel.onTileSelected(it) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
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

                Spacer(Modifier.height(12.dp))

                // Footer: Goals
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PremiumGoalPanel(
                        goals = state.goals,
                        collected = state.collectedCounts,
                        currentScore = state.score
                    )
                    
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val interactionSource = remember { MutableInteractionSource() }
                        val pressed by interactionSource.collectIsPressedAsState()
                        val buttonScale by androidx.compose.animation.core.animateFloatAsState(
                            targetValue = if (pressed) 0.97f else 1f,
                            animationSpec = androidx.compose.animation.core.spring(),
                            label = "exitButtonScale"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(buttonScale)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(PremiumColors.Emerald, PremiumColors.MutedBlue)))
                                .clickable(
                                    interactionSource = interactionSource,
                                    onClick = onBackToMap
                                )
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "EXIT LEVEL",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Win/Lose Dialogs
    if (state.status == GameStatus.WON) {
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
            onContinue = onBackToMap,
            onReplay = { viewModel.resetGame() }
        )
    } else if (state.status == GameStatus.LOST) {
        PremiumCompletionDialog(
            isWin = false,
            score = state.score,
            stars = 0,
            collected = state.collectedCounts,
            xpReward = 0,
            coinReward = 0,
            onContinue = onBackToMap,
            onReplay = { viewModel.resetGame() }
        )
    }
}

@Composable
private fun InfoPanel(label: String, value: String) {
    Column(
        modifier = Modifier
            .premiumPanel()
            .padding(horizontal = 14.dp, vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label.uppercase(), color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Preview(showBackground = true)
@Composable
fun Match3GameScreenPreview() {
    WorldFood3DAdventureTheme {
        Match3GameScreen(countryId = "germany", levelNumber = 1, onBackToMap = {})
    }
}
