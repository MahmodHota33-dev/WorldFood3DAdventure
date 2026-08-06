package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.ui.match3.components.Match3MotionTokens.RewardCountDurationMs
import com.mahmodhota.worldfood3dadventure.ui.match3.components.Match3MotionTokens.VictoryRewardDelayMs
import com.mahmodhota.worldfood3dadventure.ui.match3.components.Match3MotionTokens.VictoryXpDelayMs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PremiumCompletionDialog(
    isWin: Boolean,
    score: Int,
    stars: Int,
    collected: Map<FoodTileType, Int>,
    xpReward: Int = 0,
    coinReward: Int = 0,
    onContinue: () -> Unit,
    onReplay: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var closing by remember { mutableStateOf(false) }
    var scaleTarget by remember(isWin, score, xpReward, coinReward) { mutableFloatStateOf(0.94f) }
    var scoreTarget by remember(isWin, score) { mutableIntStateOf(if (isWin) 0 else score) }
    var xpTarget by remember(isWin, xpReward) { mutableIntStateOf(0) }
    var coinTarget by remember(isWin, coinReward) { mutableIntStateOf(0) }
    var rewardCardsVisible by remember(isWin, score, xpReward, coinReward) { mutableStateOf(false) }
    var achievementVisible by remember(isWin, score, stars) { mutableStateOf(false) }
    var panelAlphaTarget by remember(isWin, score) { mutableFloatStateOf(0f) }

    LaunchedEffect(isWin, score, xpReward, coinReward) {
        closing = false
        scaleTarget = 0.94f
        scoreTarget = if (isWin) 0 else score
        xpTarget = 0
        coinTarget = 0
        rewardCardsVisible = false
        achievementVisible = false
        panelAlphaTarget = 0f
        delay(32)
        panelAlphaTarget = 1f
        scaleTarget = 1f
        if (isWin) {
            scoreTarget = score
            delay(VictoryRewardDelayMs)
            coinTarget = coinReward
            rewardCardsVisible = true
            delay(VictoryXpDelayMs)
            xpTarget = xpReward
            if (stars >= 3) {
                delay(80)
                achievementVisible = true
            }
        }
    }

    val dialogScale by animateFloatAsState(
        targetValue = if (closing) 0.92f else scaleTarget,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "dialogScale"
    )
    val panelAlpha by animateFloatAsState(
        targetValue = if (closing) 0f else panelAlphaTarget,
        animationSpec = tween(durationMillis = 180),
        label = "panelAlpha"
    )
    val panelRotation by animateFloatAsState(
        targetValue = if (closing) -6f else 0f,
        animationSpec = tween(durationMillis = 180),
        label = "panelRotation"
    )
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .scale(dialogScale)
                    .alpha(panelAlpha)
                    .graphicsLayer { rotationX = panelRotation }
                    .premiumPanel()
                    .border(2.dp, PremiumColors.Gold, PremiumShapes.PanelShape)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isWin) "DELICIOUS VICTORY!" else "KITCHEN CLOSED",
                    color = if (isWin) PremiumColors.Gold else Color(0xFFFF5252),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // Stars Animation
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { i ->
                        AnimatedStar(active = isWin && i < stars, delayMillis = i * 200)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Score Counter
                val displayScore by animateIntAsState(
                    targetValue = scoreTarget,
                    animationSpec = tween(durationMillis = RewardCountDurationMs),
                    label = "scoreCounter"
                )
                val displayXp by animateIntAsState(
                    targetValue = xpTarget,
                    animationSpec = tween(durationMillis = RewardCountDurationMs),
                    label = "xpCounter"
                )
                val displayCoins by animateIntAsState(
                    targetValue = coinTarget,
                    animationSpec = tween(durationMillis = RewardCountDurationMs),
                    label = "coinCounter"
                )

                Text(
                    text = "SCORE: $displayScore",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )

                if (isWin) {
                    Spacer(Modifier.height(16.dp))
                    AnimatedVisibility(
                        visible = rewardCardsVisible,
                        enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.86f),
                        exit = fadeOut(tween(120))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RewardCapsule(
                                label = "COINS",
                                value = displayCoins,
                                accent = PremiumColors.Gold,
                                icon = "🪙"
                            )
                            RewardCapsule(
                                label = "XP",
                                value = displayXp,
                                accent = Color(0xFF74D38A),
                                icon = "✨"
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Collection Summary (Mini)
                if (isWin && collected.isNotEmpty()) {
                    Text(
                        text = "COLLECTED DISHES",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        collected.keys.take(5).forEach { type ->
                            FoodIcon(type = type, size = 28.dp)
                        }
                    }
                }

                if (isWin) {
                    AnimatedVisibility(
                        visible = achievementVisible,
                        enter = slideInVertically(
                            initialOffsetY = { -it / 2 },
                            animationSpec = tween(260)
                        ) + fadeIn(tween(220)),
                        exit = fadeOut(tween(120))
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = PremiumColors.Gold.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.52f))
                        ) {
                            Text(
                                text = "🏆 Master Chef Bonus",
                                color = PremiumColors.Gold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))

                // Buttons
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PremiumDialogButton(
                        text = if (isWin) "CONTINUE ADVENTURE" else "BACK TO HUB",
                        containerColor = if (isWin) Color(0xFF4CAF50) else PremiumColors.DarkSlate,
                        onClick = {
                            if (!closing) {
                                scope.launch {
                                    closing = true
                                    delay(170)
                                    onContinue()
                                }
                            }
                        }
                    )
                     
                    if (!isWin) {
                        PremiumDialogButton(
                            text = "TRY AGAIN",
                            containerColor = Color.Transparent,
                            borderColor = PremiumColors.Gold,
                            textColor = PremiumColors.Gold,
                            onClick = {
                                if (!closing) {
                                    scope.launch {
                                        closing = true
                                        delay(170)
                                        onReplay()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardCapsule(label: String, value: Int, accent: Color, icon: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        PremiumColors.DarkSlate.copy(alpha = 0.95f),
                        PremiumColors.DeepNavy.copy(alpha = 0.92f)
                    )
                )
            )
            .border(1.dp, accent.copy(alpha = 0.42f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 18.sp)
        Text(label, color = accent, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
        Text(value.toString(), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
    }
}

@Composable
private fun PremiumDialogButton(
    text: String,
    containerColor: Color,
    onClick: () -> Unit,
    textColor: Color = Color.White,
    borderColor: Color = Color.Transparent
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "dialogButtonScale"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (containerColor == Color.Transparent) {
                    Brush.horizontalGradient(listOf(PremiumColors.DarkSlate, PremiumColors.DeepNavy))
                } else {
                    Brush.horizontalGradient(listOf(containerColor, containerColor.copy(alpha = 0.86f)))
                }
            )
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}

@Composable
private fun AnimatedStar(active: Boolean, delayMillis: Int) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(active) {
        if (active) {
            delay(delayMillis.toLong())
            visible = true
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1.2f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "starScale"
    )

    Text(
        text = if (active && visible) "⭐" else "☆",
        fontSize = 48.sp,
        color = if (active && visible) PremiumColors.Gold else Color.Gray.copy(alpha = 0.3f),
        modifier = Modifier.scale(scale)
    )
}
