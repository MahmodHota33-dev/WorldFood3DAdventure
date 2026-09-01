package com.mahmodhota.worldfood3dadventure.game.world

import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CountryFoodThemingTest {

    @Test
    fun testAll213CountriesHave15Levels() {
        val allIds = LevelRegistry.allCountryIds
        assertEquals("Total countries should be 213", 213, allIds.size)
        
        allIds.forEach { id ->
            val country = LevelRegistry.getCountry(id)
            assertTrue("Country $id should be registered", country != null)
            assertEquals("Country $id should have 15 levels", 15, country!!.levels.size)
        }
    }

    @Test
    fun testAllLevelsHaveValidFoodThemes() {
        LevelRegistry.allCountryIds.forEach { id ->
            val country = LevelRegistry.getCountry(id)!!
            country.levels.forEach { level ->
                assertTrue("Level ${level.levelNumber} in $id should have allowed tiles", level.allowedTiles.isNotEmpty())
                
                // Verify that at least some tiles are country-specific or reasonable fallbacks
                val representative = LevelRegistry.getRepresentativeFoods(id)
                val intersection = level.allowedTiles.intersect(representative.toSet())
                
                // We expect at least one representative food to be in the level's allowed tiles
                // (Unless it's a very specific tutorial level, but here we want identity)
                assertTrue("Level ${level.levelNumber} in $id should include representative foods from $representative", intersection.isNotEmpty())
            }
        }
    }

    @Test
    fun testSpecificLandIdentities() {
        // Germany check
        val germanyFoods = LevelRegistry.getRepresentativeFoods("germany")
        assertTrue(germanyFoods.contains(FoodTileType.PRETZEL))
        assertTrue(germanyFoods.contains(FoodTileType.BRATWURST))
        
        // Italy check
        val italyFoods = LevelRegistry.getRepresentativeFoods("italy")
        assertTrue(italyFoods.contains(FoodTileType.PIZZA))
        assertTrue(italyFoods.contains(FoodTileType.PASTA))
        
        // Japan check
        val japanFoods = LevelRegistry.getRepresentativeFoods("japan")
        assertTrue(japanFoods.contains(FoodTileType.SUSHI))
        assertTrue(japanFoods.contains(FoodTileType.RAMEN))
        
        // Mexico check
        val mexicoFoods = LevelRegistry.getRepresentativeFoods("mexico")
        assertTrue(mexicoFoods.contains(FoodTileType.TACO))
        assertTrue(mexicoFoods.contains(FoodTileType.GUACAMOLE))
    }
}
