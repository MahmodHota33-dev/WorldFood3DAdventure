package com.mahmodhota.worldfood3dadventure.ui.world3d

import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class P10LSmartFocusIntegrityTest {

    @Test
    fun testCountryRegistryIntegrity() {
        val allIds = LevelRegistry.allCountryIds
        assertEquals("Should have 213 countries", 213, allIds.size)
        
        allIds.forEach { id ->
            val country = LevelRegistry.getCountry(id)
            assertNotNull("Country $id should exist", country)
            assertEquals("Country $id should have 15 levels", 15, country!!.levels.size)
        }
    }

    @Test
    fun testGlobeCoordinatesStability() {
        // Verify coordinates haven't changed from known baseline (P10-GEO/G/H)
        val germany = GLOBE_COUNTRIES.find { it.id == "germany" }
        assertNotNull(germany)
        assertEquals(51.165f, germany?.latDeg ?: 0f, 0.001f)
        assertEquals(10.451f, germany?.lonDeg ?: 0f, 0.001f)
        
        val japan = GLOBE_COUNTRIES.find { it.id == "japan" }
        assertNotNull(japan)
        assertEquals(36.204f, japan?.latDeg ?: 0f, 0.001f)
        assertEquals(138.252f, japan?.lonDeg ?: 0f, 0.001f)
    }

    @Test
    fun testZoomLimits() {
        // MIN_ZOOM and MAX_ZOOM are defined in GlobeCameraState.companion
        assertEquals(0.6f, GlobeCameraState.MIN_ZOOM, 0.001f)
        assertEquals(2.5f, GlobeCameraState.MAX_ZOOM, 0.001f)
    }

    @Test
    fun testMarkerStatesConsistency() {
        val stateCurrent = resolveMarkerState("any", isUnlocked = true, isCompleted = false, isNextDestination = true)
        assertEquals(MarkerState.CURRENT, stateCurrent)

        val stateCompleted = resolveMarkerState("any", isUnlocked = true, isCompleted = true, isNextDestination = false)
        assertEquals(MarkerState.COMPLETED, stateCompleted)
    }
}
