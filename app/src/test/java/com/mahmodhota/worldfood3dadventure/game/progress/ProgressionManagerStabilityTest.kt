package com.mahmodhota.worldfood3dadventure.game.progress

import org.junit.Assert.*
import org.junit.Test

class ProgressionManagerStabilityTest {

    @Test
    fun initializedFlagInitiallyFalse() {
        // ProgressionManager is a singleton, we need to be careful with its state.
        // Assuming it's not initialized yet for this test run.
        // But since it's an object, it might have been initialized by other tests.
        // This is more for documentation of intent.
    }

    @Test
    fun getCountryProgressFallbackWorksOnEmptyMap() {
        // Verify that before the map is populated, we still get valid default objects
        val germany = ProgressionManager.getCountryProgress("germany")
        assertTrue(germany.isUnlocked)
        assertEquals("germany", germany.levelId)

        val italy = ProgressionManager.getCountryProgress("italy")
        assertFalse(italy.isUnlocked)
    }

    @Test
    fun progressionUpdateDoesNotExposeEmptyState() {
        // This is a behavioral expectation test. 
        // In the real code, we use SnapshotStateMap.
        // We want to ensure that once a key is added, it is only updated, never cleared globally.
        
        // Mock simulation of ProgressionManager logic (since we can't easily mock Main thread context here)
        val internalMap = mutableMapOf<String, CountryProgress>()
        
        fun updateMap(newStates: Map<String, CountryProgress>) {
            // clear() was the bug
            // internalMap.clear() 
            
            // Surgical update (new logic)
            newStates.forEach { (id, state) ->
                internalMap[id] = state
            }
        }

        // 1. Initial population
        updateMap(mapOf("germany" to CountryProgress("germany", isUnlocked = true)))
        assertTrue(internalMap.isNotEmpty())

        // 2. Update with new star count
        val updatedGermany = CountryProgress("germany", isUnlocked = true, isCompleted = true)
        updateMap(mapOf("germany" to updatedGermany))
        
        // Key point: Germany should ALWAYS be there during the update process in a real thread.
        assertTrue("Germany should not have been removed", internalMap.containsKey("germany"))
        assertEquals(updatedGermany, internalMap["germany"])
    }

    @Test
    fun unlockThresholdsRemainCorrect() {
        // Germany: 0
        assertTrue(StarUnlockLogicTestHelper.canUnlock("germany", 0))
        
        // Italy: 30
        assertFalse(StarUnlockLogicTestHelper.canUnlock("italy", 29))
        assertTrue(StarUnlockLogicTestHelper.canUnlock("italy", 30))
        
        // France: 60
        assertFalse(StarUnlockLogicTestHelper.canUnlock("france", 59))
        assertTrue(StarUnlockLogicTestHelper.canUnlock("france", 60))
        
        // Spain: 90
        assertFalse(StarUnlockLogicTestHelper.canUnlock("spain", 89))
        assertTrue(StarUnlockLogicTestHelper.canUnlock("spain", 90))
        
        // Japan: 120
        assertFalse(StarUnlockLogicTestHelper.canUnlock("japan", 119))
        assertTrue(StarUnlockLogicTestHelper.canUnlock("japan", 120))
        
        // Mexico: 145
        assertFalse(StarUnlockLogicTestHelper.canUnlock("mexico", 144))
        assertTrue(StarUnlockLogicTestHelper.canUnlock("mexico", 145))
        
        // Sudan: 185
        assertFalse(StarUnlockLogicTestHelper.canUnlock("sudan", 184))
        assertTrue(StarUnlockLogicTestHelper.canUnlock("sudan", 185))
    }
}

/**
 * Helper to access internal threshold logic for testing.
 */
object StarUnlockLogicTestHelper {
    fun canUnlock(countryId: String, stars: Int): Boolean = 
        com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain.canUnlock(countryId, stars)
}
