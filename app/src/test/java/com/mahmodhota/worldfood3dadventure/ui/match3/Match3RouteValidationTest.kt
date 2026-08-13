package com.mahmodhota.worldfood3dadventure.ui.match3

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Match3RouteValidationTest {

    @Test
    fun invalidCountryUsesSafeFallbackValidation() {
        val result = validateMatch3Route(
            countryExists = false,
            levelExists = false,
            levelUnlocked = false
        )
        assertFalse(result.isValid)
        assertEquals("invalid_country", result.reason)
    }

    @Test
    fun invalidLevelUsesSafeFallbackValidation() {
        val result = validateMatch3Route(
            countryExists = true,
            levelExists = false,
            levelUnlocked = false
        )
        assertFalse(result.isValid)
        assertEquals("invalid_level", result.reason)
    }

    @Test
    fun lockedLevelBlocksEntry() {
        val result = validateMatch3Route(
            countryExists = true,
            levelExists = true,
            levelUnlocked = false
        )
        assertFalse("A locked level must not be enterable", result.isValid)
        assertEquals("level_locked", result.reason)
    }

    @Test
    fun unlockedLevelAllowsEntry() {
        val result = validateMatch3Route(
            countryExists = true,
            levelExists = true,
            levelUnlocked = true
        )
        assertTrue("An unlocked level must be enterable", result.isValid)
        assertEquals("ok", result.reason)
    }

    @Test
    fun completedLevelWithUnlockedStateIsReplayable() {
        // A completed level passes the same gate as an unlocked level —
        // the distinction is handled by the reward system (no duplicate first-clear rewards).
        val result = validateMatch3Route(
            countryExists = true,
            levelExists = true,
            levelUnlocked = true   // completed levels keep isUnlocked = true
        )
        assertTrue("A completed (replayed) level must be enterable", result.isValid)
    }
}
