package com.mahmodhota.worldfood3dadventure.data.progress

import android.content.Context
import androidx.datastore.preferences.core.*
import com.mahmodhota.worldfood3dadventure.data.progress.model.*
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterInventory
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType
import com.mahmodhota.worldfood3dadventure.game.match3.Match3LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.progress.PlayerLevelProgression
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import com.mahmodhota.worldfood3dadventure.telemetry.Match3Telemetry
import com.mahmodhota.worldfood3dadventure.telemetry.Match3TelemetryEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
                    _state.value = mapToGameState(prefs)
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
                shuffle = prefs[GameKeys.boosterCount(BoosterType.SHUFFLE)] ?: BoosterInventory().shuffle,
                extraMoves = prefs[GameKeys.boosterCount(BoosterType.EXTRA_MOVES)] ?: BoosterInventory().extraMoves
            )
        )

        val countries = mutableMapOf<String, CountryGameProgress>()
        LevelRegistry.allCountryIds.forEach { id ->
            // Use CountryProgressionChain for initial unlock state
            val unlocked = prefs[GameKeys.countryUnlocked(id)] 
                ?: CountryProgressionChain.isInitiallyUnlocked(id)
            
            val countryDef = LevelRegistry.getCountry(id)
            val levels = countryDef?.levels?.associate { levelDef ->
                val lvl = levelDef.levelNumber
                lvl to LevelProgress(
                    levelNumber = lvl,
                    countryId = id,
                    isUnlocked = prefs[GameKeys.levelUnlocked(id, lvl)] ?: (unlocked && lvl == 1),
                    isCompleted = prefs[GameKeys.levelCompleted(id, lvl)] ?: false,
                    bestStars = (prefs[GameKeys.levelStars(id, lvl)] ?: 0).coerceAtLeast(0),
                    bestScore = (prefs[GameKeys.levelScore(id, lvl)] ?: 0).coerceAtLeast(0)
                )
            } ?: emptyMap()
            
            countries[id] = CountryGameProgress(
                countryId = id,
                isUnlocked = unlocked,
                isCompleted = prefs[GameKeys.countryCompleted(id)] ?: false,
                levels = levels
            )
        }

        val settings = GameSettings(
            musicVolume = (prefs[GameKeys.VOL_MUSIC] ?: 1.0f).coerceIn(0f, 1f),
            sfxVolume = (prefs[GameKeys.VOL_SFX] ?: 1.0f).coerceIn(0f, 1f),
            vibrationEnabled = prefs[GameKeys.VIBRATION] ?: true,
            darkMode = prefs[GameKeys.DARK_MODE] ?: true
        )

        return PersistedGameState(
            player = player,
            countries = countries,
            settings = settings,
            lastSelectedCountry = prefs[GameKeys.LAST_COUNTRY] ?: "germany",
            lastSelectedLevel = prefs[GameKeys.LAST_LEVEL] ?: 1,
            isChapter1Completed = prefs[GameKeys.CHAPTER_1_COMPLETED] ?: false,
            worldExplorerBadge = prefs[GameKeys.WORLD_EXPLORER_BADGE] ?: false,
            schemaVersion = prefs[GameKeys.SCHEMA_VERSION] ?: 1
        )
    }

    suspend fun saveLevelProgress(
        countryId: String,
        levelNumber: Int,
        stars: Int,
        score: Int,
        xpReward: Int,
        coinReward: Int
    ) {
        context.gameDataStore.edit { prefs ->
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
            }
            
            if (rewardPlan.updatedBestScore > currentBestScore) {
                prefs[GameKeys.levelScore(countryId, levelNumber)] = rewardPlan.updatedBestScore
            }

            if (rewardPlan.isFirstClear) {
                val newXp = (prefs[GameKeys.XP] ?: 0) + rewardPlan.xpDelta
                prefs[GameKeys.XP] = newXp
                prefs[GameKeys.LEVEL] = PlayerLevelProgression.normalizedLevel(
                    totalXp = newXp,
                    storedLevel = prefs[GameKeys.LEVEL] ?: 1
                )
                prefs[GameKeys.COINS] = (prefs[GameKeys.COINS] ?: 100) + rewardPlan.coinsDelta
                Match3Telemetry.log(
                    event = Match3TelemetryEvent.REWARD_GRANTED,
                    levelId = Match3Telemetry.levelId(countryId, levelNumber),
                    countryId = countryId,
                    remainingMoves = 0,
                    score = score,
                    comboCount = stars,
                    cascadeCount = 0,
                    detail = "firstClear"
                )
                Match3Telemetry.log(
                    event = Match3TelemetryEvent.XP_GRANTED,
                    levelId = Match3Telemetry.levelId(countryId, levelNumber),
                    countryId = countryId,
                    remainingMoves = 0,
                    score = score,
                    comboCount = stars,
                    cascadeCount = 0,
                    detail = "amount=${rewardPlan.xpDelta}"
                )
                Match3Telemetry.log(
                    event = Match3TelemetryEvent.COINS_GRANTED,
                    levelId = Match3Telemetry.levelId(countryId, levelNumber),
                    countryId = countryId,
                    remainingMoves = 0,
                    score = score,
                    comboCount = stars,
                    cascadeCount = 0,
                    detail = "amount=${rewardPlan.coinsDelta}"
                )
                
                val hasNextLevel = Match3LevelRegistry.getLevel(countryId, levelNumber + 1) != null
                if (hasNextLevel) {
                    prefs[GameKeys.levelUnlocked(countryId, levelNumber + 1)] = true
                    Match3Telemetry.log(
                        event = Match3TelemetryEvent.WORLD_UNLOCK,
                        levelId = Match3Telemetry.levelId(countryId, levelNumber),
                        countryId = countryId,
                        remainingMoves = 0,
                        score = score,
                        comboCount = stars,
                        cascadeCount = 0,
                        detail = "nextLevel=${levelNumber + 1}"
                    )
                } else {
                    // Level is the last level of this country, check for country completion
                    checkAndMarkCountryCompleted(countryId, prefs)
                    
                    // Unlock next country in the chain
                    val levelChain = LevelRegistry.allCountryIds
                    val currentIndex = levelChain.indexOf(countryId)
                    val nextIndex = currentIndex + 1
                    
                    if (currentIndex >= 0 && nextIndex in levelChain.indices) {
                        val nextId = levelChain[nextIndex]
                        val nextCountryDef = LevelRegistry.getCountry(nextId)
                        if (nextCountryDef?.isComingSoon != true) {
                            prefs[GameKeys.countryUnlocked(nextId)] = true
                            prefs[GameKeys.levelUnlocked(nextId, 1)] = true
                            Match3Telemetry.log(
                                event = Match3TelemetryEvent.WORLD_UNLOCK,
                                levelId = Match3Telemetry.levelId(countryId, levelNumber),
                                countryId = nextId,
                                remainingMoves = 0,
                                score = score,
                                comboCount = stars,
                                cascadeCount = 0,
                                detail = "countryUnlockFrom=$countryId"
                            )
                        }
                    } else if (countryId == "sudan") {
                        // Chapter 1 Finale
                        prefs[GameKeys.CHAPTER_1_COMPLETED] = true
                        prefs[GameKeys.WORLD_EXPLORER_BADGE] = true
                        prefs[GameKeys.COINS] = (prefs[GameKeys.COINS] ?: 100) + 500
                        val chapterBonusXp = (prefs[GameKeys.XP] ?: 0) + 1000
                        prefs[GameKeys.XP] = chapterBonusXp
                        prefs[GameKeys.LEVEL] = PlayerLevelProgression.normalizedLevel(
                            totalXp = chapterBonusXp,
                            storedLevel = prefs[GameKeys.LEVEL] ?: 1
                        )
                        Match3Telemetry.log(
                            event = Match3TelemetryEvent.WORLD_UNLOCK,
                            levelId = Match3Telemetry.levelId(countryId, levelNumber),
                            countryId = countryId,
                            remainingMoves = 0,
                            score = score,
                            comboCount = stars,
                            cascadeCount = 0,
                            detail = "chapter1Complete"
                        )
                    }
                }
            }
        }
        Match3Telemetry.log(
            event = Match3TelemetryEvent.PROGRESS_SAVED,
            levelId = Match3Telemetry.levelId(countryId, levelNumber),
            countryId = countryId,
            remainingMoves = 0,
            score = score,
            comboCount = stars,
            cascadeCount = 0
        )
    }

    /**
     * Check if all levels of a country are completed and mark the country as completed.
     * Grant completion reward coins once.
     */
    private fun checkAndMarkCountryCompleted(countryId: String, prefs: MutablePreferences) {
        val spec = CountryProgressionChain.getSpec(countryId) ?: return
        
        // Check if this country is already marked as completed
        if (prefs[GameKeys.countryCompleted(countryId)] == true) {
            return
        }
        
        // Check if all required levels are completed
        var allLevelsCompleted = true
        for (levelNum in 1..spec.totalLevels) {
            if (!(prefs[GameKeys.levelCompleted(countryId, levelNum)] ?: false)) {
                allLevelsCompleted = false
                break
            }
        }
        
        if (allLevelsCompleted) {
            prefs[GameKeys.countryCompleted(countryId)] = true
            // Grant country completion reward coins (only once)
            prefs[GameKeys.COINS] = (prefs[GameKeys.COINS] ?: 100) + spec.rewardCoinsOnCompletion
            val completionXp = (prefs[GameKeys.XP] ?: 0) + spec.rewardXpOnCompletion
            prefs[GameKeys.XP] = completionXp
            prefs[GameKeys.LEVEL] = PlayerLevelProgression.normalizedLevel(
                totalXp = completionXp,
                storedLevel = prefs[GameKeys.LEVEL] ?: 1
            )
            Match3Telemetry.log(
                event = Match3TelemetryEvent.REWARD_GRANTED,
                levelId = Match3Telemetry.levelId(countryId, spec.totalLevels),
                countryId = countryId,
                remainingMoves = 0,
                score = 0,
                comboCount = spec.totalLevels,
                cascadeCount = 0,
                detail = "countryCompletion"
            )
            Match3Telemetry.log(
                event = Match3TelemetryEvent.XP_GRANTED,
                levelId = Match3Telemetry.levelId(countryId, spec.totalLevels),
                countryId = countryId,
                remainingMoves = 0,
                score = 0,
                comboCount = spec.totalLevels,
                cascadeCount = 0,
                detail = "amount=${spec.rewardXpOnCompletion}"
            )
            Match3Telemetry.log(
                event = Match3TelemetryEvent.COINS_GRANTED,
                levelId = Match3Telemetry.levelId(countryId, spec.totalLevels),
                countryId = countryId,
                remainingMoves = 0,
                score = 0,
                comboCount = spec.totalLevels,
                cascadeCount = 0,
                detail = "amount=${spec.rewardCoinsOnCompletion}"
            )
        }
    }

    suspend fun updatePlayerStats(lives: Int? = null, coins: Int? = null, xp: Int? = null) {
        context.gameDataStore.edit { prefs ->
            lives?.let { prefs[GameKeys.LIVES] = it }
            coins?.let { prefs[GameKeys.COINS] = it }
            xp?.let {
                val normalizedXp = it.coerceAtLeast(0)
                prefs[GameKeys.XP] = normalizedXp
                prefs[GameKeys.LEVEL] = PlayerLevelProgression.normalizedLevel(
                    totalXp = normalizedXp,
                    storedLevel = prefs[GameKeys.LEVEL] ?: 1
                )
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
                hammer = prefs[GameKeys.boosterCount(BoosterType.HAMMER)] ?: BoosterInventory().hammer,
                shuffle = prefs[GameKeys.boosterCount(BoosterType.SHUFFLE)] ?: BoosterInventory().shuffle,
                extraMoves = prefs[GameKeys.boosterCount(BoosterType.EXTRA_MOVES)] ?: BoosterInventory().extraMoves
            )
            val currentCount = currentInventory.countFor(type)
            if (currentCount <= 0) return@edit

            val nextCount = currentCount - 1
            prefs[GameKeys.boosterCount(type)] = nextCount
            updatedInventory = currentInventory.withCount(type, nextCount)
        }
        return updatedInventory
    }

    suspend fun grantBooster(type: BoosterType, amount: Int): BoosterInventory {
        var updatedInventory = BoosterInventory()
        context.gameDataStore.edit { prefs ->
            val currentInventory = BoosterInventory(
                hammer = prefs[GameKeys.boosterCount(BoosterType.HAMMER)] ?: BoosterInventory().hammer,
                shuffle = prefs[GameKeys.boosterCount(BoosterType.SHUFFLE)] ?: BoosterInventory().shuffle,
                extraMoves = prefs[GameKeys.boosterCount(BoosterType.EXTRA_MOVES)] ?: BoosterInventory().extraMoves
            )
            val nextInventory = currentInventory.withCount(
                type = type,
                count = currentInventory.countFor(type) + amount
            )
            prefs[GameKeys.boosterCount(BoosterType.HAMMER)] = nextInventory.hammer
            prefs[GameKeys.boosterCount(BoosterType.SHUFFLE)] = nextInventory.shuffle
            prefs[GameKeys.boosterCount(BoosterType.EXTRA_MOVES)] = nextInventory.extraMoves
            updatedInventory = nextInventory
        }
        return updatedInventory
    }

    suspend fun resetAllProgress() {
        context.gameDataStore.edit { it.clear() }
    }
}
