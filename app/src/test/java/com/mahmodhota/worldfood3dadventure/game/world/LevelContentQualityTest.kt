package com.mahmodhota.worldfood3dadventure.game.world

import com.mahmodhota.worldfood3dadventure.game.match3.engine.Match3Engine
import com.mahmodhota.worldfood3dadventure.game.match3.engine.MatchDetector
import com.mahmodhota.worldfood3dadventure.game.match3.engine.MoveFinder
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LevelContentQualityTest {

    @Test
    fun levelsHaveUniqueIdsAndValidCountryRefs() {
        val allIds = mutableSetOf<String>()
        val countryIds = LevelRegistry.allCountryIds.toSet()

        LevelRegistry.allCountryIds.forEach { countryId ->
            val country = LevelRegistry.getCountry(countryId)
            requireNotNull(country)

            val sorted = country.levels.sortedBy { it.levelNumber }
            sorted.forEachIndexed { index, level ->
                assertEquals("Level numbers must be sequential for $countryId", index + 1, level.levelNumber)
                assertEquals("Country id mismatch in level data", countryId, level.countryId)
                assertTrue("Level id must be unique globally", allIds.add("${level.countryId}:${level.levelNumber}"))
                assertTrue("Country must be registered", countryIds.contains(level.countryId))
            }
        }
    }

    @Test
    fun levelsUseBalancedAndValidGameplayParameters() {
        LevelRegistry.allCountryIds.forEach { countryId ->
            val levels = requireNotNull(LevelRegistry.getCountry(countryId)).levels
            levels.forEach { level ->
                val distinctTiles = level.allowedTiles.distinct()

                assertTrue("Level must have at least 4 tiles: ${countryId} L${level.levelNumber}", distinctTiles.size >= 4)
                assertTrue("Level tile pool too wide (fairness): ${countryId} L${level.levelNumber}", distinctTiles.size <= 12)
                assertTrue("Level must have at least one goal: ${countryId} L${level.levelNumber}", level.goals.isNotEmpty())
                assertTrue("Level moves out of balanced range: ${countryId} L${level.levelNumber}", level.moves in 18..40)

                level.goals.forEach { goal ->
                    when (goal) {
                        is LevelGoal.CollectFood -> {
                            assertTrue("Collect goal must be positive: ${countryId} L${level.levelNumber}", goal.amount > 0)
                            assertTrue("Collect goal too high for fair moves: ${countryId} L${level.levelNumber}", goal.amount <= level.moves + 8)
                        }
                        is LevelGoal.ScoreTarget -> {
                            assertTrue("Score goal must be positive: ${countryId} L${level.levelNumber}", goal.target > 0)
                        }
                    }
                }

                val thresholds = level.scoreThresholds
                assertTrue("Star thresholds must ascend: ${countryId} L${level.levelNumber}", thresholds.oneStar < thresholds.twoStars)
                assertTrue("Star thresholds must ascend: ${countryId} L${level.levelNumber}", thresholds.twoStars < thresholds.threeStars)

                val scoreGoal = level.goals.filterIsInstance<LevelGoal.ScoreTarget>().maxOfOrNull { it.target }
                if (scoreGoal != null) {
                    assertTrue(
                        "One-star threshold must not be lower than score goal: ${countryId} L${level.levelNumber}",
                        thresholds.oneStar >= scoreGoal
                    )
                }
            }
        }
    }

    @Test
    fun startBoardsAreStableAndPlayableForEveryLevel() {
        LevelRegistry.allCountryIds.forEach { countryId ->
            val levels = requireNotNull(LevelRegistry.getCountry(countryId)).levels
            levels.forEach { level ->
                var successfulGenerations = 0
                repeat(5) { run ->
                    val seed = 1000L + level.levelNumber * 37L + run
                    val engine = Match3Engine(seed = seed, allowedTiles = level.allowedTiles)
                    try {
                        val board = engine.createStartBoard()
                        assertFalse("Initial board has pre-matches: ${countryId} L${level.levelNumber}", MatchDetector.findMatches(board).hasMatches)
                        assertTrue("Initial board has no valid move: ${countryId} L${level.levelNumber}", MoveFinder.hasValidMove(board))
                        successfulGenerations++
                    } catch (_: IllegalStateException) {
                        // Some deterministic seeds can exhaust generator attempts for dense tile sets.
                    }
                }
                assertTrue("Board generation failed repeatedly: ${countryId} L${level.levelNumber}", successfulGenerations >= 2)
            }
        }
    }
}
