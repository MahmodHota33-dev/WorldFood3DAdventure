package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object PremiumColors {
    val DeepNavy = Color(0xFF081426)
    val DarkSlate = Color(0xFF12253F)
    val MutedBlue = Color(0xFF2F4A68)
    val Emerald = Color(0xFF2F7D65)
    val Gold = Color(0xFFFACF64)
    val GoldLight = Color(0xFFFFE7A6)
    val GoldDark = Color(0xFF9F7324)
    val WhiteHigh = Color.White
    val WhiteMed = Color.White.copy(alpha = 0.7f)
    val WhiteLow = Color.White.copy(alpha = 0.4f)
    
    val GoldGradient = Brush.verticalGradient(
        colors = listOf(GoldLight, Gold, GoldDark)
    )
    
    val PanelGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A3150), DeepNavy)
    )

    // Adventure Map V2 Colors
    val OceanDeep = Color(0xFF041326)
    val OceanMid = Color(0xFF0E2C4A)
    val OceanShallow = Color(0xFF1F5580)
    val CoastHighlight = Color(0xFF7FFFD4).copy(alpha = 0.35f)
    
    val TerrainForest = Color(0xFF2F7D65)
    val TerrainMountain = Color(0xFF56627A)
    val TerrainDesert = Color(0xFFD9AE58)
    val TerrainTundra = Color(0xFFE0E1DD)
    
    val LandShadow = Color.Black.copy(alpha = 0.25f)

    // Food Icon Drawing Constants
    val IconOutline = Color(0xFF2B2B2B)
    val IconGloss = Color.White.copy(alpha = 0.35f)
    val IconShadow = Color.Black.copy(alpha = 0.25f)
}

object PremiumShapes {
    val PanelShape = RoundedCornerShape(16.dp)
    val CapsuleShape = RoundedCornerShape(50)
}

fun Modifier.premiumPanel() = this
    .shadow(10.dp, PremiumShapes.PanelShape)
    .clip(PremiumShapes.PanelShape)
    .background(PremiumColors.PanelGradient)
    .border(1.dp, PremiumColors.WhiteLow, PremiumShapes.PanelShape)

fun Modifier.goldBorder() = this
    .border(1.5.dp, PremiumColors.GoldGradient, PremiumShapes.PanelShape)
