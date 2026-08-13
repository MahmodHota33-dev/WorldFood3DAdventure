package com.mahmodhota.worldfood3dadventure.game.progress

import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import org.junit.Assert.*
import org.junit.Test

class UnlockEventDerivationTest {

    @Test
    fun italyUnlocksExactlyAt30Stars() {
        val italySpec = CountryProgressionChain.getSpec("italy")!!
        assertEquals(30, italySpec.requiredStarsToUnlock)
        
        assertFalse(CountryProgressionChain.canUnlock("italy", 29))
        assertTrue(CountryProgressionChain.canUnlock("italy", 30))
    }

    @Test
    fun franceUnlocksExactlyAt60Stars() {
        val franceSpec = CountryProgressionChain.getSpec("france")!!
        assertEquals(60, franceSpec.requiredStarsToUnlock)
        
        assertFalse(CountryProgressionChain.canUnlock("france", 59))
        assertTrue(CountryProgressionChain.canUnlock("france", 60))
    }

    @Test
    fun germanyIsInitiallyUnlocked() {
        assertTrue(CountryProgressionChain.isInitiallyUnlocked("germany"))
    }
}
