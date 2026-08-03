package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FlightPathTest {

    @Test
    fun `sample clamps t into 0 to 1`() {
        val a = GlobeGeoPoint(51.1f, 10.4f)
        val b = GlobeGeoPoint(36.2f, 138.3f)
        val s1 = FlightPath.sample(a, b, -1f)
        val s2 = FlightPath.sample(a, b, 2f)
        assertEquals(a.latDeg, s1.latDeg, 0.02f)
        assertEquals(a.lonDeg, s1.lonDeg, 0.02f)
        assertEquals(b.latDeg, s2.latDeg, 0.02f)
    }

    @Test
    fun `sample has positive arc altitude near midpoint`() {
        val a = GlobeGeoPoint(51.1f, 10.4f)
        val b = GlobeGeoPoint(36.2f, 138.3f)
        val mid = FlightPath.sample(a, b, 0.5f)
        assertTrue(mid.altitudeNorm > 0.12f)
    }

    @Test
    fun `angular distance is bounded`() {
        val a = GlobeGeoPoint(51.1f, 10.4f)
        val b = GlobeGeoPoint(23.6f, -102.6f)
        val d = FlightPath.angularDistanceDeg(a, b)
        assertTrue(d in 0f..180f)
    }

    @Test
    fun `flight animator triggers arrival exactly once`() {
        val from = GlobeCountry("germany", "Germany", "🇩🇪", "DE", 51.1f, 10.4f)
        val to = GlobeCountry("japan", "Japan", "🇯🇵", "JP", 36.2f, 138.3f)
        val animator = FlightAnimator()
        animator.startFlight(from, to)

        var arrival: String? = null
        repeat(120) {
            animator.tick(16f)
            val hit = animator.consumeArrivalDestinationId()
            if (hit != null) arrival = hit
        }
        assertEquals("japan", arrival)
        assertFalse(animator.isActive)
        assertNotNull(arrival)
        assertEquals(null, animator.consumeArrivalDestinationId())
    }
}
