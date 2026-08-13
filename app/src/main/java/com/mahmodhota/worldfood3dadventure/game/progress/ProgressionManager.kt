package com.mahmodhota.worldfood3dadventure.game.progress

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressManager
import com.mahmodhota.worldfood3dadventure.data.progress.model.PlayerProgress
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterInventory
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.telemetry.Match3Telemetry
import com.mahmodhota.worldfood3dadventure.telemetry.Match3TelemetryEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manages game progression logic. Bridges in-memory state with Persistence.
 */
object ProgressionManager {

    private val levelChain get() = LevelRegistry.allCountryIds
    private val scope = CoroutineScope(Dispatchers.IO)
    @Volatile
    private var initialized = false

    private val _progressMap = mutableStateMapOf<String, CountryProgress>()
    private val _playerProgress = mutableStateOf(PlayerProgress())
    private val _isInitialized = mutableStateOf(false)
    private val _newlyUnlockedCountry = mutableStateOf<com.mahmodhota.worldfood3dadventure.game.world.model.CountryUnlockSpec?>(null)

    /**
     * Observable map of country progress.
     */
    val progressMap: Map<String, CountryProgress> get() = _progressMap
    val playerProgress: PlayerProgress get() = _playerProgress.value
    val boosterInventory: BoosterInventory get() = _playerProgress.value.boosterInventory
    val isInitialized: Boolean get() = _isInitialized.value
    val newlyUnlockedCountry: com.mahmodhota.worldfood3dadventure.game.world.model.CountryUnlockSpec? get() = _newlyUnlockedCountry.value

    fun consumeUnlockEvent() {
        _newlyUnlockedCountry.value = null
    }

    /**
     * Initializes the manager by observing the repository.
     */
    fun initialize() {
        if (initialized) return
        initialized = true
        scope.launch {
            GameProgressManager.repository.state.collect { gameState ->
                val mappedProgress = gameState.countries.mapValues { (id, countryData) ->
                    CountryProgress(
                        levelId = id,
                        isUnlocked = countryData.isUnlocked,
                        isCompleted = countryData.isCompleted,
                        levels = countryData.levels.values.map { 
                            Match3LevelProgress(
                                levelNumber = it.levelNumber,
                                isUnlocked = it.isUnlocked,
                                isCompleted = it.isCompleted,
                                stars = it.bestStars,
                                highScore = it.bestScore
                            )
                        }
                    )
                }
                // Compose state is updated on Main to avoid cross-thread snapshot contention
                withContext(Dispatchers.Main.immediate) {
                    // Surgical update: update existing or add new, avoid clearing the whole map
                    // to prevent MainActivity from seeing an empty state and resetting the UI.
                    mappedProgress.forEach { (id, progress) ->
                        if (_progressMap[id] != progress) {
                            _progressMap[id] = progress
                        }
                    }
                    // Optional: remove keys that no longer exist in DataStore (unlikely in this game)
                    val keysToRemove = _progressMap.keys.filter { it !in mappedProgress }
                    keysToRemove.forEach { _progressMap.remove(it) }

                    _playerProgress.value = gameState.player
                    _isInitialized.value = true
                }
                Match3Telemetry.log(
                    event = Match3TelemetryEvent.PASSPORT_UPDATED,
                    levelId = Match3Telemetry.levelId(gameState.lastSelectedCountry, gameState.lastSelectedLevel),
                    countryId = gameState.lastSelectedCountry,
                    remainingMoves = 0,
                    score = 0,
                    comboCount = gameState.player.totalStars,
                    cascadeCount = gameState.countries.size,
                    detail = "countries=${gameState.countries.size}"
                )
            }
        }
    }

    /**
     * Returns the progress for a specific country.
     * Uses [CountryProgressionChain.isInitiallyUnlocked] as fallback so Germany
     * is never shown as locked on a fresh install.
     */
    fun getCountryProgress(levelId: String): CountryProgress {
        return _progressMap[levelId] ?: CountryProgress(
            levelId = levelId,
            isUnlocked = com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
                .isInitiallyUnlocked(levelId)
        )
    }

    /**
     * Completes a Match-3 level and saves to persistence.
     */
    fun completeLevel(countryId: String, levelNumber: Int, score: Int, stars: Int) {
        scope.launch {
            val newlyUnlockedIds = GameProgressManager.repository.saveLevelProgress(
                countryId = countryId,
                levelNumber = levelNumber,
                stars = stars,
                score = score,
                xpReward = 50 * stars,
                coinReward = 10 * stars
            )
            
            if (newlyUnlockedIds.isNotEmpty()) {
                withContext(Dispatchers.Main.immediate) {
                    val spec = com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain.getSpec(newlyUnlockedIds.first())
                    _newlyUnlockedCountry.value = spec
                }
            }
        }
    }

    suspend fun consumeBooster(type: BoosterType): BoosterInventory? {
        return GameProgressManager.repository.consumeBooster(type)
    }

    suspend fun grantBooster(type: BoosterType, amount: Int): BoosterInventory {
        return GameProgressManager.repository.grantBooster(type, amount)
    }

    private fun unlockNextCountry(currentId: String) {
        val currentIndex = levelChain.indexOf(currentId)
        if (currentIndex != -1 && currentIndex < levelChain.size - 1) {
            val nextId = levelChain[currentIndex + 1]
            val nextCountryDef = LevelRegistry.getCountry(nextId)
            if (nextCountryDef?.isComingSoon == true) {
                return
            }
            val nextCountry = getCountryProgress(nextId)
            if (!nextCountry.isUnlocked) {
                val updatedLevels = nextCountry.levels.map { 
                    if (it.levelNumber == 1) it.copy(isUnlocked = true) else it 
                }
                _progressMap[nextId] = nextCountry.copy(isUnlocked = true, levels = updatedLevels)
            }
        }
    }

    /**
     * Legacy method for marking country completed (3D mode compatibility).
     */
    fun markLevelCompleted(levelId: String) {
        val country = getCountryProgress(levelId)
        _progressMap[levelId] = country.copy(isCompleted = true)
        unlockNextCountry(levelId)
        Match3Telemetry.log(
            event = Match3TelemetryEvent.PASSPORT_UPDATED,
            levelId = Match3Telemetry.levelId(levelId, 1),
            countryId = levelId,
            remainingMoves = 0,
            score = 0,
            comboCount = 0,
            cascadeCount = 0,
            detail = "legacyMarkLevelCompleted"
        )
    }
}
