package com.mahmodhota.worldfood3dadventure.data.progress

import android.content.Context
import androidx.datastore.preferences.core.*
import com.mahmodhota.worldfood3dadventure.data.progress.model.*
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterInventory
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType
import com.mahmodhota.worldfood3dadventure.game.match3.model.EconomyConfig
import com.mahmodhota.worldfood3dadventure.game.match3.Match3LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.progress.PlayerLevelProgression
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import com.mahmodhota.worldfood3dadventure.telemetry.Match3Telemetry
import com.mahmodhota.worldfood3dadventure.telemetry.Match3TelemetryEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal data class LevelCompletionRewardPlan(
    val updatedBestStars: Int,
    val updatedBestScore: Int,
    val starsDelta: Int,
    val xpDelta: Int,
    val coinsDelta: Int,
    val isFirstClear: Boolean
)

internal fun buildLevelCompletionRewardPlan(
    currentBestStars: Int,
    currentBestScore: Int,
    alreadyCompleted: Boolean,
    incomingStars: Int,
    incomingScore: Int,
    xpReward: Int,
    coinReward: Int
): LevelCompletionRewardPlan {
    val updatedBestStars = maxOf(currentBestStars, incomingStars)
    val updatedBestScore = maxOf(currentBestScore, incomingScore)
    val starsDelta = (updatedBestStars - currentBestStars).coerceAtLeast(0)
    val isFirstClear = !alreadyCompleted
    val xpDelta = if (isFirstClear) xpReward else 0
    val coinsDelta = if (isFirstClear) coinReward else 0
    return LevelCompletionRewardPlan(
        updatedBestStars = updatedBestStars,
        updatedBestScore = updatedBestScore,
        starsDelta = starsDelta,
        xpDelta = xpDelta,
        coinsDelta = coinsDelta,
        isFirstClear = isFirstClear
    )
}

/**
 * Single source of truth for persisted game state.
 */
