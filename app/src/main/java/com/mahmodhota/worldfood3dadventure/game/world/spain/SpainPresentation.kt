package com.mahmodhota.worldfood3dadventure.game.world.spain

import com.mahmodhota.worldfood3dadventure.data.progress.model.PersistedGameState
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionQuery

data class SpainSignatureFood(
    val emoji: String,
    val name: String
)

data class SpainCountryFact(
    val label: String,
    val value: String
)

data class SpainMilestoneUi(
    val title: String,
    val description: String,
    val achieved: Boolean
)

object SpainPresentation {
    val signatureFoods = listOf(
        SpainSignatureFood("🥘", "Paella"),
        SpainSignatureFood("🍳", "Tortilla Española"),
        SpainSignatureFood("🍖", "Jamón Ibérico"),
        SpainSignatureFood("🍩", "Churros"),
        SpainSignatureFood("🍅", "Gazpacho"),
        SpainSignatureFood("🥟", "Croquetas"),
        SpainSignatureFood("🥔", "Patatas Bravas"),
        SpainSignatureFood("🐙", "Pulpo a la Gallega"),
        SpainSignatureFood("🍷", "Sangria"),
        SpainSignatureFood("🍮", "Crema Catalana")
    )

    val countryFacts = listOf(
        SpainCountryFact("Description", "Vibrant plazas, coastlines, tapas bars, and unforgettable celebrations."),
        SpainCountryFact("Capital", "Madrid"),
        SpainCountryFact("Population", "48.6 million"),
        SpainCountryFact("Language", "Spanish"),
        SpainCountryFact("Currency", "Euro (EUR)"),
        SpainCountryFact("Landmarks", "Sagrada Familia, Alhambra, Royal Palace of Madrid")
    )

    val completionBadge = "Gold Passport Stamp"

    private val cuisineFoods = signatureFoods

    fun completedLevels(state: PersistedGameState): Int =
        ProgressionQuery.completedLevelsForCountry(state, "spain")

    fun totalLevels(): Int = ProgressionQuery.totalLevelsForCountry("spain").coerceAtLeast(1)

    fun completionPercent(state: PersistedGameState): Int =
        ProgressionQuery.completionPercentageForCountry(state, "spain")

    fun stars(state: PersistedGameState): Int =
        ProgressionQuery.totalStarsForCountry(state, "spain")

    fun foodsDiscovered(state: PersistedGameState): Int {
        val totalFoods = SpainFoodBookEntries.entries.size.coerceAtLeast(1)
        val discovered = (completedLevels(state) * totalFoods + totalLevels() - 1) / totalLevels()
        return discovered.coerceIn(0, totalFoods)
    }

    fun favoriteFood(state: PersistedGameState): String {
        val completed = completedLevels(state)
        if (cuisineFoods.isEmpty()) return "Paella"
        val index = ((completed * cuisineFoods.size) / totalLevels()).coerceIn(0, cuisineFoods.lastIndex)
        return cuisineFoods[index].name
    }

    fun milestones(state: PersistedGameState): List<SpainMilestoneUi> {
        val country = ProgressionQuery.countryProgressFor(state, "spain")
        val foodsDiscovered = foodsDiscovered(state)
        val totalFoods = SpainFoodBookEntries.entries.size.coerceAtLeast(1)
        return listOf(
            SpainMilestoneUi(
                title = "Spain Explorer",
                description = "Visit Spain and unlock the country card.",
                achieved = country.isUnlocked
            ),
            SpainMilestoneUi(
                title = "Royal Tour",
                description = "Complete all 15 Spain levels.",
                achieved = country.isCompleted
            ),
            SpainMilestoneUi(
                title = "Spanish Cuisine Expert",
                description = "Earn 3 stars on every Spain level.",
                achieved = country.levels.isNotEmpty() && country.levels.all { it.isCompleted && it.stars >= 3 }
            ),
            SpainMilestoneUi(
                title = "Discover All Spanish Foods",
                description = "Reveal every Spanish food book entry.",
                achieved = foodsDiscovered >= totalFoods
            ),
            SpainMilestoneUi(
                title = "Gold Passport Stamp",
                description = "Complete Spain to earn the gold passport stamp and 500 coins.",
                achieved = country.isCompleted
            )
        )
    }
}
