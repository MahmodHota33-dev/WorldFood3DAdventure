package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.data.progress.model.CountryGameProgress
import com.mahmodhota.worldfood3dadventure.data.progress.model.PersistedGameState
import com.mahmodhota.worldfood3dadventure.data.progress.model.LevelProgress
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain

/**
 * Centralized query object for deriving progression values from a single source.
 * All screens must use these functions to ensure consistency.
 * Do NOT calculate totals, unlocked counts, or completion percentages directly in composables.
 */
object ProgressionQuery {

    // ============= COUNTRY-LEVEL QUERIES =============

    fun totalCountries(): Int = LevelRegistry.allCountryIds.size

    fun unlockedCountries(state: PersistedGameState): Int =
        countryProgressMap(state).values.count { it.isUnlocked }

    fun completedCountries(state: PersistedGameState): Int =
        countryProgressMap(state).values.count { it.isCompleted }

    fun visitedCountries(state: PersistedGameState): Int =
        countryProgressMap(state).values.count { (it.isUnlocked && it.levels.any { level -> level.isCompleted }) || it.isCompleted }

    // ============= LEVEL-LEVEL QUERIES =============

    fun totalLevelsForCountry(countryId: String): Int =
        CountryProgressionChain.getTotalLevels(countryId)

    fun completedLevelsForCountry(state: PersistedGameState, countryId: String): Int =
        state.countries[countryId]?.levels?.values?.count { it.isCompleted } ?: 0

    fun totalStarsForCountry(state: PersistedGameState, countryId: String): Int =
        state.countries[countryId]?.levels?.values?.sumOf { it.bestStars } ?: 0

    fun completionPercentageForCountry(state: PersistedGameState, countryId: String): Int {
        val total = totalLevelsForCountry(countryId)
        if (total == 0) return 0
        val completed = completedLevelsForCountry(state, countryId)
        return (completed * 100) / total
    }

    // ============= PLAYER-LEVEL QUERIES =============

    fun totalStarsEarned(state: PersistedGameState): Int =
        state.countries.values.sumOf { country ->
            country.levels.values.sumOf { it.bestStars }
        }

    fun totalCoins(state: PersistedGameState): Int = state.player.coins

    fun totalXp(state: PersistedGameState): Int = state.player.xp

    fun totalLevelsAttempted(state: PersistedGameState): Int =
        countryProgressMap(state).values.sumOf { country ->
            country.levels.count { it.isUnlocked || it.isCompleted }
        }

    fun totalLevelsCompleted(state: PersistedGameState): Int =
        countryProgressMap(state).values.sumOf { country ->
            country.levels.count { it.isCompleted }
        }

    fun countryProgressMap(state: PersistedGameState): Map<String, CountryProgress> =
        LevelRegistry.allCountryIds.associateWith { countryId ->
            val persisted = state.countries[countryId]
            val levels = persisted?.levels?.values
                ?.sortedBy { it.levelNumber }
                ?.map { it.toMatch3LevelProgress() }
                .orEmpty()
            CountryProgress(
                levelId = countryId,
                isUnlocked = persisted?.isUnlocked ?: false,
                isCompleted = persisted?.isCompleted ?: false,
                levels = levels
            )
        }

    fun countryProgressFor(state: PersistedGameState, countryId: String): CountryProgress =
        countryProgressMap(state)[countryId] ?: CountryProgress(levelId = countryId)

    // ============= UNLOCK QUERIES =============

    fun canUnlockCountry(countryId: String, totalStars: Int): Boolean =
        CountryProgressionChain.canUnlock(countryId, totalStars)

    fun isCountryInitiallyUnlocked(countryId: String): Boolean =
        CountryProgressionChain.isInitiallyUnlocked(countryId)

    fun starsNeededToUnlockCountry(countryId: String): Int {
        val spec = CountryProgressionChain.getSpec(countryId) ?: return Int.MAX_VALUE
        return spec.requiredStarsToUnlock
    }

    fun starsRemainingToUnlock(countryId: String, currentTotalStars: Int): Int {
        val needed = starsNeededToUnlockCountry(countryId)
        return maxOf(0, needed - currentTotalStars)
    }

    // ============= NEXT UNLOCK INFO =============

    fun nextUnlockedCountry(state: PersistedGameState): String? {
        val currentStars = totalStarsEarned(state)
        return CountryProgressionChain.UNLOCK_ORDER
            .filter { !state.countries[it.countryId]?.isUnlocked.orFalse() }
            .minByOrNull { it.requiredStarsToUnlock }
            ?.countryId
    }

    fun starsNeededForNextUnlock(state: PersistedGameState): Int {
        val nextCountry = nextUnlockedCountry(state) ?: return 0
        val currentStars = totalStarsEarned(state)
        return starsRemainingToUnlock(nextCountry, currentStars)
    }

    // ============= VALIDATION QUERIES =============

    fun hasValidProgressionState(state: PersistedGameState): Boolean {
        // Check that unlocked levels are sequential (e.g., no level 5 without levels 1-4)
        for ((countryId, country) in state.countries) {
            if (!country.isUnlocked) continue

            val levels = country.levels
            for (levelNum in 1..totalLevelsForCountry(countryId)) {
                val level = levels[levelNum] ?: continue
                if (level.isCompleted) {
                    // Check that all prior levels are either unlocked
                    for (priorLevel in 1 until levelNum) {
                        val prior = levels[priorLevel]
                        if (prior != null && !prior.isUnlocked && !prior.isCompleted) {
                            return false
                        }
                    }
                }
            }
        }
        return true
    }

    // Helper extension
    private fun Boolean?.orFalse(): Boolean = this ?: false

    private fun LevelProgress.toMatch3LevelProgress(): Match3LevelProgress =
        Match3LevelProgress(
            levelNumber = levelNumber,
            isUnlocked = isUnlocked,
            isCompleted = isCompleted,
            stars = bestStars,
            highScore = bestScore
        )
}
