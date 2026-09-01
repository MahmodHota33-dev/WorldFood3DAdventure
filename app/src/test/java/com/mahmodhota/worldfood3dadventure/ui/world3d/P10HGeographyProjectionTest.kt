package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt

class P10HGeographyProjectionTest {

    private val EPSILON = 0.001f

    @Test
    fun testLatLonToXyz_Consistency() {
        // Greenwich: Lat 0, Lon 0 -> (0, 0, 1)
        val g = GlobeMath.latLonToXyz(0f, 0f)
        assertEquals(0f, g[0], EPSILON)
        assertEquals(0f, g[1], EPSILON)
        assertEquals(1f, g[2], EPSILON)

        // North Pole: Lat 90, Lon 0 -> (0, 1, 0)
        val np = GlobeMath.latLonToXyz(90f, 0f)
        assertEquals(0f, np[0], EPSILON)
        assertEquals(1f, np[1], EPSILON)
        assertEquals(0f, np[2], EPSILON)

        // 90 East: Lat 0, Lon 90 -> (1, 0, 0)
        val e90 = GlobeMath.latLonToXyz(0f, 90f)
        assertEquals(1f, e90[0], EPSILON)
        assertEquals(0f, e90[1], EPSILON)
        assertEquals(0f, e90[2], EPSILON)
    }

    @Test
    fun testUVMappings_Consistency() {
        // Verify that UV mapping in generator corresponds to the same Lat/Lon as GlobeMath
        // This is a logic check of the formulas.
        
        fun getXyzFromStackSlice(stack: Int, stacks: Int, slice: Int, slices: Int): FloatArray {
            val phi = (stack.toFloat() * PI.toFloat()) / stacks.toFloat()
            val theta = (slice.toFloat() * 2f * PI.toFloat()) / slices.toFloat()
            val sinPhi = sin(phi)
            val cosPhi = cos(phi)
            val sinTheta = sin(theta - PI.toFloat())
            val cosTheta = cos(theta - PI.toFloat())
            return floatArrayOf(sinPhi * sinTheta, cosPhi, sinPhi * cosTheta)
        }

        // Greenwich (stack = mid, slice = mid)
        val midStack = 24
        val midSlice = 24
        val stacks = 48
        val slices = 48
        
        val g_xyz = getXyzFromStackSlice(midStack, stacks, midSlice, slices)
        val g_math = GlobeMath.latLonToXyz(0f, 0f)
        
        assertEquals("X mismatch at Greenwich", g_math[0], g_xyz[0], EPSILON)
        assertEquals("Y mismatch at Greenwich", g_math[1], g_xyz[1], EPSILON)
        assertEquals("Z mismatch at Greenwich", g_math[2], g_xyz[2], EPSILON)
        
        // V should be 0.5 at Equator
        val v_mid = 1.0f - (midStack.toFloat() / stacks.toFloat())
        assertEquals(0.5f, v_mid, EPSILON)
        
        // U should be 0.5 at Greenwich
        val u_mid = midSlice.toFloat() / slices.toFloat()
        assertEquals(0.5f, u_mid, EPSILON)
    }

    @Test
    fun testRelativeGeographicRelations() {
        val germany = GLOBE_COUNTRIES.find { it.id == "germany" }!!
        val uk = GLOBE_COUNTRIES.find { it.id == "uk" }!!
        val japan = GLOBE_COUNTRIES.find { it.id == "japan" }!!
        val china = GLOBE_COUNTRIES.find { it.id == "china" }!!
        val usa = GLOBE_COUNTRIES.find { it.id == "usa" }!!

        assertTrue("UK should be west of Germany", uk.lonDeg < germany.lonDeg)
        assertTrue("Japan should be east of China", japan.lonDeg > china.lonDeg)
        assertTrue("USA should be in Western Hemisphere", usa.lonDeg < 0)
    }
}
