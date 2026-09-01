package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class P10UWorldMapIntegrityTest {

    @Test
    fun `World map must have exactly 213 countries`() {
        assertEquals(213, GLOBE_COUNTRIES.size)
    }

    @Test
    fun `All core campaign countries must exist and have correct IDs`() {
        val coreIds = listOf("germany", "italy", "france", "spain", "sudan", "japan", "mexico")
        coreIds.forEach { id ->
            val country = GLOBE_COUNTRIES.find { it.id == id }
            assertNotNull("Country $id must exist", country)
        }
    }

    @Test
    fun `Country coordinates must remain unchanged`() {
        val germany = GLOBE_COUNTRIES.find { it.id == "germany" }
        assertEquals(51.165f, germany?.latDeg ?: 0f, 0.001f)
        assertEquals(10.451f, germany?.lonDeg ?: 0f, 0.001f)
        
        val japan = GLOBE_COUNTRIES.find { it.id == "japan" }
        assertEquals(36.204f, japan?.latDeg ?: 0f, 0.001f)
        assertEquals(138.252f, japan?.lonDeg ?: 0f, 0.001f)
    }
}
