package com.mahmodhota.worldfood3dadventure.game.progress

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.mahmodhota.worldfood3dadventure.data.progress.model.PersistedGameState
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry

object AchievementManager {

    private val _newlyUnlocked = mutableStateOf<List<Achievement>>(emptyList())
    val newlyUnlocked: State<List<Achievement>> = _newlyUnlocked

    fun consumeAchievement() {
        if (_newlyUnlocked.value.isNotEmpty()) {
            _newlyUnlocked.value = _newlyUnlocked.value.drop(1)
        }
    }

    fun resetForTesting() {
        _newlyUnlocked.value = emptyList()
    }

    /**
     * Evaluates all achievements against current game state.
     * Returns a list of newly unlocked achievements that were not in [currentUnlockedIds].
     */
    fun evaluate(gameState: PersistedGameState): List<Achievement> {
        val currentUnlocked = gameState.unlockedAchievements
        val newlyUnlockedList = mutableListOf<Achievement>()

        // 1. Gather Stats
        val totalStars = gameState.player.totalStars
        val levelsCompleted = gameState.countries.values.sumOf { it.levels.values.count { l -> l.isCompleted } }
        val countriesMastered = gameState.countries.values.count { it.isCompleted }
        val foodsDiscovered = gameState.countries.values.sumOf { it.discoveredFoods.size }

        AchievementRegistry.achievements.forEach { achievement ->
            if (achievement.id in currentUnlocked) return@forEach

            val isMet = when (val req = achievement.requirement) {
                is AchievementRequirement.TotalStars -> totalStars >= req.count
                is AchievementRequirement.LevelsCompleted -> levelsCompleted >= req.count
                is AchievementRequirement.CountriesMastered -> countriesMastered >= req.count
                is AchievementRequirement.FoodsDiscovered -> foodsDiscovered >= req.count
                is AchievementRequirement.ContinentCompleted -> {
                    val countriesInContinent = LevelRegistry.allCountries.filter { it.continent == req.continent }.map { it.levelId }
                    countriesInContinent.isNotEmpty() && countriesInContinent.all { id ->
                        gameState.countries[id]?.isCompleted == true
                    }
                }
                is AchievementRequirement.BoostersUsed -> false // Placeholder if stats not tracked
            }

            if (isMet) {
                newlyUnlockedList.add(achievement)
            }
        }

        if (newlyUnlockedList.isNotEmpty()) {
            _newlyUnlocked.value = _newlyUnlocked.value + newlyUnlockedList
        }

        return newlyUnlockedList
    }
}
