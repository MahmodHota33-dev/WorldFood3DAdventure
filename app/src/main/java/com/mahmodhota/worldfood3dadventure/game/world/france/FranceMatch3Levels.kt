package com.mahmodhota.worldfood3dadventure.game.world.france

import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3LevelDefinition

/**
 * 15 Playable Match-3 levels for the France chapter.
 */
object FranceMatch3Levels {
    
    val levels = listOf(
        // Level 1: Croissant
        Match3LevelDefinition(
            levelNumber = 1,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.CROISSANT, FoodTileType.MACARON, FoodTileType.CREPE, FoodTileType.FRENCH_CHEESE, FoodTileType.BAGUETTE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.CROISSANT, 20)),
            moves = 20
        ),
        // Level 2: Baguette
        Match3LevelDefinition(
            levelNumber = 2,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.BAGUETTE, FoodTileType.BREAD, FoodTileType.FRENCH_CHEESE, FoodTileType.CROISSANT, FoodTileType.CREPE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.BAGUETTE, 25)),
            moves = 22
        ),
        // Level 3: French Cheese
        Match3LevelDefinition(
            levelNumber = 3,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.FRENCH_CHEESE, FoodTileType.BREAD, FoodTileType.TOMATO, FoodTileType.BASIL, FoodTileType.BAGUETTE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.FRENCH_CHEESE, 20)),
            moves = 24
        ),
        // Level 4: Crêpe
        Match3LevelDefinition(
            levelNumber = 4,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.CREPE, FoodTileType.MACARON, FoodTileType.CROISSANT, FoodTileType.FRENCH_CHEESE, FoodTileType.GELATO),
            goals = listOf(LevelGoal.ScoreTarget(7500)),
            moves = 25
        ),
        // Level 5: Macaron
        Match3LevelDefinition(
            levelNumber = 5,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.MACARON, FoodTileType.GELATO, FoodTileType.CROISSANT, FoodTileType.ECLAIR, FoodTileType.CREPE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.MACARON, 24)),
            moves = 26
        ),
        // Level 6: Ratatouille
        Match3LevelDefinition(
            levelNumber = 6,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.RATATOUILLE, FoodTileType.TOMATO, FoodTileType.BASIL, FoodTileType.POTATO, FoodTileType.FRENCH_CHEESE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.RATATOUILLE, 20)),
            moves = 28
        ),
        // Level 7: Éclair
        Match3LevelDefinition(
            levelNumber = 7,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.ECLAIR, FoodTileType.MACARON, FoodTileType.TIRAMISU, FoodTileType.BLACK_FOREST_CAKE, FoodTileType.CREPE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.ECLAIR, 15)),
            moves = 28
        ),
        // Level 8: Soufflé
        Match3LevelDefinition(
            levelNumber = 8,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.SOUFFLE, FoodTileType.CREPE, FoodTileType.FRENCH_CHEESE, FoodTileType.CROISSANT, FoodTileType.ECLAIR),
            goals = listOf(LevelGoal.ScoreTarget(10000)),
            moves = 30
        ),
        // Level 9: Tarte Tatin
        Match3LevelDefinition(
            levelNumber = 9,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.TARTE_TATIN, FoodTileType.APPLE, FoodTileType.CROISSANT, FoodTileType.MACARON, FoodTileType.SOUFFLE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.TARTE_TATIN, 15), LevelGoal.ScoreTarget(5000)),
            moves = 30
        ),
        // Level 10: France Master
        Match3LevelDefinition(
            levelNumber = 10,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.CROISSANT, FoodTileType.BAGUETTE, FoodTileType.FRENCH_CHEESE, FoodTileType.MACARON, FoodTileType.RATATOUILLE, FoodTileType.SOUFFLE, FoodTileType.ECLAIR),
            goals = listOf(LevelGoal.ScoreTarget(17500)),
            moves = 32
        ),
        // Level 11: Croissant Master
        Match3LevelDefinition(
            levelNumber = 11,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.CROISSANT, FoodTileType.BAGUETTE, FoodTileType.BREAD, FoodTileType.FRENCH_CHEESE, FoodTileType.CREPE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.CROISSANT, 30)),
            moves = 25
        ),
        // Level 12: Macaron Party
        Match3LevelDefinition(
            levelNumber = 12,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.MACARON, FoodTileType.ECLAIR, FoodTileType.SOUFFLE, FoodTileType.TARTE_TATIN, FoodTileType.GELATO),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.MACARON, 25)),
            moves = 25
        ),
        // Level 13: Soufflé Rise
        Match3LevelDefinition(
            levelNumber = 13,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.SOUFFLE, FoodTileType.CREPE, FoodTileType.FRENCH_CHEESE, FoodTileType.CROISSANT, FoodTileType.BAGUETTE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.SOUFFLE, 12)),
            moves = 22
        ),
        // Level 14: Bistro Special
        Match3LevelDefinition(
            levelNumber = 14,
            countryId = "france",
            allowedTiles = listOf(FoodTileType.RATATOUILLE, FoodTileType.TOMATO, FoodTileType.BASIL, FoodTileType.POTATO, FoodTileType.FRENCH_CHEESE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.RATATOUILLE, 20)),
            moves = 26
        ),
        // Level 15: France Grand Finale
        Match3LevelDefinition(
            levelNumber = 15,
            countryId = "france",
            allowedTiles = listOf(
                FoodTileType.CROISSANT,
                FoodTileType.BAGUETTE,
                FoodTileType.FRENCH_CHEESE,
                FoodTileType.CREPE,
                FoodTileType.MACARON,
                FoodTileType.RATATOUILLE,
                FoodTileType.ECLAIR,
                FoodTileType.SOUFFLE,
                FoodTileType.TARTE_TATIN
            ),
            goals = listOf(
                LevelGoal.ScoreTarget(24000),
                LevelGoal.CollectFood(FoodTileType.TARTE_TATIN, 10)
            ),
            moves = 32
        )
    )

    fun getLevel(levelNumber: Int): Match3LevelDefinition? {
        return levels.find { it.levelNumber == levelNumber }
    }
}
