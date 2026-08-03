package com.mahmodhota.worldfood3dadventure.game.match3.model

/**
 * Visual+logical snapshot for one resolved cascade phase.
 */
data class CascadeStep(
    val matchedPositions: Set<BoardPosition>,
    val boardAfterStep: Match3Board,
    val fallDistanceByTileId: Map<Long, Int>,
    val refillTileIds: Set<Long>,
    val specialSpawnCount: Int = 0,
    val createdSpecialTiles: Map<BoardPosition, SpecialTileType> = emptyMap(),
    val specialEffects: List<SpecialBoardEffect> = emptyList(),
    val scoreAwarded: Int = 0
)
