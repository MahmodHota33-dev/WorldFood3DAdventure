package com.mahmodhota.worldfood3dadventure.game.world.japan

import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3LevelDefinition

/**
 * 10 Playable Match-3 levels for the Japan chapter.
 */
object JapanMatch3Levels {
    
    val levels = listOf(
        // Level 1: Sushi
        Match3LevelDefinition(
            levelNumber = 1,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.SUSHI, FoodTileType.RAMEN, FoodTileType.ONIGIRI, FoodTileType.MATCHA),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.SUSHI, 20)),
            moves = 20
        ),
        // Level 2: Ramen
        Match3LevelDefinition(
            levelNumber = 2,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.RAMEN, FoodTileType.UDON, FoodTileType.ONIGIRI, FoodTileType.MATCHA),
            goals = listOf(LevelGoal.ScoreTarget(5000)),
            moves = 22
        ),
        // Level 3: Tempura
        Match3LevelDefinition(
            levelNumber = 3,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.TEMPURA, FoodTileType.SUSHI, FoodTileType.ONIGIRI, FoodTileType.MATCHA),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.TEMPURA, 15)),
            moves = 24
        ),
        // Level 4: Onigiri
        Match3LevelDefinition(
            levelNumber = 4,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.ONIGIRI, FoodTileType.SUSHI, FoodTileType.UDON, FoodTileType.MATCHA),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.ONIGIRI, 25)),
            moves = 25
        ),
        // Level 5: Mochi
        Match3LevelDefinition(
            levelNumber = 5,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.MOCHI, FoodTileType.DORAYAKI, FoodTileType.MATCHA, FoodTileType.ONIGIRI),
            goals = listOf(LevelGoal.ScoreTarget(7000)),
            moves = 26
        ),
        // Level 6: Takoyaki
        Match3LevelDefinition(
            levelNumber = 6,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.TAKOYAKI, FoodTileType.RAMEN, FoodTileType.SUSHI, FoodTileType.TEMPURA),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.TAKOYAKI, 20)),
            moves = 28
        ),
        // Level 7: Udon
        Match3LevelDefinition(
            levelNumber = 7,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.UDON, FoodTileType.RAMEN, FoodTileType.ONIGIRI, FoodTileType.SUSHI),
            goals = listOf(LevelGoal.ScoreTarget(8000)),
            moves = 28
        ),
        // Level 8: Matcha
        Match3LevelDefinition(
            levelNumber = 8,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.MATCHA, FoodTileType.MOCHI, FoodTileType.DORAYAKI, FoodTileType.SUSHI),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.MATCHA, 12)),
            moves = 30
        ),
        // Level 9: Dorayaki
        Match3LevelDefinition(
            levelNumber = 9,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.DORAYAKI, FoodTileType.MOCHI, FoodTileType.SUSHI, FoodTileType.RAMEN),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.DORAYAKI, 15)),
            moves = 30
        ),
        // Level 10: Japan Master
        Match3LevelDefinition(
            levelNumber = 10,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.SUSHI, FoodTileType.RAMEN, FoodTileType.TEMPURA, FoodTileType.ONIGIRI, FoodTileType.MOCHI, FoodTileType.MATCHA),
            goals = listOf(LevelGoal.ScoreTarget(20000)),
            moves = 32
        ),
        // Level 11: Tempura Feast
        Match3LevelDefinition(
            levelNumber = 11,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.TEMPURA, FoodTileType.UDON, FoodTileType.SUSHI, FoodTileType.ONIGIRI),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.TEMPURA, 25)),
            moves = 28
        ),
        // Level 12: Matcha Ceremony
        Match3LevelDefinition(
            levelNumber = 12,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.MATCHA, FoodTileType.DORAYAKI, FoodTileType.MOCHI, FoodTileType.ONIGIRI),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.MATCHA, 20)),
            moves = 25
        ),
        // Level 13: Udon Rush
        Match3LevelDefinition(
            levelNumber = 13,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.UDON, FoodTileType.TAKOYAKI, FoodTileType.RAMEN, FoodTileType.SUSHI),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.UDON, 22)),
            moves = 30
        ),
        // Level 14: Sweet Treats
        Match3LevelDefinition(
            levelNumber = 14,
            countryId = "japan",
            allowedTiles = listOf(FoodTileType.MOCHI, FoodTileType.DORAYAKI, FoodTileType.MATCHA, FoodTileType.TAKOYAKI),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.MOCHI, 30), LevelGoal.CollectFood(FoodTileType.DORAYAKI, 20)),
            moves = 35
        ),
        // Level 15: Imperial Challenge
        Match3LevelDefinition(
            levelNumber = 15,
            countryId = "japan",
            allowedTiles = FoodTileType.values().filter { it.name in listOf("SUSHI", "RAMEN", "TEMPURA", "ONIGIRI", "MOCHI", "MATCHA", "DORAYAKI", "TAKOYAKI", "UDON") },
            goals = listOf(LevelGoal.ScoreTarget(35000), LevelGoal.CollectFood(FoodTileType.SUSHI, 25)),
            moves = 40
        )
    )

    fun getLevel(levelNumber: Int): Match3LevelDefinition? {
        return levels.find { it.levelNumber == levelNumber }
    }
}
