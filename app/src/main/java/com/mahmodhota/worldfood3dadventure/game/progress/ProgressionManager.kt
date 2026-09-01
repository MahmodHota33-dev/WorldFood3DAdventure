package com.mahmodhota.worldfood3dadventure.game.progress

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressManager
import com.mahmodhota.worldfood3dadventure.data.progress.model.PlayerProgress
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterInventory
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType
import com.mahmodhota.worldfood3dadventure.game.match3.model.EconomyConfig
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.telemetry.Match3Telemetry
import com.mahmodhota.worldfood3dadventure.telemetry.Match3TelemetryEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manages game progression logic. Bridges in-memory state with Persistence.
 */
object ProgressionManager {

    private val levelChain get() = LevelRegistry.allCountryIds
    private var scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    
    // P10-Y: Injectable dispatcher for testing
    private var ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    @Volatile
    private var initialized = false

    fun resetForTesting(dispatcher: CoroutineDispatcher? = null) {
        scope.cancel()
        scope = CoroutineScope(SupervisorJob() + (dispatcher ?: Dispatchers.Main.immediate))
        if (dispatcher != null) ioDispatcher = dispatcher
        initialized = false
        _isInitialized.value = false
        _progressMap.clear()
        _playerProgress.value = PlayerProgress()
        _newlyUnlockedCountry.value = null
        _pendingTravelDestination.value = null
        AchievementManager.resetForTesting()
    }

    private val _progressMap = mutableStateMapOf<String, CountryProgress>()
    private val _playerProgress = mutableStateOf(PlayerProgress())
    private val _isInitialized = mutableStateOf(false)
    private val _newlyUnlockedCountry = mutableStateOf<com.mahmodhota.worldfood3dadventure.game.world.model.CountryUnlockSpec?>(null)
    private val _pendingTravelDestination = mutableStateOf<String?>(null)

    /**
     * Observable map of country progress.
     */
    val progressMap: Map<String, CountryProgress> get() = _progressMap
    val playerProgress: PlayerProgress get() = _playerProgress.value
    val boosterInventory: BoosterInventory get() = _playerProgress.value.boosterInventory
    val isInitialized: Boolean get() = _isInitialized.value
    val newlyUnlockedCountry: com.mahmodhota.worldfood3dadventure.game.world.model.CountryUnlockSpec? get() = _newlyUnlockedCountry.value
    val pendingTravelDestination: String? get() = _pendingTravelDestination.value

    fun consumeUnlockEvent() {
        _newlyUnlockedCountry.value = null
    }

    fun setTravelDestination(countryId: String?) {
        _pendingTravelDestination.value = countryId
    }

    fun consumeTravelDestination(): String? {
        val dest = _pendingTravelDestination.value
        _pendingTravelDestination.value = null
        return dest
    }

    /**
     * Initializes the manager by observing the repository.
     */
    fun initialize() {
        if (initialized) return
        initialized = true
        scope.launch {
            GameProgressManager.repository.state.collect { gameState ->
                // P10-AO: Move heavy mapping of 3,195 levels to background thread
                val mappedProgress = withContext(Dispatchers.Default) {
                    gameState.countries.mapValues { (id, countryData) ->
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
                            },
                            discoveredFoods = countryData.discoveredFoods
                        )
                    }
                }
                
                // Surgical update on Main thread
                mappedProgress.forEach { (id, progress) ->
                    if (_progressMap[id] != progress) {
                        _progressMap[id] = progress
                    }
                }
                val keysToRemove = _progressMap.keys.filter { it !in mappedProgress }
                keysToRemove.forEach { _progressMap.remove(it) }

                _playerProgress.value = gameState.player
                _isInitialized.value = true
            }
        }
    }

    /**
     * Returns the progress for a specific country.
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
    fun completeLevel(countryId: String, levelNumber: Int, score: Int, stars: Int, discoveredFood: String? = null) {
        scope.launch(ioDispatcher) {
            val newlyUnlockedIds = GameProgressManager.repository.saveLevelProgress(
                countryId = countryId,
                levelNumber = levelNumber,
                stars = stars,
                score = score,
                xpReward = EconomyConfig.LEVEL_VICTORY_XP_PER_STAR * stars,
                coinReward = EconomyConfig.LEVEL_VICTORY_COINS_PER_STAR * stars,
                discoveredFood = discoveredFood
            )
            
            // P10-W: Update Daily Mission Progress
            GameProgressManager.repository.updateDailyMissionProgress(MissionType.LEVELS, 1)
            GameProgressManager.repository.updateDailyMissionProgress(MissionType.STARS, stars)
            if (discoveredFood != null) {
                GameProgressManager.repository.updateDailyMissionProgress(MissionType.FOOD, 1)
            }
            if (levelNumber >= 15) {
                GameProgressManager.repository.updateDailyMissionProgress(MissionType.TRAVEL, 1)
            }

            if (newlyUnlockedIds?.isNotEmpty() == true) {
                withContext(Dispatchers.Main.immediate) {
                    val firstId = newlyUnlockedIds.firstOrNull() ?: return@withContext
                    val spec = com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain.getSpec(firstId)
                    _newlyUnlockedCountry.value = spec
                }
            }
        }
    }

    suspend fun consumeBooster(type: BoosterType): BoosterInventory? {
        return GameProgressManager.repository.consumeBooster(type)
    }

    suspend fun purchaseBooster(type: BoosterType): Boolean {
        val cost = com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterConfig.costFor(type)
        if (playerProgress.coins < cost) return false
        
        return GameProgressManager.repository.purchaseBooster(type, cost)
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
     * Legacy method for marking country completed.
     */
    fun markLevelCompleted(levelId: String) {
        val country = getCountryProgress(levelId)
        _progressMap[levelId] = country.copy(isCompleted = true)
        unlockNextCountry(levelId)
    }
}
