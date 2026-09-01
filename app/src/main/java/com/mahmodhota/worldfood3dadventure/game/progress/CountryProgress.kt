package com.mahmodhota.worldfood3dadventure.game.progress

/**
 * Progress for a single Match-3 level.
 */
data class Match3LevelProgress(
    val levelNumber: Int,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val stars: Int = 0,
    val highScore: Int = 0
)

/**
 * Immutable data model for tracking country-specific progress.
 */
data class CountryProgress(
    val levelId: String,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val levels: List<Match3LevelProgress> = emptyList(),
    val discoveredFoods: Set<String> = emptySet()
) {
    val totalStars: Int get() = levels.sumOf { it.stars }
    val discoveredFoodCount: Int get() = discoveredFoods.size
    val completionPercentage: Int get() = if (levels.isEmpty()) 0 else (levels.count { it.isCompleted } * 100) / levels.size
}
