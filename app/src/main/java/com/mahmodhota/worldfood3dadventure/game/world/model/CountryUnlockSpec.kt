package com.mahmodhota.worldfood3dadventure.game.world.model

/**
 * Defines unlock requirements, progression order, and completion rules for each country.
 * This is the authoritative source for country progression logic.
 */
data class CountryUnlockSpec(
    val countryId: String,
    val displayName: String,
    val unlockedInitially: Boolean,
    val requiredStarsToUnlock: Int,
    val totalLevels: Int,
    val completionRequirement: Int = totalLevels,
    val rewardCoinsOnCompletion: Int = 100,
    val rewardXpOnCompletion: Int = 500
)

/**
 * Centralized progression unlock chain.
 * Update this single list to change country unlock order or requirements.
 */
object CountryProgressionChain {
    val UNLOCK_ORDER = listOf(
        CountryUnlockSpec(
            countryId = "germany",
            displayName = "Germany",
            unlockedInitially = true,
            requiredStarsToUnlock = 0,
            totalLevels = 15,
            completionRequirement = 15,
            rewardCoinsOnCompletion = 150,
            rewardXpOnCompletion = 500
        ),
        CountryUnlockSpec(
            countryId = "italy",
            displayName = "Italy",
            unlockedInitially = false,
            requiredStarsToUnlock = 30,
            totalLevels = 15,
            completionRequirement = 15,
            rewardCoinsOnCompletion = 150,
            rewardXpOnCompletion = 500
        ),
        CountryUnlockSpec(
            countryId = "france",
            displayName = "France",
            unlockedInitially = false,
            requiredStarsToUnlock = 60,
            totalLevels = 15,
            completionRequirement = 15,
            rewardCoinsOnCompletion = 150,
            rewardXpOnCompletion = 500
        ),
        CountryUnlockSpec(
            countryId = "japan",
            displayName = "Japan",
            unlockedInitially = false,
            requiredStarsToUnlock = 90,
            totalLevels = 10,
            completionRequirement = 10,
            rewardCoinsOnCompletion = 150,
            rewardXpOnCompletion = 500
        ),
        CountryUnlockSpec(
            countryId = "mexico",
            displayName = "Mexico",
            unlockedInitially = false,
            requiredStarsToUnlock = 120,
            totalLevels = 10,
            completionRequirement = 10,
            rewardCoinsOnCompletion = 150,
            rewardXpOnCompletion = 500
        ),
        CountryUnlockSpec(
            countryId = "sudan",
            displayName = "Sudan",
            unlockedInitially = false,
            requiredStarsToUnlock = 150,
            totalLevels = 10,
            completionRequirement = 10,
            rewardCoinsOnCompletion = 200,
            rewardXpOnCompletion = 1000
        )
    )

    fun getSpec(countryId: String): CountryUnlockSpec? = UNLOCK_ORDER.find { it.countryId == countryId }

    fun canUnlock(countryId: String, currentTotalStars: Int): Boolean {
        val spec = getSpec(countryId) ?: return false
        return currentTotalStars >= spec.requiredStarsToUnlock
    }

    fun isInitiallyUnlocked(countryId: String): Boolean {
        val spec = getSpec(countryId) ?: return false
        return spec.unlockedInitially
    }

    fun getTotalLevels(countryId: String): Int {
        return getSpec(countryId)?.totalLevels ?: 0
    }
}
