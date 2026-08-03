package com.mahmodhota.worldfood3dadventure.ui.world3d

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class GlobeLightingState(
    val sunX: Float,
    val sunY: Float,
    val sunZ: Float
)

object GlobeLighting {
    private val DEFAULT = normalizeOrDefault(-0.30f, 0.34f, 0.88f)

    fun defaultState(): GlobeLightingState = DEFAULT

    /**
     * Slow deterministic sun cycle.
     * phaseRad is expected to evolve slowly (e.g. tiny increments in the frame loop).
     */
    fun forCycle(phaseRad: Float): GlobeLightingState {
        if (!phaseRad.isFinite()) return DEFAULT
        val x = -0.30f + sin(phaseRad) * 0.11f
        val y =  0.34f + cos(phaseRad * 0.82f) * 0.07f
        val z =  0.88f + cos(phaseRad * 0.57f) * 0.05f
        return normalizeOrDefault(x, y, z)
    }

    fun dotWithSun(x2: Float, y2: Float, z2: Float, lighting: GlobeLightingState): Float {
        if (!x2.isFinite() || !y2.isFinite() || !z2.isFinite()) return 0f
        val dot = x2 * lighting.sunX + y2 * lighting.sunY + z2 * lighting.sunZ
        return dot.coerceIn(-1f, 1f)
    }

    fun dayIntensity(x2: Float, y2: Float, z2: Float, lighting: GlobeLightingState): Float {
        val dot = dotWithSun(x2, y2, z2, lighting)
        return dot.coerceIn(0f, 1f)
    }

    fun nightIntensity(x2: Float, y2: Float, z2: Float, lighting: GlobeLightingState): Float {
        val dot = dotWithSun(x2, y2, z2, lighting)
        return (-dot).coerceIn(0f, 1f)
    }

    fun oceanHighlightIntensity(x2: Float, y2: Float, z2: Float, lighting: GlobeLightingState): Float {
        val day = dayIntensity(x2, y2, z2, lighting)
        return ((day - 0.22f) / 0.78f).coerceIn(0f, 1f)
    }

    private fun normalizeOrDefault(x: Float, y: Float, z: Float): GlobeLightingState {
        if (!x.isFinite() || !y.isFinite() || !z.isFinite()) return DEFAULT
        val len = sqrt(x * x + y * y + z * z)
        if (!len.isFinite() || len <= 0.0001f) return DEFAULT
        val nx = x / len
        val ny = y / len
        val nz = z / len
        if (!nx.isFinite() || !ny.isFinite() || !nz.isFinite()) return DEFAULT
        return GlobeLightingState(nx, ny, nz)
    }
}
