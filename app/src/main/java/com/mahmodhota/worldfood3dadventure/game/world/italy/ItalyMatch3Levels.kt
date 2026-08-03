package com.mahmodhota.worldfood3dadventure.game.world.italy

import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3LevelDefinition

/**
 * 10 Playable Match-3 levels for the Italy chapter.
 */
object ItalyMatch3Levels {
    
    val levels = listOf(
        // Level 1: Pizza Margherita
        Match3LevelDefinition(
            levelNumber = 1,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.PIZZA, FoodTileType.TOMATO, FoodTileType.CHEESE, FoodTileType.BASIL),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.PIZZA, 15)),
            moves = 30,
            title = "Delicious Pizza Adventure"
        ),
        // Level 2: Tomatoes
        Match3LevelDefinition(
            levelNumber = 2,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.TOMATO, FoodTileType.BASIL, FoodTileType.CHEESE, FoodTileType.PIZZA),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.TOMATO, 20)),
            moves = 25
        ),
        // Level 3: Mozzarella
        Match3LevelDefinition(
            levelNumber = 3,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.CHEESE, FoodTileType.TOMATO, FoodTileType.BASIL, FoodTileType.PIZZA),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.CHEESE, 25)),
            moves = 25
        ),
        // Level 4: Spaghetti Carbonara
        Match3LevelDefinition(
            levelNumber = 4,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.SPAGHETTI, FoodTileType.CHEESE, FoodTileType.PASTA, FoodTileType.BASIL),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.SPAGHETTI, 15)),
            moves = 28
        ),
        // Level 5: Lasagne
        Match3LevelDefinition(
            levelNumber = 5,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.LASAGNE, FoodTileType.TOMATO, FoodTileType.CHEESE, FoodTileType.PASTA),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.LASAGNE, 10)),
            moves = 22
        ),
        // Level 6: Ravioli
        Match3LevelDefinition(
            levelNumber = 6,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.RAVIOLI, FoodTileType.TOMATO, FoodTileType.BASIL, FoodTileType.CHEESE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.RAVIOLI, 20)),
            moves = 30
        ),
        // Level 7: Gnocchi
        Match3LevelDefinition(
            levelNumber = 7,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.GNOCCHI, FoodTileType.TOMATO, FoodTileType.CHEESE, FoodTileType.BASIL),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.GNOCCHI, 20)),
            moves = 25
        ),
        // Level 8: Gelato
        Match3LevelDefinition(
            levelNumber = 8,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.GELATO, FoodTileType.TIRAMISU, FoodTileType.CHEESE, FoodTileType.BASIL),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.GELATO, 15)),
            moves = 20
        ),
        // Level 9: Tiramisu
        Match3LevelDefinition(
            levelNumber = 9,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.TIRAMISU, FoodTileType.GELATO, FoodTileType.CHEESE, FoodTileType.BASIL),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.TIRAMISU, 12)),
            moves = 20
        ),
        // Level 10: Italy Master
        Match3LevelDefinition(
            levelNumber = 10,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.PIZZA, FoodTileType.PASTA, FoodTileType.SPAGHETTI, FoodTileType.LASAGNE, FoodTileType.GELATO, FoodTileType.TIRAMISU),
            goals = listOf(LevelGoal.ScoreTarget(17000)),
            moves = 34
        ),
        // Level 11: Pizza Master
        Match3LevelDefinition(
            levelNumber = 11,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.PIZZA, FoodTileType.TOMATO, FoodTileType.CHEESE, FoodTileType.BASIL),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.PIZZA, 30)),
            moves = 25
        ),
        // Level 12: Spaghetti Night
        Match3LevelDefinition(
            levelNumber = 12,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.SPAGHETTI, FoodTileType.CHEESE, FoodTileType.PASTA, FoodTileType.TOMATO),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.SPAGHETTI, 25)),
            moves = 25
        ),
        // Level 13: Lasagne Feast
        Match3LevelDefinition(
            levelNumber = 13,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.LASAGNE, FoodTileType.CHEESE, FoodTileType.TOMATO, FoodTileType.BASIL),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.LASAGNE, 20)),
            moves = 30
        ),
        // Level 14: Gelato Galore
        Match3LevelDefinition(
            levelNumber = 14,
            countryId = "italy",
            allowedTiles = listOf(FoodTileType.GELATO, FoodTileType.TIRAMISU, FoodTileType.CHEESE, FoodTileType.BASIL),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.GELATO, 18)),
            moves = 24
        ),
        // Level 15: Italy Grand Finale
        Match3LevelDefinition(
            levelNumber = 15,
            countryId = "italy",
            allowedTiles = listOf(
                FoodTileType.PIZZA,
                FoodTileType.PASTA,
                FoodTileType.TOMATO,
                FoodTileType.CHEESE,
                FoodTileType.BASIL,
                FoodTileType.SPAGHETTI,
                FoodTileType.LASAGNE,
                FoodTileType.RAVIOLI,
                FoodTileType.GNOCCHI,
                FoodTileType.TIRAMISU,
                FoodTileType.GELATO
            ),
            goals = listOf(
                LevelGoal.ScoreTarget(24000),
                LevelGoal.CollectFood(FoodTileType.TIRAMISU, 10)
            ),
            moves = 32
        )
    )

    fun getLevel(levelNumber: Int): Match3LevelDefinition? {
        return levels.find { it.levelNumber == levelNumber }
    }
}
