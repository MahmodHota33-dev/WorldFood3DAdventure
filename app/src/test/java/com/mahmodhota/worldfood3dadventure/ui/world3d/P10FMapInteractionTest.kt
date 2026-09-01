package com.mahmodhota.worldfood3dadventure.ui.world3d

import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.Continent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class P10FMapInteractionTest {

    @Test
    fun testContinentFilterLogic() {
        val allCountries = GLOBE_COUNTRIES
        val europeCountries = allCountries.filter { id -> 
            LevelRegistry.getCountry(id.id)?.metadata?.continent == Continent.EUROPE 
        }
        
        assertTrue("Should have multiple Europe countries", europeCountries.size > 5)
        assertTrue("Germany should be in Europe", europeCountries.any { it.id == "germany" })
        
        val asiaCountries = allCountries.filter { id -> 
            LevelRegistry.getCountry(id.id)?.metadata?.continent == Continent.ASIA 
        }
        assertTrue("Japan should be in Asia", asiaCountries.any { it.id == "japan" })
    }

    @Test
    fun testMarkerStateHierarchy() {
        // Test logic for resolveMarkerState
        val stateCurrent = resolveMarkerState("italy", isUnlocked = true, isCompleted = false, isNextDestination = true)
        assertEquals(MarkerState.CURRENT, stateCurrent)

        val stateCompleted = resolveMarkerState("germany", isUnlocked = true, isCompleted = true, isNextDestination = false)
        assertEquals(MarkerState.COMPLETED, stateCompleted)

        val stateLocked = resolveMarkerState("korea", isUnlocked = false, isCompleted = false, isNextDestination = false)
        assertEquals(MarkerState.LOCKED, stateLocked)
    }
}
