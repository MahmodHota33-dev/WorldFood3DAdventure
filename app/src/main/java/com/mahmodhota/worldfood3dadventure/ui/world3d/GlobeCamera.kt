package com.mahmodhota.worldfood3dadventure.ui.world3d

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import kotlin.math.abs

/**
 * Mutable camera state for the experimental 3D globe.
 *
 * [rotationY] pans left/right (longitude rotation, degrees).
 * [rotationX] tilts up/down (latitude rotation, degrees), clamped to ±80°.
 * [zoom]      distance multiplier — 0.6 (far) … 2.2 (close-up).
 *
 * [velY] / [velX] are per-frame inertia velocities at 60 fps; they decay
 * automatically in [tickInertia] which must be called from a frame loop.
 */
@Stable
class GlobeCameraState(
    initialRotY: Float = -15f,
    initialRotX: Float = 25f,
    initialZoom: Float = 1f
) {
    var rotationY by mutableFloatStateOf(initialRotY)
    var rotationX by mutableFloatStateOf(initialRotX)
    var zoom       by mutableFloatStateOf(initialZoom)

    // Inertia — not Compose state to avoid recomposition overhead per tick
    @Volatile var velY: Float = 0f
    @Volatile var velX: Float = 0f

    fun rotate(dY: Float, dX: Float) {
        rotationY = (rotationY + dY) % 360f
        rotationX = (rotationX + dX).coerceIn(-80f, 80f)
    }

    fun applyZoom(factor: Float) {
        zoom = (zoom * factor).coerceIn(0.6f, 2.2f)
    }

    /** Advance one frame of inertia; call inside a [withFrameNanos] loop. */
    fun tickInertia() {
        if (!hasVelocity) return
        rotate(velY, velX)
        velY *= 0.90f
        velX *= 0.90f
        if (abs(velY) < 0.15f) velY = 0f
        if (abs(velX) < 0.15f) velX = 0f
    }

    val hasVelocity: Boolean get() = abs(velY) > 0.1f || abs(velX) > 0.1f

    fun stopInertia() {
        velY = 0f
        velX = 0f
    }

    fun resetToDefault() {
        rotationY = -15f
        rotationX = 25f
        zoom = 1f
        velY = 0f
        velX = 0f
    }

    /**
     * Immediately snap camera to focus on a geographic position.
     * A smooth-fly-to animation is planned for Phase 2.
     */
    fun focusOn(latDeg: Float, lonDeg: Float) {
        rotationY = -lonDeg
        rotationX = (latDeg * 0.8f).coerceIn(-80f, 80f)
        stopInertia()
    }
}
