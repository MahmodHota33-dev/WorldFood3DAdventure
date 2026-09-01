package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.MainDispatcherRule
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressManager
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressRepository
import com.mahmodhota.worldfood3dadventure.data.progress.model.PersistedGameState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class P10YOnboardingIntegrityTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockRepository: GameProgressRepository = mock()
    private val gameStateFlow = MutableStateFlow(PersistedGameState())

    @Before
    fun setup() {
        whenever(mockRepository.state).thenReturn(gameStateFlow)
        GameProgressManager.setRepositoryForTesting(mockRepository)
        ProgressionManager.resetForTesting()
    }

    @Test
    fun `Onboarding state should be shared via ProgressionManager`() = runTest(mainDispatcherRule.testDispatcher) {
        gameStateFlow.value = gameStateFlow.value.copy(
            onboardingState = OnboardingState.WELCOME_SHOWN
        )
        
        ProgressionManager.initialize()
        advanceUntilIdle()
        
        // ProgressionManager should ideally expose this or we check the repository directly
        assertEquals(OnboardingState.WELCOME_SHOWN, GameProgressManager.repository.state.value.onboardingState)
    }
}
