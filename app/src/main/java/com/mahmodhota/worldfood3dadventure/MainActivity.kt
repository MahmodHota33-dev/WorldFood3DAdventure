package com.mahmodhota.worldfood3dadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat // Added
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager
import com.mahmodhota.worldfood3dadventure.data.audio.MusicType
import com.mahmodhota.worldfood3dadventure.data.audio.SoundRepository
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressManager
import com.mahmodhota.worldfood3dadventure.game.progress.OnboardingState
import com.mahmodhota.worldfood3dadventure.game.progress.PlayerProfile
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import com.mahmodhota.worldfood3dadventure.ui.match3.*
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumColors
import com.mahmodhota.worldfood3dadventure.ui.match3.components.WelcomeOnboardingScreen
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
    PREMIUM_ADVENTURE,
    FOOD_COLLECTION
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false) // Added for ZTE support
        super.onCreate(savedInstanceState)
        
        // Initialize Managers
        GameProgressManager.initialize(this)
        GlobalSystemManager.initialize(this)
        ProgressionManager.initialize()
        PlayerProfile.initialize()
        // Force initialization of AchievementManager to prevent snapshot read errors during initial composition
        com.mahmodhota.worldfood3dadventure.game.progress.AchievementManager.hashCode()

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
                var previousScreen by remember { mutableStateOf<AppScreen?>(null) }
                var selectedLevelId by remember { mutableStateOf<String?>(null) }
                var selectedMatch3Level by remember { mutableStateOf(1) }
                // Tracks whether PREMIUM_ADVENTURE was entered from LEVEL_SELECTION (true)
                // or directly from the Globe Continue button (false).
                var showLevelsForReturn by remember { mutableStateOf(false) }

                var showSettings by remember { mutableStateOf(false) }

                if (showSettings) {
                    com.mahmodhota.worldfood3dadventure.ui.match3.components.SettingsDialog(onDismiss = { showSettings = false })
                }

                // Navigation Debounce: prevent rapid double-clicks from double-navigating
                var lastNavTime by remember { mutableStateOf(0L) }
                fun navigateTo(screen: AppScreen, debounce: Boolean = true) {
                    val now = System.currentTimeMillis()
                    if (debounce && now - lastNavTime < 500) return
                    lastNavTime = now
                    previousScreen = currentScreen
                    currentScreen = screen
                }

                // Music Controller
                LaunchedEffect(currentScreen, selectedLevelId) {
                    when (currentScreen) {
                        AppScreen.WORLD_MAP_V2 -> GlobalSystemManager.audio.playMusic(MusicType.WORLD_MAP)
                        AppScreen.PREMIUM_ADVENTURE -> {
                            selectedLevelId?.let { id ->
                                GlobalSystemManager.audio.playMusic(SoundRepository.getMusicForCountry(id))
                            } ?: GlobalSystemManager.audio.playMusic(MusicType.WORLD_MAP)
                        }
                        AppScreen.MATCH3_GAME -> {
                            selectedLevelId?.let { id ->
                                GlobalSystemManager.audio.playMusic(SoundRepository.getMusicForCountry(id))
                            }
                        }
                        else -> {} // Keep current or stop
                    }
                }

                // Check if progression is initialized
                val isProgressLoaded = ProgressionManager.isInitialized
                val onboardingState = GameProgressManager.repository.state.collectAsState().value.onboardingState

                if (!isProgressLoaded) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(PremiumColors.DeepNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "🌍", 
                                fontSize = 48.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            CircularProgressIndicator(
                                color = PremiumColors.Gold,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "LOADING ADVENTURE...",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 16.dp),
                                letterSpacing = 1.sp
                            )
                        }
                    }
                } else if (onboardingState == OnboardingState.NOT_STARTED) {
                    WelcomeOnboardingScreen(
                        onStart = {
                            lifecycleScope.launch {
                                GameProgressManager.repository.updateOnboardingState(OnboardingState.WELCOME_SHOWN)
                            }
                        },
                        onSkip = {
                            lifecycleScope.launch {
                                GameProgressManager.repository.updateOnboardingState(OnboardingState.COMPLETED)
                            }
                        }
                    )
                } else {
                    // System back button handling
                    BackHandler(enabled = currentScreen != AppScreen.WORLD_MAP_V2) {
                        val next = when (currentScreen) {
                            AppScreen.MATCH3_GAME -> AppScreen.LEVEL_SELECTION
                            AppScreen.LEVEL_SELECTION -> AppScreen.WORLD_MAP_V2
                            AppScreen.PREMIUM_ADVENTURE ->
                                if (showLevelsForReturn) AppScreen.LEVEL_SELECTION
                                else AppScreen.WORLD_MAP_V2
                            else -> AppScreen.WORLD_MAP_V2
                        }
                        navigateTo(next, debounce = false)
                    }

                    Box(modifier = Modifier.fillMaxSize().background(PremiumColors.DeepNavy)) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                val isForward = isForwardTransition(initialState, targetState)
                                if (isForward) {
                                    (slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400)) + fadeIn())
                                        .togetherWith(slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(400)) + fadeOut())
                                } else {
                                    (slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = tween(400)) + fadeIn())
                                        .togetherWith(slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400)) + fadeOut())
                                }
                            },
                            label = "screenTransition"
                        ) { targetScreen ->
                            when (targetScreen) {
                                AppScreen.WORLD_MAP_V2 -> {
                                    Globe3DScreen(
                                        onLevelSelected = { countryId, levelNum ->
                                            android.util.Log.d("MainActivity", "Level selected: country=$countryId level=$levelNum")
                                            selectedLevelId = countryId
                                            selectedMatch3Level = levelNum
                                            showLevelsForReturn = false
                                            navigateTo(AppScreen.PREMIUM_ADVENTURE)
                                        },
                                        onShowLevels = { countryId ->
                                            android.util.Log.d("MainActivity", "Show levels for country: $countryId")
                                            selectedLevelId = countryId
                                            navigateTo(AppScreen.LEVEL_SELECTION)
                                        },
                                        onTabSelected = { tab ->
                                            val next = when (tab) {
                                                "book" -> AppScreen.FOOD_BOOK
                                                "rewards" -> AppScreen.REWARDS
                                                "profile" -> AppScreen.PROFILE
                                                "collection" -> AppScreen.FOOD_COLLECTION
                                                else -> AppScreen.WORLD_MAP_V2
                                            }
                                            if (next != currentScreen) navigateTo(next)
                                        },
                                        onSettingsClick = { showSettings = true }
                                    )
                                }
                                AppScreen.PREMIUM_ADVENTURE -> {
                                    PremiumAdventureScreen(
                                        countryId = selectedLevelId ?: "germany",
                                        levelNumber = selectedMatch3Level,
                                        onReturn = {
                                            val next = if (showLevelsForReturn) AppScreen.LEVEL_SELECTION
                                            else AppScreen.WORLD_MAP_V2
                                            navigateTo(next)
                                        },
                                        onNextLevelSelected = { nextLevel ->
                                            selectedMatch3Level = nextLevel
                                        },
                                        onTabSelected = { tab ->
                                            val next = when (tab) {
                                                "world" -> AppScreen.WORLD_MAP_V2
                                                "rewards" -> AppScreen.REWARDS
                                                "book" -> AppScreen.FOOD_BOOK
                                                "profile" -> AppScreen.PROFILE
                                                "collection" -> AppScreen.FOOD_COLLECTION
                                                else -> AppScreen.PREMIUM_ADVENTURE
                                            }
                                            if (next != currentScreen) navigateTo(next)
                                        },
                                        onSettingsClick = { showSettings = true }
                                    )
                                }
                                AppScreen.LEVEL_SELECTION -> {
                                    LevelSelectionScreen(
                                        countryId = selectedLevelId ?: "germany",
                                        onLevelSelected = { num ->
                                            selectedMatch3Level = num
                                            showLevelsForReturn = true
                                            navigateTo(AppScreen.PREMIUM_ADVENTURE)
                                        },
                                        onBackToMap = { navigateTo(AppScreen.WORLD_MAP_V2) },
                                        onSettingsClick = { showSettings = true }
                                    )
                                }
                                AppScreen.MATCH3_GAME -> {
                                    Match3GameScreen(
                                        countryId = selectedLevelId ?: "germany",
                                        levelNumber = selectedMatch3Level,
                                        onBackToMap = { navigateTo(AppScreen.LEVEL_SELECTION) }
                                    )
                                }
                                AppScreen.FOOD_BOOK -> PassportScreen(
                                    onTabSelected = { tab ->
                                        val next = when (tab) {
                                            "world" -> AppScreen.WORLD_MAP_V2
                                            "rewards" -> AppScreen.REWARDS
                                            "profile" -> AppScreen.PROFILE
                                            "collection" -> AppScreen.FOOD_COLLECTION
                                            else -> AppScreen.FOOD_BOOK
                                        }
                                        if (next != currentScreen) navigateTo(next)
                                    },
                                    onSettingsClick = { showSettings = true }
                                )
                                AppScreen.REWARDS -> RewardsScreen(
                                    onTabSelected = { tab ->
                                        val next = when (tab) {
                                            "world" -> AppScreen.WORLD_MAP_V2
                                            "book" -> AppScreen.FOOD_BOOK
                                            "profile" -> AppScreen.PROFILE
                                            "collection" -> AppScreen.FOOD_COLLECTION
                                            else -> AppScreen.REWARDS
                                        }
                                        if (next != currentScreen) navigateTo(next)
                                    },
                                    onSettingsClick = { showSettings = true }
                                )
                                AppScreen.PROFILE -> ProfileScreenV2(
                                    onTabSelected = { tab: String ->
                                        val next = when (tab) {
                                            "world" -> AppScreen.WORLD_MAP_V2
                                            "book" -> AppScreen.FOOD_BOOK
                                            "rewards" -> AppScreen.REWARDS
                                            "collection" -> AppScreen.FOOD_COLLECTION
                                            else -> AppScreen.PROFILE
                                        }
                                        if (next != currentScreen) navigateTo(next)
                                    },
                                    onSettingsClick = { showSettings = true }
                                )
                                AppScreen.FOOD_COLLECTION -> FoodCollectionScreen(
                                    onTabSelected = { tab: String ->
                                        val next = when (tab) {
                                            "world" -> AppScreen.WORLD_MAP_V2
                                            "book" -> AppScreen.FOOD_BOOK
                                            "rewards" -> AppScreen.REWARDS
                                            "profile" -> AppScreen.PROFILE
                                            "collection" -> AppScreen.FOOD_COLLECTION
                                            else -> AppScreen.FOOD_COLLECTION
                                        }
                                        if (next != currentScreen) navigateTo(next)
                                    },
                                    onSettingsClick = { showSettings = true }
                                )
                            }
                        }
                        
                        // P10-V: Global Achievement Overlay
                        com.mahmodhota.worldfood3dadventure.ui.match3.components.AchievementUnlockOverlay()
                    }
                }
            }
        }
    }

    private fun isForwardTransition(from: AppScreen, to: AppScreen): Boolean {
        // Hierarchy: WORLD_MAP (0) < LEVEL_SELECTION (1) < PREMIUM_ADVENTURE (2)
        // Others (Profile/Rewards/Book) are siblings of WORLD_MAP but for animation
        // we'll treat them as forward from world map.
        val fromVal = from.ordinal
        val toVal = to.ordinal
        return toVal > fromVal
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
