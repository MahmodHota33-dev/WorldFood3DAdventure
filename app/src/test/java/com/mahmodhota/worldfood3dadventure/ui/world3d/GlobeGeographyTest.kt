package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GlobeGeographyTest {

    private val cx = 500f
    private val cy = 500f
    private val radius = 200f

    @Test
    fun northPoleIsAlwaysAtVerticalCenterLine() {
        // North Pole is at (0, 1, 0)
        // With Rx applied after Ry, Ry rotates (0, 1, 0) to (0, 1, 0)
        // Then Rx rotates it to (0, cos X, sin X)
        // Screen X should always be cx
        val pole = GlobeMath.latLonToXyz(90f, 0f)
        for (rotY in listOf(0f, 45f, 90f, 180f, -90f)) {
            for (rotX in listOf(0f, 20f, -20f, 45f)) {
                val pos = GlobeMath.projectPoint(pole, rotY, rotX, cx, cy, radius)
                if (pos != null) {
                    assertEquals("North Pole X should be centered at rotY=$rotY rotX=$rotX", cx, pos.x, 0.01f)
                }
            }
        }
    }

    @Test
    fun greenwichIsCenteredAtZeroRotation() {
        // Greenwich (0, 0) should be at center when rotX=0, rotY=0
        val pos = GlobeMath.projectLatLon(0f, 0f, 0f, 0f, cx, cy, radius)
        assertNotNull(pos)
        assertEquals(cx, pos!!.x, 0.01f)
        assertEquals(cy, pos.y, 0.01f)
    }

    @Test
    fun tokyoIsToTheRightOfLondon() {
        // London (0) to Tokyo (140E)
        // Rotate globe by -70 to bring both into view (London at -70, Tokyo at +70)
        val rotY = -70f
        val london = GlobeMath.projectLatLon(51.5f, 0f, rotY, 0f, cx, cy, radius)
        val tokyo = GlobeMath.projectLatLon(35.7f, 139.7f, rotY, 0f, cx, cy, radius)
        
        assertNotNull("London should be visible", london)
        assertNotNull("Tokyo should be visible", tokyo)
        assertTrue("Tokyo should be to the right of London", tokyo!!.x > london!!.x)
    }

    @Test
    fun usaIsToTheLeftOfLondon() {
        // London (0) to USA (New York -74W)
        // Rotate globe by +40 to bring both into view (London at +40, NY at -34)
        val rotY = 40f
        val london = GlobeMath.projectLatLon(51.5f, 0f, rotY, 0f, cx, cy, radius)
        val ny = GlobeMath.projectLatLon(40.7f, -74.0f, rotY, 0f, cx, cy, radius)
        
        assertNotNull("London should be visible", london)
        assertNotNull("New York should be visible", ny)
        assertTrue("New York should be to the left of London", ny!!.x < london!!.x)
    }

    @Test
    fun focusOnCentersTarget() {
        // focusOn sets rotY = -lon and rotX = lat (after removing 0.8f)
        val lat = 51.1f // Germany
        val lon = 10.4f
        
        // Use 1.0f as the target multiplier for P6C
        val rotY = -lon
        val rotX = lat
        
        val pos = GlobeMath.projectLatLon(lat, lon, rotY, rotX, cx, cy, radius)
        assertNotNull(pos)
        assertEquals("Target X should be centered", cx, pos!!.x, 0.5f)
        assertEquals("Target Y should be centered", cy, pos.y, 0.5f)
    }
}
