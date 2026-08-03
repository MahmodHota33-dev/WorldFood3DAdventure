package com.mahmodhota.worldfood3dadventure.ui.world3d

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import kotlin.math.abs

/**
 * Mutable camera state for the 3D globe world screen.
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
        val speed = maxOf(abs(velY), abs(velX))
        val damping = when {
            speed > 3.5f -> 0.92f
            speed > 1.4f -> 0.89f
            else         -> 0.84f
        }
        velY *= damping
        velX *= damping
        if (abs(velY) < 0.04f) velY = 0f
        if (abs(velX) < 0.04f) velX = 0f
    }

    val hasVelocity: Boolean get() = abs(velY) > 0.1f || abs(velX) > 0.1f

    fun stopInertia() {
        velY = 0f
        velX = 0f
    }

    /**
     * Apply touch drag impulse to camera velocity with soft acceleration and clamping.
     * The blend term reduces abrupt speed jumps while preserving responsiveness.
     */
    fun applyDragImpulse(panX: Float, panY: Float) {
        if (!panX.isFinite() || !panY.isFinite()) return
        val impulseY = (panX * 0.24f).coerceIn(-5f, 5f)
        val impulseX = (-panY * 0.24f).coerceIn(-5f, 5f)
        velY = (velY * 0.78f + impulseY).coerceIn(-9f, 9f)
        velX = (velX * 0.78f + impulseX).coerceIn(-9f, 9f)
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
     * Use [startFlyTo] for smooth animated transition.
     */
    fun focusOn(latDeg: Float, lonDeg: Float) {
        rotationY = -lonDeg
        rotationX = (latDeg * 0.8f).coerceIn(-80f, 80f)
        stopInertia()
    }

    // ── Smooth fly-to animation ──────────────────────────────────────────────

    private var flyFromY = 0f
    private var flyFromX = 0f
    private var flyTargetY = 0f
    private var flyTargetX = 0f
    private var flyProgress = 1f     // 0..1; 1 = idle/complete
    var isFlyingTo = false
        private set

    /**
     * Start a smooth camera fly-to for a geographic position.
     * Uses the shortest rotation path. Cancels any current inertia.
     */
    fun startFlyTo(latDeg: Float, lonDeg: Float) {
        val targetY = -lonDeg
        val targetX = (latDeg * 0.8f).coerceIn(-80f, 80f)
        flyFromY = rotationY
        flyFromX = rotationX
        // Shortest longitude path avoids flying 300° the wrong way
        val dY = normalizeAngleDiff(targetY - flyFromY)
        flyTargetY = flyFromY + dY
        flyTargetX = targetX
        flyProgress = 0f
        isFlyingTo = true
        stopInertia()
    }

    /**
     * Advance fly-to by one frame tick (~16 ms at 60 fps).
     * Total duration ≈ 600 ms. Call from the animation frame loop.
     */
    fun tickFlyTo() {
        if (!isFlyingTo) return
        flyProgress = (flyProgress + FLY_STEP).coerceAtMost(1f)
        val t = easeOutBack(flyProgress)
        rotationY = flyFromY + (flyTargetY - flyFromY) * t
        rotationX = flyFromX + (flyTargetX - flyFromX) * t
        if (flyProgress >= 1f) isFlyingTo = false
    }

    /** Cancel an in-progress fly-to; leaves camera at current position. */
    fun cancelFlyTo() {
        isFlyingTo = false
        flyProgress = 1f
    }

    companion object {
        /** Frames to reach destination (≈600 ms at 60 fps). */
        const val FLY_STEP = 1f / 36f

        /**
         * Ease-out cubic: smooth deceleration without overshoot.
         * Used by tests; [tickFlyTo] now uses [easeOutBack].
         */
        fun easeOutCubic(t: Float): Float {
            val c = 1f - t.coerceIn(0f, 1f)
            return 1f - c * c * c
        }

        /**
         * Ease-out back: smooth deceleration with a tiny (≈4%) overshoot
         * that creates a premium "spring settle" feel.
         * f(0)=0, f(1)=1, brief peak ≈1.04 near t≈0.65.
         */
        fun easeOutBack(t: Float): Float {
            val c = t.coerceIn(0f, 1f)
            val c1 = 1.10f            // overshoot magnitude (tiny)
            val c3 = c1 + 1f
            val s  = c - 1f
            return 1f + c3 * s * s * s + c1 * s * s
        }

        /**
         * Normalise a longitude difference to [−180, 180] so the
         * camera always takes the shortest arc.
         */
        fun normalizeAngleDiff(diff: Float): Float {
            var d = diff % 360f
            if (d > 180f) d -= 360f
            else if (d < -180f) d += 360f
            return d
        }
    }
}
