package com.mahmodhota.worldfood3dadventure.ui.match3

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
}
