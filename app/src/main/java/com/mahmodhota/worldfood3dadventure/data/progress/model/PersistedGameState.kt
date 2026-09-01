package com.mahmodhota.worldfood3dadventure.data.progress.model

import com.mahmodhota.worldfood3dadventure.game.progress.DailyJourney
import com.mahmodhota.worldfood3dadventure.game.progress.OnboardingState

/**
 * Root object for the entire persisted game state.
 */
data class PersistedGameState(
    val player: PlayerProgress = PlayerProgress(),
    val countries: Map<String, CountryGameProgress> = emptyMap(),
    val settings: GameSettings = GameSettings(),
    val stats: GameStatistics = GameStatistics(),
    val dailyLogin: DailyLoginData = DailyLoginData(),
    val dailyJourney: DailyJourney = DailyJourney(dateKey = ""),
    val onboardingState: OnboardingState = OnboardingState.NOT_STARTED,
    val lastSelectedCountry: String = "germany",
    val lastSelectedLevel: Int = 1,
    val isChapter1Completed: Boolean = false,
    val worldExplorerBadge: Boolean = false,
    val hasSeenOnboarding: Boolean = false,
    val unlockedAchievements: Set<String> = emptySet(),
    val schemaVersion: Int = 1
)
