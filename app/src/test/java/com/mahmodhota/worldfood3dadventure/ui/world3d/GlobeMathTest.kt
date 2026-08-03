package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

/**
 * Pure-math unit tests for the experimental 3D globe.
 *
 * All tested code is JVM-compatible (no Android framework or Compose UI).
 * [latLonToXyz] and [projectPoint] use only FloatArrays and basic math.
 * [GlobeCameraState] uses Compose runtime state which is JVM-compatible.
 */
class GlobeMathTest {

    // ── latLonToXyz ──────────────────────────────────────────────────────────

    @Test
    fun `latLonToXyz at origin faces viewer`() {
        // Lat=0, Lon=0 → x=0, y=0, z=1 (pointing towards the camera)
        val v = latLonToXyz(0f, 0f)
        assertEquals(0f, v[0], 0.001f)
        assertEquals(0f, v[1], 0.001f)
        assertEquals(1f, v[2], 0.001f)
    }

    @Test
    fun `latLonToXyz north pole returns Y-up vector`() {
        val v = latLonToXyz(90f, 0f)
        assertEquals(0f, v[0], 0.001f)
        assertEquals(1f, v[1], 0.001f)
        assertEquals(0f, v[2], 0.001f)
    }

    @Test
    fun `latLonToXyz south pole returns Y-down vector`() {
        val v = latLonToXyz(-90f, 0f)
        assertEquals(0f,  v[0], 0.001f)
        assertEquals(-1f, v[1], 0.001f)
        assertEquals(0f,  v[2], 0.001f)
    }

