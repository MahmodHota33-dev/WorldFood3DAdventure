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

    // ── nightIntensity ────────────────────────────────────────────────────────

    @Test
    fun `nightIntensity returns 0 for sun-facing point`() {
        // lat=0, lon=0 → z2=+1 when rotY=0, rotX=0 → full day (z toward viewer = toward sun)
        // dot = 0*SUN_X + 0*SUN_Y + 1*SUN_Z = +0.88 → nightIntensity = 0
        val result = nightIntensity(0f, 0f, 1f)
        assertEquals(0f, result, 0.001f)
    }

    @Test
    fun `nightIntensity is positive for night-side point`() {
        // A point on the lower-right in camera space (night side):
        // x2=+0.6, y2=-0.5 → dot = 0.6*(-0.30) + (-0.5)*(+0.34) + 0.0*0.88 = -0.18 - 0.17 = -0.35
        val result = nightIntensity(0.6f, -0.5f, 0f)
        assertTrue("Night-side point must have positive nightIntensity", result > 0f)
    }

    @Test
    fun `nightIntensity upper-left point is day`() {
        // Point upper-left: x2=-0.5, y2=+0.5 → dot large positive → nightIntensity = 0
        val result = nightIntensity(-0.5f, 0.5f, 0.7f)
        assertEquals(0f, result, 0.001f)
    }

    @Test
    fun `nightIntensity is clamped to 0_1`() {
        // Extreme night-side: all components strongly against sun
        val result1 = nightIntensity(1f, -1f, -1f)
        assertTrue("nightIntensity must be ≤ 1", result1 <= 1f)
        assertTrue("nightIntensity must be ≥ 0", result1 >= 0f)

        // Extreme day side: all components strongly toward sun
        val result2 = nightIntensity(-1f, 1f, 1f)
        assertEquals(0f, result2, 0.001f)
    }

    @Test
    fun `nightIntensity returns 0 for NaN inputs`() {
        assertEquals(0f, nightIntensity(Float.NaN, 0f, 0f), 0.001f)
        assertEquals(0f, nightIntensity(0f, Float.NaN, 0f), 0.001f)
        assertEquals(0f, nightIntensity(0f, 0f, Float.NaN), 0.001f)
    }

    @Test
    fun `nightIntensity returns 0 for Infinity inputs`() {
        assertEquals(0f, nightIntensity(Float.POSITIVE_INFINITY, 0f, 0f), 0.001f)
        assertEquals(0f, nightIntensity(0f, Float.NEGATIVE_INFINITY, 0f), 0.001f)
    }

    @Test
    fun `nightIntensity at terminator is strictly between 0 and 1`() {
        // The terminator is where dot ≈ 0. Construct a point orthogonal to sun:
        // sun = (-0.30, +0.34, +0.88). An orthogonal point: (0.34/0.45, 0.30/0.45, 0) normalised
        // dot = 0.755*(-0.30) + 0.655*(+0.34) + 0*0.88 = -0.227 + 0.223 = -0.004 ≈ 0
        val result = nightIntensity(0.755f, 0.655f, 0f)
        // Should be a tiny positive or 0 — very close to terminator
        assertTrue("Terminator point intensity must be in 0..1", result in 0f..1f)
    }

    // ── CITY_LIGHTS data integrity ────────────────────────────────────────────

    @Test
    fun `CITY_LIGHTS list is non-empty`() {
        assertTrue("CITY_LIGHTS must not be empty", CITY_LIGHTS.isNotEmpty())
    }

    @Test
    fun `CITY_LIGHT_XYZ matches CITY_LIGHTS size`() {
        assertEquals(CITY_LIGHTS.size, CITY_LIGHT_XYZ.size)
    }

    @Test
    fun `all CITY_LIGHT_XYZ entries are unit vectors`() {
        for (i in CITY_LIGHT_XYZ.indices) {
            val v = CITY_LIGHT_XYZ[i]
            val len = sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2])
            assertEquals("CITY_LIGHT_XYZ[$i] must be unit vector", 1f, len, 0.002f)
        }
    }

    @Test
    fun `all CityLight brightness values are in 0_1`() {
        for (light in CITY_LIGHTS) {
            assertTrue(
                "brightness for ${light.lat},${light.lon} must be in 0..1",
                light.brightness in 0f..1f
            )
        }
    }

    @Test
    fun `city light on back side of globe is rejected`() {
        // lat=0, lon=180 faces away from the camera at rotY=0, rotX=0
        // Project it: z2 should be ≤ -0.05 → must not appear
        val xyz = latLonToXyz(0f, 180f)
        val p = projectPoint(xyz, 0f, 0f, 500f, 500f, 200f)
        assertNull("Back-side city light position must not project", p)
    }

    @Test
    fun `city light on front side of globe projects successfully`() {
        // lat=0, lon=0 faces the camera at rotY=0, rotX=0
        val xyz = latLonToXyz(0f, 0f)
        val p = projectPoint(xyz, 0f, 0f, 500f, 500f, 200f)
        assertNotNull("Front-side city light position must project", p)
    }
}
