package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.data.audio.SfxType
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoardPosition
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialBoardEffect
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialBoardEffectType
import kotlin.math.abs

internal enum class HapticFeedbackStrength {
    NONE,
    LIGHT,
    MEDIUM,
    HEAVY
}

internal data class CascadeFeedbackPlan(
    val comboLabel: String?,
    val sfxType: SfxType,
    val haptic: HapticFeedbackStrength
)

internal fun resolveCascadeFeedback(
    stepIndex: Int,
    matchedCount: Int,
    specialSpawnCount: Int,
    specialEffects: List<SpecialBoardEffect>
): CascadeFeedbackPlan {
    val chainCount = stepIndex + 1
    val hasColorClear = specialEffects.any { it.type == SpecialBoardEffectType.COLOR_CLEAR }
    val hasSpecial = specialSpawnCount > 0 || specialEffects.isNotEmpty()
    val comboLabel = when {
        chainCount >= 5 -> "Amazing!"
        chainCount == 4 -> "Delicious!"
        chainCount == 3 -> "Great!"
        chainCount == 2 -> "Nice!"
        else -> null
    }
    val sfx = when {
        hasColorClear || matchedCount >= 5 -> SfxType.MATCH_LARGE
        hasSpecial -> SfxType.COMBO_2
        chainCount >= 5 -> SfxType.COMBO_3
        chainCount == 4 -> SfxType.COMBO_3
        chainCount == 3 -> SfxType.COMBO_2
        chainCount == 2 -> SfxType.COMBO_1
        else -> SfxType.MATCH_SMALL
    }
    val haptic = when {
        hasColorClear || matchedCount >= 5 -> HapticFeedbackStrength.HEAVY
        hasSpecial -> HapticFeedbackStrength.MEDIUM
        chainCount >= 4 -> HapticFeedbackStrength.HEAVY
        chainCount >= 2 -> HapticFeedbackStrength.MEDIUM
        else -> HapticFeedbackStrength.MEDIUM
    }
    return CascadeFeedbackPlan(comboLabel = comboLabel, sfxType = sfx, haptic = haptic)
}

internal fun floatingScoreLabel(scoreAwarded: Int, cascadeIndex: Int): String {
    val suffix = if (cascadeIndex > 1) " x$cascadeIndex" else ""
    return "+$scoreAwarded$suffix"
}

internal fun scoreAnchorPosition(matchedPositions: Set<BoardPosition>): BoardPosition? {
    if (matchedPositions.isEmpty()) return null
    val avgRow = matchedPositions.sumOf { it.row } / matchedPositions.size.toFloat()
    val avgCol = matchedPositions.sumOf { it.column } / matchedPositions.size.toFloat()
    return matchedPositions.minByOrNull {
        abs(it.row - avgRow) + abs(it.column - avgCol)
    }
}

internal fun specialEffectEventLabel(effects: List<SpecialBoardEffect>): String? {
    if (effects.isEmpty()) return null
    return when {
        effects.any { it.type == SpecialBoardEffectType.COLOR_CLEAR } -> "Color Pulse!"
        effects.any { it.type == SpecialBoardEffectType.BOMB } -> "Bomb Burst!"
        effects.any { it.type == SpecialBoardEffectType.HORIZONTAL_LINE } -> "Row Flash!"
        effects.any { it.type == SpecialBoardEffectType.VERTICAL_LINE } -> "Column Flash!"
        else -> null
    }
}

internal fun Match3UiState.resetTransientUi(isAnimating: Boolean): Match3UiState {
    return copy(
        isAnimating = isAnimating,
        selectedPosition = null,
        matchedPositions = emptySet(),
        comboCount = 0,
        animationPhase = Match3AnimationPhase.Idle,
        activeTileAnimationIds = emptySet(),
        fallDistanceByTileId = emptyMap(),
        refillTileIds = emptySet(),
        landingTileIds = emptySet(),
        comboLabel = null,
        boardShakeEnabled = false,
        specialEffects = emptyList(),
        selectedBooster = null,
        floatingScoreText = null,
        floatingScoreNonce = 0,
        floatingScoreAnchor = null,
        specialEffectLabel = null,
        specialEffectNonce = 0,
        goalPulseType = null,
        goalPulseNonce = 0,
        activatedBooster = null,
        boosterActivationNonce = 0,
        spawnedSpecialTiles = emptyMap(),
        spawnedSpecialNonce = 0
    )
}
