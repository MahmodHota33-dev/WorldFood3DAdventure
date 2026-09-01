package com.mahmodhota.worldfood3dadventure.ui.match3

import com.mahmodhota.worldfood3dadventure.data.audio.SfxType
import com.mahmodhota.worldfood3dadventure.MainDispatcherRule
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialBoardEffect
import com.mahmodhota.worldfood3dadventure.game.match3.model.SpecialBoardEffectType
import com.mahmodhota.worldfood3dadventure.game.match3.model.BoardPosition
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class P10TAudioSystemTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun testCascadeFeedbackSoundMapping() {
        // Cascade 1, 3 tiles
        val res1 = resolveCascadeFeedback(stepIndex = 0, matchedCount = 3, specialSpawnCount = 0, specialEffects = emptyList())
        assertEquals(SfxType.MATCH_3, res1.sfxType)

        // Cascade 1, 4 tiles
        val res2 = resolveCascadeFeedback(stepIndex = 0, matchedCount = 4, specialSpawnCount = 0, specialEffects = emptyList())
        assertEquals(SfxType.MATCH_4, res2.sfxType)

        // Cascade 1, 5 tiles
        val res3 = resolveCascadeFeedback(stepIndex = 0, matchedCount = 5, specialSpawnCount = 0, specialEffects = emptyList())
        assertEquals(SfxType.MATCH_5, res3.sfxType)

        // Cascade 2
        val res4 = resolveCascadeFeedback(stepIndex = 1, matchedCount = 3, specialSpawnCount = 0, specialEffects = emptyList())
        assertEquals(SfxType.CASCADE_2, res4.sfxType)

        // Cascade 3+
        val res5 = resolveCascadeFeedback(stepIndex = 2, matchedCount = 3, specialSpawnCount = 0, specialEffects = emptyList())
        assertEquals(SfxType.CASCADE_3_PLUS, res5.sfxType)
    }

    @Test
    fun testSpecialEffectSoundMapping() {
        val bombEffect = listOf(SpecialBoardEffect(SpecialBoardEffectType.BOMB, BoardPosition(0,0), emptySet()))
        assertEquals(SfxType.BOMB_EXPLOSION, specialEffectSfx(bombEffect))

        val rocketEffect = listOf(SpecialBoardEffect(SpecialBoardEffectType.HORIZONTAL_LINE, BoardPosition(0,0), emptySet()))
        assertEquals(SfxType.ROCKET_SWEEP, specialEffectSfx(rocketEffect))

        val colorEffect = listOf(SpecialBoardEffect(SpecialBoardEffectType.COLOR_CLEAR, BoardPosition(0,0), emptySet()))
        assertEquals(SfxType.COLOR_BOMB_FINISH, specialEffectSfx(colorEffect))

        assertEquals(null, specialEffectSfx(emptyList()))
    }
}
