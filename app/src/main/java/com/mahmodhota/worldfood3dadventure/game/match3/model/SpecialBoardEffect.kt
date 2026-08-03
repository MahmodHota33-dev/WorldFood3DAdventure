package com.mahmodhota.worldfood3dadventure.game.match3.model

enum class SpecialBoardEffectType {
    HORIZONTAL_LINE,
    VERTICAL_LINE,
    BOMB,
    COLOR_CLEAR
}

data class SpecialBoardEffect(
    val type: SpecialBoardEffectType,
    val origin: BoardPosition,
    val affectedPositions: Set<BoardPosition>,
    val affectedTileType: FoodTileType? = null
)
