package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

object Match3MotionTokens {
    const val SelectionScale = 1.09f
    const val SelectionDurationMs = 65
    const val SwapDurationMs = 142
    const val InvalidSwapOutDurationMs = 95
    const val InvalidSwapReturnDurationMs = 95
    const val MatchAnticipationMs = 45
    const val MatchPopDurationMs = 185
    const val FallBaseDurationMs = 85
    const val FallPerRowDurationMs = 40
    const val FallMaxDurationMs = 260
    const val RefillDurationMs = 180
    const val LandingCompressionDurationMs = 50
    const val LandingRecoveryDurationMs = 80
    const val LandingCompressionScale = 0.92f
    const val CascadePauseMs = 22
    const val ComboOverlayDurationMs = 600
    const val BoardShakeOffsetDp = 2.0f
    const val BoardShakeDurationMs = 100
    const val ParticleLifetimeMs = 240
    const val MaxMatchParticles = 8
    const val RewardCountDurationMs = 880
    const val VictoryRewardDelayMs = 200L
    const val VictoryXpDelayMs = 150L
    
    const val CascadeFeedbackMultiplier = 1.12f
    const val MaxCascadeJuiceLevel = 5
    const val ScoreFloatingDurationMs = 650L
    const val SpecialCreationPopDurationMs = 220

    val SwapEasing: Easing = CubicBezierEasing(0.16f, 0f, 0.12f, 1f)
    val FallEasing: Easing = CubicBezierEasing(0.12f, 0.78f, 0.18f, 1f)

    fun fallDurationForRows(rowDistance: Int): Int {
        if (rowDistance <= 0) return 0
        return (FallBaseDurationMs + rowDistance * FallPerRowDurationMs).coerceAtMost(FallMaxDurationMs)
    }
}
