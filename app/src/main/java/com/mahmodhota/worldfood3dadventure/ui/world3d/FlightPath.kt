package com.mahmodhota.worldfood3dadventure.ui.world3d

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal data class GlobeGeoPoint(val latDeg: Float, val lonDeg: Float)

internal data class FlightSample(
    val latDeg: Float,
    val lonDeg: Float,
    val altitudeNorm: Float
)

internal object FlightPath {
    private const val DEG2RAD = (PI / 180.0).toFloat()
    private const val RAD2DEG = (180.0 / PI).toFloat()

    fun sample(start: GlobeGeoPoint, end: GlobeGeoPoint, tRaw: Float): FlightSample {
        val t = tRaw.coerceIn(0f, 1f)
        val p0 = GlobeMath.latLonToXyz(start.latDeg, start.lonDeg)
        val p1 = GlobeMath.latLonToXyz(end.latDeg, end.lonDeg)

        val dot = ((p0[0] * p1[0]) + (p0[1] * p1[1]) + (p0[2] * p1[2])).coerceIn(-1f, 1f)
        val omega = acos(dot)

        val xyz = if (!omega.isFinite() || omega < 0.0001f) {
            floatArrayOf(
                p0[0] + (p1[0] - p0[0]) * t,
                p0[1] + (p1[1] - p0[1]) * t,
                p0[2] + (p1[2] - p0[2]) * t
            )
        } else {
            val sinOmega = sin(omega)
            val a = sin((1f - t) * omega) / sinOmega
            val b = sin(t * omega) / sinOmega
            floatArrayOf(
                p0[0] * a + p1[0] * b,
                p0[1] * a + p1[1] * b,
                p0[2] * a + p1[2] * b
            )
        }

        val len = sqrt(xyz[0] * xyz[0] + xyz[1] * xyz[1] + xyz[2] * xyz[2]).takeIf { it > 0f } ?: 1f
        val nx = xyz[0] / len
        val ny = xyz[1] / len
        val nz = xyz[2] / len

        val lat = asin(ny.coerceIn(-1f, 1f)) * RAD2DEG
        val lon = atan2(nx, nz) * RAD2DEG
        val altitude = (sin(t * PI.toFloat()) * 0.18f).coerceIn(0f, 0.22f)
        return FlightSample(lat, normalizeLongitude(lon), altitude)
    }

    fun angularDistanceDeg(start: GlobeGeoPoint, end: GlobeGeoPoint): Float {
        val a = GlobeMath.latLonToXyz(start.latDeg, start.lonDeg)
        val b = GlobeMath.latLonToXyz(end.latDeg, end.lonDeg)
        val dot = (a[0] * b[0] + a[1] * b[1] + a[2] * b[2]).coerceIn(-1f, 1f)
        return acos(dot) * RAD2DEG
    }

    fun bearingDeg(from: GlobeGeoPoint, to: GlobeGeoPoint): Float {
        val lat1 = from.latDeg * DEG2RAD
        val lat2 = to.latDeg * DEG2RAD
        val dLon = (to.lonDeg - from.lonDeg) * DEG2RAD
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        return normalizeHeading(atan2(y, x) * RAD2DEG)
    }

    fun normalizeLongitude(lonDeg: Float): Float {
        var lon = lonDeg
        while (lon > 180f) lon -= 360f
        while (lon < -180f) lon += 360f
        return lon
    }

    private fun normalizeHeading(deg: Float): Float {
        var d = deg % 360f
        if (d < 0f) d += 360f
        return d
    }
}
