package com.mahmodhota.worldfood3dadventure.ui.world3d

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pure math helpers for 3D Globe operations.
 */
object GlobeMath {

    fun latLonToXyz(latDeg: Float, lonDeg: Float): FloatArray {
        // P9-A Unified Coordinate System:
        // Y+ = North, Y- = South
        // XZ plane = Equator
        // Z+ = Greenwich (0 long), X+ = 90 East
        val phi = (90f - latDeg) * (PI.toFloat() / 180f)
        val theta = lonDeg * (PI.toFloat() / 180f)
        return floatArrayOf(
            sin(phi) * sin(theta), // X = R * sin(phi) * sin(theta)
            cos(phi),              // Y = R * cos(phi)
            sin(phi) * cos(theta)  // Z = R * sin(phi) * cos(theta)
        )
    }

    fun projectPoint(
        xyz: FloatArray,
        rotY: Float,
        rotX: Float,
        cx: Float,
        cy: Float,
        r: Float
    ): androidx.compose.ui.geometry.Offset? {
        // Unified Rotation Order: Spin (Ry) first, then Tilt (Rx)
        // 1. Longitude Spin (Ry)
        val ry = rotY * (PI.toFloat() / 180f)
        val cosY = cos(ry); val sinY = sin(ry)
        val x1 = xyz[0] * cosY + xyz[2] * sinY
        val y1 = xyz[1]
        val z1 = -xyz[0] * sinY + xyz[2] * cosY

        // 2. Latitude Tilt (Rx)
        val rx = rotX * (PI.toFloat() / 180f)
        val cosX = cos(rx); val sinX = sin(rx)
        val x2 = x1
        val y2 = y1 * cosX - z1 * sinX
        val z2 = y1 * sinX + z1 * cosX

        // Near-plane culling (Filament near is 0.1, globe radius is 1.0)
        // Z+ is towards the camera after all transforms in Filament's view space convention?
        // Actually, our projectPoint logic returns null if z2 <= -0.05f (backside).
        if (z2 <= -0.05f) return null
        return androidx.compose.ui.geometry.Offset(cx + x2 * r, cy - y2 * r)
    }

    fun projectLatLon(lat: Float, lon: Float, rY: Float, rX: Float, cx: Float, cy: Float, r: Float) = 
        projectPoint(latLonToXyz(lat, lon), rY, rX, cx, cy, r)

    /** Optimized Z-Depth calculation using pre-calculated XYZ to avoid allocations. */
    fun getZDepth(xyz: FloatArray, rotY: Float, rotX: Float): Float {
        val ry = rotY * (PI.toFloat() / 180f)
        val cosY = cos(ry)
        val sinY = sin(ry)
        val z1 = -xyz[0] * sinY + xyz[2] * cosY
        
        val rx = rotX * (PI.toFloat() / 180f)
        val cosX = cos(rx)
        val sinX = sin(rx)
        return xyz[1] * sinX + z1 * cosX
    }

    fun getZDepth(lat: Float, lon: Float, rotY: Float, rotX: Float): Float {
        return getZDepth(latLonToXyz(lat, lon), rotY, rotX)
    }

    fun nightIntensity(x2: Float, y2: Float, z2: Float): Float {
        if (!x2.isFinite() || !y2.isFinite() || !z2.isFinite()) return 0f
        val sunX = -0.30f; val sunY = 0.34f; val sunZ = 0.88f
        val dot = x2 * sunX + y2 * sunY + z2 * sunZ
        return (1.0f - (dot + 0.15f) / 0.30f).coerceIn(0f, 1f)
    }
}

// ── City Light Data ──────────────────────────────────────────────────────────

data class CityLight(val lat: Float, val lon: Float, val brightness: Float)

val CITY_LIGHTS: List<CityLight> = listOf(
    CityLight( 51.5f,  -0.1f, 0.88f), // London
    CityLight( 35.7f, 139.7f, 0.95f), // Tokyo
    CityLight( 40.7f, -74.0f, 0.94f), // New York
    CityLight( 23.6f, -102.6f, 0.80f), // Mexico
    CityLight( 15.6f, 32.5f, 0.70f)   // Sudan
)

val CITY_LIGHT_XYZ: List<FloatArray> = CITY_LIGHTS.map { GlobeMath.latLonToXyz(it.lat, it.lon) }
