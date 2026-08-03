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
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .shadow(20.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp)),
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
                    .border(1.dp, PremiumColors.WhiteLow.copy(alpha = 0.32f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationSpec.orderedTabs.forEach { tab ->
                    NavItem(
                        icon = iconForTab(tab),
                        label = tab.label,
                        isSelected = currentTab == tab.key,
                        modifier = Modifier.weight(1f)
                    ) {
                        onTabSelected(tab.key)
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) PremiumColors.Gold else Color.Gray,
        label = "navColor"
    )
    val itemScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(),
        label = "navScale"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
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
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .heightIn(min = 46.dp)
            .scale(itemScale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(27.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(3.dp)
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