    @Test
    fun `latLonToXyz always returns a unit vector`() {
        val coords = listOf(
            0f to 0f, 45f to 90f, -45f to -90f,
            51.1f to 10.4f,   // Germany
            36.2f to 138.3f,  // Japan
            23.6f to -102.6f  // Mexico
        )
        for ((lat, lon) in coords) {
            val v = latLonToXyz(lat, lon)
            val len = sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2])
            assertEquals("unit vector for lat=$lat, lon=$lon", 1f, len, 0.001f)
        }
    }

    // ── projectPoint ─────────────────────────────────────────────────────────

    @Test
    fun `projectPoint returns non-null for point facing camera`() {
        // lat=0, lon=0 is directly in front at rotY=0, rotX=0
        val p = latLonToXyz(0f, 0f)
        val result = projectPoint(p, 0f, 0f, 500f, 500f, 200f)
        assertNotNull("Front-facing point should project successfully", result)
    }

    @Test
    fun `projectPoint returns null for point on far side`() {
        // lat=0, lon=180 is directly behind the globe when camera is at rotY=0
        val p = latLonToXyz(0f, 180f)
        val result = projectPoint(p, 0f, 0f, 500f, 500f, 200f)
        assertNull("Point on far side should return null", result)
    }

    @Test
    fun `projectPoint maps origin to canvas centre`() {
        val p = latLonToXyz(0f, 0f)
        val cx = 400f; val cy = 600f
        val result = projectPoint(p, 0f, 0f, cx, cy, 200f)
        assertNotNull(result)
        assertEquals(cx, result!!.x, 0.5f)
        assertEquals(cy, result.y, 0.5f)
    }

    @Test
    fun `Germany is visible at default camera orientation`() {
        // Default camera: rotY = -15, rotX = 25
        val pos = projectLatLon(51.1f, 10.4f, -15f, 25f, 500f, 500f, 200f)
        assertNotNull("Germany should be visible at the default camera angle", pos)
    }

    @Test
    fun `projectLatLon matches projectPoint pipeline`() {
        val lat = 46.2f; val lon = 2.2f  // France
        val xyz = latLonToXyz(lat, lon)
        val fromXyz  = projectPoint(xyz, -15f, 25f, 300f, 400f, 150f)
        val fromLatLon = projectLatLon(lat, lon, -15f, 25f, 300f, 400f, 150f)
        if (fromXyz == null) {
            assertNull(fromLatLon)
        } else {
            assertNotNull(fromLatLon)
            assertEquals(fromXyz.x, fromLatLon!!.x, 0.001f)
            assertEquals(fromXyz.y, fromLatLon.y, 0.001f)
        }
    }

    // ── GlobeCameraState — normalizeAngleDiff ─────────────────────────────────

    @Test
    fun `normalizeAngleDiff small positive unchanged`() {
        assertEquals(45f, GlobeCameraState.normalizeAngleDiff(45f), 0.001f)
    }

    @Test
    fun `normalizeAngleDiff wraps 200 to -160`() {
        assertEquals(-160f, GlobeCameraState.normalizeAngleDiff(200f), 0.001f)
    }

    @Test
    fun `normalizeAngleDiff wraps -200 to 160`() {
        assertEquals(160f, GlobeCameraState.normalizeAngleDiff(-200f), 0.001f)
    }

    @Test
    fun `normalizeAngleDiff handles 360 as 0`() {
        assertEquals(0f, GlobeCameraState.normalizeAngleDiff(360f), 0.001f)
    }

    @Test
    fun `normalizeAngleDiff handles -360 as 0`() {
        assertEquals(0f, GlobeCameraState.normalizeAngleDiff(-360f), 0.001f)
    }

    // ── GlobeCameraState — easeOutCubic ───────────────────────────────────────

    @Test
    fun `easeOutCubic returns 0 at t=0`() {
        assertEquals(0f, GlobeCameraState.easeOutCubic(0f), 0.001f)
    }

    @Test
    fun `easeOutCubic returns 1 at t=1`() {
        assertEquals(1f, GlobeCameraState.easeOutCubic(1f), 0.001f)
    }

    @Test
    fun `easeOutCubic is monotonically increasing`() {
        var prev = -0.001f
        for (i in 0..10) {
            val v = GlobeCameraState.easeOutCubic(i / 10f)
            assertTrue("easeOutCubic must increase at i=$i", v > prev)
            prev = v
        }
    }

    @Test
    fun `easeOutCubic clamps inputs outside 0-1`() {
        assertEquals(0f, GlobeCameraState.easeOutCubic(-1f), 0.001f)
        assertEquals(1f, GlobeCameraState.easeOutCubic(2f),  0.001f)
    }

    // ── GlobeCameraState — zoom clamping ──────────────────────────────────────

    @Test
    fun `zoom stays above minimum when factor is very small`() {
        val cam = GlobeCameraState(initialZoom = 1f)
        cam.applyZoom(0.001f)
        assertTrue("zoom must be ≥ 0.6", cam.zoom >= 0.6f)
    }

    @Test
    fun `zoom stays below maximum when factor is very large`() {
        val cam = GlobeCameraState(initialZoom = 1f)
        cam.applyZoom(1000f)
        assertTrue("zoom must be ≤ 2.2", cam.zoom <= 2.2f)
    }

    // ── GlobeCameraState — fly-to ─────────────────────────────────────────────

    @Test
    fun `startFlyTo cancels inertia`() {
        val cam = GlobeCameraState()
        cam.velY = 8f
        cam.velX = 4f
        cam.startFlyTo(51f, 10f)   // Germany
        assertEquals(0f, cam.velY, 0.001f)
        assertEquals(0f, cam.velX, 0.001f)
    }

    @Test
    fun `startFlyTo marks isFlyingTo as true`() {
        val cam = GlobeCameraState()
        cam.startFlyTo(51f, 10f)
        assertTrue(cam.isFlyingTo)
    }

    @Test
    fun `tickFlyTo finishes within 60 frames`() {
        val cam = GlobeCameraState(initialRotY = 0f, initialRotX = 0f)
        cam.startFlyTo(51f, 10f)
        repeat(60) { cam.tickFlyTo() }
        assertFalse("fly-to should complete in ≤60 frames", cam.isFlyingTo)
    }

    @Test
    fun `cancelFlyTo stops animation immediately`() {
        val cam = GlobeCameraState()
        cam.startFlyTo(51f, 10f)
        assertTrue(cam.isFlyingTo)
        cam.cancelFlyTo()
        assertFalse(cam.isFlyingTo)
    }

    @Test
    fun `fly-to takes shortest longitude path`() {
        // Camera at rotY=170, target = Germany (lon=10 → targetRotY=-10)
        // Naive diff = -10 - 170 = -180 → after normalise should be 180 not -540
        val diff = GlobeCameraState.normalizeAngleDiff(-10f - 170f)
        assertTrue("Shortest path must be within ±180", diff >= -180f && diff <= 180f)
    }
}
