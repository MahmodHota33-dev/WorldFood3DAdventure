package com.mahmodhota.worldfood3dadventure.ui.match3

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PremiumAdventureLayoutTest {

    @Test
    fun compactPhoneUsesNonTabletLayout() {
        assertFalse(shouldUseTabletLandscapeLayout(411.dp, 891.dp))
    }

    @Test
    fun tabletLandscapeUsesExpandedLayout() {
        assertTrue(shouldUseTabletLandscapeLayout(1280.dp, 800.dp))
    }

    @Test
    fun tabletPortraitDoesNotForceExpandedLayout() {
        assertFalse(shouldUseTabletLandscapeLayout(800.dp, 1280.dp))
    }

    @Test
    fun boardSizeClampsToHeroRangeWhenSpaceAllows() {
        assertEquals(520.dp, tabletBoardSize(900.dp, 700.dp))
        assertEquals(430.dp, tabletBoardSize(430.dp, 700.dp))
    }

    @Test
    fun boardSizeNeverExceedsAvailableSpace() {
        assertEquals(390.dp, tabletBoardSize(390.dp, 780.dp))
    }

    @Test
    fun panelWidthLeavesPositiveCenterSpace() {
        val totalWidth = 900.dp
        val side = tabletGameplaySidePanelWidth(totalWidth)
        val center = totalWidth - side - side - 24.dp
        assertTrue(center > 0.dp)
    }
}

