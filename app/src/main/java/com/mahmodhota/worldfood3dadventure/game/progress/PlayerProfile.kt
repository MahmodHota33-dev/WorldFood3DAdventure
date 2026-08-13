package com.mahmodhota.worldfood3dadventure.game.progress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressManager
import com.mahmodhota.worldfood3dadventure.telemetry.Match3Telemetry
import com.mahmodhota.worldfood3dadventure.telemetry.Match3TelemetryEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Global player profile and stats. Syncs with Persistence.
 */
object PlayerProfile {
    var lives by mutableStateOf(5)
    var coins by mutableStateOf(100)
    var stars by mutableStateOf(0)
    var xp by mutableStateOf(0)
    var level by mutableStateOf(1)
    var username by mutableStateOf("FoodieTraveler")
    
    private val scope = CoroutineScope(Dispatchers.Main)
    @Volatile
    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        scope.launch {
            GameProgressManager.repository.state.collect { gameState ->
                lives = gameState.player.lives
                coins = gameState.player.coins
                stars = gameState.player.totalStars
                xp = gameState.player.xp
                level = gameState.player.level
                username = gameState.player.username
                Match3Telemetry.log(
                    event = Match3TelemetryEvent.PROFILE_UPDATED,
                    levelId = Match3Telemetry.levelId(gameState.lastSelectedCountry, gameState.lastSelectedLevel),
                    countryId = gameState.lastSelectedCountry,
                    remainingMoves = 0,
                    score = gameState.player.coins,
                    comboCount = gameState.player.totalStars,
                    cascadeCount = gameState.player.xp,
                    detail = "level=${gameState.player.level} user=$username"
                )
            }
        }
    }

    val xpToNextLevel: Int get() = PlayerLevelProgression.XP_PER_LEVEL
    val xpProgress: Float
        get() {
            val snapshot = PlayerLevelProgression.snapshot(totalXp = xp, storedLevel = level)
            return snapshot.xpIntoCurrentLevel.toFloat() / snapshot.xpForNextLevel.toFloat()
        }

    /**
     * Adds XP and handles level up (Logic handled in Repository now, but keeping for compatibility).
     */
    fun addXp(amount: Int) {
        scope.launch {
            GameProgressManager.repository.updatePlayerStats(xp = xp + amount)
        }
    }
}
