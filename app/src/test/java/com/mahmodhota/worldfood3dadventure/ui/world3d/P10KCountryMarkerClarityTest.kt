package com.mahmodhota.worldfood3dadventure.ui.world3d

import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class P10KCountryMarkerClarityTest {

    @Test
    fun testMarkerCount() {
        assertEquals("Should have exactly 213 markers", 213, GLOBE_COUNTRIES.size)
    }

    @Test
    fun testCoordinateIntegrity() {
        val germany = GLOBE_COUNTRIES.find { it.id == "germany" }
        assertNotNull(germany)
        // Verify coordinates haven't changed from known baseline (P10-GEO/G)
        assertEquals(51.165f, germany?.latDeg ?: 0f, 0.001f)
        assertEquals(10.451f, germany?.lonDeg ?: 0f, 0.001f)
    }

    @Test
    fun testMarkerStateHierarchyLogic() {
        // Test internal resolveMarkerState function
        val stateCurrent = resolveMarkerState("any", isUnlocked = true, isCompleted = false, isNextDestination = true)
        assertEquals(MarkerState.CURRENT, stateCurrent)

        val stateCompleted = resolveMarkerState("any", isUnlocked = true, isCompleted = true, isNextDestination = false)
        assertEquals(MarkerState.COMPLETED, stateCompleted)
        
        val stateUnlocked = resolveMarkerState("any", isUnlocked = true, isCompleted = false, isNextDestination = false)
        assertEquals(MarkerState.UNLOCKED, stateUnlocked)

        val stateLocked = resolveMarkerState("any", isUnlocked = false, isCompleted = false, isNextDestination = false)
        assertEquals(MarkerState.LOCKED, stateLocked)
    }
}
