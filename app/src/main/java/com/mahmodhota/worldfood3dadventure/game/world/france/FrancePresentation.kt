package com.mahmodhota.worldfood3dadventure.game.world.france

import com.mahmodhota.worldfood3dadventure.data.progress.model.PersistedGameState
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionQuery

data class FranceSignatureFood(
    val emoji: String,
    val name: String,
    val decorative: Boolean = false
)

data class FranceMilestoneUi(
    val title: String,
    val description: String,
    val achieved: Boolean
)

object FrancePresentation {
    val signatureFoods = listOf(
        FranceSignatureFood("🥐", "Croissant"),
        FranceSignatureFood("🥖", "Baguette"),
        FranceSignatureFood("🧀", "Camembert"),
        FranceSignatureFood("🍮", "Crème Brûlée"),
        FranceSignatureFood("🥘", "Ratatouille"),
        FranceSignatureFood("🍷", "Wine", decorative = true)
    )

    private val cuisineFoods = signatureFoods.filterNot { it.decorative }

    fun completedLevels(state: PersistedGameState): Int =
        ProgressionQuery.completedLevelsForCountry(state, "france")

    fun totalLevels(): Int =
        ProgressionQuery.totalLevelsForCountry("france").coerceAtLeast(1)

    fun completionPercent(state: PersistedGameState): Int =
        ProgressionQuery.completionPercentageForCountry(state, "france")

    fun stars(state: PersistedGameState): Int =
        ProgressionQuery.totalStarsForCountry(state, "france")

    fun foodsDiscovered(state: PersistedGameState): Int {
        val totalFoods = FranceFoodBookEntries.entries.size.coerceAtLeast(1)
        val discovered = (completedLevels(state) * totalFoods + totalLevels() - 1) / totalLevels()
        return discovered.coerceIn(0, totalFoods)
    }

    fun favoriteFood(state: PersistedGameState): String {
        val completed = completedLevels(state)
        if (cuisineFoods.isEmpty()) return "Croissant"
        val index = ((completed * cuisineFoods.size) / totalLevels()).coerceIn(0, cuisineFoods.lastIndex)
        return cuisineFoods[index].name
    }

    fun milestones(state: PersistedGameState): List<FranceMilestoneUi> {
        val country = ProgressionQuery.countryProgressFor(state, "france")
        val foodsDiscovered = foodsDiscovered(state)
        val totalFoods = FranceFoodBookEntries.entries.size.coerceAtLeast(1)
        return listOf(
            FranceMilestoneUi(
                title = "French Explorer",
                description = "Visit France and unlock the country card.",
                achieved = country.isUnlocked
            ),
            FranceMilestoneUi(
                title = "Master of France",
                description = "Complete all 15 France levels.",
                achieved = country.isCompleted
            ),
            FranceMilestoneUi(
                title = "French Cuisine Expert",
                description = "Earn 3 stars on every France level.",
                achieved = country.levels.isNotEmpty() && country.levels.all { it.isCompleted && it.stars >= 3 }
            ),
            FranceMilestoneUi(
                title = "Discover All French Foods",
                description = "Reveal every French food book entry.",
                achieved = foodsDiscovered >= totalFoods
            )
        )
    }
}
