package com.mahmodhota.worldfood3dadventure.game.world.model

import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry

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
    private val BUILT_IN_SPECS = listOf(
        CountryUnlockSpec(
            countryId = "germany",
            displayName = "Germany",
            unlockedInitially = true,
            requiredStarsToUnlock = 0,
            totalLevels = 15,
            rewardCoinsOnCompletion = 150,
            rewardXpOnCompletion = 500
        ),
        CountryUnlockSpec(
            countryId = "italy",
            displayName = "Italy",
            unlockedInitially = false,
            requiredStarsToUnlock = 30,
            totalLevels = 15,
            rewardCoinsOnCompletion = 150,
            rewardXpOnCompletion = 500
        ),
        CountryUnlockSpec(
            countryId = "france",
            displayName = "France",
            unlockedInitially = false,
            requiredStarsToUnlock = 60,
            totalLevels = 15,
            rewardCoinsOnCompletion = 150,
            rewardXpOnCompletion = 500
        ),
        CountryUnlockSpec(
            countryId = "spain",
            displayName = "Spain",
            unlockedInitially = false,
            requiredStarsToUnlock = 90,
            totalLevels = 15,
            rewardCoinsOnCompletion = 500,
            rewardXpOnCompletion = 1000
        ),
        CountryUnlockSpec(
            countryId = "japan",
            displayName = "Japan",
            unlockedInitially = false,
            requiredStarsToUnlock = 120,
            totalLevels = 15,
            rewardCoinsOnCompletion = 150,
            rewardXpOnCompletion = 500
        ),
        CountryUnlockSpec(
            countryId = "mexico",
            displayName = "Mexico",
            unlockedInitially = false,
            requiredStarsToUnlock = 145,
            totalLevels = 15,
            rewardCoinsOnCompletion = 150,
            rewardXpOnCompletion = 500
        ),
        CountryUnlockSpec(
            countryId = "sudan",
            displayName = "Sudan",
            unlockedInitially = false,
            requiredStarsToUnlock = 185,
            totalLevels = 15,
            rewardCoinsOnCompletion = 200,
            rewardXpOnCompletion = 1000
        )
    )

    val UNLOCK_ORDER: List<CountryUnlockSpec> = BUILT_IN_SPECS + com.mahmodhota.worldfood3dadventure.game.world.GlobalContentRegistry.EXTRA_COUNTRIES.mapIndexed { index, metadata ->
        CountryUnlockSpec(
            countryId = metadata.levelId,
            displayName = metadata.displayName,
            unlockedInitially = metadata.unlockedInitially,
            requiredStarsToUnlock = 220 + index * 10, // Adjusted for 213 countries
            totalLevels = 15, // P9 Scaling: All countries have 15 levels
            rewardCoinsOnCompletion = 100,
            rewardXpOnCompletion = 300
        )
    }

    fun getSpec(countryId: String): CountryUnlockSpec? = UNLOCK_ORDER.find { it.countryId == countryId }

    fun canUnlock(countryId: String, currentTotalStars: Int): Boolean {
        val spec = getSpec(countryId) ?: return false
        if (LevelRegistry.getCountry(countryId)?.isComingSoon == true) return false
        return currentTotalStars >= spec.requiredStarsToUnlock
    }

    fun isInitiallyUnlocked(countryId: String): Boolean {
        return LevelRegistry.getCountry(countryId)?.metadata?.unlockedInitially ?: false
    }

    fun getTotalLevels(countryId: String): Int {
        return getSpec(countryId)?.totalLevels ?: 0
    }

    fun getNextCountrySpec(currentId: String): CountryUnlockSpec? {
        val index = UNLOCK_ORDER.indexOfFirst { it.countryId == currentId }
        if (index != -1 && index < UNLOCK_ORDER.size - 1) {
            return UNLOCK_ORDER[index + 1]
        }
        return null
    }
}
