package com.mahmodhota.worldfood3dadventure.game.world

import com.mahmodhota.worldfood3dadventure.game.match3.Match3LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import org.junit.Assert.*
import org.junit.Test

class LevelIntegrityTest {

    @Test
    fun allCountriesInProgressionChainExistInRegistry() {
        CountryProgressionChain.UNLOCK_ORDER.forEach { spec ->
            assertNotNull("Country ${spec.countryId} should exist in registry", LevelRegistry.getCountry(spec.countryId))
        }
    }

    @Test
    fun allPlayableLevelsHaveDefinitions() {
        CountryProgressionChain.UNLOCK_ORDER.forEach { spec ->
            for (levelNum in 1..spec.totalLevels) {
                val level = Match3LevelRegistry.getLevel(spec.countryId, levelNum)
                assertNotNull("Level $levelNum for ${spec.countryId} should exist", level)
                assertEquals(spec.countryId, level!!.countryId)
                assertEquals(levelNum, level.levelNumber)
            }
        }
    }

    @Test
    fun levelGoalsAreSatisfiableByTilePool() {
        CountryProgressionChain.UNLOCK_ORDER.forEach { spec ->
            for (levelNum in 1..spec.totalLevels) {
                val level = Match3LevelRegistry.getLevel(spec.countryId, levelNum)!!
                level.goals.filterIsInstance<LevelGoal.CollectFood>().forEach { goal ->
                    assertTrue(
                        "Goal ${goal.type} in Level $levelNum for ${spec.countryId} must be in allowedTiles",
                        level.allowedTiles.contains(goal.type)
                    )
                }
            }
        }
    }
}