class GameProgressRepository(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _state = MutableStateFlow(PersistedGameState())
    val state: StateFlow<PersistedGameState> = _state.asStateFlow()

    init {
        scope.launch {
            context.gameDataStore.data
                .catch { emit(emptyPreferences()) }
                .collect { prefs ->
                    val xp = (prefs[GameKeys.XP] ?: 0).coerceAtLeast(0)
                    val storedLevel = (prefs[GameKeys.LEVEL] ?: 1).coerceAtLeast(1)
                    if (PlayerLevelProgression.needsLegacyLevelMigration(xp, storedLevel)) {
                        val migratedLevel = PlayerLevelProgression.levelFromTotalXp(xp)
                        context.gameDataStore.edit { mutable ->
                            val latestStored = (mutable[GameKeys.LEVEL] ?: 1).coerceAtLeast(1)
                            if (latestStored < migratedLevel) {
                                mutable[GameKeys.LEVEL] = migratedLevel
                            }
                        }
                    }

                    // Migration: reconcile TOTAL_STARS against the per-level bestStars sum.
                    val storedStars = (prefs[GameKeys.TOTAL_STARS] ?: 0).coerceAtLeast(0)
                    val computedStars = LevelRegistry.allCountryIds.sumOf { cId ->
                        LevelRegistry.getCountry(cId)?.levels?.sumOf { lvDef ->
                            (prefs[GameKeys.levelStars(cId, lvDef.levelNumber)] ?: 0).coerceIn(0, 3)
                        } ?: 0
                    }
                    if (computedStars > storedStars) {
                        context.gameDataStore.edit { mutable ->
                            mutable[GameKeys.TOTAL_STARS] = computedStars
                        }
                        return@collect
                    }

                    _state.value = mapToGameState(prefs)
                    
                    // P10-W: Daily Journey Logic
                    checkAndUpdateDailyJourney(prefs)
                    
                    // P10-V: Achievement Evaluation
                    withContext(Dispatchers.Main) {
                        val newlyUnlocked = com.mahmodhota.worldfood3dadventure.game.progress.AchievementManager.evaluate(_state.value)
                        if (newlyUnlocked.isNotEmpty()) {
                            saveUnlockedAchievements(newlyUnlocked.map { it.id }.toSet())
                        }
                    }
                }
        }
    }

    private fun checkAndUpdateDailyJourney(prefs: Preferences) {
        val current = _state.value.dailyJourney
        val updated = com.mahmodhota.worldfood3dadventure.game.progress.DailyJourneyManager.checkDayChange(current)
        if (updated != current) {
            saveDailyJourney(updated)
        }
    }

    private fun saveDailyJourney(journey: com.mahmodhota.worldfood3dadventure.game.progress.DailyJourney) {
        scope.launch {
            context.gameDataStore.edit { prefs ->
                prefs[GameKeys.DAILY_DATE_KEY] = journey.dateKey
                prefs[GameKeys.DAILY_STREAK] = journey.streak
                prefs[GameKeys.LAST_ACTIVE_DATE] = journey.lastActiveDate
                prefs[GameKeys.DAILY_MISSIONS_JSON] = journey.missions.joinToString(";") { it.serialize() }
            }
        }
    }

    fun updateDailyMissionProgress(type: com.mahmodhota.worldfood3dadventure.game.progress.MissionType, amount: Int = 1) {
        val current = _state.value.dailyJourney
        val updated = com.mahmodhota.worldfood3dadventure.game.progress.DailyJourneyManager.updateProgress(current, type, amount)
        if (updated != current) {
            saveDailyJourney(updated)
        }
    }

    fun claimDailyMissionReward(missionId: String) {
        scope.launch {
            context.gameDataStore.edit { prefs ->
                val missionsJson = prefs[GameKeys.DAILY_MISSIONS_JSON] ?: return@edit
                val missions = missionsJson.split(";")
                    .mapNotNull { com.mahmodhota.worldfood3dadventure.game.progress.DailyMission.deserialize(it) }
                    .toMutableList()
                
                val index = missions.indexOfFirst { it.id == missionId }
                if (index != -1) {
                    val mission = missions[index]
                    if (mission.isCompleted && !mission.isClaimed) {
                        missions[index] = mission.copy(isClaimed = true)
                        prefs[GameKeys.DAILY_MISSIONS_JSON] = missions.joinToString(";") { it.serialize() }
                        
                        if (mission.rewardXp > 0) {
                            val xp = (prefs[GameKeys.XP] ?: 0) + mission.rewardXp
                            prefs[GameKeys.XP] = xp
                            prefs[GameKeys.LEVEL] = PlayerLevelProgression.normalizedLevel(xp, prefs[GameKeys.LEVEL] ?: 1)
                        }
                        if (mission.rewardCoins > 0) {
                            prefs[GameKeys.COINS] = (prefs[GameKeys.COINS] ?: 100) + mission.rewardCoins
                        }
                        mission.rewardBooster?.let { booster ->
                            val currentBooster = prefs[GameKeys.boosterCount(booster)] ?: 0
                            prefs[GameKeys.boosterCount(booster)] = currentBooster + 1
                        }
                    }
                }
            }
        }
    }

    private fun saveUnlockedAchievements(ids: Set<String>) {
        scope.launch {
            context.gameDataStore.edit { prefs ->
                val current = prefs[GameKeys.UNLOCKED_ACHIEVEMENTS] ?: emptySet()
                val updated = current + ids
                prefs[GameKeys.UNLOCKED_ACHIEVEMENTS] = updated
                
                ids.forEach { id ->
                    val achievement = com.mahmodhota.worldfood3dadventure.game.progress.AchievementRegistry.getAchievement(id)
                    if (achievement != null && id !in current) {
                        if (achievement.rewardXp > 0) {
                            val xp = (prefs[GameKeys.XP] ?: 0) + achievement.rewardXp
                            prefs[GameKeys.XP] = xp
                            prefs[GameKeys.LEVEL] = PlayerLevelProgression.normalizedLevel(xp, prefs[GameKeys.LEVEL] ?: 1)
                        }
                        if (achievement.rewardCoins > 0) {
                            prefs[GameKeys.COINS] = (prefs[GameKeys.COINS] ?: 100) + achievement.rewardCoins
                        }
                    }
                }
            }
        }
    }

    private fun mapToGameState(prefs: Preferences): PersistedGameState {
        val xp = (prefs[GameKeys.XP] ?: 0).coerceAtLeast(0)
        val storedLevel = (prefs[GameKeys.LEVEL] ?: 1).coerceAtLeast(1)
        val normalizedLevel = PlayerLevelProgression.normalizedLevel(xp, storedLevel)
        val player = PlayerProgress(
            lives = prefs[GameKeys.LIVES] ?: 5,
            coins = (prefs[GameKeys.COINS] ?: 100).coerceAtLeast(0),
            xp = xp,
            level = normalizedLevel,
            totalStars = (prefs[GameKeys.TOTAL_STARS] ?: 0).coerceAtLeast(0),
            username = prefs[GameKeys.USERNAME] ?: "Traveler",
            boosterInventory = BoosterInventory(
                hammer = prefs[GameKeys.boosterCount(BoosterType.HAMMER)] ?: BoosterInventory().hammer,
                rocket = prefs[GameKeys.boosterCount(BoosterType.ROCKET)] ?: BoosterInventory().rocket,
                hand = prefs[GameKeys.boosterCount(BoosterType.HAND)] ?: BoosterInventory().hand,
                extraMoves = prefs[GameKeys.boosterCount(BoosterType.EXTRA_MOVES)] ?: BoosterInventory().extraMoves,
                shuffle = prefs[GameKeys.boosterCount(BoosterType.SHUFFLE)] ?: BoosterInventory().shuffle
            )
        )

        val countries = mutableMapOf<String, CountryGameProgress>()
        LevelRegistry.allCountryIds.forEach { id ->
            val unlocked = prefs[GameKeys.countryUnlocked(id)] 
                ?: CountryProgressionChain.isInitiallyUnlocked(id)
            val countryDef = LevelRegistry.getCountry(id)
            val foods = LevelRegistry.getRepresentativeFoods(id)
            val discoveredFoods = foods.filter { food ->
                prefs[GameKeys.foodDiscovered(id, food.name)] == true
            }.map { it.name }.toSet()

            val levels = countryDef?.levels?.associate { levelDef ->
                val lvl = levelDef.levelNumber
                lvl to LevelProgress(
                    levelNumber = lvl,
                    countryId = id,
                    isUnlocked = prefs[GameKeys.levelUnlocked(id, lvl)] ?: (unlocked && lvl == 1),
                    isCompleted = prefs[GameKeys.levelCompleted(id, lvl)] ?: false,
                    bestStars = (prefs[GameKeys.levelStars(id, lvl)] ?: 0).coerceIn(0, 3),
                    bestScore = (prefs[GameKeys.levelScore(id, lvl)] ?: 0).coerceAtLeast(0)
                )
            } ?: emptyMap()
            
            countries[id] = CountryGameProgress(
                countryId = id,
                isUnlocked = unlocked,
                isCompleted = prefs[GameKeys.countryCompleted(id)] ?: false,
                levels = levels,
                discoveredFoods = discoveredFoods
            )
        }

        val settings = GameSettings(
            musicVolume = (prefs[GameKeys.VOL_MUSIC] ?: 1.0f).coerceIn(0f, 1f),
            sfxVolume = (prefs[GameKeys.VOL_SFX] ?: 1.0f).coerceIn(0f, 1f),
            vibrationEnabled = prefs[GameKeys.VIBRATION] ?: true,
            darkMode = prefs[GameKeys.DARK_MODE] ?: true
        )

        val unlockedAchievements = prefs[GameKeys.UNLOCKED_ACHIEVEMENTS] ?: emptySet()
        val dailyDate = prefs[GameKeys.DAILY_DATE_KEY] ?: ""
        val dailyStreak = prefs[GameKeys.DAILY_STREAK] ?: 0
        val lastActive = prefs[GameKeys.LAST_ACTIVE_DATE] ?: ""
        val missionsJson = prefs[GameKeys.DAILY_MISSIONS_JSON] ?: ""
        val missions = missionsJson.split(";").mapNotNull { com.mahmodhota.worldfood3dadventure.game.progress.DailyMission.deserialize(it) }
        
        val dailyJourney = com.mahmodhota.worldfood3dadventure.game.progress.DailyJourney(
            dateKey = dailyDate,
            streak = dailyStreak,
            lastActiveDate = lastActive,
            missions = missions
        )

        val onboardingStr = prefs[GameKeys.ONBOARDING_STATE] ?: com.mahmodhota.worldfood3dadventure.game.progress.OnboardingState.NOT_STARTED.name
        var onboardingState = com.mahmodhota.worldfood3dadventure.game.progress.OnboardingState.valueOf(onboardingStr)
        
        // P10-Y: Auto-complete onboarding for existing players with progress to avoid forcing tutorial
        if (onboardingState == com.mahmodhota.worldfood3dadventure.game.progress.OnboardingState.NOT_STARTED) {
            if (player.totalStars > 0 || player.xp > 0) {
                onboardingState = com.mahmodhota.worldfood3dadventure.game.progress.OnboardingState.COMPLETED
            }
        }

        return PersistedGameState(
            player = player,
            countries = countries,
            settings = settings,
            lastSelectedCountry = prefs[GameKeys.LAST_COUNTRY] ?: "germany",
            lastSelectedLevel = prefs[GameKeys.LAST_LEVEL] ?: 1,
            isChapter1Completed = prefs[GameKeys.CHAPTER_1_COMPLETED] ?: false,
            worldExplorerBadge = prefs[GameKeys.WORLD_EXPLORER_BADGE] ?: false,
            hasSeenOnboarding = prefs[GameKeys.HAS_SEEN_ONBOARDING] ?: false,
            onboardingState = onboardingState,
            unlockedAchievements = unlockedAchievements,
            dailyJourney = dailyJourney,
            schemaVersion = prefs[GameKeys.SCHEMA_VERSION] ?: 1
        )
    }

    suspend fun updateOnboardingState(newState: com.mahmodhota.worldfood3dadventure.game.progress.OnboardingState) {
        context.gameDataStore.edit { prefs ->
            prefs[GameKeys.ONBOARDING_STATE] = newState.name
            if (newState == com.mahmodhota.worldfood3dadventure.game.progress.OnboardingState.COMPLETED) {
                prefs[GameKeys.HAS_SEEN_ONBOARDING] = true
            }
        }
    }

    suspend fun markOnboardingComplete() {
        context.gameDataStore.edit { it[GameKeys.HAS_SEEN_ONBOARDING] = true }
    }

    suspend fun saveLevelProgress(
        countryId: String,
        levelNumber: Int,
        stars: Int,
        score: Int,
        xpReward: Int,
        coinReward: Int,
        discoveredFood: String? = null
    ): List<String> {
        val newlyUnlockedCountryIds = mutableListOf<String>()
        context.gameDataStore.edit { prefs ->
            if (discoveredFood != null) {
                val alreadyDiscovered = prefs[GameKeys.foodDiscovered(countryId, discoveredFood)] == true
                if (!alreadyDiscovered) {
                    prefs[GameKeys.foodDiscovered(countryId, discoveredFood)] = true
                    // P10-Y: Grant Food Discovery XP (One-time)
                    val foodXp = EconomyConfig.FOOD_DISCOVERY_XP
                    val currentXp = prefs[GameKeys.XP] ?: 0
                    val updatedXp = currentXp + foodXp
                    prefs[GameKeys.XP] = updatedXp
                    prefs[GameKeys.LEVEL] = PlayerLevelProgression.normalizedLevel(updatedXp, prefs[GameKeys.LEVEL] ?: 1)
                }
            }

            val currentBestStars = prefs[GameKeys.levelStars(countryId, levelNumber)] ?: 0
            val currentBestScore = prefs[GameKeys.levelScore(countryId, levelNumber)] ?: 0
            val rewardPlan = buildLevelCompletionRewardPlan(
                currentBestStars = currentBestStars,
                currentBestScore = currentBestScore,
                alreadyCompleted = prefs[GameKeys.levelCompleted(countryId, levelNumber)] ?: false,
                incomingStars = stars,
                incomingScore = score,
                xpReward = xpReward,
                coinReward = coinReward
            )

            prefs[GameKeys.levelCompleted(countryId, levelNumber)] = true
            
            if (rewardPlan.starsDelta > 0) {
                prefs[GameKeys.TOTAL_STARS] = (prefs[GameKeys.TOTAL_STARS] ?: 0) + rewardPlan.starsDelta
                prefs[GameKeys.levelStars(countryId, levelNumber)] = rewardPlan.updatedBestStars

                val newTotalStars = (prefs[GameKeys.TOTAL_STARS] ?: 0).coerceAtLeast(0)
                LevelRegistry.allCountryIds.forEach { cId ->
                    val alreadyUnlocked = prefs[GameKeys.countryUnlocked(cId)]
                        ?: CountryProgressionChain.isInitiallyUnlocked(cId)
                    if (!alreadyUnlocked && CountryProgressionChain.canUnlock(cId, newTotalStars)) {
                        val def = LevelRegistry.getCountry(cId)
                        if (def?.isComingSoon != true) {
                            prefs[GameKeys.countryUnlocked(cId)] = true
                            prefs[GameKeys.levelUnlocked(cId, 1)] = true
                            newlyUnlockedCountryIds.add(cId)
                        }
                    }
                }
            }
            
            if (rewardPlan.updatedBestScore > currentBestScore) {
                prefs[GameKeys.levelScore(countryId, levelNumber)] = rewardPlan.updatedBestScore
            }

            if (rewardPlan.isFirstClear) {
                val newXp = (prefs[GameKeys.XP] ?: 0) + rewardPlan.xpDelta
                prefs[GameKeys.XP] = newXp
                prefs[GameKeys.LEVEL] = PlayerLevelProgression.normalizedLevel(newXp, prefs[GameKeys.LEVEL] ?: 1)
                prefs[GameKeys.COINS] = (prefs[GameKeys.COINS] ?: 100) + rewardPlan.coinsDelta
                
                val hasNextLevel = Match3LevelRegistry.getLevel(countryId, levelNumber + 1) != null
                if (hasNextLevel) {
                    prefs[GameKeys.levelUnlocked(countryId, levelNumber + 1)] = true
                } else {
                    checkAndMarkCountryCompleted(countryId, prefs)
                    if (countryId == "sudan") {
                        prefs[GameKeys.CHAPTER_1_COMPLETED] = true
                        prefs[GameKeys.WORLD_EXPLORER_BADGE] = true
                        prefs[GameKeys.COINS] = (prefs[GameKeys.COINS] ?: 100) + 500
                        val chapterBonusXp = (prefs[GameKeys.XP] ?: 0) + 1000
                        prefs[GameKeys.XP] = chapterBonusXp
                        prefs[GameKeys.LEVEL] = PlayerLevelProgression.normalizedLevel(chapterBonusXp, prefs[GameKeys.LEVEL] ?: 1)
                    }
                }
            }
        }
        return newlyUnlockedCountryIds
    }

    private fun checkAndMarkCountryCompleted(countryId: String, prefs: MutablePreferences) {
        val spec = CountryProgressionChain.getSpec(countryId) ?: return
        if (prefs[GameKeys.countryCompleted(countryId)] == true) return
        
        var allLevelsCompleted = true
        for (levelNum in 1..spec.totalLevels) {
            if (!(prefs[GameKeys.levelCompleted(countryId, levelNum)] ?: false)) {
                allLevelsCompleted = false
                break
            }
        }
        
        if (allLevelsCompleted) {
            prefs[GameKeys.countryCompleted(countryId)] = true
            prefs[GameKeys.COINS] = (prefs[GameKeys.COINS] ?: 100) + spec.rewardCoinsOnCompletion
            val completionXp = (prefs[GameKeys.XP] ?: 0) + spec.rewardXpOnCompletion
            prefs[GameKeys.XP] = completionXp
            prefs[GameKeys.LEVEL] = PlayerLevelProgression.normalizedLevel(completionXp, prefs[GameKeys.LEVEL] ?: 1)
        }
    }

    suspend fun updatePlayerStats(lives: Int? = null, coins: Int? = null, xp: Int? = null) {
        context.gameDataStore.edit { prefs ->
            lives?.let { prefs[GameKeys.LIVES] = it }
            coins?.let { prefs[GameKeys.COINS] = it }
            xp?.let {
                val normalizedXp = it.coerceAtLeast(0)
                prefs[GameKeys.XP] = normalizedXp
                prefs[GameKeys.LEVEL] = PlayerLevelProgression.normalizedLevel(normalizedXp, prefs[GameKeys.LEVEL] ?: 1)
            }
        }
    }

    suspend fun updateSettings(
        musicVolume: Float? = null,
        sfxVolume: Float? = null,
        vibrationEnabled: Boolean? = null,
        darkMode: Boolean? = null
    ) {
        context.gameDataStore.edit { prefs ->
            musicVolume?.let { prefs[GameKeys.VOL_MUSIC] = it.coerceIn(0f, 1f) }
            sfxVolume?.let { prefs[GameKeys.VOL_SFX] = it.coerceIn(0f, 1f) }
            vibrationEnabled?.let { prefs[GameKeys.VIBRATION] = it }
            darkMode?.let { prefs[GameKeys.DARK_MODE] = it }
        }
    }

    suspend fun consumeBooster(type: BoosterType): BoosterInventory? {
        var updatedInventory: BoosterInventory? = null
        context.gameDataStore.edit { prefs ->
            val currentInventory = BoosterInventory(
                hammer = prefs[GameKeys.boosterCount(BoosterType.HAMMER)] ?: 0,
                rocket = prefs[GameKeys.boosterCount(BoosterType.ROCKET)] ?: 0,
                hand = prefs[GameKeys.boosterCount(BoosterType.HAND)] ?: 0,
                extraMoves = prefs[GameKeys.boosterCount(BoosterType.EXTRA_MOVES)] ?: 0,
                shuffle = prefs[GameKeys.boosterCount(BoosterType.SHUFFLE)] ?: 0
            )
            val currentCount = currentInventory.countFor(type)
            if (currentCount <= 0) return@edit
            val nextCount = currentCount - 1
            prefs[GameKeys.boosterCount(type)] = nextCount
            updatedInventory = currentInventory.withCount(type, nextCount)
        }
        return updatedInventory
    }

    suspend fun purchaseBooster(type: BoosterType, cost: Int): Boolean {
        var success = false
        context.gameDataStore.edit { prefs ->
            val currentCoins = (prefs[GameKeys.COINS] ?: 100).coerceAtLeast(0)
            if (currentCoins < cost) return@edit
            prefs[GameKeys.COINS] = currentCoins - cost
            val currentCount = prefs[GameKeys.boosterCount(type)] ?: 0
            prefs[GameKeys.boosterCount(type)] = currentCount + 1
            success = true
        }
        return success
    }

    suspend fun grantBooster(type: BoosterType, amount: Int): BoosterInventory {
        var updatedInventory = BoosterInventory()
        context.gameDataStore.edit { prefs ->
            val currentCount = prefs[GameKeys.boosterCount(type)] ?: 0
            val nextCount = currentCount + amount
            prefs[GameKeys.boosterCount(type)] = nextCount
            updatedInventory = BoosterInventory(
                hammer = prefs[GameKeys.boosterCount(BoosterType.HAMMER)] ?: 0,
                rocket = prefs[GameKeys.boosterCount(BoosterType.ROCKET)] ?: 0,
                hand = prefs[GameKeys.boosterCount(BoosterType.HAND)] ?: 0,
                extraMoves = prefs[GameKeys.boosterCount(BoosterType.EXTRA_MOVES)] ?: 0,
                shuffle = prefs[GameKeys.boosterCount(BoosterType.SHUFFLE)] ?: 0
            )
        }
        return updatedInventory
    }

    suspend fun resetAllProgress() {
        context.gameDataStore.edit { it.clear() }
    }
}
