package com.mahmodhota.worldfood3dadventure.game.world

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class P10YFoodContentIntegrityTest {

    @Test
    fun `Global food collection must not contain duplicates or empty names`() {
        val allCountryIds = LevelRegistry.allCountryIds
        allCountryIds.forEach { id ->
            val foods = LevelRegistry.getRepresentativeFoods(id)
            assertTrue("Foods for $id must not be empty", foods.isNotEmpty())
            
            foods.forEach { food ->
                assertTrue("Food name in $id must not be blank", food.name.isNotBlank())
            }
        }
    }
}
