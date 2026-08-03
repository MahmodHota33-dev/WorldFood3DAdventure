package com.mahmodhota.worldfood3dadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager
import com.mahmodhota.worldfood3dadventure.data.audio.MusicType
import com.mahmodhota.worldfood3dadventure.data.audio.SoundRepository
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressManager
import com.mahmodhota.worldfood3dadventure.game.progress.PlayerProfile
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import com.mahmodhota.worldfood3dadventure.ui.match3.*
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumColors
import com.mahmodhota.worldfood3dadventure.ui.theme.WorldFood3DAdventureTheme
import com.mahmodhota.worldfood3dadventure.ui.world3d.Globe3DScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Top-level application state for navigation.
 */
enum class AppScreen {
    WORLD_MAP_V2,
    LEVEL_SELECTION,
    MATCH3_GAME,
    FOOD_BOOK,
    REWARDS,
    PROFILE,
    PREMIUM_ADVENTURE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Managers
        GameProgressManager.initialize(this)
        GlobalSystemManager.initialize(this)
        ProgressionManager.initialize()
        PlayerProfile.initialize()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                GameProgressManager.repository.state
                    .map { it.settings }
                    .distinctUntilChanged()
                    .collect { settings ->
                        GlobalSystemManager.applySettings(
                            musicVolume = settings.musicVolume,
                            sfxVolume = settings.sfxVolume,
                            vibrationEnabled = settings.vibrationEnabled
                        )
                    }
            }
        }

        enableEdgeToEdge()
        setContent {
            WorldFood3DAdventureTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.WORLD_MAP_V2) }
                var selectedLevelId by remember { mutableStateOf<String?>(null) }
                var selectedMatch3Level by remember { mutableStateOf(1) }

                // Music Controller
                LaunchedEffect(currentScreen, selectedLevelId) {
                    when (currentScreen) {
                        AppScreen.WORLD_MAP_V2 -> GlobalSystemManager.audio.playMusic(MusicType.WORLD_MAP)
                        AppScreen.PREMIUM_ADVENTURE -> GlobalSystemManager.audio.playMusic(MusicType.WORLD_MAP)
                        AppScreen.MATCH3_GAME -> {
                            selectedLevelId?.let { id ->
                                GlobalSystemManager.audio.playMusic(SoundRepository.getMusicForCountry(id))
                            }
                        }
                        else -> {} // Keep current or stop
                    }
                }

                // Check if progression is initialized
                val isProgressLoaded = ProgressionManager.progressMap.isNotEmpty()

                if (!isProgressLoaded) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color(0xFF121212)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                    }
                } else {
                    // System back button handling
                    BackHandler(enabled = currentScreen != AppScreen.WORLD_MAP_V2) {
                        currentScreen = when (currentScreen) {
                            AppScreen.MATCH3_GAME -> AppScreen.LEVEL_SELECTION
                            AppScreen.LEVEL_SELECTION -> AppScreen.WORLD_MAP_V2
                            AppScreen.PREMIUM_ADVENTURE -> AppScreen.WORLD_MAP_V2
                            else -> AppScreen.WORLD_MAP_V2
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize().background(PremiumColors.DeepNavy)) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f))
                                    .togetherWith(fadeOut(animationSpec = tween(300)))
                            },
                            label = "screenTransition"
                        ) { targetScreen ->
                            when (targetScreen) {
                                AppScreen.WORLD_MAP_V2 -> {
                                    Globe3DScreen(
                                        onLevelSelected = { countryId, levelNum ->
                                            selectedLevelId = countryId
                                            selectedMatch3Level = levelNum
                                            currentScreen = AppScreen.PREMIUM_ADVENTURE
                                        },
                                        onTabSelected = { tab ->
                                            currentScreen = when (tab) {
                                                "book" -> AppScreen.FOOD_BOOK
                                                "rewards" -> AppScreen.REWARDS
                                                "profile" -> AppScreen.PROFILE
                                                else -> AppScreen.WORLD_MAP_V2
                                            }
                                        }
                                    )
                                }
                                AppScreen.PREMIUM_ADVENTURE -> {
                                    PremiumAdventureScreen(
                                        countryId = selectedLevelId ?: "germany",
                                        levelNumber = selectedMatch3Level,
                                        onTabSelected = { tab ->
                                            currentScreen = when (tab) {
                                                "world" -> AppScreen.WORLD_MAP_V2
                                                "rewards" -> AppScreen.REWARDS
                                                "book" -> AppScreen.FOOD_BOOK
                                                "profile" -> AppScreen.PROFILE
                                                else -> AppScreen.PREMIUM_ADVENTURE
                                            }
                                        },
                                        onSettingsClick = { /* Handle settings */ }
                                    )
                                }
                                AppScreen.LEVEL_SELECTION -> {
                                    LevelSelectionScreen(
                                        countryId = selectedLevelId ?: "germany",
                                        onLevelSelected = { num ->
                                            selectedMatch3Level = num
                                            currentScreen = AppScreen.MATCH3_GAME
                                        },
                                        onBackToMap = { currentScreen = AppScreen.WORLD_MAP_V2 }
                                    )
                                }
                                    AppScreen.MATCH3_GAME -> {
                                    Match3GameScreen(
                                        countryId = selectedLevelId ?: "germany",
                                        levelNumber = selectedMatch3Level,
                                        onBackToMap = { currentScreen = AppScreen.LEVEL_SELECTION }
                                    )
                                }
                                AppScreen.FOOD_BOOK -> PassportScreen(onTabSelected = { tab ->
                                    currentScreen = when (tab) {
                                        "world" -> AppScreen.WORLD_MAP_V2
                                        "rewards" -> AppScreen.REWARDS
                                        "profile" -> AppScreen.PROFILE
                                        else -> AppScreen.FOOD_BOOK
                                    }
                                })
                                AppScreen.REWARDS -> RewardsScreen(onTabSelected = { tab ->
                                    currentScreen = when (tab) {
                                        "world" -> AppScreen.WORLD_MAP_V2
                                        "book" -> AppScreen.FOOD_BOOK
                                        "profile" -> AppScreen.PROFILE
                                        else -> AppScreen.REWARDS
                                    }
                                })
                                AppScreen.PROFILE -> ProfileScreenV2(onTabSelected = { tab ->
                                    currentScreen = when (tab) {
                                        "world" -> AppScreen.WORLD_MAP_V2
                                        "book" -> AppScreen.FOOD_BOOK
                                        "rewards" -> AppScreen.REWARDS
                                        else -> AppScreen.PROFILE
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalSystemManager.release()
    }

    override fun onStop() {
        GlobalSystemManager.onAppBackground()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        GlobalSystemManager.onAppForeground()
    }
}
