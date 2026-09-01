package com.mahmodhota.worldfood3dadventure.game.world

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class P10YLevelContentIntegrityTest {

    @Test
    fun `All levels must have positive moves and achievable goals`() {
        LevelRegistry.allCountryIds.forEach { countryId ->
            val country = LevelRegistry.getCountry(countryId)
            country?.levels?.forEach { level ->
                assertTrue("Level ${level.levelNumber} in $countryId must have positive moves", level.moves > 0)
                assertTrue("Level ${level.levelNumber} in $countryId must have at least one goal", level.goals.isNotEmpty())
            }
        }
    }
}
