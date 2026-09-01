package com.mahmodhota.worldfood3dadventure.game.world.mexico

import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3LevelDefinition

/**
 * 15 Playable Match-3 levels for the Mexico chapter.
 */
object MexicoMatch3Levels {
    
    val levels = listOf(
        // Level 1: Taco
        Match3LevelDefinition(
            levelNumber = 1,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.TACO, FoodTileType.GUACAMOLE, FoodTileType.CHILI, FoodTileType.TOMATO, FoodTileType.CORN),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.TACO, 20)),
            moves = 20
        ),
        // Level 2: Burrito
        Match3LevelDefinition(
            levelNumber = 2,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.BURRITO, FoodTileType.TACO, FoodTileType.QUESADILLA, FoodTileType.CHILI, FoodTileType.CORN),
            goals = listOf(LevelGoal.ScoreTarget(5000)),
            moves = 22
        ),
        // Level 3: Guacamole
        Match3LevelDefinition(
            levelNumber = 3,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.GUACAMOLE, FoodTileType.NACHOS, FoodTileType.CHILI, FoodTileType.TOMATO, FoodTileType.TACO),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.GUACAMOLE, 15)),
            moves = 24
        ),
        // Level 4: Nachos
        Match3LevelDefinition(
            levelNumber = 4,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.NACHOS, FoodTileType.TACO, FoodTileType.GUACAMOLE, FoodTileType.CHILI, FoodTileType.CORN),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.NACHOS, 25)),
            moves = 25
        ),
        // Level 5: Chili
        Match3LevelDefinition(
            levelNumber = 5,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.CHILI, FoodTileType.TACO, FoodTileType.BURRITO, FoodTileType.POZOLE, FoodTileType.CORN),
            goals = listOf(LevelGoal.ScoreTarget(7000)),
            moves = 26
        ),
        // Level 6: Tamale
        Match3LevelDefinition(
            levelNumber = 6,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.TAMALE, FoodTileType.POZOLE, FoodTileType.CHILI, FoodTileType.GUACAMOLE, FoodTileType.CORN),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.TAMALE, 20)),
            moves = 28
        ),
        // Level 7: Quesadilla
        Match3LevelDefinition(
            levelNumber = 7,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.QUESADILLA, FoodTileType.NACHOS, FoodTileType.TACO, FoodTileType.CHILI, FoodTileType.CORN),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.QUESADILLA, 15)),
            moves = 28
        ),
        // Level 8: Churros
        Match3LevelDefinition(
            levelNumber = 8,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.CHURROS, FoodTileType.TACO, FoodTileType.GELATO, FoodTileType.MACARON, FoodTileType.COFFEE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.CHURROS, 12)),
            moves = 30
        ),
        // Level 9: Pozole
        Match3LevelDefinition(
            levelNumber = 9,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.POZOLE, FoodTileType.TAMALE, FoodTileType.CHILI, FoodTileType.TOMATO, FoodTileType.TACO),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.POZOLE, 15)),
            moves = 30
        ),
        // Level 10: Mexico Master
        Match3LevelDefinition(
            levelNumber = 10,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.TACO, FoodTileType.BURRITO, FoodTileType.GUACAMOLE, FoodTileType.NACHOS, FoodTileType.CHILI, FoodTileType.TAMALE, FoodTileType.CORN),
            goals = listOf(LevelGoal.ScoreTarget(20000)),
            moves = 32
        ),
        // Level 11: Fiesta Nachos
        Match3LevelDefinition(
            levelNumber = 11,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.NACHOS, FoodTileType.CHURROS, FoodTileType.TACO, FoodTileType.GUACAMOLE, FoodTileType.CORN),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.NACHOS, 30)),
            moves = 28
        ),
        // Level 12: Pozole Pot
        Match3LevelDefinition(
            levelNumber = 12,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.POZOLE, FoodTileType.TAMALE, FoodTileType.CHILI, FoodTileType.TOMATO, FoodTileType.TACO),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.POZOLE, 20)),
            moves = 25
        ),
        // Level 13: Quesadilla Queen
        Match3LevelDefinition(
            levelNumber = 13,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.QUESADILLA, FoodTileType.BURRITO, FoodTileType.TACO, FoodTileType.GUACAMOLE, FoodTileType.CORN),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.QUESADILLA, 22)),
            moves = 30
        ),
        // Level 14: Street Food Mix
        Match3LevelDefinition(
            levelNumber = 14,
            countryId = "mexico",
            allowedTiles = listOf(FoodTileType.TACO, FoodTileType.TAMALE, FoodTileType.NACHOS, FoodTileType.CHURROS, FoodTileType.CORN),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.TACO, 25), LevelGoal.CollectFood(FoodTileType.CHURROS, 15)),
            moves = 35
        ),
        // Level 15: Aztec Challenge
        Match3LevelDefinition(
            levelNumber = 15,
            countryId = "mexico",
            allowedTiles = FoodTileType.values().filter { it.name in listOf("TACO", "BURRITO", "GUACAMOLE", "NACHOS", "CHILI", "TAMALE", "POZOLE", "QUESADILLA", "CHURROS", "CORN", "TOMATO") },
            goals = listOf(LevelGoal.ScoreTarget(35000), LevelGoal.CollectFood(FoodTileType.TAMALE, 15)),
            moves = 40
        )
    )

    fun getLevel(levelNumber: Int): Match3LevelDefinition? {
        return levels.find { it.levelNumber == levelNumber }
    }
}
