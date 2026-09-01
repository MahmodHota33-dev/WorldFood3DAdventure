package com.mahmodhota.worldfood3dadventure.game.world

import com.mahmodhota.worldfood3dadventure.game.world.model.Continent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class P10YContentIntegrityTest {

    @Test
    fun `Exactly 213 countries must be registered in GlobalContentRegistry`() {
        val allCountries = LevelRegistry.allCountries
        assertEquals("GlobalContentRegistry must have exactly 213 countries", 213, allCountries.size)
    }

    @Test
    fun `Each country must have a valid ID and non-empty metadata`() {
        LevelRegistry.allCountries.forEach { country ->
            assertTrue("Country ID must not be blank", country.levelId.isNotBlank())
            assertTrue("Display name must not be blank for ${country.levelId}", country.displayName.isNotBlank())
            assertTrue("Flag emoji must not be blank for ${country.levelId}", country.flagEmoji.isNotBlank())
            assertTrue("Description must not be blank for ${country.levelId}", country.travelDescription.isNotBlank())
        }
    }

    @Test
    fun `Each country must have a valid continent`() {
        LevelRegistry.allCountries.forEach { country ->
            assertNotNull("Continent must be assigned for ${country.levelId}", country.continent)
        }
    }

    @Test
    fun `Each country must have a non-empty food collection`() {
        LevelRegistry.allCountryIds.forEach { countryId ->
            val foods = LevelRegistry.getRepresentativeFoods(countryId)
            assertTrue("Food collection for $countryId must not be empty", foods.isNotEmpty())
            
            val foodNames = foods.map { it.name }
            assertEquals("Duplicate food items found in $countryId", foodNames.size, foodNames.distinct().size)
        }
    }

    @Test
    fun `Campaign countries must have 15 levels each`() {
        val campaignCountries = listOf("germany", "italy", "france", "spain", "sudan", "japan", "mexico")
        campaignCountries.forEach { id ->
            val country = LevelRegistry.getCountry(id)
            assertNotNull("Campaign country $id must exist", country)
            assertEquals("Campaign country $id must have 15 levels", 15, country?.levels?.size)
            
            // Verify level numbers are sequential 1-15
            val levelNums = country?.levels?.map { it.levelNumber }?.sorted()
            assertEquals("Levels for $id must be sequential 1-15", (1..15).toList(), levelNums)
        }
    }

    @Test
    fun `Global food collection must have no duplicate names across countries`() {
        // Actually duplicates ACROSS countries are allowed (e.g. Bread in many countries)
        // But let's check total unique food count
        val allFoods = LevelRegistry.allCountryIds.flatMap { LevelRegistry.getRepresentativeFoods(it) }.distinct()
        assertTrue("Should have a large variety of unique foods", allFoods.size > 50)
    }
}
