package com.mahmodhota.worldfood3dadventure.game.progress

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerLevelProgressionTest {

    @Test
    fun xpBelowThreshold_staysLevelOne() {
        val snapshot = PlayerLevelProgression.snapshot(totalXp = 499, storedLevel = 1)
        assertEquals(1, snapshot.level)
        assertEquals(499, snapshot.xpIntoCurrentLevel)
        assertEquals(500, snapshot.xpForNextLevel)
    }

    @Test
    fun xpAtThreshold_levelsUpExactly() {
        val snapshot = PlayerLevelProgression.snapshot(totalXp = 500, storedLevel = 1)
        assertEquals(2, snapshot.level)
        assertEquals(0, snapshot.xpIntoCurrentLevel)
    }

    @Test
    fun xpAboveThreshold_carriesExcess() {
        val snapshot = PlayerLevelProgression.snapshot(totalXp = 900, storedLevel = 1)
        assertEquals(2, snapshot.level)
        assertEquals(400, snapshot.xpIntoCurrentLevel)
        assertEquals(500, snapshot.xpForNextLevel)
    }

    @Test
    fun xpCanTriggerMultipleLevelUps() {
        val snapshot = PlayerLevelProgression.snapshot(totalXp = 1600, storedLevel = 1)
        assertEquals(4, snapshot.level)
        assertEquals(100, snapshot.xpIntoCurrentLevel)
    }

    @Test
    fun legacyInvalidState_isDetectedAndNormalized() {
        assertTrue(PlayerLevelProgression.needsLegacyLevelMigration(totalXp = 900, storedLevel = 1))
        assertEquals(2, PlayerLevelProgression.normalizedLevel(totalXp = 900, storedLevel = 1))
    }

    @Test
    fun normalization_isIdempotentAcrossReload() {
        val normalizedLevel = PlayerLevelProgression.normalizedLevel(totalXp = 900, storedLevel = 1)
        val firstLoad = PlayerLevelProgression.snapshot(totalXp = 900, storedLevel = normalizedLevel)
        val secondLoad = PlayerLevelProgression.snapshot(totalXp = firstLoad.totalXp, storedLevel = firstLoad.level)
        assertEquals(firstLoad.level, secondLoad.level)
        assertEquals(firstLoad.xpIntoCurrentLevel, secondLoad.xpIntoCurrentLevel)
        assertFalse(PlayerLevelProgression.needsLegacyLevelMigration(totalXp = secondLoad.totalXp, storedLevel = secondLoad.level))
    }

    @Test
    fun normalizationDoesNotMutateRewardTotals() {
        val snapshot = PlayerLevelProgression.snapshot(totalXp = 900, storedLevel = 1)
        assertEquals(900, snapshot.totalXp)
    }
}
