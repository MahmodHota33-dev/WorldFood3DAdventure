package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3LevelDefinition
import com.mahmodhota.worldfood3dadventure.game.progress.Match3LevelProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.compose.ui.unit.dp

class LevelSelectionPresentationTest {

    @Test
    fun starsAreClampedToThreeForDisplay() {
        assertEquals(0, clampStars(-2))
        assertEquals(2, clampStars(2))
        assertEquals(3, clampStars(7))
    }

    @Test
    fun nextRecommendedPrefersFirstUnlockedIncompleteLevel() {
        val levels = listOf(
            Match3LevelProgress(levelNumber = 1, isUnlocked = true, isCompleted = true, stars = 3),
            Match3LevelProgress(levelNumber = 2, isUnlocked = true, isCompleted = false, stars = 0),
            Match3LevelProgress(levelNumber = 3, isUnlocked = true, isCompleted = false, stars = 0)
        )
        assertEquals(2, nextRecommendedLevelNumber(levels))
    }

    @Test
    fun nextRecommendedFallsBackToFirstUnlockedWhenAllCompleted() {
        val levels = listOf(
            Match3LevelProgress(levelNumber = 1, isUnlocked = true, isCompleted = true, stars = 3),
            Match3LevelProgress(levelNumber = 2, isUnlocked = true, isCompleted = true, stars = 2)
        )
        assertEquals(1, nextRecommendedLevelNumber(levels))
    }

    @Test
    fun previewFoodPrefersCollectGoalType() {
        val level = Match3LevelDefinition(
            levelNumber = 1,
            countryId = "germany",
            allowedTiles = listOf(FoodTileType.BREAD, FoodTileType.PRETZEL),
            goals = listOf(
                LevelGoal.ScoreTarget(1000),
                LevelGoal.CollectFood(type = FoodTileType.PRETZEL, amount = 10)
            ),
            moves = 20,
            title = "Test"
        )
        assertEquals(FoodTileType.PRETZEL, selectPreviewFoodType(level))
    }

    @Test
    fun previewFoodFallsBackToAllowedTileAndHandlesEmpty() {
        val levelWithFallback = Match3LevelDefinition(
            levelNumber = 2,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.PIZZA),
            goals = listOf(LevelGoal.ScoreTarget(1200)),
            moves = 22,
            title = "Fallback"
        )
        assertEquals(FoodTileType.PIZZA, selectPreviewFoodType(levelWithFallback))

        val emptyLevel = Match3LevelDefinition(
            levelNumber = 3,
            countryId = "france",
            allowedTiles = emptyList(),
            goals = listOf(LevelGoal.ScoreTarget(900)),
            moves = 18,
            title = "Empty"
        )
        assertNull(selectPreviewFoodType(emptyLevel))
    }

    @Test
    fun tabletGridMinCellSizePreventsOverDenseRows() {
        assertEquals(200.dp, gridMinCellSize(1200.dp))
        assertEquals(184.dp, gridMinCellSize(980.dp))
        assertEquals(148.dp, gridMinCellSize(620.dp))
    }

    @Test
    fun bottomPaddingIncludesSafeInsetAndBase() {
        val tabletPadding = gridBottomContentPadding(16.dp, isTabletLayout = true)
        val phonePadding = gridBottomContentPadding(16.dp, isTabletLayout = false)
        assertEquals(128.dp, tabletPadding)
        assertEquals(108.dp, phonePadding)
        assertTrue(tabletPadding > phonePadding)
    }
}
