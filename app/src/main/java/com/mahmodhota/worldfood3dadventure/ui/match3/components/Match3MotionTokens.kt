package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

object Match3MotionTokens {
    const val SelectionScale = 1.08f
    const val SelectionDurationMs = 55
    const val SwapDurationMs = 132
    const val InvalidSwapOutDurationMs = 95
    const val InvalidSwapReturnDurationMs = 95
    const val MatchAnticipationMs = 52
    const val MatchPopDurationMs = 130
    const val FallBaseDurationMs = 85
    const val FallPerRowDurationMs = 46
    const val FallMaxDurationMs = 240
    const val RefillDurationMs = 145
    const val LandingCompressionDurationMs = 55
    const val LandingRecoveryDurationMs = 70
    const val LandingCompressionScale = 0.94f
    const val CascadePauseMs = 18
    const val ComboOverlayDurationMs = 470
    const val BoardShakeOffsetDp = 1.5f
    const val BoardShakeDurationMs = 80
    const val ParticleLifetimeMs = 160
    const val MaxMatchParticles = 4

    val SwapEasing: Easing = CubicBezierEasing(0.16f, 0f, 0.12f, 1f)
    val FallEasing: Easing = CubicBezierEasing(0.12f, 0.78f, 0.18f, 1f)

    fun fallDurationForRows(rowDistance: Int): Int {
        if (rowDistance <= 0) return 0
        return (FallBaseDurationMs + rowDistance * FallPerRowDurationMs).coerceAtMost(FallMaxDurationMs)
    }
}
