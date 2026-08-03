package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection

/**
 * Keeps coordinate-sensitive gameplay/map surfaces in LTR so logical X-axis math
 * is never mirrored by locale direction.
 */
@Composable
fun FixedCoordinateSurface(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        content()
    }
}

/**
 * Keeps fixed-order HUD/navigation controls in a stable visual order on all locales.
 */
@Composable
fun FixedOrderSurface(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        content()
    }
}

