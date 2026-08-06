package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

object Match3MotionTokens {
    const val SelectionScale = 1.09f
    const val SelectionDurationMs = 65
    const val SwapDurationMs = 142
    const val InvalidSwapOutDurationMs = 95
    const val InvalidSwapReturnDurationMs = 95
    const val MatchAnticipationMs = 52
    const val MatchPopDurationMs = 160
    const val FallBaseDurationMs = 96
    const val FallPerRowDurationMs = 45
    const val FallMaxDurationMs = 275
    const val RefillDurationMs = 170
    const val LandingCompressionDurationMs = 55
    const val LandingRecoveryDurationMs = 70
    const val LandingCompressionScale = 0.94f
    const val CascadePauseMs = 18
    const val ComboOverlayDurationMs = 470
    const val BoardShakeOffsetDp = 1.5f
    const val BoardShakeDurationMs = 80
    const val ParticleLifetimeMs = 160
    const val MaxMatchParticles = 4
    const val RewardCountDurationMs = 880
    const val VictoryRewardDelayMs = 140L
    const val VictoryXpDelayMs = 120L

    val SwapEasing: Easing = CubicBezierEasing(0.16f, 0f, 0.12f, 1f)
    val FallEasing: Easing = CubicBezierEasing(0.12f, 0.78f, 0.18f, 1f)

    fun fallDurationForRows(rowDistance: Int): Int {
        if (rowDistance <= 0) return 0
        return (FallBaseDurationMs + rowDistance * FallPerRowDurationMs).coerceAtMost(FallMaxDurationMs)
    }
}
