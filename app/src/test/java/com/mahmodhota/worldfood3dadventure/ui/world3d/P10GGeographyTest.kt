package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class P10GGeographyTest {

    private val EPSILON = 0.001f

    @Test
    fun testGlobeCoordinateConvention() {
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
    }

    @Test
    fun testAllCountryCoordinatesOnSurface() {
        GLOBE_COUNTRIES.forEach { country ->
            val xyz = country.xyz
            val dist = sqrt(xyz[0]*xyz[0] + xyz[1]*xyz[1] + xyz[2]*xyz[2])
            assertEquals("${country.displayName} marker not on surface", 1.0f, dist, EPSILON)
        }
    }

    @Test
    fun testRelativeGeographicRelations() {
        val germany = GLOBE_COUNTRIES.find { it.id == "germany" }!!
        val france = GLOBE_COUNTRIES.find { it.id == "france" }!!
        val italy = GLOBE_COUNTRIES.find { it.id == "italy" }!!
        val uk = GLOBE_COUNTRIES.find { it.id == "uk" }!!

        assertTrue("France should be West of Germany", france.lonDeg < germany.lonDeg)
        assertTrue("UK should be West of Germany", uk.lonDeg < germany.lonDeg)
        assertTrue("Italy should be South of Germany", italy.latDeg < germany.latDeg)
    }
}
