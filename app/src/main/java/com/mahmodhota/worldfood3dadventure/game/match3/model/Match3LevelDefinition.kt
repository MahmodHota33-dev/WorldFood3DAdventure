package com.mahmodhota.worldfood3dadventure.game.match3.model

/**
 * Defines the configuration for a Match-3 level.
 */
data class Match3LevelDefinition(
    val levelNumber: Int,
    val countryId: String,
    val allowedTiles: List<FoodTileType>,
    val goals: List<LevelGoal>,
    val moves: Int,
    val title: String? = null,
    val scoreThresholds: StarThresholds = DEFAULT_STAR_THRESHOLDS
) {
    fun withBalancedThresholds(): Match3LevelDefinition {
        if (scoreThresholds != DEFAULT_STAR_THRESHOLDS) return this

        val scoreGoal = goals
            .filterIsInstance<LevelGoal.ScoreTarget>()
            .maxOfOrNull { it.target } ?: 0
        val collectDemand = goals
            .filterIsInstance<LevelGoal.CollectFood>()
            .sumOf { it.amount }

        val objectiveBase = maxOf(
            scoreGoal,
            collectDemand * 340 + goals.size.coerceAtLeast(1) * 500
        )
        val movePressure = (28 - moves).coerceAtLeast(0) * 120
        val varietyFactor = (allowedTiles.distinct().size - 4).coerceAtLeast(0) * 260

        var oneStar = maxOf(1700, objectiveBase - 700 + varietyFactor)
        var twoStars = maxOf(oneStar + 700, objectiveBase + varietyFactor + (movePressure / 3))
        var threeStars = maxOf(twoStars + 900, objectiveBase + varietyFactor + 1300 + movePressure)

        if (scoreGoal > 0) {
            oneStar = maxOf(oneStar, scoreGoal)
            twoStars = maxOf(twoStars, (scoreGoal * 1.2f).toInt())
            threeStars = maxOf(threeStars, (scoreGoal * 1.45f).toInt())
        }

        return copy(
            scoreThresholds = StarThresholds(
                oneStar = oneStar,
                twoStars = twoStars,
                threeStars = threeStars
            )
        )
    }
}

data class StarThresholds(
    val oneStar: Int,
    val twoStars: Int,
    val threeStars: Int
)

val DEFAULT_STAR_THRESHOLDS = StarThresholds(800, 1200, 1500)
