package com.mahmodhota.worldfood3dadventure.ui.world3d

import kotlin.math.max

internal class FlightAnimator {
    private var start = GlobeGeoPoint(0f, 0f)
    private var end = GlobeGeoPoint(0f, 0f)
    private var progress = 1f
    private var durationMs = 1000f
    private var routeAlpha = 0f
    private var arrivalPulse = 0f
    private var destinationId: String? = null
    private var justArrivedId: String? = null

    val isActive: Boolean get() = progress < 1f
    val hasVisiblePath: Boolean get() = isActive || routeAlpha > 0.01f
    val pathAlpha: Float get() = routeAlpha.coerceIn(0f, 1f)

    fun startFlight(from: GlobeCountry, to: GlobeCountry) {
        start = GlobeGeoPoint(from.latDeg, from.lonDeg)
        end = GlobeGeoPoint(to.latDeg, to.lonDeg)
        val angular = FlightPath.angularDistanceDeg(start, end)
        durationMs = (800f + (angular / 180f).coerceIn(0f, 1f) * 600f).coerceIn(800f, 1400f)
        progress = 0f
        routeAlpha = 1f
        arrivalPulse = 0f
        destinationId = to.id
        justArrivedId = null
    }

    fun tick(deltaMs: Float) {
        val dt = max(0f, deltaMs)
        if (isActive) {
            progress = (progress + dt / durationMs).coerceIn(0f, 1f)
            routeAlpha = 1f
            if (!isActive && destinationId != null) {
                arrivalPulse = 1f
                justArrivedId = destinationId
            }
        } else {
            routeAlpha = (routeAlpha - dt / 420f).coerceAtLeast(0f)
            arrivalPulse = (arrivalPulse - dt / 500f).coerceAtLeast(0f)
        }
    }

    fun consumeArrivalDestinationId(): String? {
        val id = justArrivedId
        justArrivedId = null
        return id
    }

    fun sampleAt(t: Float): FlightSample = FlightPath.sample(start, end, t)

    fun currentSample(): FlightSample? = if (isActive) sampleAt(progress) else null

    fun currentDirectionSample(step: Float = 0.012f): Pair<FlightSample, FlightSample>? {
        if (!isActive) return null
        val t1 = progress
        val t2 = (progress + step).coerceIn(0f, 1f)
        return sampleAt(t1) to sampleAt(t2)
    }

    fun arrivalBoostFor(countryId: String): Float {
        if (countryId != destinationId) return 0f
        return arrivalPulse.coerceIn(0f, 1f)
    }

    fun clear() {
        progress = 1f
        routeAlpha = 0f
        arrivalPulse = 0f
        destinationId = null
        justArrivedId = null
    }
}
