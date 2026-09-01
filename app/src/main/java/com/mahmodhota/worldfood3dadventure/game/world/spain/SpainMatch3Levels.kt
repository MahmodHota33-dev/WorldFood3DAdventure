package com.mahmodhota.worldfood3dadventure.game.world.spain

import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3LevelDefinition

object SpainMatch3Levels {

    val levels = listOf(
        Match3LevelDefinition(
            levelNumber = 1,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.PAELLA, FoodTileType.TOMATO, FoodTileType.CHEESE, FoodTileType.BREAD, FoodTileType.OLIVES),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.PAELLA, 16)),
            moves = 24,
            title = "Madrid"
        ),
        Match3LevelDefinition(
            levelNumber = 2,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.TORTILLA_ESPANOLA, FoodTileType.PAELLA, FoodTileType.CHEESE, FoodTileType.POTATO, FoodTileType.TOMATO),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.TORTILLA_ESPANOLA, 18)),
            moves = 24,
            title = "Barcelona"
        ),
        Match3LevelDefinition(
            levelNumber = 3,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.JAMON_IBERICO, FoodTileType.BREAD, FoodTileType.CHEESE, FoodTileType.TOMATO, FoodTileType.OLIVES),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.JAMON_IBERICO, 18)),
            moves = 25,
            title = "Valencia"
        ),
        Match3LevelDefinition(
            levelNumber = 4,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.CHURROS, FoodTileType.GELATO, FoodTileType.MACARON, FoodTileType.BREAD, FoodTileType.COFFEE),
            goals = listOf(LevelGoal.ScoreTarget(8200)),
            moves = 25,
            title = "Seville"
        ),
        Match3LevelDefinition(
            levelNumber = 5,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.GAZPACHO, FoodTileType.TOMATO, FoodTileType.BASIL, FoodTileType.CHEESE, FoodTileType.BREAD),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.GAZPACHO, 20)),
            moves = 26,
            title = "Granada"
        ),
        Match3LevelDefinition(
            levelNumber = 6,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.PATATAS_BRAVAS, FoodTileType.BREAD, FoodTileType.CHEESE, FoodTileType.POTATO, FoodTileType.TOMATO),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.PATATAS_BRAVAS, 20)),
            moves = 26,
            title = "Bilbao"
        ),
        Match3LevelDefinition(
            levelNumber = 7,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.PATATAS_BRAVAS, FoodTileType.POTATO, FoodTileType.TOMATO, FoodTileType.CHILI, FoodTileType.OLIVES),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.PATATAS_BRAVAS, 22)),
            moves = 27,
            title = "Malaga"
        ),
        Match3LevelDefinition(
            levelNumber = 8,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.PULPO_A_LA_GALLEGA, FoodTileType.POTATO, FoodTileType.BREAD, FoodTileType.CHEESE, FoodTileType.TOMATO),
            goals = listOf(LevelGoal.ScoreTarget(10500)),
            moves = 28,
            title = "Toledo"
        ),
        Match3LevelDefinition(
            levelNumber = 9,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.SANGRIA, FoodTileType.PAELLA, FoodTileType.CHURROS, FoodTileType.BREAD, FoodTileType.APPLE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.SANGRIA, 14)),
            moves = 28,
            title = "Mallorca"
        ),
        Match3LevelDefinition(
            levelNumber = 10,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.CREMA_CATALANA, FoodTileType.CROISSANT, FoodTileType.MACARON, FoodTileType.CHEESE, FoodTileType.MILK),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.CREMA_CATALANA, 16)),
            moves = 28,
            title = "Zaragoza"
        ),
        Match3LevelDefinition(
            levelNumber = 11,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.PAELLA, FoodTileType.PATATAS_BRAVAS, FoodTileType.TORTILLA_ESPANOLA, FoodTileType.TOMATO, FoodTileType.OLIVES),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.PAELLA, 24), LevelGoal.ScoreTarget(6000)),
            moves = 29,
            title = "Costa Brava"
        ),
        Match3LevelDefinition(
            levelNumber = 12,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.PATATAS_BRAVAS, FoodTileType.TORTILLA_ESPANOLA, FoodTileType.PAELLA, FoodTileType.CHURROS, FoodTileType.COFFEE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.PATATAS_BRAVAS, 20)),
            moves = 30,
            title = "Canary Islands"
        ),
        Match3LevelDefinition(
            levelNumber = 13,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.PAELLA, FoodTileType.PULPO_A_LA_GALLEGA, FoodTileType.CREMA_CATALANA, FoodTileType.BREAD, FoodTileType.CHEESE),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.PAELLA, 22)),
            moves = 30,
            title = "Santiago de Compostela"
        ),
        Match3LevelDefinition(
            levelNumber = 14,
            countryId = "spain",
            allowedTiles = listOf(FoodTileType.JAMON_IBERICO, FoodTileType.CHURROS, FoodTileType.GAZPACHO, FoodTileType.MACARON, FoodTileType.TOMATO),
            goals = listOf(LevelGoal.CollectFood(FoodTileType.JAMON_IBERICO, 18), LevelGoal.ScoreTarget(9000)),
            moves = 31,
            title = "Ibiza"
        ),
        Match3LevelDefinition(
            levelNumber = 15,
            countryId = "spain",
            allowedTiles = listOf(
                FoodTileType.PAELLA,
                FoodTileType.TORTILLA_ESPANOLA,
                FoodTileType.JAMON_IBERICO,
                FoodTileType.CHURROS,
                FoodTileType.GAZPACHO,
                FoodTileType.PATATAS_BRAVAS,
                FoodTileType.PULPO_A_LA_GALLEGA,
                FoodTileType.CREMA_CATALANA,
                FoodTileType.OLIVES,
                FoodTileType.TOMATO,
                FoodTileType.BREAD,
                FoodTileType.CHEESE
            ),
            goals = listOf(
                LevelGoal.ScoreTarget(26000),
                LevelGoal.CollectFood(FoodTileType.CREMA_CATALANA, 12)
            ),
            moves = 32,
            title = "Royal Palace Finale"
        )
    )

    fun getLevel(levelNumber: Int): Match3LevelDefinition? {
        return levels.find { it.levelNumber == levelNumber }
    }
}
