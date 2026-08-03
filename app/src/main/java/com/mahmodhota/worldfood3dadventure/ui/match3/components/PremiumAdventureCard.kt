package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable adventure navigation card for the main hub.
 */
@Composable
fun PremiumAdventureCard(
    title: String,
    subtitle: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    progress: Float? = null,
    isLocked: Boolean = false,
    enabled: Boolean = true
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(),
        label = "cardPress"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) PremiumColors.Gold else PremiumColors.WhiteLow.copy(alpha = 0.2f),
        label = "cardBorder"
    )
    
    val backgroundColor = if (isLocked) Color.Black.copy(alpha = 0.4f) else PremiumColors.DarkSlate

    Surface(
        modifier = modifier
            .heightIn(min = 112.dp, max = 144.dp)
            .scale(pressScale)
            .shadow(if (isSelected) 14.dp else 6.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                enabled = enabled && !isLocked,
                interactionSource = interaction,
                indication = null
            ) { onClick() },
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isLocked) {
                    Text("🔒", fontSize = 28.sp)
                } else {
                    Text(icon, fontSize = 32.sp)
                }
                
                Spacer(Modifier.height(4.dp))
                
                Text(
                    text = title,
                    color = if (isSelected) PremiumColors.Gold else Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
                
                Text(
                    text = if (isLocked) "LOCKED" else subtitle,
                    color = if (isSelected) Color.White else Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
                
                progress?.let {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(it)
                                .fillMaxHeight()
                                .background(PremiumColors.Gold)
                        )
                    }
                }
            }

            if (badgeCount > 0) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(18.dp),
                    shape = CircleShape,
                    color = Color.Red,
                    tonalElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = badgeCount.toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}
