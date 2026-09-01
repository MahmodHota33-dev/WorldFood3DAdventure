package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertTrue
import org.junit.Test

class GlobeDepthSortingTest {

    @Test
    fun zDepthCorrectlyDistinguishesFrontAndBack() {
        // Germany is roughly 51N, 10E
        // If camera is at 0, 0
        val frontDepth = GlobeMath.getZDepth(51f, 10f, rotY = 0f, rotX = 0f)
        assertTrue("Germany should be on front side at 0,0 rotation", frontDepth > 0f)

        // Rotate globe 180 degrees
        val backDepth = GlobeMath.getZDepth(51f, 10f, rotY = 180f, rotX = 0f)
        assertTrue("Germany should be on back side at 180,0 rotation", backDepth < 0f)
    }

    @Test
    fun markersCloseInLongitudeCanBeSortedByDepth() {
        // Germany (10E) and Sudan (32E)
        // At rotY = -20 (camera looking at 20E)
        val germanyDepth = GlobeMath.getZDepth(51.1f, 10.4f, rotY = -20f, rotX = 0f)
        val sudanDepth = GlobeMath.getZDepth(15.6f, 32.5f, rotY = -20f, rotX = 0f)
        
        // Both should be on front side
        assertTrue(germanyDepth > 0f)
        assertTrue(sudanDepth > 0f)
        
        // One should be deeper than the other (sorting is possible)
        assertTrue("Markers should have different depths for sorting", germanyDepth != sudanDepth)
    }
}
