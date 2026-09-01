package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class GeographicalSystemTest {

    private val EPSILON = 0.0001f

    @Test
    fun testLatLonToXyz_Poles() {
        // North Pole
        val np = GlobeMath.latLonToXyz(90f, 0f)
        assertEquals(0f, np[0], EPSILON)
        assertEquals(1f, np[1], EPSILON)
        assertEquals(0f, np[2], EPSILON)

        // South Pole
        val sp = GlobeMath.latLonToXyz(-90f, 0f)
        assertEquals(0f, sp[0], EPSILON)
        assertEquals(-1f, sp[1], EPSILON)
        assertEquals(0f, sp[2], EPSILON)
    }

    @Test
    fun testLatLonToXyz_Equator() {
        // Greenwich
        val g = GlobeMath.latLonToXyz(0f, 0f)
        assertEquals(0f, g[0], EPSILON)
        assertEquals(0f, g[1], EPSILON)
        assertEquals(1f, g[2], EPSILON)

        // 90 East
        val e90 = GlobeMath.latLonToXyz(0f, 90f)
        assertEquals(1f, e90[0], EPSILON)
        assertEquals(0f, e90[1], EPSILON)
        assertEquals(0f, e90[2], EPSILON)

        // 90 West
        val w90 = GlobeMath.latLonToXyz(0f, -90f)
        assertEquals(-1f, w90[0], EPSILON)
        assertEquals(0f, w90[1], EPSILON)
        assertEquals(0f, w90[2], EPSILON)
    }

    @Test
    fun testAll213CountriesHaveValidCoordinates() {
        val countries = GLOBE_COUNTRIES
        assertEquals(213, countries.size)

        countries.forEach { country ->
            assertTrue("${country.displayName} latitude out of bounds: ${country.latDeg}", country.latDeg in -90f..90f)
            assertTrue("${country.displayName} longitude out of bounds: ${country.lonDeg}", country.lonDeg in -180f..180f)
            
            // Check XYZ is on unit sphere
            val xyz = country.xyz
            val r = kotlin.math.sqrt(xyz[0] * xyz[0] + xyz[1] * xyz[1] + xyz[2] * xyz[2])
            assertEquals("${country.displayName} marker is not on globe surface", 1.0f, r, EPSILON)
        }
    }

    @Test
    fun testRelativePositions() {
        fun getCountry(id: String) = GLOBE_COUNTRIES.find { it.id == id }!!

        val germany = getCountry("germany")
        val france = getCountry("france")
        val uk = getCountry("uk")
        val italy = getCountry("italy")
        val japan = getCountry("japan")
        val usa = getCountry("usa")

        // France is West of Germany
        assertTrue("France should be West of Germany", france.lonDeg < germany.lonDeg)
        
        // UK is West of Germany
        assertTrue("UK should be West of Germany", uk.lonDeg < germany.lonDeg)

        // Italy is South of Germany
        assertTrue("Italy should be South of Germany", italy.latDeg < germany.latDeg)

        // Japan is far East
        assertTrue("Japan should be far East", japan.lonDeg > 130f)

        // USA is West
        assertTrue("USA should be in Western Hemisphere", usa.lonDeg < 0f)
    }
}
