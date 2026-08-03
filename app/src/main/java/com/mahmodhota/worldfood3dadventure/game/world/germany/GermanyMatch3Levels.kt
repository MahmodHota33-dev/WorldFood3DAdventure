package com.mahmodhota.worldfood3dadventure.game.world.germany

import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3LevelDefinition

/**
 * 10 Playable Match-3 levels for the Germany chapter.
 */
object GermanyMatch3Levels {
    
    val levels = listOf(
        // Level 1: Pretzel
        Match3LevelDefinition(
            levelNumber = 1,
            countryId = "germany",
            allowedTiles = listOf(FoodTileType.PRETZEL, FoodTileType.POTATO, FoodTileType.APPLE, FoodTileType.CHEESE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.PRETZEL, 10)),
            moves = 25,
            title = "The Golden Pretzel Hunt"
        ),
        // Level 2: Bratwurst
        Match3LevelDefinition(
            levelNumber = 2,
            countryId = "germany",
            allowedTiles = listOf(FoodTileType.BRATWURST, FoodTileType.BREAD, FoodTileType.CHEESE, FoodTileType.POTATO),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.BRATWURST, 15)),
            moves = 20,
            title = "Bratwurst Banquet"
        ),
        // Level 3: Potato
        Match3LevelDefinition(
            levelNumber = 3,
            countryId = "germany",
            allowedTiles = listOf(FoodTileType.POTATO, FoodTileType.BREAD, FoodTileType.APPLE, FoodTileType.CHEESE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.POTATO, 20)),
            moves = 25
        ),
        // Level 4: Apple
        Match3LevelDefinition(
            levelNumber = 4,
            countryId = "germany",
            allowedTiles = listOf(FoodTileType.APPLE, FoodTileType.PRETZEL, FoodTileType.CHEESE, FoodTileType.POTATO),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.APPLE, 25)),
            moves = 30
        ),
        // Level 5: Cheese
        Match3LevelDefinition(
            levelNumber = 5,
            countryId = "germany",
            allowedTiles = listOf(FoodTileType.CHEESE, FoodTileType.BREAD, FoodTileType.BRATWURST, FoodTileType.POTATO),
            goals = listOf(LevelGoal.ScoreTarget(2000)),
            moves = 20
        ),
        // Level 6: Bread
        Match3LevelDefinition(
            levelNumber = 6,
            countryId = "germany",
            allowedTiles = listOf(FoodTileType.BREAD, FoodTileType.PRETZEL, FoodTileType.APPLE, FoodTileType.CHEESE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.BREAD, 30)),
            moves = 30
        ),
        // Level 7: Black Forest Cake
        Match3LevelDefinition(
            levelNumber = 7,
            countryId = "germany",
            allowedTiles = listOf(FoodTileType.BLACK_FOREST_CAKE, FoodTileType.APPLE, FoodTileType.CHEESE, FoodTileType.POTATO, FoodTileType.PRETZEL),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.BLACK_FOREST_CAKE, 5)),
            moves = 20
        ),
        // Level 8: Mixed Foods
        Match3LevelDefinition(
            levelNumber = 8,
            countryId = "germany",
            allowedTiles = FoodTileType.values().filter { it.name in listOf("PRETZEL", "BRATWURST", "POTATO", "BREAD", "CHEESE", "APPLE") },
            goals = listOf(LevelGoal.ScoreTarget(5000)),
            moves = 35
        ),
        // Level 9: Germany Challenge
        Match3LevelDefinition(
            levelNumber = 9,
            countryId = "germany",
            allowedTiles = listOf(
                FoodTileType.PRETZEL,
                FoodTileType.BRATWURST,
                FoodTileType.POTATO,
                FoodTileType.BREAD,
                FoodTileType.CHEESE,
                FoodTileType.APPLE,
                FoodTileType.BLACK_FOREST_CAKE
            ),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.PRETZEL, 18), LevelGoal.CollectFood(FoodTileType.BRATWURST, 18)),
            moves = 34
        ),
        // Level 10: Germany Master
        Match3LevelDefinition(
            levelNumber = 10,
            countryId = "germany",
            allowedTiles = listOf(
                FoodTileType.PRETZEL,
                FoodTileType.BRATWURST,
                FoodTileType.POTATO,
                FoodTileType.BREAD,
                FoodTileType.CHEESE,
                FoodTileType.APPLE,
                FoodTileType.BLACK_FOREST_CAKE
            ),
            goals = listOf(LevelGoal.ScoreTarget(14000)),
            moves = 36
        ),
        // Level 11: The Classic
        Match3LevelDefinition(
            levelNumber = 11,
            countryId = "germany",
            allowedTiles = listOf(FoodTileType.BRATWURST, FoodTileType.POTATO, FoodTileType.BREAD, FoodTileType.PRETZEL),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.BRATWURST, 20), LevelGoal.CollectFood(FoodTileType.POTATO, 20)),
            moves = 35
        ),
        // Level 12: Apple Strudel
        Match3LevelDefinition(
            levelNumber = 12,
            countryId = "germany",
            allowedTiles = listOf(FoodTileType.APPLE, FoodTileType.BREAD, FoodTileType.CHEESE, FoodTileType.POTATO),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.APPLE, 30), LevelGoal.CollectFood(FoodTileType.BREAD, 20)),
            moves = 30
        ),
        // Level 13: Pretzel Master
        Match3LevelDefinition(
            levelNumber = 13,
            countryId = "germany",
            allowedTiles = listOf(FoodTileType.PRETZEL, FoodTileType.APPLE, FoodTileType.BRATWURST, FoodTileType.POTATO),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.PRETZEL, 28)),
            moves = 24
        ),
        // Level 14: Gateau Celebration
        Match3LevelDefinition(
            levelNumber = 14,
            countryId = "germany",
            allowedTiles = listOf(FoodTileType.BLACK_FOREST_CAKE, FoodTileType.APPLE, FoodTileType.BREAD, FoodTileType.CHEESE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.BLACK_FOREST_CAKE, 12)),
            moves = 24
        ),
        // Level 15: Germany Grand Finale
        Match3LevelDefinition(
            levelNumber = 15,
            countryId = "germany",
            allowedTiles = listOf(
                FoodTileType.PRETZEL,
                FoodTileType.BRATWURST,
                FoodTileType.POTATO,
                FoodTileType.BREAD,
                FoodTileType.CHEESE,
                FoodTileType.APPLE,
                FoodTileType.BLACK_FOREST_CAKE
            ),
            goals = listOf(
                LevelGoal.ScoreTarget(22000),
                LevelGoal.CollectFood(FoodTileType.BLACK_FOREST_CAKE, 10)
            ),
            moves = 30
        )
    )

    fun getLevel(levelNumber: Int): Match3LevelDefinition? {
        return levels.find { it.levelNumber == levelNumber }
    }
}
