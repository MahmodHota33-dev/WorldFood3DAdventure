package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.AppScreen
import org.junit.Assert.*
import org.junit.Test

class NavigationLogicTest {

    @Test
    fun isForwardTransitionCorrectlyIdentifiesHierarchy() {
        // Simple hierarchy test for the transition logic
        // Hierarchy: WORLD_MAP (0) < LEVEL_SELECTION (1) < PREMIUM_ADVENTURE (6)
        // Wait, ordinals depend on enum order.
        /*
        enum class AppScreen {
            WORLD_MAP_V2,       // 0
            LEVEL_SELECTION,    // 1
            MATCH3_GAME,        // 2
            FOOD_BOOK,          // 3
            REWARDS,            // 4
            PROFILE,            // 5
            PREMIUM_ADVENTURE   // 6
        }
        */
        
        fun isForward(from: AppScreen, to: AppScreen): Boolean {
            return to.ordinal > from.ordinal
        }

        assertTrue(isForward(AppScreen.WORLD_MAP_V2, AppScreen.LEVEL_SELECTION))
        assertTrue(isForward(AppScreen.LEVEL_SELECTION, AppScreen.PREMIUM_ADVENTURE))
        assertFalse(isForward(AppScreen.PREMIUM_ADVENTURE, AppScreen.WORLD_MAP_V2))
        assertFalse(isForward(AppScreen.LEVEL_SELECTION, AppScreen.WORLD_MAP_V2))
    }

    @Test
    fun navigationDebouncePreventsRapidMultipleEvents() {
        var navCount = 0
        var lastNavTime = 0L
        
        fun navigate(now: Long) {
            if (now - lastNavTime < 500) return
            lastNavTime = now
            navCount++
        }

        navigate(1000)
        assertEquals(1, navCount)
        
        navigate(1100) // Too soon
        assertEquals(1, navCount)
        
        navigate(1499) // Too soon
        assertEquals(1, navCount)
        
        navigate(1500) // Exactly 500ms
        assertEquals(2, navCount)
        
        navigate(2500) // Well after
        assertEquals(3, navCount)
    }
}
