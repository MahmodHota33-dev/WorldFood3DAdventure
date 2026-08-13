package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GlobeFlightGuardsTest {

    @Test
    fun markerTapBlockedDuringAutomaticUnlockFlight() {
        assertFalse(canStartManualMarkerFlight(isFlightActive = true, activeFlightDestinationId = "italy"))
        assertFalse(canStartManualMarkerFlight(isFlightActive = false, activeFlightDestinationId = "italy"))
        assertTrue(canStartManualMarkerFlight(isFlightActive = false, activeFlightDestinationId = null))
    }
}
