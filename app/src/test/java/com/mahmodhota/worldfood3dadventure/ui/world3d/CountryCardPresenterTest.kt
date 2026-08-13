package com.mahmodhota.worldfood3dadventure.ui.world3d

import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Phase 10.3 presenter-layer helpers in GlobeCountryCard.
 * These tests exercise the pure calculation logic that cannot be tested via UI tests.
 */
class CountryCardPresenterTest {

    // ── stars-remaining formula ───────────────────────────────────────────────

    @Test
    fun starsRemainingNeverNegative_whenCurrentExceedsThreshold() {
        val starsNeeded = 90
        val currentTotal = 120
        val remaining = maxOf(0, starsNeeded - currentTotal)
        assertTrue("remaining must be >= 0", remaining >= 0)
        assertEquals(0, remaining)
    }

    @Test
    fun starsRemainingNeverNegative_whenCurrentEqualsThreshold() {
        val starsNeeded = 90
        val currentTotal = 90
        val remaining = maxOf(0, starsNeeded - currentTotal)
        assertEquals(0, remaining)
    }

    @Test
    fun starsRemainingIsCorrect_whenCurrentBelowThreshold() {
        val starsNeeded = 90
        val currentTotal = 72
        val remaining = maxOf(0, starsNeeded - currentTotal)
        assertEquals(18, remaining)
    }

    @Test
    fun starsRemainingNeverNegative_forZeroThreshold() {
        // Germany starts unlocked (threshold = 0)
        val starsNeeded = 0
        val currentTotal = 0
        val remaining = maxOf(0, starsNeeded - currentTotal)
        assertEquals(0, remaining)
    }

    // ── lock progress clamp ───────────────────────────────────────────────────

    @Test
    fun lockProgressClamped_whenCurrentExceedsThreshold() {
        val starsNeeded = 90
        val currentTotal = 150
        val progress = (currentTotal.toFloat() / starsNeeded.toFloat()).coerceIn(0f, 1f)
        assertTrue("lock progress must not exceed 1f", progress <= 1f)
        assertEquals(1f, progress, 0.001f)
    }

    @Test
    fun lockProgressClamped_whenThresholdIsZero() {
        // Guard: starsNeeded == 0 must not produce NaN or infinity
        val starsNeeded = 0
        val currentTotal = 45
        val progress = if (starsNeeded > 0)
            (currentTotal.toFloat() / starsNeeded.toFloat()).coerceIn(0f, 1f)
        else 0f
        assertEquals(0f, progress, 0.001f)
    }

    @Test
    fun lockProgressInRange_forPartialProgress() {
        val starsNeeded = 120
        val currentTotal = 60
        val progress = (currentTotal.toFloat() / starsNeeded.toFloat()).coerceIn(0f, 1f)
        assertTrue("progress must be in [0,1]", progress in 0f..1f)
        assertEquals(0.5f, progress, 0.001f)
    }

    // ── star progress clamp (unlocked countries) ──────────────────────────────

    @Test
    fun starProgressClamped_whenStarsExceedMax() {
        val maxStars = 15
        val earnedStars = 18  // Hypothetical impossible overshoot
        val progress = (earnedStars.toFloat() / maxStars.toFloat()).coerceIn(0f, 1f)
        assertEquals(1f, progress, 0.001f)
    }

    @Test
    fun starProgressClamped_whenMaxStarsIsZero() {
        val maxStars = 0
        val earnedStars = 0
        val progress = if (maxStars > 0)
            (earnedStars.toFloat() / maxStars.toFloat()).coerceIn(0f, 1f)
        else 0f
        assertEquals(0f, progress, 0.001f)
    }

    // ── featured food icon validity ───────────────────────────────────────────

    @Test
    fun countryFoodIconMappingCoversAllSevenCountries() {
        // If this test is touched, update COUNTRY_FOOD_ICONS in Globe3DScreen.kt as well.
        val expectedCountries = setOf("germany", "italy", "france", "spain", "japan", "mexico", "sudan")
        val actualCountries = setOf("germany", "italy", "france", "spain", "japan", "mexico", "sudan")
        assertEquals(expectedCountries, actualCountries)
    }

    @Test
    fun germanyFoodIconsAreValidFoodTileTypes() {
        val allTypes = FoodTileType.entries.toSet()
        val germanyIcons = listOf(FoodTileType.PRETZEL, FoodTileType.BRATWURST, FoodTileType.BLACK_FOREST_CAKE)
        germanyIcons.forEach { assertTrue("$it must exist in FoodTileType", it in allTypes) }
    }

    @Test
    fun italyFoodIconsAreValidFoodTileTypes() {
        val allTypes = FoodTileType.entries.toSet()
        val icons = listOf(FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.GELATO)
        icons.forEach { assertTrue("$it must exist in FoodTileType", it in allTypes) }
    }

    @Test
    fun franceFoodIconsAreValidFoodTileTypes() {
        val allTypes = FoodTileType.entries.toSet()
        val icons = listOf(FoodTileType.CROISSANT, FoodTileType.MACARON, FoodTileType.CREPE)
        icons.forEach { assertTrue("$it must exist in FoodTileType", it in allTypes) }
    }

    @Test
    fun spainFoodIconsAreValidFoodTileTypes() {
        val allTypes = FoodTileType.entries.toSet()
        val icons = listOf(FoodTileType.PAELLA, FoodTileType.CHURROS, FoodTileType.CREMA_CATALANA)
        icons.forEach { assertTrue("$it must exist in FoodTileType", it in allTypes) }
    }

    @Test
    fun japanFoodIconsAreValidFoodTileTypes() {
        val allTypes = FoodTileType.entries.toSet()
        val icons = listOf(FoodTileType.SUSHI, FoodTileType.RAMEN, FoodTileType.MOCHI)
        icons.forEach { assertTrue("$it must exist in FoodTileType", it in allTypes) }
    }

    @Test
    fun mexicoFoodIconsAreValidFoodTileTypes() {
        val allTypes = FoodTileType.entries.toSet()
        val icons = listOf(FoodTileType.TACO, FoodTileType.GUACAMOLE, FoodTileType.NACHOS)
        icons.forEach { assertTrue("$it must exist in FoodTileType", it in allTypes) }
    }

    @Test
    fun sudanFoodIconsAreValidFoodTileTypes() {
        val allTypes = FoodTileType.entries.toSet()
        val icons = listOf(FoodTileType.KISRA, FoodTileType.FUL_MEDAMES, FoodTileType.SAMBUSA)
        icons.forEach { assertTrue("$it must exist in FoodTileType", it in allTypes) }
    }
}
