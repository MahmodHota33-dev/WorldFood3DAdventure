package com.mahmodhota.worldfood3dadventure.ui.world3d

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Handles dynamic lighting calculations for the 3D Globe.
 */
data class LightingData(
    val sunX: Float,
    val sunY: Float,
    val sunZ: Float
)

object GlobeLighting {
    fun forCycle(phase: Float): LightingData {
        if (!phase.isFinite()) {
            // Default sun direction
            return LightingData(-0.30f, 0.34f, 0.88f)
        }
        val sunX = -cos(phase)
        val sunY = 0.34f
        val sunZ = sin(phase)
        
        val len = sqrt(sunX * sunX + sunY * sunY + sunZ * sunZ)
        return LightingData(sunX / len, sunY / len, sunZ / len)
    }

    fun dayIntensity(x: Float, y: Float, z: Float, lighting: LightingData): Float {
        val dot = x * lighting.sunX + y * lighting.sunY + z * lighting.sunZ
        return (dot + 0.15f).coerceIn(0f, 1f)
    }

    fun nightIntensity(x: Float, y: Float, z: Float, lighting: LightingData): Float {
        val dot = x * lighting.sunX + y * lighting.sunY + z * lighting.sunZ
        return (1.0f - (dot + 0.15f) / 0.30f).coerceIn(0f, 1f)
    }

    fun oceanHighlightIntensity(x: Float, y: Float, z: Float, lighting: LightingData): Float {
        val dot = x * lighting.sunX + y * lighting.sunY + z * lighting.sunZ
        return if (dot > 0.92f) ((dot - 0.92f) / 0.08f).coerceIn(0f, 1f) else 0f
    }
}
