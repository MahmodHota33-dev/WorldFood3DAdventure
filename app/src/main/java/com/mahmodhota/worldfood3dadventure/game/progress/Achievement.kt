package com.mahmodhota.worldfood3dadventure.game.progress

enum class AchievementCategory {
    TRAVEL,
    FOOD,
    LEVELS,
    STARS,
    BOOSTERS,
    CONTINENTS,
    SPECIAL
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val category: AchievementCategory,
    val icon: String,
    val requirement: AchievementRequirement,
    val rewardXp: Int = 0,
    val rewardCoins: Int = 0
)

sealed class AchievementRequirement {
    data class CountriesMastered(val count: Int) : AchievementRequirement()
    data class LevelsCompleted(val count: Int) : AchievementRequirement()
    data class FoodsDiscovered(val count: Int) : AchievementRequirement()
    data class TotalStars(val count: Int) : AchievementRequirement()
    data class BoostersUsed(val count: Int) : AchievementRequirement()
    data class ContinentCompleted(val continent: com.mahmodhota.worldfood3dadventure.game.world.model.Continent) : AchievementRequirement()
}

object AchievementRegistry {
    val achievements = listOf(
        // TRAVEL
        Achievement("travel_first", "First Destination", "Complete your first country.", AchievementCategory.TRAVEL, "🌍", AchievementRequirement.CountriesMastered(1), rewardXp = 100),
        Achievement("travel_10", "World Explorer", "Master 10 countries.", AchievementCategory.TRAVEL, "✈️", AchievementRequirement.CountriesMastered(10), rewardXp = 500, rewardCoins = 100),
        Achievement("travel_25", "Global Traveler", "Master 25 countries.", AchievementCategory.TRAVEL, "🗺️", AchievementRequirement.CountriesMastered(25), rewardXp = 1000, rewardCoins = 250),
        Achievement("travel_all", "World Master", "Master all 213 countries!", AchievementCategory.TRAVEL, "👑", AchievementRequirement.CountriesMastered(213), rewardXp = 5000, rewardCoins = 1000),

        // FOOD
        Achievement("food_first", "First Taste", "Discover your first food.", AchievementCategory.FOOD, "🍎", AchievementRequirement.FoodsDiscovered(1), rewardXp = 50),
        Achievement("food_25", "Food Explorer", "Discover 25 different foods.", AchievementCategory.FOOD, "🍔", AchievementRequirement.FoodsDiscovered(25), rewardXp = 200, rewardCoins = 50),
        Achievement("food_100", "Culinary Traveler", "Discover 100 different foods.", AchievementCategory.FOOD, "🍱", AchievementRequirement.FoodsDiscovered(100), rewardXp = 1000, rewardCoins = 200),

        // LEVELS
        Achievement("levels_50", "Puzzle Traveler", "Complete 50 levels.", AchievementCategory.LEVELS, "🧩", AchievementRequirement.LevelsCompleted(50), rewardXp = 300),
        Achievement("levels_100", "Match Master", "Complete 100 levels.", AchievementCategory.LEVELS, "🔥", AchievementRequirement.LevelsCompleted(100), rewardXp = 600, rewardCoins = 100),

        // STARS
        Achievement("stars_50", "Star Collector", "Earn 50 stars.", AchievementCategory.STARS, "⭐", AchievementRequirement.TotalStars(50), rewardXp = 250),
        Achievement("stars_100", "Star Master", "Earn 100 stars.", AchievementCategory.STARS, "✨", AchievementRequirement.TotalStars(100), rewardXp = 500, rewardCoins = 50),

        // CONTINENTS
        Achievement("cont_europe", "Europe Specialist", "Complete all European countries.", AchievementCategory.CONTINENTS, "🇪🇺", AchievementRequirement.ContinentCompleted(com.mahmodhota.worldfood3dadventure.game.world.model.Continent.EUROPE), rewardXp = 1000, rewardCoins = 200)
    )

    fun getAchievement(id: String) = achievements.find { it.id == id }
}
