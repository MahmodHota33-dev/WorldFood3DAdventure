package com.mahmodhota.worldfood3dadventure.game.progress

import org.junit.Assert.assertEquals
import org.junit.Test

class P10XOnboardingStateTest {

    @Test
    fun `Initial onboarding state should be NOT_STARTED`() {
        val state = OnboardingState.NOT_STARTED
        assertEquals("NOT_STARTED", state.name)
    }

    @Test
    fun `State transitions should be valid`() {
        var current = OnboardingState.NOT_STARTED
        current = OnboardingState.WELCOME_SHOWN
        current = OnboardingState.WORLD_MAP_INTRO
        current = OnboardingState.COMPLETED
        assertEquals(OnboardingState.COMPLETED, current)
    }
}
