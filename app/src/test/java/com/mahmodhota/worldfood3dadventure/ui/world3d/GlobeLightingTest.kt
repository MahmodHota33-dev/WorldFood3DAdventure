package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class GlobeLightingTest {

    @Test
    fun `sun direction is normalized and finite across cycle`() {
        for (i in 0..360 step 7) {
            val phase = i / 57.29578f
            val s = GlobeLighting.forCycle(phase)
            val len = sqrt(s.sunX * s.sunX + s.sunY * s.sunY + s.sunZ * s.sunZ)
            assertTrue(s.sunX.isFinite() && s.sunY.isFinite() && s.sunZ.isFinite())
            assertEquals(1f, len, 0.001f)
        }
    }

    @Test
    fun `sun cycle changes smoothly`() {
        val a = GlobeLighting.forCycle(1.0f)
        val b = GlobeLighting.forCycle(1.01f)
        val delta = kotlin.math.abs(a.sunX - b.sunX) +
            kotlin.math.abs(a.sunY - b.sunY) +
            kotlin.math.abs(a.sunZ - b.sunZ)
        assertTrue(delta < 0.03f)
    }

    @Test
    fun `invalid phase returns finite default lighting`() {
        val a = GlobeLighting.forCycle(Float.NaN)
        val b = GlobeLighting.forCycle(Float.POSITIVE_INFINITY)
        assertTrue(a.sunX.isFinite() && a.sunY.isFinite() && a.sunZ.isFinite())
        assertTrue(b.sunX.isFinite() && b.sunY.isFinite() && b.sunZ.isFinite())
    }

    @Test
    fun `day and night intensity stay within bounds`() {
        val lighting = GlobeLighting.forCycle(0.7f)
        val day = GlobeLighting.dayIntensity(0.2f, 0.3f, 0.9f, lighting)
        val night = GlobeLighting.nightIntensity(0.2f, 0.3f, 0.9f, lighting)
        assertTrue(day in 0f..1f)
        assertTrue(night in 0f..1f)
        assertTrue(day + night <= 1.001f)
    }

    @Test
    fun `ocean highlight intensity stays within bounds`() {
        val lighting = GlobeLighting.forCycle(2.4f)
        val hi = GlobeLighting.oceanHighlightIntensity(-0.2f, 0.2f, 0.95f, lighting)
        val lo = GlobeLighting.oceanHighlightIntensity(0.3f, -0.7f, -0.4f, lighting)
        assertTrue(hi in 0f..1f)
        assertTrue(lo in 0f..1f)
    }

    @Test
    fun `shared lighting keeps city and day-night consistency`() {
        val lighting = GlobeLighting.forCycle(1.8f)
        val n1 = nightIntensity(0.35f, -0.22f, 0.88f, lighting)
        val n2 = GlobeLighting.nightIntensity(0.35f, -0.22f, 0.88f, lighting)
        assertEquals(n2, n1, 0.0001f)
    }
}
