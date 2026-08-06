package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Premium bottom navigation bar for the game hub.
 */
@Composable
fun BottomNavigationBar(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    FixedOrderSurface {
    BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        val compact = maxWidth < 390.dp
        val navShape = RoundedCornerShape(if (compact) 22.dp else 26.dp)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .shadow(20.dp, navShape)
                .clip(navShape),
            color = Color.Transparent,
            tonalElevation = 10.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                PremiumColors.DarkSlate.copy(alpha = 0.92f),
                                PremiumColors.DeepNavy.copy(alpha = 0.96f)
                            )
                        )
                    )
                    .border(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.32f), navShape)
                    .padding(horizontal = if (compact) 6.dp else 8.dp, vertical = if (compact) 6.dp else 8.dp),
                horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationSpec.orderedTabs.forEach { tab ->
                    NavItem(
                        icon = iconForTab(tab),
                        label = tab.label,
                        isSelected = currentTab == tab.key,
                        compact = compact,
                        modifier = Modifier.weight(1f)
                    ) {
                        onTabSelected(tab.key)
                    }
                }
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    compact: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) PremiumColors.Gold else Color.Gray,
        label = "navColor"
    )
    val itemScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.96f
            isSelected -> 1.08f
            else -> 1.0f
        },
        animationSpec = spring(),
        label = "navScale"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(if (compact) 16.dp else 18.dp))
            .then(
                if (isSelected) {
                    Modifier.background(
                        Brush.verticalGradient(
                            listOf(
                                PremiumColors.Gold.copy(alpha = 0.14f),
                                PremiumColors.WhiteLow.copy(alpha = 0.08f)
                            )
                        )
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interaction,
                onClick = onClick
            )
            .padding(horizontal = if (compact) 4.dp else 6.dp, vertical = if (compact) 5.dp else 6.dp)
            .heightIn(min = if (compact) 48.dp else 54.dp)
            .scale(itemScale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .then(
                    if (isSelected) {
                        Modifier.background(PremiumColors.Gold.copy(alpha = 0.14f))
                    } else {
                        Modifier.background(Color.White.copy(alpha = 0.05f))
                    }
                )
                .padding(horizontal = if (compact) 10.dp else 12.dp, vertical = if (compact) 6.dp else 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(if (compact) 22.dp else 24.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = if (compact) 9.sp else 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor,
            maxLines = 1
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(if (compact) 3.dp else 4.dp)
                .fillMaxWidth(if (isSelected) 0.72f else 0.28f)
                .clip(RoundedCornerShape(99.dp))
                .then(
                    if (isSelected) Modifier.background(PremiumColors.GoldGradient) else Modifier
                )
        )
    }
}

private fun iconForTab(tab: NavTab): ImageVector {
    return when (tab) {
        NavTab.World -> Icons.Default.Public
        NavTab.Passport -> Icons.AutoMirrored.Filled.MenuBook
        NavTab.Rewards -> Icons.Default.CardGiftcard
        NavTab.Profile -> Icons.Default.Person
    }
}
