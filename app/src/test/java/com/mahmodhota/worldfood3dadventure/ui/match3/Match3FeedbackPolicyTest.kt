package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.data.audio.SfxType
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoardPosition
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoosterType
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTile
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3Board
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialBoardEffect
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialBoardEffectType
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialTileType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Match3FeedbackPolicyTest {

    @Test
    fun cascadeLabelAndFeedbackMappingUsesRequestedChainLabels() {
        val chainTwo = resolveCascadeFeedback(
            stepIndex = 1,
            matchedCount = 3,
            specialSpawnCount = 0,
            specialEffects = emptyList()
        )
        val chainThree = resolveCascadeFeedback(
            stepIndex = 2,
            matchedCount = 3,
            specialSpawnCount = 0,
            specialEffects = emptyList()
        )
        val chainFour = resolveCascadeFeedback(
            stepIndex = 3,
            matchedCount = 3,
            specialSpawnCount = 0,
            specialEffects = emptyList()
        )
        val chainFive = resolveCascadeFeedback(
            stepIndex = 4,
            matchedCount = 3,
            specialSpawnCount = 0,
            specialEffects = emptyList()
        )

        assertEquals("Nice!", chainTwo.comboLabel)
        assertEquals(SfxType.CASCADE_2, chainTwo.sfxType)
        assertEquals(HapticFeedbackStrength.MEDIUM, chainTwo.haptic)
        assertEquals("Great!", chainThree.comboLabel)
        assertEquals(SfxType.CASCADE_3_PLUS, chainThree.sfxType)
        assertEquals("Delicious!", chainFour.comboLabel)
        assertEquals(SfxType.CASCADE_3_PLUS, chainFour.sfxType)
        assertEquals(HapticFeedbackStrength.HEAVY, chainFour.haptic)
        assertEquals("Amazing!", chainFive.comboLabel)
        assertEquals(SfxType.CASCADE_3_PLUS, chainFive.sfxType)
        assertEquals(HapticFeedbackStrength.HEAVY, chainFive.haptic)
    }

    @Test
    fun scoreAndSpecialEventLabelsMapDeterministically() {
        assertEquals("+950 x3", floatingScoreLabel(scoreAwarded = 950, cascadeIndex = 3))
        assertEquals("+300", floatingScoreLabel(scoreAwarded = 300, cascadeIndex = 1))

        val colorClearLabel = specialEffectEventLabel(
            listOf(
                SpecialBoardEffect(
                    type = SpecialBoardEffectType.COLOR_CLEAR,
                    origin = BoardPosition(1, 1),
                    affectedPositions = setOf(BoardPosition(1, 1))
                )
            )
        )
        assertEquals("Color Pulse!", colorClearLabel)
    }

    @Test
    fun specialEffectLabelMapsAllEffectTypes() {
        assertEquals(
            "Row Flash!",
            specialEffectEventLabel(
                listOf(
                    SpecialBoardEffect(
                        type = SpecialBoardEffectType.HORIZONTAL_LINE,
                        origin = BoardPosition(0, 1),
                        affectedPositions = setOf(BoardPosition(0, 1))
                    )
                )
            )
        )
        assertEquals(
            "Column Flash!",
            specialEffectEventLabel(
                listOf(
                    SpecialBoardEffect(
                        type = SpecialBoardEffectType.VERTICAL_LINE,
                        origin = BoardPosition(1, 0),
                        affectedPositions = setOf(BoardPosition(1, 0))
                    )
                )
            )
        )
        assertEquals(
            "Bomb Burst!",
            specialEffectEventLabel(
                listOf(
                    SpecialBoardEffect(
                        type = SpecialBoardEffectType.BOMB,
                        origin = BoardPosition(1, 1),
                        affectedPositions = setOf(BoardPosition(1, 1))
                    )
                )
            )
        )
    }

    @Test
    fun scoreAnchorMappingReturnsClosestToCentroid() {
        val anchor = scoreAnchorPosition(
            setOf(
                BoardPosition(0, 0),
                BoardPosition(0, 1),
                BoardPosition(1, 1),
                BoardPosition(2, 2)
            )
        )
        assertEquals(BoardPosition(1, 1), anchor)
    }

    @Test
    fun hapticMappingEscalatesForSpecialAndLargeMatches() {
        val normal = resolveCascadeFeedback(
            stepIndex = 0,
            matchedCount = 3,
            specialSpawnCount = 0,
            specialEffects = emptyList()
        )
        val special = resolveCascadeFeedback(
            stepIndex = 0,
            matchedCount = 4,
            specialSpawnCount = 1,
            specialEffects = emptyList()
        )
        val fiveMatch = resolveCascadeFeedback(
            stepIndex = 0,
            matchedCount = 5,
            specialSpawnCount = 0,
            specialEffects = emptyList()
        )

        assertEquals(HapticFeedbackStrength.LIGHT, normal.haptic)
        assertEquals(HapticFeedbackStrength.MEDIUM, special.haptic)
        assertEquals(HapticFeedbackStrength.HEAVY, fiveMatch.haptic)
    }

    @Test
    fun transientResetAlwaysUnlocksAndClearsEffects() {
        val board = Match3Board(
            rows = 1,
            columns = 1,
            tiles = listOf(FoodTile(id = 1L, type = FoodTileType.PIZZA))
        )
        val dirty = Match3UiState(
            board = board,
            isAnimating = true,
            selectedPosition = BoardPosition(0, 0),
            matchedPositions = setOf(BoardPosition(0, 0)),
            comboCount = 2,
            animationPhase = Match3AnimationPhase.RemovingMatches,
            comboLabel = "Great!",
            floatingScoreText = "+300",
            floatingScoreNonce = 8,
            specialEffectLabel = "Bomb Burst!",
            specialEffectNonce = 4,
            selectedBooster = BoosterType.HAMMER,
            activatedBooster = BoosterType.HAMMER,
            boosterActivationNonce = 7,
            spawnedSpecialTiles = mapOf(BoardPosition(0, 0) to SpecialTileType.ROW_CLEAR),
            spawnedSpecialNonce = 3
        )

        val reset = dirty.resetTransientUi(isAnimating = false)

        assertFalse(reset.isAnimating)
        assertNull(reset.selectedPosition)
        assertTrue(reset.matchedPositions.isEmpty())
        assertEquals(0, reset.comboCount)
        assertEquals(Match3AnimationPhase.Idle, reset.animationPhase)
        assertNull(reset.comboLabel)
        assertTrue(reset.specialEffects.isEmpty())
        assertNull(reset.floatingScoreText)
        assertEquals(0, reset.floatingScoreNonce)
        assertNull(reset.specialEffectLabel)
        assertNull(reset.selectedBooster)
        assertNull(reset.activatedBooster)
        assertTrue(reset.spawnedSpecialTiles.isEmpty())
        assertEquals(0, reset.spawnedSpecialNonce)
    }
}
