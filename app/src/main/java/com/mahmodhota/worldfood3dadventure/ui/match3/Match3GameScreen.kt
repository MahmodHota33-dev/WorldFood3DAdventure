package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    
    val backgroundColor = when (countryId) {
        "germany" -> Color(0xFF1B5E20)
        "italy" -> Color(0xFFE1F5FE) // Light Sky Blue
        "france" -> Color(0xFFF3E5F5) // Lavender Mist
        "japan" -> Color(0xFFFCE4EC) // Soft Pink
        "mexico" -> Color(0xFFFFF3E0) // Warm Sand
        "sudan" -> Color(0xFFFFF9C4) // Sunset Yellow
        else -> Color(0xFF121212)
    }

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
            if (countryId == "germany") {
                GermanyBackground()
            } else if (countryId == "italy") {
                ItalyBackground()
            } else if (countryId == "france") {
                FranceBackground()
            } else if (countryId == "japan") {
                JapanBackground()
            } else if (countryId == "mexico") {
                MexicoBackground()
            } else if (countryId == "sudan") {
                SudanBackground()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
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
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Level $levelNumber", 
                            color = Color.White, 
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(text = countryId.uppercase(), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, letterSpacing = 1.sp)
                    }
                    InfoPanel(label = "Score", value = state.score.toString())
                }

                Spacer(Modifier.height(16.dp))

                PremiumBoosterPanel(
                    inventory = state.boosterInventory,
                    selectedBooster = state.selectedBooster,
                    onBoosterSelected = viewModel::onBoosterSelected,
                    modifier = Modifier.fillMaxWidth(),
                    isHorizontal = true,
                    enabled = !state.isAnimating && state.status == GameStatus.PLAYING
                )

                Spacer(Modifier.height(16.dp))

                // Board
                Match3BoardComposable(
                    board = state.board,
                    selectedPosition = state.selectedPosition,
                    matchedPositions = state.matchedPositions,
                    onTileClick = { viewModel.onTileSelected(it) },
                    modifier = Modifier.weight(1f),
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

                Spacer(Modifier.height(16.dp))

                // Footer: Goals
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PremiumGoalPanel(
                        goals = state.goals,
                        collected = state.collectedCounts,
                        currentScore = state.score
                    )
                    
                    Spacer(Modifier.height(16.dp))

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
                                    indication = null,
                                    onClick = onBackToMap
                                )
                                .padding(vertical = 14.dp),
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
            onContinue = onBackToMap,
            onReplay = { viewModel.resetGame() }
        )
    } else if (state.status == GameStatus.LOST) {
        PremiumCompletionDialog(
            isWin = false,
            score = state.score,
            stars = 0,
            collected = state.collectedCounts,
            onContinue = onBackToMap,
            onReplay = { viewModel.resetGame() }
        )
    }
}

@Composable
private fun SudanBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Nile River and Sunset Sky
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFFFFB300), Color(0xFFFBC02D), Color(0xFF0D47A1).copy(alpha = 0.2f)))
        ).alpha(0.5f))
        
        // Nile river silhouette (bottom)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xFF01579B).copy(alpha = 0.15f))
        )

        // Abstract Palm trees / Pyramids
        repeat(3) { i ->
            Box(
                modifier = Modifier
                    .size(60.dp, 120.dp)
                    .offset(x = (40 + i * 120).dp, y = 520.dp)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(Color(0xFF2E7D32).copy(alpha = 0.15f))
            )
        }
    }
}

@Composable
private fun MexicoBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Desert Sunset
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFFFFB74D), Color(0xFFFFCC80), Color(0xFFD84315).copy(alpha = 0.3f)))
        ).alpha(0.5f))
        
        // Cactus Silhouettes
        repeat(3) { i ->
            Box(
                modifier = Modifier
                    .size(40.dp, 100.dp)
                    .offset(x = (50 + i * 140).dp, y = 500.dp)
                    .background(Color(0xFF2E7D32).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            )
        }

        // Colorful village hint (Bottom right)
        Box(
            modifier = Modifier
                .size(120.dp, 60.dp)
                .align(Alignment.BottomEnd)
                .background(Color(0xFFE91E63).copy(alpha = 0.15f), RoundedCornerShape(topStart = 40.dp))
        )
    }
}

@Composable
private fun JapanBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Soft Pink Atmosphere
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0), Color(0xFFFFEB3B).copy(alpha = 0.1f)))
        ).alpha(0.5f))
        
        // Abstract Cherry Blossoms / Mountain
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .offset(y = 100.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF80AB).copy(alpha = 0.2f))
        )

        // Torii Gate hint (Top left)
        Box(
            modifier = Modifier
                .padding(24.dp)
                .size(40.dp, 6.dp)
                .background(Color(0xFFD32F2F), RoundedCornerShape(2.dp))
        )
    }
}

@Composable
private fun FranceBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Evening Lavender Sky
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFFE1BEE7), Color(0xFFF3E5F5), Color(0xFFFFF9C4)))
        ).alpha(0.5f))
        
        // Lavender fields silhouettes
        repeat(3) { i ->
            Box(
                modifier = Modifier
                    .size(200.dp, 100.dp)
                    .offset(x = (i * 150).dp, y = 550.dp)
                    .clip(RoundedCornerShape(topStart = 80.dp, topEnd = 80.dp))
                    .background(Color(0xFF9575CD).copy(alpha = 0.2f))
            )
        }
        
        // Café awning hint (Top corner)
        Box(
            modifier = Modifier
                .size(100.dp, 40.dp)
                .align(Alignment.TopEnd)
                .background(Brush.horizontalGradient(listOf(Color.Red.copy(alpha = 0.2f), Color.White.copy(alpha = 0.2f))))
        )
    }
}

@Composable
private fun ItalyBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Mediterranean Sea and Coast
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFFE1F5FE), Color(0xFF039BE5), Color(0xFF8D6E63)))
        ).alpha(0.4f))
        
        // Abstract coastal features (Vineyards/Olive Trees feel)
        repeat(4) { i ->
            Box(
                modifier = Modifier
                    .size(120.dp, 80.dp)
                    .offset(x = (i * 100).dp, y = 500.dp)
                    .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                    .background(Color(0xFF558B2F).copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
private fun GermanyBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Soft gradient for landscape
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF81D4FA), Color(0xFF1B5E20)))
        ).alpha(0.3f))
        
        // Abstract trees / forest feel
        repeat(5) { i ->
            Box(
                modifier = Modifier
                    .size(100.dp, 150.dp)
                    .offset(x = (i * 80).dp, y = 400.dp)
                    .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                    .background(Color(0xFF0D47A1).copy(alpha = 0.1f))
            )
        }
    }
}

@Composable
private fun InfoPanel(label: String, value: String) {
    Column(
        modifier = Modifier
            .premiumPanel()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label.uppercase(), color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Preview(showBackground = true)
@Composable
fun Match3GameScreenPreview() {
    WorldFood3DAdventureTheme {
        Match3GameScreen(countryId = "germany", levelNumber = 1, onBackToMap = {})
    }
}
