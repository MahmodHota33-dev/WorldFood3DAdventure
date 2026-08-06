package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterInventory
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType

/**
 * Premium Booster Panel for the game screen.
 */
@Composable
fun PremiumBoosterPanel(
    inventory: BoosterInventory,
    selectedBooster: BoosterType?,
    onBoosterSelected: (BoosterType) -> Unit,
    modifier: Modifier = Modifier,
    activatedBooster: BoosterType? = null,
    activationNonce: Int = 0,
    isHorizontal: Boolean = false,
    enabled: Boolean = true
) {
    val boosters = listOf(
        BoosterUiModel(BoosterType.HAMMER, "Hammer", "🔨", inventory.hammer),
        BoosterUiModel(BoosterType.SHUFFLE, "Shuffle", "🔀", inventory.shuffle),
        BoosterUiModel(BoosterType.EXTRA_MOVES, "Extra Moves", "➕", inventory.extraMoves)
    )

    if (isHorizontal) {
        Row(
            modifier = modifier
                .premiumPanel()
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            boosters.forEach { booster ->
                BoosterSlot(
                    booster = booster,
                    isSelected = selectedBooster == booster.type,
                    isActivated = activatedBooster == booster.type,
                    activationNonce = activationNonce,
                    enabled = enabled,
                    onClick = onBoosterSelected
                )
            }
        }
    } else {
        Column(
            modifier = modifier
                .premiumPanel()
                .padding(vertical = 12.dp)
                .width(78.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            boosters.forEach { booster ->
                BoosterSlot(
                    booster = booster,
                    isSelected = selectedBooster == booster.type,
                    isActivated = activatedBooster == booster.type,
                    activationNonce = activationNonce,
                    enabled = enabled,
                    onClick = onBoosterSelected
                )
            }
        }
    }
}

private data class BoosterUiModel(
    val type: BoosterType,
    val label: String,
    val icon: String,
    val count: Int
)

@Composable
private fun BoosterSlot(
    booster: BoosterUiModel,
    isSelected: Boolean,
    isActivated: Boolean,
    activationNonce: Int,
    enabled: Boolean,
    onClick: (BoosterType) -> Unit
) {
    val isLocked = booster.count <= 0
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val boostScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.9f
            isSelected -> 1.06f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 520f),
        label = "boosterScale"
    )
    val activationGlow = remember(booster.type) { Animatable(0f) }
    LaunchedEffect(isActivated, activationNonce) {
        if (isActivated && activationNonce > 0) {
            activationGlow.snapTo(0.9f)
            activationGlow.animateTo(0f, animationSpec = tween(durationMillis = 260))
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(boostScale)
                .shadow(if (isSelected) 14.dp else 6.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    when {
                        !enabled -> PremiumColors.DarkSlate.copy(alpha = 0.55f)
                        isSelected -> PremiumColors.Gold.copy(alpha = 0.35f)
                        else -> PremiumColors.DarkSlate.copy(alpha = 0.96f)
                    }
                )
                .background(
                    Brush.radialGradient(
                        listOf(
                            if (isSelected) PremiumColors.Gold.copy(alpha = 0.32f) else Color.Transparent,
                            Color.Transparent
                        )
                    )
                )
                .background(
                    Brush.radialGradient(
                        listOf(
                            PremiumColors.Emerald.copy(alpha = activationGlow.value * 0.32f),
                            Color.Transparent
                        )
                    )
                )
                .border(
                    width = if (isSelected) 2.4.dp else 1.6.dp,
                    color = if (isSelected) PremiumColors.Gold else PremiumColors.WhiteLow,
                    shape = CircleShape
                )
                .clickable(
                    enabled = enabled && !isLocked,
                    interactionSource = interactionSource
                ) { onClick(booster.type) }
                .semantics {
                    contentDescription = "${booster.label} booster ${booster.count} remaining"
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (isLocked) "🔒" else booster.icon, fontSize = 25.sp)

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(20.dp),
                shape = CircleShape,
                color = if (isLocked) Color(0xFF6B7280) else PremiumColors.Gold,
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = booster.count.toString(),
                        color = if (isLocked) Color.White else PremiumColors.DeepNavy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Text(
            text = booster.label,
            color = Color.White.copy(alpha = if (enabled) 0.82f else 0.55f),
            fontSize = 9.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
