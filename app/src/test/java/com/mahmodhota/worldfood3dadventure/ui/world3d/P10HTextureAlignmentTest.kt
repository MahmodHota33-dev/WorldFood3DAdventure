package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertEquals
import org.junit.Test

class P10HTextureAlignmentTest {

    @Test
    fun testLatLonToUV_Logic() {
        // Based on FilamentSphereGenerator mapping:
        // v = 1.0f - (stack / stacks)
        // stack 0 = North (+90), stack stacks = South (-90)
        
        fun getV(lat: Float): Float {
            // phi = (90 - lat) in degrees
            val phiPercent = (90f - lat) / 180f
            return 1.0f - phiPercent
        }

        assertEquals(1.0f, getV(90f), 0.001f)   // North Pole
        assertEquals(0.5f, getV(0f), 0.001f)    // Equator
        assertEquals(0.0f, getV(-90f), 0.001f)  // South Pole

        fun getU(lon: Float): Float {
            // slice 0 = -180, slice mid = 0, slice max = 180
            return (lon + 180f) / 360f
        }

        assertEquals(0.0f, getU(-180f), 0.001f)
        assertEquals(0.5f, getU(0f), 0.001f)    // Greenwich
        assertEquals(0.75f, getU(90f), 0.001f)  // East
        assertEquals(1.0f, getU(180f), 0.001f)
    }
}
